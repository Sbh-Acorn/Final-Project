package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Repository.SearchProductRepository;
import com.example.Caltizm.Service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FTAController {

    @Autowired
    DataRepository repository;

    @Autowired
    CalculatorService calculatorService;

    private static final int taxBaseAmount = 150;

    @Autowired
    SearchProductRepository searchProductRepository;

    // 모든 메서드에서 사용할 제품 리스트를 미리 로드

    @ModelAttribute("products")
    public List<ProductDTO> getAllFTAProducts() {
        List<ProductDTO> ftaProducts = repository.getFTAProduct();
        ftaProducts.sort(
                Comparator.comparing(ProductDTO::getBrand)
                        .thenComparing(ProductDTO::getName)
        );
        for (ProductDTO product : ftaProducts) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }
        return ftaProducts;
    }

    @ModelAttribute("brandNames")
    public List<String> getAllFTABrandName() {
        return repository.getAllFTABrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllFTACategoryName() {
        return repository.getAllFTACategoryName();
    }

    @ModelAttribute("MaxPrice")
    public Map<String, Object> getFTAMaxPrice() {
        Map<String, Object> priceData = repository.getFTAMaxPrice();

        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }


    @GetMapping("/fta")
    public String ftaProduct(Model model) {
        return "product/fta-product-list";
    }


    @GetMapping("/fta/filter")
    public String filter(
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tax,
            Model model
    ) {
        // 필터링 로직 처리
        List<ProductDTO> ftaProducts = repository.getFTAProduct();

        // 가격 필터링
        if (minPrice != null && maxPrice != null) {
            ftaProducts = ftaProducts.stream()
                    .filter(p -> p.getCurrent_price() >= calculatorService.convertKrwToEur(minPrice)
                            && p.getCurrent_price() <= calculatorService.convertKrwToEur(maxPrice))
                    .collect(Collectors.toList());
        }

        // 브랜드 필터링
        if (brands != null && !brands.isEmpty()) {
            ftaProducts = ftaProducts.stream()
                    .filter(p -> brands.contains(p.getBrand()))
                    .collect(Collectors.toList());
        }

        // 카테고리 필터링
        if (categories != null && !categories.isEmpty()) {
            ftaProducts = ftaProducts.stream()
                    .filter(p -> categories.contains(p.getCategory1()) ||
                            categories.contains(p.getCategory2()) ||
                            categories.contains(p.getCategory3()))
                    .collect(Collectors.toList());
        }

        // 세금 필터링
        if ("TAX".equals(tax)) {
            ftaProducts = ftaProducts.stream()
                    .filter(p -> p.getCurrent_price() >= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        } else if ("NOT TAX".equals(tax)) {
            ftaProducts = ftaProducts.stream()
                    .filter(p -> p.getCurrent_price() <= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        }


        ftaProducts.sort(
                Comparator.comparing(ProductDTO::getBrand)
                        .thenComparing(ProductDTO::getName)
        );

        for (ProductDTO product : ftaProducts) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }

        model.addAttribute("products", ftaProducts);
        return "product/fta-product-list";
    }



}
