package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Service.CalculatorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    DataRepository repository;

    @Autowired
    CalculatorService calculatorService;

    private static final int taxBaseAmount = 150;

    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(
                Comparator.comparing(ProductDTO::getBrand)
                        .thenComparing(ProductDTO::getName)
        );
        for (ProductDTO product : products) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }
        return products;
    }

    @ModelAttribute("brandNames")
    public List<String> getAllBrandName() {
        return repository.getAllBrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllCategoryName() {
        return repository.getAllCategoryName();
    }

    @ModelAttribute("maxPrice")
    public Map<String, Object> getMinAndMaxPrice() {
        Map<String, Object> priceData = repository.getMaxPrice();

        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.convertEurToKrw(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }


    @GetMapping("/product")
    public String product(Model model) {
        return "product/product-list";
    }

    @GetMapping("/product/{product_id}")
    public String productDetail(@PathVariable(name = "product_id") String product_id, Model model, HttpSession session) {
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        ProductDTO product = products.stream()
                .filter(p -> p.getProduct_id().equals(product_id))
                .findFirst()
                .orElse(null);

        model.addAttribute("product", product);

        if (product.getOriginal_price() != null) {
            int discountRate = (int) Math.round((1 - ((double) product.getCurrent_price() / product.getOriginal_price())) * 100);
            model.addAttribute("discountRate", discountRate);
        }

        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        model.addAttribute("cartList", cartList);
        return "product/product-detail";
    }


    @GetMapping("/product/filter")
    public String filter(
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tax,
            @RequestParam(required = false) String fta,
            Model model
    ) {
        // 필터링 로직 처리
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

        allProducts.sort(
                Comparator.comparing(ProductDTO::getBrand)
                        .thenComparing(ProductDTO::getName)
        );

        for (ProductDTO product : allProducts) {
            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
            if (product.getOriginal_price() != null) {
                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
            }
        }

        model.addAttribute("products", allProducts);
        return "product/product-list";
    }



}
