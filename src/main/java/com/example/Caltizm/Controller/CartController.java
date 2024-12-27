package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.CartListDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.CartRepository;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Repository.UserRepository;
import com.example.Caltizm.Service.CalculatorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    DataRepository repository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalculatorService calculatorService;

    @ModelAttribute("taxBaseAmount")
    public Double getConvert150UsdToEur(){
        return calculatorService.convertUsdToKrw(150);
    }

    @ModelAttribute("shipPrice")
    public double getShipPrice(){
        return calculatorService.convertEurToKrwWithTax(25);
    }

//    // 모든 메서드에서 사용할 제품 리스트를 미리 로드
//    @ModelAttribute("products")
//    public List<ProductDTO> getAllProducts() {
//        List<ProductDTO> products = repository.getProduct();
//        products.sort(Comparator.comparing(ProductDTO::getBrand));
//        for (ProductDTO product : products){
//            product.setCurrent_price(calculatorService.convertEurToKrw(product.getCurrent_price()));
//            if(product.getOriginal_price() != null){
//                product.setOriginal_price(calculatorService.convertEurToKrw(product.getOriginal_price()));
//            }
//        }
//        return products;
//    }

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
                return "수량을 증가하였습니다."; // 이미 존재하면 수량만 증가하고 메서드 종료
            }
        }

        cartList.add(addCartItem);
        System.out.println("장바구니 추가");
        System.out.println("현재 장바구니 : " + cartList);
        session.setAttribute("cartList", cartList);
        return "상품이 정상적으로 추가되었습니다.";
    }


    @GetMapping("/view")
    public String cartView(Model model, HttpSession session) {


        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");
        System.out.println(cartList);
        if (cartList == null) {
            cartList = new ArrayList<>();
            session.setAttribute("cartList", cartList);
            return "cart/cart_test";
        }

        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        List<CartListDTO> finalCartList = new ArrayList<>();

        // cartList의 각 항목에 대해
        for (CartDTO cartDTO : cartList) {
            // cartDTO의 product_id와 일치하는 productDTO 찾기
            for (ProductDTO productDTO : products) {
                if (cartDTO.getProduct_id().equals(productDTO.getProduct_id())) {
                    // 일치하는 제품을 찾으면 CartListDTO로 변환하여 리스트에 추가
                    CartListDTO cartListDTO = new CartListDTO(cartDTO, productDTO);
                    finalCartList.add(cartListDTO);
                    break; // 일치하는 첫 번째 객체만 처리 후 종료
                }
            }
        }

        model.addAttribute("cartProducts", finalCartList);

        return "cart/cart_test";
    }

    // 장바구니에서 상품을 제거하는 메서드
    @ResponseBody
    @PostMapping("/view/remove")
    public String viewRemoveCart(@RequestParam(name = "product_id") String product_id,
                                 HttpSession session, Model model) {

        // 세션에서 장바구니 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList != null) {
            // 조건에 맞는 상품 제거
            cartList.removeIf(item -> item.getProduct_id().equals(product_id));
        }

        // 갱신된 장바구니를 세션에 저장
        session.setAttribute("cartList", cartList);

        // 상품 리스트와 장바구니 제품 정보를 갱신
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");
        List<CartListDTO> finalCartList = new ArrayList<>();

        // cartList의 각 항목에 대해
        for (CartDTO cartDTO : cartList) {
            // cartDTO의 product_id와 일치하는 productDTO 찾기
            for (ProductDTO productDTO : products) {
                if (cartDTO.getProduct_id().equals(productDTO.getProduct_id())) {
                    // 일치하는 제품을 찾으면 CartListDTO로 변환하여 리스트에 추가
                    CartListDTO cartListDTO = new CartListDTO(cartDTO, productDTO);
                    finalCartList.add(cartListDTO);
                    break; // 일치하는 첫 번째 객체만 처리 후 종료
                }
            }
        }

        model.addAttribute("cartProducts", finalCartList);

        // 장바구니 페이지로 리다이렉트
        return "상품이 삭제되었습니다";
    }

    // 장바구니 수량을 업데이트하는 메서드
    @ResponseBody
    @PostMapping("/view/updateQuantity")
    public String updateQuantity(@RequestParam(name = "product_id") String product_id,
                                 @RequestParam(name = "action") String action,
                                 HttpSession session, Model model) {

        // 세션에서 장바구니 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        if (cartList != null) {
            // 해당 상품에 대해 수량을 증가 또는 감소
            for (CartDTO cartItem : cartList) {
                if (cartItem.getProduct_id().equals(product_id)) {
                    if ("plus".equals(action)) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1); // 수량 증가
                    } else if ("minus".equals(action)) {
                        if (cartItem.getQuantity() > 1) {
                            cartItem.setQuantity(cartItem.getQuantity() - 1); // 수량 감소
                        } else {
                            cartItem.setQuantity(1);
                        }
                    }
                    break; // 해당 상품을 처리했으므로 루프 종료
                }
            }
        }

        // 갱신된 장바구니를 세션에 저장
        session.setAttribute("cartList", cartList);

        // 상품 리스트와 장바구니 제품 정보를 갱신
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");
        List<CartListDTO> finalCartList = new ArrayList<>();

        // cartList의 각 항목에 대해
        for (CartDTO cartDTO : cartList) {
            // cartDTO의 product_id와 일치하는 productDTO 찾기
            for (ProductDTO productDTO : products) {
                if (cartDTO.getProduct_id().equals(productDTO.getProduct_id())) {
                    // 일치하는 제품을 찾으면 CartListDTO로 변환하여 리스트에 추가
                    CartListDTO cartListDTO = new CartListDTO(cartDTO, productDTO);
                    finalCartList.add(cartListDTO);
                    break; // 일치하는 첫 번째 객체만 처리 후 종료
                }
            }
        }

        model.addAttribute("cartProducts", finalCartList);

        // 장바구니 페이지로 리다이렉트
        return "장바구니 수량이 변경되었습니다";
    }
}
