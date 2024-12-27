package com.example.Caltizm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄러 활성화
public class CaltizmApplication {
	public static void main(String[] args) {
		SpringApplication.run(CaltizmApplication.class, args);
	}



//	 애플리케이션 시작 후 DB에 데이터 삽입
//	첫 실행 시 주석 해제하기
//	DB에 데이터 저장 후에는 주석 처리하면 실행 안 됨
//	@PostConstruct
//	public void init() {
//		try {
//			repository.collectAndInsertData();
//		} catch (Exception e) {
//			e.printStackTrace(); // 예외 처리
//		}
//	}

}
