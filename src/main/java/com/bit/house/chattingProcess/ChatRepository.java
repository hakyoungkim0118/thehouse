package com.bit.house.chattingProcess;

import com.bit.house.domain.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRepository {

    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private static final String CHAT_ROOMS="CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomVO> opsHashChatRoom;
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    public void init() {
        opsHashChatRoom=redisTemplate.opsForHash();
        topics=new HashMap<>();
    }

    public List<ChatRoomVO> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoomVO findRoombyId(String chatId) {
        return opsHashChatRoom.get(CHAT_ROOMS, chatId);
    }
    public ChatRoomVO setAdmin(ChatRoomVO chatRoomVO) {
        opsHashChatRoom.put(CHAT_ROOMS, chatRoomVO.getChatId(), chatRoomVO);
        return opsHashChatRoom.get(CHAT_ROOMS, chatRoomVO.getChatId());
    }

    public ChatRoomVO createRoom(String memberId) {
        //채팅방생성-채팅방공유를 위해 redis hash에 저장
        ChatRoomVO chatRoomVO=ChatRoomVO.create(memberId);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoomVO.getChatId(), chatRoomVO);

        return chatRoomVO;
    }

    public void enterChatRoom(String chatId) {
        //채팅방 입장-redis에 topic을 만들고 pub/sub 통신을 위한 리스너 설정
        ChannelTopic topic=topics.get(chatId);
        if(topic==null){
            topic=new ChannelTopic(chatId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(chatId, topic);
        }
    }

    public ChannelTopic getTopic(String chatId) {
        return topics.get(chatId);
    }

    public void deleteChatRoom(String chatId){
        ChannelTopic topic=topics.get(chatId);
        redisMessageListener.removeMessageListener(redisSubscriber, topic);
        topics.remove(chatId);
        opsHashChatRoom.delete(CHAT_ROOMS, chatId);
    }

    public ChatRoomVO addCount(ChatRoomVO chatRoomVO){
        opsHashChatRoom.put(CHAT_ROOMS, chatRoomVO.getChatId(), chatRoomVO);
        return opsHashChatRoom.get(CHAT_ROOMS, chatRoomVO.getChatId());
    }
}
