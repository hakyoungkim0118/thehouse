package com.bit.house.controller;

import com.bit.house.domain.*;
import com.bit.house.mapper.MyPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MyPageController {

    @Autowired(required = false)
    MyPageMapper myPageMapper;


    //프로필설정
    @RequestMapping("/myProfile")
    private String viewProfile(Model model, String memberId) throws Exception{

        model.addAttribute("profile", myPageMapper.selectProfile(memberId));

        return "th/member/mypage/profile/profileInfo";
    }
    //프로필수정
    @RequestMapping("/modifyProfile")
    private String modifyProfile(MemberVO memberVO, HttpServletRequest request) throws Exception{

        memberVO.setNickName(request.getParameter("nickName"));
        memberVO.setMemberImg(request.getParameter("memberImg"));
        memberVO.setMemberIntro(request.getParameter("memberIntro"));

        myPageMapper.modifyProfile(memberVO);
        return "redirect:/myProfile";
    }
    //팔로워메뉴
    @RequestMapping("/viewFollow")
    private String viewFollow(Model model) throws Exception{

        model.addAttribute("follower", myPageMapper.follower());
        model.addAttribute("following", myPageMapper.following());

        return "/th/member/mypage/viewFollow";
    }
    //팔로우 팔로잉 전체보기 html 하나로 합쳐볼것.
    @RequestMapping("/allFollow")
    private String allFollow(Model model) throws Exception{

        model.addAttribute("follow", myPageMapper.follower());

        return "/th/member/mypage/allFollow";
    }
    //팔로잉 전체보기
    @RequestMapping("allFollowing")
    private String allFollowing(Model model) throws Exception{

        model.addAttribute("following", myPageMapper.following());

        return "/th/member/mypage/allFollowing";
    }
    //팔로우
    @RequestMapping("/follow")
    private String follow(HttpServletRequest request, FollowVO followVO, @PathVariable String memberId) throws Exception{

        followVO.setFollowId(request.getParameter(memberId));

        myPageMapper.follow(memberId);
        return "";  //어느페이지에서 하던 페이지 변경없이 되도록 설정해야함.
    }

    //팔로우취소
    @RequestMapping("/cancelFollow")
    private String cancelFollow(@PathVariable String memberId) throws Exception{

        myPageMapper.cancelFollow(memberId);

        return "";
    }
    //내 프로필
    @RequestMapping("/myProfile")
    private String myProfile(Model model) throws Exception{

        model.addAttribute("profile", myPageMapper.myProfile());
        model.addAttribute("mphoto", myPageMapper.profilePhoto());
        model.addAttribute("mscrap", myPageMapper.profileScrap());

        return "/th/member/mypage/myProfile";
    }
    //이것도 내 프로필이랑 같은 페이지에서 처리 가능할지 확인
    @RequestMapping("/memberProfile")
    private String memberProfile(Model model, @PathVariable String memberId) throws Exception{

        model.addAttribute("memprofile", myPageMapper.myProfile());
        model.addAttribute("mphoto", myPageMapper.profilePhoto());
        model.addAttribute("mscrap", myPageMapper.profileScrap());

        return "/th/member/mypage/memberProfile";
    }
    //사진 게시글 전체보기
    @RequestMapping("/allPhoto")
    private String allPhoto(Model model, @PathVariable String memberId) throws Exception{
        //이거는 세션 받지 말고 프로필창에서 아이디 넘겨받아서 처리하는쪽으로

        model.addAttribute("photo", myPageMapper.allPhoto());

        return "/th/member/mypage/allPhoto";
    }

    //스크랩 전체보기
    @RequestMapping("/allScrap")
    private String allScrap(Model model, @PathVariable String memberId) throws Exception{
        //이것도 세션안받음

        model.addAttribute("scrap", myPageMapper.allScrap());

        return "/th/member/mypage/allScrap";
    }

    //보낸쪽지함
    @RequestMapping("/sendNote")
    private String sendNote(Model model) throws Exception{

        model.addAttribute("sendnote", myPageMapper.sendNote());

        return "/th/member/mypage/sendNote";
    }

    //받은쪽지함
    @RequestMapping("/receiveNote")
    private String receiveNote(Model model) throws Exception{

        model.addAttribute("receiveNote", myPageMapper.receiveNote());

        return "/th/member/mypage/receiveNote";
    }

    //쪽지보내기 폼
    @RequestMapping("/noteSending")
    private String noteSending(){return "/th/member/mypage/noteSending";}

    //쪽지보내기
    @RequestMapping("/noteSendingProc")
    private String noteSendingProc(MsgVO msgVO, HttpServletRequest request) throws Exception{

        msgVO.setMemberId(request.getParameter("memberId")); //내 아이디 = 세션처리
        msgVO.setMsgContents(request.getParameter("msgContent"));
        msgVO.setRecieveId(request.getParameter("memberId"));

        myPageMapper.noteSending(msgVO);

        return "redirect:/sendNote";
    }

    //쪽지 삭제
    @RequestMapping("/deleteNote")
    private String deleteNote(@PathVariable int msgNo) throws Exception{

        myPageMapper.deleteNote(msgNo);

        return "redirect:/sendNote";
    }
}
