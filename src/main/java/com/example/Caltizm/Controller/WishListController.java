package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.WishlistRequestDTO;
import com.example.Caltizm.DTO.WishlistDTO;
import com.example.Caltizm.Repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WishListController {

    @Autowired
    WishlistRepository repository;

    // Post로 위시라스트 추가 요청
//    @PostMapping("/wishlist/add")
//    public String addWishlist2(@SessionAttribute(value="email", required=false) String email,
//                              @ModelAttribute WishlistRequestDTO wishlistRequestDTO){
//
//        if(email == null){
//            return "redirect:/login";
//        }
//
//        String productId = wishlistRequestDTO.getProductId();
//        System.out.println(productId);
//
//        System.out.println(wishlistRequestDTO);
//        wishlistRequestDTO.setEmail(email);
//        System.out.println(wishlistRequestDTO);
//
//        int rRow = repository.insertWishlist(wishlistRequestDTO);
//        System.out.println(rRow);
//
//        return "redirect:/product/" + productId;
//
//    }

    // Get으로 위시리스트 추가 요청
    @GetMapping("/wishlist/add/{productId}")
    public String addWishlist(@PathVariable("productId") String productId,
                              @SessionAttribute(value="email", required=false) String email){

        if(email == null){
            return "redirect:/login";
        }

        System.out.println(productId);

        WishlistRequestDTO wishlistRequestDTO = new WishlistRequestDTO();
        wishlistRequestDTO.setEmail(email);
        wishlistRequestDTO.setProductId(productId);
        System.out.println(wishlistRequestDTO);

        int cnt = repository.isInWishlist(wishlistRequestDTO);
        System.out.println("cnt: " + cnt);
        if(cnt > 0){
            System.out.println("이미 위시리스트에 등록됨");
            return "redirect:/product/" + productId;
        }

        int rRow = repository.insertWishlist(wishlistRequestDTO);
        System.out.println(rRow);

        return "redirect:/product/" + productId;

    }

    @GetMapping("/wishlist")
    public String wishlist(@SessionAttribute(value="email", required=false) String email,
                           Model model){

        if(email == null){
            return "redirect:/login";
        }

        List<WishlistDTO> wishlist = repository.selectWishlist(email);
        model.addAttribute("wishlist", wishlist);

        return "wishlist/wishlist";

    }

    @GetMapping("/wishlist/delete/{productId}")
    public String deleteWishlist(@PathVariable("productId") String productId,
                                 @SessionAttribute(value="email", required=false) String email){

        if(email == null){
            return "redirect:/login";
        }

        System.out.println(productId);

        WishlistRequestDTO wishlistRequestDTO = new WishlistRequestDTO();
        wishlistRequestDTO.setEmail(email);
        wishlistRequestDTO.setProductId(productId);
        System.out.println(wishlistRequestDTO);

        int cnt = repository.isInWishlist(wishlistRequestDTO);
        System.out.println("cnt: " + cnt);
        if(cnt < 1){
            System.out.println("위시리스트에 없음");
            return "redirect:/wishlist";
        }

        int rRow = repository.deleteWishlist(wishlistRequestDTO);
        System.out.println(rRow);

        return "redirect:/wishlist";

    }

}
