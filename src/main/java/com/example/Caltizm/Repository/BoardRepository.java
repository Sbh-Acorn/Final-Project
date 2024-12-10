package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.PostDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
