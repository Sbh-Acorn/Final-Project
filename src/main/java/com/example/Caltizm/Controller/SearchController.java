package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.SearchProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchProductRepository searchProductRepository;

    @GetMapping("/search")
    @ResponseBody
    public List<ProductDTO> searchProducts(@RequestParam("query") String query) {
        return searchProductRepository.findProductsByName(query);
    }
}
