package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.BrandDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class BrandController_Test {

    @Autowired
    DataRepository repository;

    @GetMapping("/brand")
    public String brandList(Model model) throws IOException {

        List<BrandDTO> brands = repository.getAllBrand();
        model.addAttribute("brand" , brands);

        return "product/brand_test";
    }

//    @GetMapping("/brand/{brandName}")
//    public String brandDetail(@PathVariable(name = "brandName") String brandName, Model model) throws IOException {
//        BrandDTO brand = repository.getBrandByName(brandName);  // 브랜드 이름으로 상세 정보 가져오기
//        model.addAttribute("brand", brand);  // 해당 브랜드 상세 정보 추가
//
//        return "product/brandDetail_test";  // 브랜드 상세 정보를 보여주는 뷰
//    }

    @GetMapping("/brand/{brandName}")
    public String brandDetail(@PathVariable(name = "brandName") String brandName, Model model) throws IOException {
        Map<String , Object> brandAndProduct = repository.getBrandAndProduct(brandName);
        BrandDTO brand = (BrandDTO) brandAndProduct.get("brand");
        List<ProductDTO> products = (List<ProductDTO>) brandAndProduct.get("products");
        model.addAttribute("brand", brand);  // 해당 브랜드 상세 정보 추가
        model.addAttribute("products", products);  // 해당 브랜드 상세 정보 추가

        return "product/brandDetail_test";  // 브랜드 상세 정보를 보여주는 뷰
    }
}
