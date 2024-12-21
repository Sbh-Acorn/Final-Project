package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    // API URLs
    private static final String EUR_TO_USD_API = "https://v6.exchangerate-api.com/v6/4c53144b44d5d039c08eaaba/latest/EUR"; // EUR to USD API
    private static final String USD_TO_KRW_API = "https://v6.exchangerate-api.com/v6/4c53144b44d5d039c08eaaba/latest/USD"; // USD to KRW API

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate(); // RestTemplate 생성
    }

    // 유로 → 달러 환율 가져오기
    public Double getEuroToUsdRate() {
        Map<String, Object> response = restTemplate.getForObject(EUR_TO_USD_API, Map.class);

        if (response != null && response.containsKey("conversion_rates")) {
            Map<String, Double> conversionRates = (Map<String, Double>) response.get("conversion_rates");
            return conversionRates.get("USD");
        }

        throw new RuntimeException("Failed to fetch EUR to USD exchange rate data");
    }

    // 달러 → 원화 환율 가져오기
    public Double getUsdToKrwRate() {
        Map<String, Object> response = restTemplate.getForObject(USD_TO_KRW_API, Map.class);

        if (response != null && response.containsKey("conversion_rates")) {
            Map<String, Double> conversionRates = (Map<String, Double>) response.get("conversion_rates");
            return conversionRates.get("KRW");
        }

        throw new RuntimeException("Failed to fetch USD to KRW exchange rate data");
    }

    // 유로 → 원화 계산
    public Double convertEuroToKrw(Double euroAmount) {
        Double euroToUsdRate = getEuroToUsdRate();
        Double usdToKrwRate = getUsdToKrwRate();

        // 유로 → 달러 → 원화 변환
        return euroAmount * euroToUsdRate * usdToKrwRate;
    }

    // 단독 실행을 위한 main 메서드
    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService();
        try {
            Double euroToUsd = service.getEuroToUsdRate();
            Double usdToKrw = service.getUsdToKrwRate();
            Double euroToKrw = service.convertEuroToKrw(100.0); // 예: 100유로 변환

            System.out.println("EUR to USD Rate: " + euroToUsd);
            System.out.println("USD to KRW Rate: " + usdToKrw);
            System.out.println("100 EUR to KRW: " + euroToKrw);
        } catch (Exception e) {
            System.err.println("Failed to fetch exchange rate: " + e.getMessage());
        }
    }
}
