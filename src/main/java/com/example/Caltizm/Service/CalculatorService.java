package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CalculatorService {


    ExchangeRateService service = new ExchangeRateService();

    Map<String, Double> rates = service.getExchangeRates();
    Double eurToKrwRate = rates.get("EUR_TO_KRW");
    Double usdToEurRate = rates.get("USD_TO_EUR");

    public double convertEurToKrw(double price) {
        double taxFreePrice = price / 1.19;

        BigDecimal roundedTaxFreePrice = new BigDecimal(taxFreePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal priceInWon = roundedTaxFreePrice.multiply(new BigDecimal(eurToKrwRate));

        BigDecimal roundedPriceInWon = priceInWon.setScale(0, RoundingMode.HALF_UP);

//        System.out.println("현재 환율: " + EXCHANGE);
//        System.out.println("세금 제외 가격 (소수점 두 자리): " + roundedTaxFreePrice + "€");
//        System.out.println("환율 적용 원화 가격 (정수 반올림): " + roundedPriceInWon + "원");

        return roundedPriceInWon.doubleValue();
    }

    public double convertKrwToEur(double priceInWon) {
        // 세금 포함 가격 계산
        double taxIncludedPrice = priceInWon * 1.19;

        // BigDecimal로 소수점 두 자리 반올림
        BigDecimal roundedTaxIncludedPrice = new BigDecimal(taxIncludedPrice).setScale(2, RoundingMode.HALF_UP);

        // 환율로 유로 금액 계산
        BigDecimal priceInEuro = roundedTaxIncludedPrice.divide(new BigDecimal(eurToKrwRate), RoundingMode.HALF_UP);

        // 결과 소수점 두 자리 반올림
        BigDecimal roundedPriceInEuro = priceInEuro.setScale(2, RoundingMode.HALF_UP);

        //        System.out.println("현재 환율: " + EXCHANGE);
        //        System.out.println("원화 가격: " + priceInWon + "원");
        //        System.out.println("세금 포함 유로 가격 (소수점 두 자리): " + roundedPriceInEuro + "€");

        return roundedPriceInEuro.doubleValue();
    }

    public double convertEurToKrwWithTax(double price) {
        BigDecimal priceInWon = new BigDecimal(price).multiply(new BigDecimal(eurToKrwRate));
        BigDecimal roundedPriceInWon = priceInWon.setScale(0, RoundingMode.HALF_UP);
        return roundedPriceInWon.doubleValue();
    }

    // USD -> EUR 환전 후, EUR -> KRW 환전하는 메서드
    public double convertUsdToKrw(int usdAmount) {
        // USD를 EUR로 변환
        double eurAmount = convertUsdToEur(usdAmount);

        // EUR를 KRW로 변환
        return convertEurToKrwWithTax(eurAmount);
    }

    // USD -> EUR 150달러 환전금액
    public double convertUsdToEur(int taxBaseAmount) {
        return taxBaseAmount * usdToEurRate;
    }

    public static void main(String[] args) {
//        CalculatorService service = new CalculatorService();
//
//
//        System.out.println("KRW -> EUR 10000원 환전금액 : " + service.convertKrwToEur(10000));
//        System.out.println("USD -> KRW 150달러 환전금액 : " + service.convertUsdToKrw(150));
//        System.out.println("USD -> EUR 150달러 환전금액 : " + service.convertUsdToEur(150));
//        System.out.println();
    }
}
