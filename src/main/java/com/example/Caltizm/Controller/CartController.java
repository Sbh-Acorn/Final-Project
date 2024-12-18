package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.CartRepository;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    DataRepository repository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;


    // 모든 메서드에서 사용할 제품 리스트를 미리 로드
    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(Comparator.comparing(ProductDTO::getBrand));
        return products;
    }

    @PostMapping("/add")
    @ResponseBody
    public String addCart(@RequestParam(name = "product_id") String product_id, HttpSession session, Model model) {


        // 상품 정보를 DB에서 가져오기
        CartDTO addCartItem = repository.getCartItemInfo(product_id);
        if (addCartItem == null) {
            return "해당 상품을 찾을 수 없습니다.";
        }
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        for (CartDTO cartItem : cartList) {
            if (cartItem.getProduct_id().equals(product_id)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                System.out.println("수량 추가");
                System.out.println("현재 장바구니 : " + cartList);
                session.setAttribute("cartList", cartList);
                return "수량을 증가하였습니다. (비로그인 상태)"; // 이미 존재하면 수량만 증가하고 메서드 종료
            }
        }


        cartList.add(addCartItem);
        System.out.println("장바구니 추가");
        System.out.println("현재 장바구니 : " + cartList);
        session.setAttribute("cartList", cartList);
        return "상품이 정상적으로 추가되었습니다. (비로그인 상태)";
    }


    @GetMapping("/view")
    public String cartView(Model model, HttpSession session) {


        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        System.out.println(cartList);
        if (cartList == null) {
            cartList = new ArrayList<>();
            session.setAttribute("cartList", cartList);
            return "cart/cart";
        }

        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        List<CartDTO> finalCartList = cartList;
        List<ProductDTO> cartProducts = products.stream()
                .filter(product -> finalCartList.stream()
                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
                .collect(Collectors.toList());

        model.addAttribute("cartProducts", cartProducts);

        return "cart/cart";
    }


    //    @PostMapping("/remove")
    //    public String removeCart(@RequestParam(name = "product_id") String product_id,
    //                             @RequestParam(name = "current_product_id") String current_product_id,
    //                             HttpSession session) {
    //        // 세션에서 장바구니 가져오기
    //        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
    //
    //        if (cartList != null) {
    //            // 조건에 맞는 상품 제거
    //            cartList.removeIf(item -> item.getProduct_id().equals(product_id));
    //        }
    //
    //        // 갱신된 장바구니 세션에 저장
    //        session.setAttribute("cartList", cartList);
    //
    //        // 상품 상세 페이지로 리다이렉트
    //        return "redirect:/product/" + current_product_id;
    //    }


    //    @PostMapping("/view/remove")
    //    public String viewRemoveCart(@RequestParam(name = "product_id") String product_id,
    //                             HttpSession session,Model model) {
    //        // 세션에서 장바구니 가져오기
    //        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
    //
    //        if (cartList != null) {
    //            // 조건에 맞는 상품 제거
    //            cartList.removeIf(item -> item.getProduct_id().equals(product_id));
    //        }
    //
    //        // 갱신된 장바구니 세션에 저장
    //        session.setAttribute("cartList", cartList);
    //        model.addAttribute("cartList", cartList);
    //
    //        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");
    //
    //        List<CartDTO> finalCartList = cartList;
    //        List<ProductDTO> cartProducts = products.stream()
    //                .filter(product -> finalCartList.stream()
    //                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
    //                .collect(Collectors.toList());
    //
    //        model.addAttribute("cartProducts", cartProducts);
    //
    //        // 상품 상세 페이지로 리다이렉트
    //        return "redirect:/cart/view";
    //    }
    //
    //    @PostMapping("/view/updateQuantity")
    //    public String updateQuantity(@RequestParam(name = "product_id") String product_id,
    //                                 @RequestParam(name = "action") String action,
    //                                 HttpSession session, Model model) {
    //        // 세션에서 장바구니 가져오기
    //        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
    //
    //        if (cartList != null) {
    //            for (CartDTO cartItem : cartList) {
    //                if (cartItem.getProduct_id().equals(product_id)) {
    //                    if ("increase".equals(action)) {
    //                        cartItem.setQuantity(cartItem.getQuantity() + 1); // 수량 증가
    //                    } else if ("decrease".equals(action)) {
    //                        if (cartItem.getQuantity() > 1) {
    //                            cartItem.setQuantity(cartItem.getQuantity() - 1); // 수량 감소
    //                        } else {
    //                            // 수량이 1일 때 감소 요청 -> 장바구니에서 제거
    //                            cartList.remove(cartItem);
    //                        }
    //                    }
    //                    break; // 해당 상품을 처리했으므로 루프 종료
    //                }
    //            }
    //        }
    //
    //        // 갱신된 장바구니를 세션에 저장
    //        session.setAttribute("cartList", cartList);
    //        model.addAttribute("cartList", cartList);
    //
    //        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");
    //
    //        List<CartDTO> finalCartList = cartList;
    //        List<ProductDTO> cartProducts = products.stream()
    //                .filter(product -> finalCartList.stream()
    //                        .anyMatch(cart -> cart.getProduct_id().equals(product.getProduct_id())))
    //                .collect(Collectors.toList());
    //
    //        model.addAttribute("cartProducts", cartProducts);
    //
    //        // 장바구니 보기 페이지로 리다이렉트
    //        return "redirect:/cart/view";
    //    }


}
