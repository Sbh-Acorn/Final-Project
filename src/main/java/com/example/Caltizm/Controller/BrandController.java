package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.BrandDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Repository.SearchProductRepository;
import com.example.Caltizm.Service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BrandController {

    private static final int taxBaseAmount = 150;
    private static final int ITEMS_PER_PAGE = 20; // 페이지당 표시할 상품 수
    @Autowired
    DataRepository repository;
    @Autowired
    CalculatorService calculatorService;
    @Autowired
    SearchProductRepository searchProductRepository;

    @ModelAttribute("categoryNames")
    public List<String> getAllCategoryName() {
        return repository.getAllCategoryName();
    }


    @GetMapping("/brand")
    public String brand(Model model) {
        List<String> brands = repository.getAllBrandName();

        // 그룹화 작업
        Map<Character, List<String>> groupedBrands = brands.stream()
                .collect(Collectors.groupingBy(brand -> {
                    char firstChar = Character.toUpperCase(brand.charAt(0));
                    return Character.isDigit(firstChar) ? '0' : firstChar;
                }));

        // LinkedHashMap으로 순서를 보장
        Map<Character, List<String>> orderedBrands = new LinkedHashMap<>();
        if (groupedBrands.containsKey('0')) {
            orderedBrands.put('0', groupedBrands.remove('0')); // 0-9를 가장 먼저 추가
        }
        orderedBrands.putAll(groupedBrands); // 나머지 알파벳 그룹 추가

        model.addAttribute("BrandNames", orderedBrands);


        return "product/brand_list";
    }


    @GetMapping("/brand/{brandName:.+}")
    public String selectBrand(@PathVariable(name = "brandName") String brandName, Model model) {
        System.out.println(brandName);
        Map<String, Object> brandAndProduct = repository.getBrandAndProduct(brandName);
        BrandDTO brand = (BrandDTO) brandAndProduct.get("brand");
        model.addAttribute("brand", brand);  // 해당 브랜드 상세 정보 추가

        Map<String, Object> priceData = repository.getBrandMaxPrice(brandName);
        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");
        Double maxPriceAsDouble = maxPrice.doubleValue();
        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);
        priceData.put("max_price_in_won", maxPriceInWon);
        model.addAttribute("maxPrice", priceData);
        return "product/brand";
    }


    @GetMapping("/brand/{brandName:.+}/")
    public ResponseEntity<Map<String, Object>> getProductList(
            @PathVariable(name = "brandName") String brandName,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        Map<String, Object> brandAndProduct = repository.getBrandAndProduct(brandName);
        List<ProductDTO> products = (List<ProductDTO>) brandAndProduct.get("products");


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

    @GetMapping("/brand/{brandName}/filter")
    public String filter(@PathVariable(name = "brandName") String brandName, Model model) {
        Map<String, Object> brandAndProduct = repository.getBrandAndProduct(brandName);
        BrandDTO brand = (BrandDTO) brandAndProduct.get("brand");
        model.addAttribute("brand", brand);  // 해당 브랜드 상세 정보 추가

        Map<String, Object> priceData = repository.getBrandMaxPrice(brandName);
        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");
        Double maxPriceAsDouble = maxPrice.doubleValue();
        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);
        priceData.put("max_price_in_won", maxPriceInWon);
        model.addAttribute("maxPrice", priceData);
        return "product/brand"; // HTML 뷰 반환
    }


    @GetMapping("/brand/{brandName}/filter/")
    public ResponseEntity<Map<String, Object>> getFilterList(
            @PathVariable(name = "brandName") String brandName,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tax,
            @RequestParam(required = false) String fta
    ) {

        // 전체 상품 가져오기
        Map<String, Object> brandAndProduct = repository.getBrandAndProduct(brandName);
        List<ProductDTO> allProducts = (List<ProductDTO>) brandAndProduct.get("products");


        // 가격 필터링
        if (minPrice != null && maxPrice != null) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCurrent_price() >= calculatorService.convertKrwToEur(minPrice)
                            && p.getCurrent_price() <= calculatorService.convertKrwToEur(maxPrice))
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
