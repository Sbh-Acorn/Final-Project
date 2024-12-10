package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.PostDTO;
import com.example.Caltizm.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    BoardRepository repository;

    // 전체 게시판 조회
    @GetMapping("/boardAll")
    public String boardAll(Model model){
        List<PostDTO> boardList = repository.selectAll();
        model.addAttribute("boardAll",boardList);
        return "board/boardtest";
    }

    // 공지사항 조회
    @GetMapping("/boardNotice")
    public String boardNotice(Model model){
        List<PostDTO> boardList = repository.selectNotice();
        model.addAttribute("boardNotice",boardList);
        return "board/boardtest";
    }

    // 자유게시판 조회
    @GetMapping("/boardFree")
    public String boardFree(Model model){
        List<PostDTO> boardList = repository.selectFree();
        model.addAttribute("boardFree", boardList);
        return "board/boardtest";
    }

    // 리뷰 조회
    @GetMapping("/boardReview")
    public String boardReview(Model model){
        List<PostDTO> boardList = repository.selectReview();
        model.addAttribute("boardReview", boardList);
        return "board/boardtest";
    }

    // Q&A 조회
    @GetMapping("/boardQna")
    public String boardQna(Model model){
        List<PostDTO> boardList = repository.selectQna();
        model.addAttribute("boardQna",boardList);
        return "board/boardtest";
    }



}
