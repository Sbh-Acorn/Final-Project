package com.example.Caltizm.Service;

import com.example.Caltizm.Repository.DataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ScheduledTasks {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private GetDataService getDataService;

//    // 앱 시작 시 초기 데이터 수집 및 삽입
    @PostConstruct
    public void initializeData() {
        try {

//            사진 데이터 초기화
            List<String> bannerImages = getDataService.collectBannerImage();
            String bannerDirectory = "src/main/resources/static/bannerImages";
            getDataService.saveImage(bannerImages, bannerDirectory);
            // 상품 데이터 초기화
            dataRepository.collectAndInsertData();
//            System.out.println("초기 상품 데이터 삽입 완료");
//
//            // 환율 데이터 초기화
//            Map<String, Double> exchangeRates = exchangeRateService.getExchangeRates();
//
//
//            System.out.println("환율 데이터 초기화 완료");
//            System.out.println("EUR to KRW: " + exchangeRates.get("EUR_TO_KRW"));
//            System.out.println("USD to EUR: " + exchangeRates.get("USD_TO_EUR"));

        } catch (Exception e) {
            System.err.println("초기 데이터 삽입 중 오류 발생:");
            e.printStackTrace();
        }
    }

    // 매일 (03:00)에 상품 데이터 갱신
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul") // 매일 자정 (한국 시간 기준)
    public void updateProductData() {
        try {
                dataRepository.collectAndInsertData();
            System.out.println("상품 데이터 갱신 완료");
        } catch (Exception e) {
            System.err.println("상품 데이터 갱신 중 오류 발생:");
            e.printStackTrace();
        }
    }

    // 매일 (03:00)에 환율 데이터 갱신
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul") // 매일 자정 (한국 시간 기준)
    public void updateExchangeRateData() {
        try {
            Map<String, Double> exchangeRates = exchangeRateService.getExchangeRates();
            System.out.println("환율 데이터 갱신 완료");
            System.out.println("EUR to KRW: " + exchangeRates.get("EUR_TO_KRW"));
            System.out.println("KRW to USD: " + exchangeRates.get("KRW_TO_USD"));
        } catch (Exception e) {
            System.err.println("환율 데이터 갱신 중 오류 발생:");
            e.printStackTrace();
        }
    }

    // 매일 (03:00)에 환율 데이터 갱신
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul") // 매일 자정 (한국 시간 기준)
    public void updateBannerImageData() {
        try {
            List<String> bannerImages = getDataService.collectBannerImage();
            String bannerDirectory = "src/main/resources/static/bannerImages";
            getDataService.saveImage(bannerImages, bannerDirectory);
            System.out.println("BannerImage 로딩 완료");
        } catch (Exception e) {
            System.err.println("BannerImage 로딩 중 오류 발생:");
            e.printStackTrace();
        }
    }
}
