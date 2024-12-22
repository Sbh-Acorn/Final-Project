package com.example.Caltizm.Service;

import com.example.Caltizm.Repository.DataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    // 앱 시작 시 초기 데이터 수집 및 삽입
    @PostConstruct
    public void initializeData() {
        try {
            dataRepository.collectAndInsertData();
            System.out.println("초기 상품 데이터 삽입 완료");
            Double euroToKrw = exchangeRateService.getEuroToKrwRate();
            Double krwToUsd = exchangeRateService.getKrwToUsdRate();
            System.out.println("환율 데이터 초기화 완료");
            System.out.println("EUR to KRW: " + euroToKrw);
            System.out.println("KRW to USD: " + krwToUsd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 24시간마다 상품 데이터 갱신
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24시간 (밀리초 단위)
    public void updateProductData() {
        try {
            dataRepository.collectAndInsertData();
            System.out.println("상품 데이터 갱신 완료");
        } catch (Exception e) {
            System.err.println("상품 데이터 갱신 중 오류 발생:");
            e.printStackTrace();
        }
    }

    // 24시간마다 환율 데이터 갱신
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, initialDelay = 1000) // 24시간 (밀리초 단위), 초기 딜레이 1초
    public void updateExchangeRateData() {
        try {
            Double euroToKrw = exchangeRateService.getEuroToKrwRate();
            Double krwToUsd = exchangeRateService.getKrwToUsdRate();
            System.out.println("환율 데이터 갱신 완료");
            System.out.println("EUR to KRW: " + euroToKrw);
            System.out.println("KRW to USD: " + krwToUsd);
        } catch (Exception e) {
            System.err.println("환율 데이터 갱신 중 오류 발생:");
            e.printStackTrace();
        }
    }
}
