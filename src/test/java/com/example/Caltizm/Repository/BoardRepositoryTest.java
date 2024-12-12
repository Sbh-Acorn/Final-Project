package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.PostDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class BoardRepositoryTest {

    @Autowired
    BoardRepository repository;

    @Test

    public  void test(){
       List<PostDTO> list  = repository.selectAll();
       int size  = list.size() ;

       assertTrue( size  ==0);
    }

}