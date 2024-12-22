package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    // API URL
    private static final String EXCHANGE_RATE_API = "https://v6.exchangerate-api.com/v6/5ed0b678a435c9472a396a7a/latest/USD"; // USD 기준 환율 API

    // 환율 데이터를 캐싱할 변수
    private Map<String, Double> conversionRates;

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate(); // RestTemplate 생성
    }

    // 환율 데이터 초기화 (USD 기준으로 가져오기)
    private void initializeConversionRates() {
        if (conversionRates == null) {
            Map<String, Object> response = restTemplate.getForObject(EXCHANGE_RATE_API, Map.class);

            if (response != null && response.containsKey("conversion_rates")) {
                conversionRates = (Map<String, Double>) response.get("conversion_rates");
            } else {
                throw new RuntimeException("Failed to fetch exchange rate data");
            }
        }
    }

    // 유로 → 원화 환율 가져오기
    public Double getEuroToKrwRate() {
        initializeConversionRates(); // 환율 데이터 초기화
        Double usdToKrw = conversionRates.get("KRW");
        Double eurToUsd = conversionRates.get("EUR");

        if (usdToKrw == null || eurToUsd == null) {
            throw new RuntimeException("Failed to fetch EUR to KRW exchange rate data");
        }

        // 유로 → 달러 → 원화 환율 계산
        return eurToUsd * usdToKrw;
    }

    // 원화 → 달러 환율 가져오기
    public Double getKrwToUsdRate() {
        initializeConversionRates(); // 환율 데이터 초기화
        Double usdToKrw = conversionRates.get("KRW");

        if (usdToKrw == null || usdToKrw == 0) {
            throw new RuntimeException("Failed to fetch KRW to USD exchange rate data");
        }

        // 달러 → 원화 역계산 (1 / USD to KRW)
        return 1 / usdToKrw;
    }

    // 단독 실행을 위한 main 메서드
    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService();
        try {
            Double euroToKrw = service.getEuroToKrwRate();
            Double krwToUsd = service.getKrwToUsdRate();

            System.out.println("EUR to KRW Rate: " + euroToKrw);
            System.out.println("KRW to USD Rate: " + krwToUsd);
        } catch (Exception e) {
            System.err.println("Failed to fetch exchange rate: " + e.getMessage());
        }
    }
}
