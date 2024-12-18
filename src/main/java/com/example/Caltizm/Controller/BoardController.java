package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.PostDTO;
import com.example.Caltizm.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    //Test
    @GetMapping("/post")
    public String postTest(){
        return "board/formtest";
    }

    // 게시판 작성
    @PostMapping("/savePost")
    public String savePost(@ModelAttribute PostDTO postDTO,
                           RedirectAttributes redirectAttributes,
                           @SessionAttribute(value = "email") String email) {

        int user_id = repository.getUser(email);
        postDTO.setUser_id(user_id);
        repository.insertPost(postDTO);
        return "redirect:/boardAll";
    }


    // 파일 업로드 경로

    final Path FILE_ROOT = Paths.get("./").toAbsolutePath().normalize();
    private String uploadPath = FILE_ROOT.toString() + "/upload/image/";

    @ResponseBody
    @PostMapping("/imageUpload")
    public ResponseEntity<?> imageUpload(@RequestParam(name = "file") MultipartFile file) throws Exception{

        System.err.println("이미지 업로드");


        try {
            // 업로드 파일의 이름
            String originalFileName = file.getOriginalFilename();

            // 업로드 파일의 확장자
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // 업로드 된 파일이 중복될 수 있어서 파일 이름 재설정
            String reFileName = UUID.randomUUID().toString() + fileExtension;

            // 업로드 경로에 파일명을 변경하여 저장
            file.transferTo(new File(uploadPath, reFileName));

            // 파일이름을 재전송
            return ResponseEntity.ok(reFileName);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("업로드 에러");
        }
    }


    @ResponseBody
    @PostMapping("/imageDelete")
    public void imageDelete(@RequestParam String file) throws Exception{

        System.err.println("이미지 삭제");

        try {
            Path path = Paths.get(uploadPath, file);
            Files.delete(path);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }









}
