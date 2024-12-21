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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    DataRepository repository;

    @Autowired
    CalculatorService calculatorService;

    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(Comparator.comparing(ProductDTO::getBrand));
        for (ProductDTO product : products){
            product.setCurrent_price(calculatorService.calculator(product.getCurrent_price()));
            if(product.getOriginal_price() != null){
                product.setOriginal_price(calculatorService.calculator(product.getOriginal_price()));
            }
        }
        return products;
    }

    @ModelAttribute("brandNames")
    public List<String> getAllBrandName(){
        return repository.getAllBrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllCategoryName(){
        return repository.getAllCategoryName();
    }

    @ModelAttribute("maxPrice")
    public Map<String, Object> getMinAndMaxPrice() {
        Map<String, Object> priceData = repository.getMaxPrice();

        BigDecimal maxPrice = (BigDecimal) priceData.get("max_price");

        Double maxPriceAsDouble = maxPrice.doubleValue();

        Double maxPriceInWon = calculatorService.calculator(maxPriceAsDouble);

        priceData.put("max_price_in_won", maxPriceInWon);

        return priceData;
    }


    @GetMapping("/product")
    public String product(Model model) {
        return "product/product-list";
    }

    @GetMapping("/product/{product_id}")
    public String productDetail(@PathVariable(name = "product_id") String product_id, Model model , HttpSession session) {
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



    /*

    @PostMapping("/product/filter")
    public List<ProductDTO> filter(@RequestBody FilterDTO filterData) {
    // 필터링 로직을 처리
    List<ProductDTO> allProducts = repository.getProduct();

    // 가격 필터링
    allProducts = allProducts.stream()
            .filter(p -> p.getCurrent_price() >= filterData.getPrice().getMin()
                    && p.getCurrent_price() <= filterData.getPrice().getMax())
            .collect(Collectors.toList());

    // 브랜드 필터링
    if (filterData.getBrands() != null && !filterData.getBrands().isEmpty()) {
        allProducts = allProducts.stream()
                .filter(p -> filterData.getBrands().contains(p.getBrand()))
                .collect(Collectors.toList());
    }

    // 카테고리 필터링
    if (filterData.getCategories() != null && !filterData.getCategories().isEmpty()) {
        allProducts = allProducts.stream()
                .filter(p -> filterData.getCategories().contains(p.getCategory1()) ||
                        filterData.getCategories().contains(p.getCategory2()) ||
                        filterData.getCategories().contains(p.getCategory3()))
                .collect(Collectors.toList());
    }

    // 세금 필터링: 가격이 150 이상인 제품만 필터링
    if (filterData.getTax() != null && filterData.getTax().equals("TAX")) {
        allProducts = allProducts.stream()
                .filter(p -> p.getCurrent_price() >= 150 && p.isTax())
                .collect(Collectors.toList());
    } else if (filterData.getTax() != null && filterData.getTax().equals("NOT TAX")) {
        allProducts = allProducts.stream()
                .filter(p -> p.getCurrent_price() >= 150 && !p.isTax())
                .collect(Collectors.toList());
    }

    // FTA 필터링
    if (filterData.getFta() != null) {
        allProducts = allProducts.stream()
                .filter(p -> (filterData.getFta().equals("FTA") && p.is_fta())
                        || (filterData.getFta().equals("NOT FTA") && !p.is_fta()))
                .collect(Collectors.toList());
    }

    // 가격 계산
    allProducts.forEach(product -> product.setCurrent_price(calculatorService.calculator(product.getCurrent_price())));

    return allProducts;
    }

    */
}
