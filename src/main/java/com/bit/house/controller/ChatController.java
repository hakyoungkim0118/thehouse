package com.bit.house.controller;

import com.bit.house.chattingProcess.ChatRepository;
import com.bit.house.chattingProcess.RedisPublisher;
import com.bit.house.domain.ChatRoomVO;
import com.bit.house.domain.ChatVO;
import com.bit.house.domain.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final ChatRepository chatRepository;

    @MessageMapping("/message")
    public void message(ChatVO message){

        if(ChatVO.MessageType.ENTER.equals(message.getType())){
            chatRepository.enterChatRoom(message.getChatId());
            redisPublisher.publish(chatRepository.getTopic(message.getChatId()), message);
        } else if(ChatVO.MessageType.LEAVE.equals(message.getType())){
            message.setMsg(message.getSender()+"님이 퇴장하셨습니다.");
            redisPublisher.publish(chatRepository.getTopic(message.getChatId()), message);
            chatRepository.deleteChatRoom(message.getChatId());
        } else{
            redisPublisher.publish(chatRepository.getTopic(message.getChatId()), message);
        }

    }

    @GetMapping("/admin/chatList")
    public String startChat(Model model, HttpSession session){
        //채팅리스트
        model.addAttribute("roomLists", chatRepository.findAllRoom());
        return "th/admin/chat/chatList";
    }

    @PostMapping("/chat")
    public String createChat(HttpSession session){
        //채팅방 생성
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        String memberId = memberVO.getMemberId();
        ChatRoomVO vo=chatRepository.createRoom(memberId);
        String chatId=vo.getChatId();

        return "redirect:/enter/"+chatId;
    }

    @GetMapping("/enter/{chatId}")
    public String enterChat(Model model, @PathVariable String chatId){
        //채팅방 입장
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ChatRoomVO vo=chatRepository.findRoombyId(chatId);

        if(String.valueOf(authentication.getAuthorities()).equals("[ROLE_ADMIN]")) {
            //관리자아이디 set
            vo.setAdminId(authentication.getName());
            chatRepository.setAdmin(vo);
        }
        //인원수+1
        int count=vo.getCount();
        vo.setCount(++count);
        chatRepository.addCount(vo);

        model.addAttribute("chatInfo", vo);

        return "th/main/chatting";
    }
}
