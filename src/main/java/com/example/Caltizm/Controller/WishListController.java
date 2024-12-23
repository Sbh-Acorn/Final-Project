package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.WishlistRequestDTO;
import com.example.Caltizm.DTO.WishlistDTO;
import com.example.Caltizm.Repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WishListController {

    @Autowired
    WishlistRepository repository;

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

    // Post로 위시라스트 추가 요청
    @ResponseBody
    @PostMapping("/wishlist/add")
    public Map<String, String> addWishlist(@SessionAttribute(value="email", required=false) String email,
                                           @RequestParam(name="productId") String productId){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println(productId);

        WishlistRequestDTO wishlistRequestDTO = new WishlistRequestDTO();
        wishlistRequestDTO.setEmail(email);
        wishlistRequestDTO.setProductId(productId);
        System.out.println(wishlistRequestDTO);

        boolean isInWishlist = repository.isInWishlist(wishlistRequestDTO);
        System.out.println("isInWishlist: " + isInWishlist);
        if(isInWishlist){
            response.put("status", "already_exists");
            response.put("message", "이미 등록된 제품입니다.");
            System.out.println(response);
            return response;
        }

        int rRow = repository.insertWishlist(wishlistRequestDTO);
        System.out.println(rRow);
        if(rRow != 1){
            response.put("status", "add_fail");
            response.put("message", "위시리스트 등록에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "add_success");
        response.put("message", "제품을 위시리스트에 등록했습니다.");
        System.out.println(response);
        return response;

    }

    @ResponseBody
    @DeleteMapping("/wishlist/delete/{productId}")
    public Map<String, String> deleteWishlist(@PathVariable("productId") String productId,
                                 @SessionAttribute(value="email", required=false) String email){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println(productId);

        WishlistRequestDTO wishlistRequestDTO = new WishlistRequestDTO();
        wishlistRequestDTO.setEmail(email);
        wishlistRequestDTO.setProductId(productId);
        System.out.println(wishlistRequestDTO);

        boolean isInWishlist = repository.isInWishlist(wishlistRequestDTO);
        System.out.println("isInWishlist: " + isInWishlist);
        if(!isInWishlist){
            response.put("status", "not_exists");
            response.put("message", "등록되지 않은 제품입니다.");
            System.out.println(response);
            return response;
        }

        int rRow = repository.deleteWishlist(wishlistRequestDTO);
        System.out.println(rRow);
        if(rRow != 1){
            response.put("status", "delete_fail");
            response.put("message", "위시리스트 삭제에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "delete_success");
        response.put("message", "제품을 위시리스트에서 삭제했습니다.");
        System.out.println(response);
        return response;

    }

    @ResponseBody
    @PostMapping("/notification/read")
    public Map<String, String> readNotification(@SessionAttribute(value="email", required=false) String email,
                                                @RequestParam(name="notificationId") String notificationId){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println("notificationId: " + notificationId);

        int rRow = repository.readNotification(notificationId);
        System.out.println("rRow: " + rRow);
        if(rRow != 1){
            response.put("status", "update_fail");
            response.put("message", "알림 수정에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "update_success");
        response.put("message", "알림을 수정했습니다.");
        System.out.println(response);
        return response;

    }

}
