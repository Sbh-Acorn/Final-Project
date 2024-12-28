package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CommentDTO;
import com.example.Caltizm.DTO.PostDTO;
import com.example.Caltizm.Repository.BoardRepository;
import jakarta.servlet.http.HttpSession;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    BoardRepository repository;

    // 전체 게시판 조회
    @GetMapping("/boardAll")
    public String boardAll(@RequestParam(value = "query", required = false) String query, Model model){
        if (query != null && !query.trim().isEmpty()) {
            // 검색 결과가 있을 경우
            List<PostDTO> searchResults = repository.searchPosts(query);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("isSearch", true);
            model.addAttribute("query", query); // 검색어 전달
        } else {
            model.addAttribute("isSearch", false);
        }
  
        List<PostDTO> boardList = repository.selectAll();
        List<PostDTO> boardNotice = repository.selectNotice();
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        model.addAttribute("boardNotice", boardNotice);
        model.addAttribute("boardList",boardList);
        model.addAttribute("boardName", "all");

        return "board/board_main";
    }

    // 공지사항 조회
    @GetMapping("/boardNotice")
    public String boardNotice(Model model){
        List<PostDTO> boardList = repository.selectNotice();
        List<PostDTO> boardNotice = repository.selectNotice();
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        model.addAttribute("boardNotice", boardNotice);
        model.addAttribute("boardList",boardList);
        model.addAttribute("boardName", "notice");
        return "board/board_main";
    }

    // 자유게시판 조회
    @GetMapping("/boardFree")
    public String boardFree(Model model){
        List<PostDTO> boardList = repository.selectFree();
        List<PostDTO> boardNotice = repository.selectNotice();
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        model.addAttribute("boardNotice", boardNotice);
        model.addAttribute("boardList", boardList);
        model.addAttribute("boardName", "free");
        return "board/board_main";
    }

    // 리뷰 조회
    @GetMapping("/boardReview")
    public String boardReview(Model model){
        List<PostDTO> boardList = repository.selectReview();
        List<PostDTO> boardNotice = repository.selectNotice();
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        model.addAttribute("boardNotice", boardNotice);
        model.addAttribute("boardList", boardList);
        model.addAttribute("boardName", "review");
        return "board/board_main";
    }

    // Q&A 조회
    @GetMapping("/boardQna")
    public String boardQna(Model model){
        List<PostDTO> boardList = repository.selectQna();
        List<PostDTO> boardNotice = repository.selectNotice();
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        model.addAttribute("boardNotice", boardNotice);
        model.addAttribute("boardList",boardList);
        model.addAttribute("boardName", "qna");
        return "board/board_main";
    }

    //Test
    @GetMapping("/post")
    public String postTest(Model model){

        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);

        return "board/board_write";
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

    // 게시글 한 개 조회
    @GetMapping("/postone/{post_id}")
    public String postOne(@PathVariable("post_id") int post_id,
                          @SessionAttribute(value = "email", required=false) String email,
                          Model model){
//        System.out.println("Received post_id: " + post_id);
        PostDTO post = repository.selectPostOne(post_id);
        repository.incViews(post_id);

        List<CommentDTO> comments = repository.commentList(post_id);

        model.addAttribute("postOne",post);
        model.addAttribute("commentList", comments);

        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);

        model.addAttribute("boardName", post.getSubject());

        if(email != null) {
            int user =  repository.getUser(email);
            model.addAttribute("user",user);

            // 사용자의 게시글 추천 여부 확인
            int user_id = repository.getUser(email);
            int count = repository.checkLikes(post_id, user_id);
            if(count > 0){
                model.addAttribute("isLiked", true);
            } else{
                model.addAttribute("isLiked", false);
            }
        }

        return "board/board";
    }

    // 게시글 삭제
    @GetMapping("/deletePost/{post_id}")
    public String deletePost(@PathVariable("post_id") int post_id){
        repository.deletePost(post_id);
        return "redirect:/boardAll";
    }

    // 게시글 페이지 이동
    @GetMapping("/editPost/{post_id}")
    public String editPost(@PathVariable("post_id")int post_id,
                           Model model){
        PostDTO post = repository.selectPostOne(post_id);
        model.addAttribute("postOne", post);
        List<PostDTO> hotview = repository.hotview();
        model.addAttribute("hotview", hotview);
        return "board/board_edit";
    }
    
    // 게시글 수정
    @PostMapping("/updatePost/{post_id}")
    public String updatePost(@PathVariable("post_id") int post_id,
                             @ModelAttribute PostDTO postDTO,
                             RedirectAttributes redirectAttributes) {

        postDTO.setPost_id(post_id);
        repository.editPost(postDTO);

        return "redirect:/postone/" + post_id; // 수정 후 상세 페이지로 리다이렉트
    }

    // 게시글 추천
    @ResponseBody
    @PostMapping("/likePost")
    public Map<String, Object> likePost(@RequestParam(value="post_id") String post_id,
                                        @SessionAttribute(value = "email") String email) {

        System.out.println(post_id);
        System.out.println(email);

        Map<String, Object> response = new HashMap<>();

        int user_id = repository.getUser(email);
        int count = repository.checkLikes(Integer.parseInt(post_id), user_id);

        if(count == 0){
            repository.incLikes(Integer.parseInt(post_id));
            repository.insertLikes(Integer.parseInt(post_id), user_id);
            response.put("status", "likes_increased");
        } else {
            repository.decLikes(Integer.parseInt(post_id));
            repository.deleteLikes(Integer.parseInt(post_id), user_id);
            response.put("status", "likes_decreased");
        }

        response.put("likes", repository.countLikes(Integer.parseInt(post_id)));
        System.out.println(response);

        return response;
    }

    // 댓글 작성
    @ResponseBody
    @PostMapping("/insertComment")
    public List<CommentDTO> insertComment(@RequestBody CommentDTO comment,
                                          @SessionAttribute(value = "email") String email) {
        int user_id = repository.getUser(email);
        comment.setUser_id(user_id);
        System.out.println(comment);
        repository.insertComment(comment);
        List<CommentDTO> commentList = repository.commentList(comment.getPost_id());

        return commentList;
    }

    @ResponseBody
    @GetMapping("/deleteComment/{comment_id}")
    public List<CommentDTO> deleteComment(@PathVariable("comment_id") int comment_id){
        int post_id = repository.getPost_id(comment_id);
        repository.deleteComment(comment_id);

        List<CommentDTO> commentList = repository.commentList(post_id);
        return commentList;
    }









}
