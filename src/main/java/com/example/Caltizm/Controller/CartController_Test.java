package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.Repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/cart")
public class CartController_Test {

    @Autowired
    ProductRepository repository;

    @PostMapping("/add")
    public String addCart(@RequestParam(name = "product_id") String product_id, HttpSession session) {
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        CartDTO cartItem = repository.getCartItemInfo(product_id);
        System.out.println(cartItem);
        cartList.add(cartItem);

        session.setAttribute("cartList", cartList);

        return "redirect:/product/" + product_id;
    }


}
