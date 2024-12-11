package com.example.Caltizm.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

//    @Value("${exchange.rate.api.url}")
//    private String apiUrl;
    private String apiUrl = "https://v6.exchangerate-api.com/v6/4c53144b44d5d039c08eaaba/latest/EUR"; // API URL
    private final RestTemplate restTemplate;

//    public ExchangeRateService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//
//    }
    public ExchangeRateService() {
        this.restTemplate = new RestTemplate(); // RestTemplate 직접 생성
    }
    // 환율 데이터 가져오기
    public Double getEuroToKrwRate() {
        // API 호출
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        // "conversion_rates"에서 KRW 환율 가져오기
        if (response != null && response.containsKey("conversion_rates")) {
            Map<String, Double> conversionRates = (Map<String, Double>) response.get("conversion_rates");
            return conversionRates.get("KRW");
        }

        throw new RuntimeException("Failed to fetch exchange rate data");
    }
    // 단독 실행을 위한 main 메서드
    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService();
        try {
            Double rate = service.getEuroToKrwRate();
            System.out.println("Current EUR to KRW Exchange Rate: " + rate);
        } catch (Exception e) {
            System.err.println("Failed to fetch exchange rate: " + e.getMessage());
        }
    }
}
