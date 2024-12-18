package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {


    ExchangeRateService service = new ExchangeRateService();

    Double EXCHANGE = service.getEuroToKrwRate();

    public void calculator(double price) {
        // 세금 제외 가격 계산
        double taxFreePrice = price / 1.19;

        // 세금 제외 가격을 BigDecimal로 반올림
        BigDecimal roundedTaxFreePrice = new BigDecimal(taxFreePrice).setScale(2, RoundingMode.HALF_UP);

        // 환율을 적용한 원화 가격 계산
        BigDecimal priceInWon = roundedTaxFreePrice.multiply(new BigDecimal(EXCHANGE));

        // 원화 가격을 정수로 반올림
        BigDecimal roundedPriceInWon = priceInWon.setScale(0, RoundingMode.HALF_UP);

        // 출력
        System.out.println("현재 환율: " + EXCHANGE);
        System.out.println("세금 제외 가격 (소수점 두 자리): " + roundedTaxFreePrice + "€");
        System.out.println("환율 적용 원화 가격 (정수 반올림): " + roundedPriceInWon + "원");
    }


    public static void main(String[] args) {
        CalculatorService service = new CalculatorService();
        service.calculator(990);
    }
}
