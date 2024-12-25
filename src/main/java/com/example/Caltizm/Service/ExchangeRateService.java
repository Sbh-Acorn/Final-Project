package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateService {


    private final RestTemplate restTemplate;

    // API URL
    private static final String EXCHANGE_RATE_API = "https://v6.exchangerate-api.com/v6/436f5972e58a155b9464f0e2/latest/USD";

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

    // 유로 → 원화와 달러 → 유로 환율 가져오기
    public Map<String, Double> getExchangeRates() {
        initializeConversionRates(); // 환율 데이터 초기화

        Double usdToKrw = conversionRates.get("KRW");
        Double eurToUsd = conversionRates.get("EUR");

        if (usdToKrw == null || eurToUsd == null) {
            throw new RuntimeException("Failed to fetch exchange rate data");
        }

        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("EUR_TO_KRW", eurToUsd * usdToKrw); // 유로 → 원화
        exchangeRates.put("USD_TO_EUR", 1 / eurToUsd);       // 달러 → 유로

        return exchangeRates;
    }

    // 단독 실행을 위한 main 메서드
    public static void main(String[] args) {

//        ExchangeRateService service = new ExchangeRateService();
//        try {
//            Map<String, Double> rates = service.getExchangeRates();
//
//            System.out.println("EUR to KRW Rate: " + rates.get("EUR_TO_KRW"));
//            System.out.println("USD to EUR Rate: " + rates.get("USD_TO_EUR"));
//        } catch (Exception e) {
//            System.err.println("Failed to fetch exchange rate: " + e.getMessage());
//        }

    }
}