package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.CommentDTO;
import com.example.Caltizm.DTO.PostDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BoardRepository {

    @Autowired
    SqlSession session;


    // 전체 게시글 조회
    public List<PostDTO> selectAll() {
        return session.selectList("board.all");
    }

    // 공지사항 게시글 조회
    public List<PostDTO> selectNotice() {
        return session.selectList("board.notice");
    }

    // 자유 게시판 게시글 조회
    public List<PostDTO> selectFree() {
        return session.selectList("board.free");
    }

    // 리뷰 게시글 조회
    public List<PostDTO> selectReview() {
        return session.selectList("board.review");
    }

    // Q&A 게시글 조회
    public List<PostDTO> selectQna() {
        return session.selectList("board.qna");
    }

    public List<PostDTO> searchPosts(String query) {
        return session.selectList("board.searchPosts", query);
    }

    // 게시글 작성
    public int insertPost(PostDTO postDTO){
        return session.insert("board.insert-post",postDTO);}

    // 유저 아이디 가져오기
    public int getUser(String email){
        return session.selectOne("board.user_id",email);}

    // 게시글 한개 조회
    public PostDTO selectPostOne(int post_id){return session.selectOne("board.postOne",post_id);}

    // 게시글 삭제
    public int deletePost(int post_id){
        return session.update("board.delete-post",post_id);
    }

    // 게시글 수정
    public int editPost(PostDTO postDTO){
        return session.update("board.edit-post",postDTO);
    }

    //조회수 증가
    public int incViews(int post_id) {
        return session.update("board.incViews", post_id);
    }

    // 추천
    public int incLikes(int post_id){
        return session.update("board.incLikes", post_id);
    }

    public int decLikes(int post_id){
        return session.update("board.decLikes", post_id);
    }
    
    // 추천 테이블 추가
    public int insertLikes(int post_id, int user_id){
        Map<String, Integer> map = new HashMap<>();
        map.put("post_id",post_id);
        map.put("user_id",user_id);
        return session.insert("board.insert-likes",map);
    }

    // 추천 테이블 삭제
    public int deleteLikes(int post_id, int user_id){
        Map<String, Integer> map = new HashMap<>();
        map.put("post_id",post_id);
        map.put("user_id",user_id);
        return session.delete("board.delete-likes",map);
    }

    // 추천 테이블 확인
    public int checkLikes(int post_id, int user_id){
        Map<String, Integer> map = new HashMap<>();
        map.put("post_id",post_id);
        map.put("user_id",user_id);
        return session.selectOne("board.check-likes",map);
    }

    // 좋아요 수
    public int countLikes(int post_id) {
        return session.selectOne("board.count-likes", post_id);
    }


    // 댓글 작성
    public int insertComment(CommentDTO commentDTO){
        return  session.insert("board.insert-comment", commentDTO);
    }

    // 댓글 목록
    public List<CommentDTO> commentList(int post_id){
        return session.selectList("board.list-comment", post_id);
    }

    // 댓글 삭제
    public int deleteComment(int comment_id){
        return session.update("board.delete-comment",comment_id);
    }

    // 댓글의 포스트ID 가져오기
    public int getPost_id(int comment_id) {
        return session.selectOne("board.getPost_id",comment_id);
    }

    public List<PostDTO> hotview(){
        return session.selectList("board.hotview");
    }

    public List<PostDTO> selectAllByEmail(String email){
        return session.selectList("board.allByEmail", email);
    }

}
