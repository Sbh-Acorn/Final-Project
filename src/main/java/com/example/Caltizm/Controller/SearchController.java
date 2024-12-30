package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.SearchProductRepository;
import com.example.Caltizm.Service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private static final int taxBaseAmount = 150;
    private static final int ITEMS_PER_PAGE = 20; // 페이지당 표시할 상품 수
    @Autowired
    private SearchProductRepository searchProductRepository;
    @Autowired
    private CalculatorService calculatorService;
    // 검색된 데이터의 브랜드 목록
    @ModelAttribute("brandNames")
    public List<String> getSearchBrandNames(@RequestParam("query") String query) {
        List<String> productIds = searchProductRepository.findAllProductIdsByName(query);
        return searchProductRepository.findBrandsByProductIds(productIds);
    }

    // 검색된 데이터의 카테고리 목록
    @ModelAttribute("categoryNames")
    public List<String> getSearchCategoryNames(@RequestParam("query") String query) {
        List<String> productIds = searchProductRepository.findAllProductIdsByName(query);
        return searchProductRepository.findCategoriesByProductIds(productIds);
    }

    // 검색된 데이터의 최대 가격
    @ModelAttribute("maxPrice")
    public Map<String, Object> getSearchMaxPrice(@RequestParam("query") String query) {
        List<String> productIds = searchProductRepository.findAllProductIdsByName(query);
        Map<String, Object> priceData = searchProductRepository.findMaxPriceByProductIds(productIds);
        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }
    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, Object>> searchProducts(@RequestParam("query") String query) {
        return searchProductRepository.findProductsByName(query);
    }
    
    @GetMapping("/searchResultProduct")
    public String searchResultProductPage() {
        return "product/product-searchresult";
    }
    @GetMapping("/searchResultProduct/")
    public ResponseEntity<Map<String, Object>> getSearchResults(
            @RequestParam("query") String query,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        System.out.println("Received query: " + query); // 로그 추가
        // 검색된 상품 ID 리스트 가져오기
        List<String> productIds = searchProductRepository.findAllProductIdsByName(query);
        System.out.println("Product IDs: " + productIds);
        // ID 리스트로 상품 세부 데이터 가져오기
        List<ProductDTO> products = searchProductRepository.findProductsByIds(productIds);
        System.out.println("Products: " + products);
        // 페이징 처리
        int start = (page - 1) * ITEMS_PER_PAGE;
        if (start >= products.size()) {
            return ResponseEntity.ok(Map.of("products", List.of())); // 빈 리스트 반환
        }
        int end = Math.min(start + ITEMS_PER_PAGE, products.size());

        // 필요한 범위의 상품 리스트 추출
        List<ProductDTO> paginatedProducts = products.subList(start, end);

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


    @GetMapping("/searchResultProduct/filter")
    public String filter() {
        // 필터 페이지로 이동
        return "product/product-list"; // HTML 뷰 반환
    }

    @GetMapping("/searchResultProduct/filter/")
    public ResponseEntity<Map<String, Object>> getFilteredSearchResults(
            @RequestParam("query") String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tax,
            @RequestParam(required = false) String fta

    ) {
        List<String> productIds = searchProductRepository.findAllProductIdsByName(query);

        // ID 리스트로 상품 세부 데이터 가져오기
        List<ProductDTO> products = searchProductRepository.findProductsByIds(productIds);


        // 브랜드 필터링
        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(product -> brands.contains(product.getBrand()))
                    .collect(Collectors.toList());
        }

        // 카테고리 필터링
        if (categories != null && !categories.isEmpty()) {
            products = products.stream()
                    .filter(product -> categories.contains(product.getCategory1())
                            || categories.contains(product.getCategory2())
                            || categories.contains(product.getCategory3()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null && maxPrice != null) {
            products = products.stream()
                    .filter(product -> product.getCurrent_price() >= calculatorService.convertKrwToEur(minPrice)
                            && product.getCurrent_price() <= calculatorService.convertKrwToEur(maxPrice))
                    .collect(Collectors.toList());
        }

        // 세금 필터링
        if ("TAX".equals(tax)) {
            products = products.stream()
                    .filter(product -> product.getCurrent_price() >= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        } else if ("NOT TAX".equals(tax)) {
            products = products.stream()
                    .filter(product -> product.getCurrent_price() <= calculatorService.convertUsdToEur(taxBaseAmount))
                    .collect(Collectors.toList());
        }

        // FTA 필터링
        if ("FTA".equals(fta)) {
            products = products.stream()
                    .filter(ProductDTO::is_fta)
                    .collect(Collectors.toList());
        } else if ("NOT FTA".equals(fta)) {
            products = products.stream()
                    .filter(p -> !p.is_fta())
                    .collect(Collectors.toList());
        }
        // 페이징 처리
        int start = (page - 1) * ITEMS_PER_PAGE;
        if (start >= products.size()) {
            return ResponseEntity.ok(Map.of("products", List.of())); // 빈 리스트 반환
        }
        int end = Math.min(start + ITEMS_PER_PAGE, products.size());

        // 필요한 범위의 상품 리스트 추출
        List<ProductDTO> paginatedProducts = products.subList(start, end);

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