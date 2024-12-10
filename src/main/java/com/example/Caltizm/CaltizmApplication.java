package com.example.Caltizm;

import com.example.Caltizm.Repository.BrandRepository;
import com.example.Caltizm.Repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class CaltizmApplication {

	@Autowired
	private BrandRepository repository;

	@Autowired
	private ProductRepository repository2;

	public static void main(String[] args) {
		SpringApplication.run(CaltizmApplication.class, args);
	}

	// 애플리케이션 시작 후 DB에 데이터 삽입
	@PostConstruct
	public void init() {
		try {
			repository.collectAndInsertBrandData();
			repository2.collectAndInsertCategoryData();
		} catch (Exception e) {
			e.printStackTrace(); // 예외 처리
		}
	}
}
