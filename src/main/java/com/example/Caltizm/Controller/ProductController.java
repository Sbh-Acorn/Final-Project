package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Repository.SearchProductRepository;
import com.example.Caltizm.Service.CalculatorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    private static final int taxBaseAmount = 150;
    private static final int ITEMS_PER_PAGE = 20; // 페이지당 표시할 상품 수
    @Autowired
    DataRepository repository;
    @Autowired
    CalculatorService calculatorService;
    @Autowired
    SearchProductRepository searchProductRepository;



    @ModelAttribute("brandNames")
    public List<String> getAllBrandName() {
        return repository.getAllBrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllCategoryName() {
        return repository.getAllCategoryName();
    }

    @ModelAttribute("maxPrice")
    public Map<String, Object> getMaxPrice() {
        Map<String, Object> priceData = repository.getMaxPrice();

        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }


    @GetMapping("/product")
    public String product() {
        return "product/product-list";
    }


    @GetMapping("/product/")
    public ResponseEntity<Map<String, Object>> getProductList(@RequestParam(name = "page", defaultValue = "1") int page) {
        // 전체 상품 리스트를 가져옴
        List<ProductDTO> products = repository.getProduct();


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

    @GetMapping("/product/{product_id}")
    public String productDetail(
            @PathVariable(name = "product_id") String product_id,
            Model model,
            HttpSession session
    ) {
        // 우선 모델에서 products를 가져와 조회
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");
        ProductDTO product = null;

        if (products != null) {
            // 모델에서 product_id로 필터링
            product = products.stream()
                    .filter(p -> p.getProduct_id().equals(product_id))
                    .findFirst()
                    .orElse(null);
        }

        // 모델에 없는 경우 DB에서 조회
        if (product == null) {
            product = searchProductRepository.findProductById(product_id);
        }

        // 상품이 여전히 null인 경우 404 페이지로 이동
        if (product == null) {
            return "error/404";
        }

        // 모델에 상품 데이터 추가
        model.addAttribute("product", product);

        // 할인율 계산 후 모델에 추가
        if (product.getOriginal_price() != null) {
            int discountRate = (int) Math.round((1 - (product.getCurrent_price() / product.getOriginal_price())) * 100);
            model.addAttribute("discountRate", discountRate);
        }

        // 세션에서 cartList 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        model.addAttribute("cartList", cartList);

        return "product/product-detail"; // 상세 페이지로 이동
    }

    @GetMapping("/product/filter")
    public String filter() {
        // 필터 페이지로 이동
        return "product/product-list"; // HTML 뷰 반환
    }


    @GetMapping("/product/filter/")
    public ResponseEntity<Map<String, Object>> getFilterList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tax,
            @RequestParam(required = false) String fta
    ) {
        // 전체 상품 가져오기
        List<ProductDTO> allProducts = repository.getProduct();


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
