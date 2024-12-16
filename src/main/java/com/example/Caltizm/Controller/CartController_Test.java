package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/cart")
public class CartController_Test {

    @Autowired
    DataRepository repository;

    // 모든 메서드에서 사용할 제품 리스트를 미리 로드
    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(Comparator.comparing(ProductDTO::getBrand));
        return products;
    }

    @PostMapping("/add")
    public String addCart(@RequestParam(name = "product_id") String product_id, HttpSession session, Model model) {
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        CartDTO addCartItem = repository.getCartItemInfo(product_id);
        boolean itemExists = false;

        // 이미 장바구니에 상품이 있는지 확인
        for (CartDTO cartItem : cartList) {
            if (cartItem.getProduct_id().equals(product_id)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1); // 수량 증가
                itemExists = true;  // 동일한 상품이 있음을 표시
                break; // 상품을 찾으면 루프 종료
            }
        }
        // 장바구니에 동일한 상품이 없다면 새로운 상품 추가
        if (!itemExists) {
            cartList.add(addCartItem);
        }
        // 장바구니 정보 세션에 저장
        session.setAttribute("cartList", cartList);
//        model.addAttribute("cartList", cartList);
        return "redirect:/product/" + product_id;
    }


    @PostMapping("/remove")
    public String removeCart(@RequestParam(name = "product_id") String product_id,
                             @RequestParam(name = "current_product_id") String current_product_id,
                             HttpSession session) {
        // 세션에서 장바구니 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList != null) {
            // 조건에 맞는 상품 제거
            cartList.removeIf(item -> item.getProduct_id().equals(product_id));
        }

        // 갱신된 장바구니 세션에 저장
        session.setAttribute("cartList", cartList);

        // 상품 상세 페이지로 리다이렉트
        return "redirect:/product/" + current_product_id;
    }


    @GetMapping("/view")
    public String cartView(Model model, HttpSession session){

        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

//        session.setAttribute("cartList", cartList);
        model.addAttribute("cartList", cartList);

        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        List<CartDTO> finalCartList = cartList;
        List<ProductDTO> cartProducts = products.stream()
                .filter(product -> finalCartList.stream()
                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
                .collect(Collectors.toList());

        model.addAttribute("cartProducts", cartProducts);

        return "cart/cart_test";
    }



    @PostMapping("/view/remove")
    public String viewRemoveCart(@RequestParam(name = "product_id") String product_id,
                             HttpSession session,Model model) {
        // 세션에서 장바구니 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList != null) {
            // 조건에 맞는 상품 제거
            cartList.removeIf(item -> item.getProduct_id().equals(product_id));
        }

        // 갱신된 장바구니 세션에 저장
        session.setAttribute("cartList", cartList);
        model.addAttribute("cartList", cartList);

        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        List<CartDTO> finalCartList = cartList;
        List<ProductDTO> cartProducts = products.stream()
                .filter(product -> finalCartList.stream()
                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
                .collect(Collectors.toList());

        model.addAttribute("cartProducts", cartProducts);

        // 상품 상세 페이지로 리다이렉트
        return "redirect:/cart/view";
    }

    @PostMapping("/view/updateQuantity")
    public String updateQuantity(@RequestParam(name = "product_id") String product_id,
                                 @RequestParam(name = "action") String action,
                                 HttpSession session, Model model) {
        // 세션에서 장바구니 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList != null) {
            for (CartDTO cartItem : cartList) {
                if (cartItem.getProduct_id().equals(product_id)) {
                    if ("increase".equals(action)) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1); // 수량 증가
                    } else if ("decrease".equals(action)) {
                        if (cartItem.getQuantity() > 1) {
                            cartItem.setQuantity(cartItem.getQuantity() - 1); // 수량 감소
                        } else {
                            // 수량이 1일 때 감소 요청 -> 장바구니에서 제거
                            cartList.remove(cartItem);
                        }
                    }
                    break; // 해당 상품을 처리했으므로 루프 종료
                }
            }
        }

        // 갱신된 장바구니를 세션에 저장
        session.setAttribute("cartList", cartList);
        model.addAttribute("cartList", cartList);

        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        List<CartDTO> finalCartList = cartList;
        List<ProductDTO> cartProducts = products.stream()
                .filter(product -> finalCartList.stream()
                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
                .collect(Collectors.toList());

        model.addAttribute("cartProducts", cartProducts);

        // 장바구니 보기 페이지로 리다이렉트
        return "redirect:/cart/view";
    }


}
