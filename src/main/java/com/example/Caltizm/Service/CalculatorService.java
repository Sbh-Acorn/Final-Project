package com.example.Caltizm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {

    @Autowired
    private ExchangeRateService exchangeRateService;

    public double calculator(double price) {
        // 세금 제외 가격 계산
        double taxFreePrice = price / 1.19;

        // 세금 제외 가격을 BigDecimal로 반올림
        BigDecimal roundedTaxFreePrice = new BigDecimal(taxFreePrice).setScale(2, RoundingMode.HALF_UP);

        // 현재 유로 -> 원화 환율 가져오기
        Double exchangeRate = exchangeRateService.getExchangeRates().get("EUR_TO_KRW");

        if (exchangeRate == null) {
            throw new RuntimeException("환율 데이터를 가져올 수 없습니다.");
        }

        // 환율을 적용한 원화 가격 계산
        BigDecimal priceInWon = roundedTaxFreePrice.multiply(new BigDecimal(exchangeRate));

        // 원화 가격을 정수로 반올림
        BigDecimal roundedPriceInWon = priceInWon.setScale(0, RoundingMode.HALF_UP);

        // 출력
        System.out.println("현재 환율: " + exchangeRate);
        System.out.println("환율 적용 원화 가격 (정수 반올림): " + roundedPriceInWon + "원");

        return roundedPriceInWon.doubleValue();
    }
}
