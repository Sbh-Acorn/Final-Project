package com.example.Caltizm.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {


    @GetMapping("/health")
    public ResponseEntity<String> helath(){

        return   ResponseEntity.ok( " running");
    }
}