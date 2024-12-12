package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.WishlistAddDTO;
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

//    @PostMapping("/wishlist/add")
//    public String addWishlist(@SessionAttribute(value="email", required=false) String email,
//                              @ModelAttribute WishlistAddDTO wishlistAddDTO){
//
//        if(email == null){
//            return "redirect:/login";
//        }
//
//        String productId = wishlistAddDTO.getProductId();
//        System.out.println(productId);
//
//        System.out.println(wishlistAddDTO);
//        wishlistAddDTO.setEmail(email);
//        System.out.println(wishlistAddDTO);
//
//        int rRow = repository.insertWishlist(wishlistAddDTO);
//        System.out.println(rRow);
//
//        return "redirect:/product/" + productId;
//
//    }

    @GetMapping("/wishlist/add/{productId}")
    public String addWishlist(@PathVariable("productId") String productId,
                              @SessionAttribute(value="email", required=false) String email){

        if(email == null){
            return "redirect:/login";
        }

        System.out.println(productId);

        WishlistAddDTO wishlistAddDTO = new WishlistAddDTO();
        wishlistAddDTO.setEmail(email);
        wishlistAddDTO.setProductId(productId);
        System.out.println(wishlistAddDTO);

        int cnt = repository.isInWishlist(wishlistAddDTO);
        System.out.println("cnt: " + cnt);
        if(cnt > 0){
            System.out.println("이미 위시리스트에 등록됨");
            return "redirect:/product/" + productId;
        }

        int rRow = repository.insertWishlist(wishlistAddDTO);
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

    @GetMapping("/wishlist/delete/{wishlistId}")
    public String deleteWishlist(@PathVariable("wishlistId") String wishlistId,
                                 @SessionAttribute(value="email", required=false) String email){

        if(email == null){
            return "redirect:/login";
        }

        System.out.println(wishlistId);

        int rRow = repository.deleteWishlist(wishlistId);
        System.out.println(rRow);

        return "redirect:/wishlist";

    }

}
