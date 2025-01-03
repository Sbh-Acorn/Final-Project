package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FTAProductController {

    private static final int taxBaseAmount = 150;
    private static final int ITEMS_PER_PAGE = 20; // 페이지당 표시할 상품 수
    @Autowired
    DataRepository repository;
    @Autowired
    CalculatorService calculatorService;


    @ModelAttribute("brandNames")
    public List<String> getAllFTABrandName() {
        return repository.getAllFTABrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllFTACategoryName() {
        return repository.getAllFTACategoryName();
    }

    @ModelAttribute("maxPrice")
    public Map<String, Object> getFTAMaxPrice() {
        Map<String, Object> priceData = repository.getFTAMaxPrice();

        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }


    @GetMapping("/fta-product")
    public String product() {
        return "product/fta-product-list";
    }


    @GetMapping("/fta-product/")
    public ResponseEntity<Map<String, Object>> getProductList(@RequestParam(name = "page", defaultValue = "1") int page) {
        // 전체 상품 리스트를 가져옴
        List<ProductDTO> products = repository.getFTAProduct();

//        System.out.println("Products: " + products);

        // 페이징 처리
        int start = (page - 1) * ITEMS_PER_PAGE;
        if (start >= products.size()) {
            return ResponseEntity.ok(Map.of("products", List.of()));  // 빈 리스트 반환
        }

        int end = Math.min(start + ITEMS_PER_PAGE, products.size());

        // 필요한 범위의 상품 리스트 추출
        List<ProductDTO> paginatedProducts = products.subList(start, end);
//        System.out.println("Paginated Products: " + paginatedProducts);

        // 가격 변환
        for (ProductDTO product : paginatedProducts) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }

        // JSON 응답 생성
        Map<String, Object> response = Map.of("products", paginatedProducts);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/fta-product/filter")
    public String filter() {
        // 필터 페이지로 이동
        return "product/fta-product-list"; // HTML 뷰 반환
    }


    @GetMapping("/fta-product/filter/")
    public ResponseEntity<Map<String, Object>> getFilterList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "brands",required = false) List<String> brands,
            @RequestParam(name = "categories",required = false) List<String> categories,
            @RequestParam(name = "minPrice",required = false) Double minPrice,
            @RequestParam(name = "maxPrice",required = false) Double maxPrice,
            @RequestParam(name = "tax",required = false) String tax
    ) {
        // 전체 상품 가져오기
        List<ProductDTO> allProducts = repository.getFTAProduct();


        // 가격 필터링
        if (minPrice != null && maxPrice != null) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCurrent_price() >= calculatorService.convertKrwToEur(minPrice)
                            && p.getCurrent_price() <= calculatorService.convertKrwToEur(maxPrice))
                    .collect(Collectors.toList());
        }

        // 브랜드 필터링
        if (brands != null && !brands.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> brands.contains(p.getBrand()))
                    .collect(Collectors.toList());
        }

        // 카테고리 필터링
        if (categories != null && !categories.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> categories.contains(p.getCategory1()) ||
                            categories.contains(p.getCategory2()) ||
                            categories.contains(p.getCategory3()))
                    .collect(Collectors.toList());
        }

        // 세금 필터링
        if ("TAX".equals(tax)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCurrent_price() >= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        } else if ("NOT TAX".equals(tax)) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCurrent_price() <= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        }


        // 페이징 처리
        int start = (page - 1) * ITEMS_PER_PAGE;
        if (start >= allProducts.size()) {
            return ResponseEntity.ok(Map.of("products", List.of()));  // 빈 리스트 반환
        }
        int end = Math.min(start + ITEMS_PER_PAGE, allProducts.size());

        // 필요한 범위의 상품 리스트 추출
        List<ProductDTO> paginatedProducts = allProducts.subList(start, end);

        // 가격 변환
        for (ProductDTO product : paginatedProducts) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }

        // JSON 응답 생성
        Map<String, Object> response = Map.of("products", paginatedProducts);
        return ResponseEntity.ok(response);
    }


}
