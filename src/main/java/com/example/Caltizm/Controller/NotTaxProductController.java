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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class NotTaxProductController {

    private static final int taxBaseAmount = 150;
    private static final int ITEMS_PER_PAGE = 20; // 페이지당 표시할 상품 수
    @Autowired
    DataRepository repository;
    @Autowired
    CalculatorService calculatorService;


    @ModelAttribute("brandNames")
    public List<String> getAllTaxBrandName() {
        List<ProductDTO> productDTOs = repository.getProduct();
        Set<String> brands = new TreeSet<>();
        // calculatorService에서 세금 기반 금액을 환산한 값 구하기
        double taxBaseAmountInEur = calculatorService.convertUsdToEur(taxBaseAmount);

        // productDTOs를 순회하면서 조건에 맞는 브랜드 추가
        for (ProductDTO productDTO : productDTOs) {
            // taxBaseAmount보다 작은 제품의 브랜드를 brands 리스트에 추가
            if (productDTO.getCurrent_price() <= taxBaseAmountInEur) {
                brands.add(productDTO.getBrand());
            }
        }

        // 브랜드 리스트 반환
        return new ArrayList(brands);
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllTaxCategoryName() {
        List<ProductDTO> productDTOs = repository.getProduct();
        Set<String> categories = new TreeSet<>();
        // calculatorService에서 세금 기반 금액을 환산한 값 구하기
        double taxBaseAmountInEur = calculatorService.convertUsdToEur(taxBaseAmount);

        for (ProductDTO productDTO : productDTOs) {
            if (productDTO.getCurrent_price() <= taxBaseAmountInEur) {
                categories.add(productDTO.getCategory1());
                categories.add(productDTO.getCategory2());
                if(productDTO.getCategory3() != null){
                    categories.add(productDTO.getCategory3());
                }
            }
        }

        return new ArrayList<>(categories);
    }

    @ModelAttribute("maxPrice")
    public Map<String, Object> getTaxMaxPrice() {
        Map<String, Object> priceData = repository.getMaxPrice();

        double taxBaseAmountInKrw = calculatorService.convertUsdToKrw(taxBaseAmount);

        priceData.put("max_price_in_won", taxBaseAmountInKrw);

        return priceData;
    }


    @GetMapping("/not-tax-product")
    public String product() {
        return "product/not-tax-product-list";
    }


    @GetMapping("/not-tax-product/")
    public ResponseEntity<Map<String, Object>> getProductList(@RequestParam(name = "page", defaultValue = "1") int page) {
        // 전체 상품 리스트를 가져옴
        List<ProductDTO> products = repository.getProduct();

        //세금 이하 필터링
        products = products.stream()
                .filter(p -> p.getCurrent_price() <= calculatorService.convertUsdToEur(taxBaseAmount))
                .collect(Collectors.toList());


        // 페이징 처리
        int start = (page - 1) * ITEMS_PER_PAGE;
        if (start >= products.size()) {
            return ResponseEntity.ok(Map.of("products", List.of()));  // 빈 리스트 반환
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


    @GetMapping("/not-tax-product/filter")
    public String filter() {
        // 필터 페이지로 이동
        return "product/not-tax-product-list"; // HTML 뷰 반환
    }


    @GetMapping("/not-tax-product/filter/")
    public ResponseEntity<Map<String, Object>> getFilterList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String fta
    ) {
        // 전체 상품 가져오기
        List<ProductDTO> allProducts = repository.getProduct();

        //세금 이하 필터링
        allProducts = allProducts.stream()
                .filter(p -> p.getCurrent_price() <= calculatorService.convertUsdToEur(taxBaseAmount))
                .collect(Collectors.toList());


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

        // FTA 필터링
        if ("FTA".equals(fta)) {
            allProducts = allProducts.stream()
                    .filter(ProductDTO::is_fta)
                    .collect(Collectors.toList());
        } else if ("NOT FTA".equals(fta)) {
            allProducts = allProducts.stream()
                    .filter(p -> !p.is_fta())
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
