package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {


    ExchangeRateService service = new ExchangeRateService();

    Double EXCHANGE = service.getEuroToKrwRate();

    public double calculator(double price) {
        double taxFreePrice = price / 1.19;

        BigDecimal roundedTaxFreePrice = new BigDecimal(taxFreePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal priceInWon = roundedTaxFreePrice.multiply(new BigDecimal(EXCHANGE));

        BigDecimal roundedPriceInWon = priceInWon.setScale(0, RoundingMode.HALF_UP);

//        System.out.println("현재 환율: " + EXCHANGE);
//        System.out.println("세금 제외 가격 (소수점 두 자리): " + roundedTaxFreePrice + "€");
//        System.out.println("환율 적용 원화 가격 (정수 반올림): " + roundedPriceInWon + "원");

        return roundedPriceInWon.doubleValue();
    }


    public static void main(String[] args) {
//        CalculatorService service = new CalculatorService();
//        service.calculator(990);
    }
}
