package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.NotificationDTO;
import com.example.Caltizm.DTO.WishlistRequestDTO;
import com.example.Caltizm.DTO.WishlistDTO;
import com.example.Caltizm.Repository.WishlistRepository;
import com.example.Caltizm.Service.CalculatorService;
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

    @Autowired
    CalculatorService calculatorService;

    @GetMapping("/wishlist")
    public String wishlist(@SessionAttribute(value="email", required=false) String email,
                           Model model){

        if(email == null){
            return "redirect:/login";
        }

        List<WishlistDTO> wishlist = repository.selectWishlist(email);

        for(WishlistDTO product : wishlist){
            product.setOriginalPrice(calculatorService.convertEurToKrw(product.getOriginalPrice()));
            product.setCurrentPrice(calculatorService.convertEurToKrw(product.getCurrentPrice()));
        }

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

        int wishlistSize = repository.selectWishlistSize(email);
        response.put("status", "add_success");
        response.put("message", "제품을 위시리스트에 등록했습니다.");
        response.put("wishlistSize", Integer.toString(wishlistSize));
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

        int wishlistSize = repository.selectWishlistSize(email);
        response.put("status", "delete_success");
        response.put("message", "제품을 위시리스트에서 삭제했습니다.");
        response.put("wishlistSize", Integer.toString(wishlistSize));
        System.out.println(response);
        return response;

    }

    @ResponseBody
    @GetMapping("/notification")
    public Map<String, Object> getNotification(@SessionAttribute(value="email", required=false) String email){

        Map<String, Object> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        List<NotificationDTO> notificationList = repository.selectNotification(email);

        for(NotificationDTO n : notificationList){
            n.setPreviousPrice(calculatorService.convertEurToKrw(n.getPreviousPrice()));
            n.setCurrentPrice(calculatorService.convertEurToKrw(n.getCurrentPrice()));
        }

        System.out.println(notificationList);
        response.put("status", "fetch_success");
        response.put("message", "알림 목록을 불러왔습니다.");
        response.put("notificationList", notificationList);
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
//        List<NotificationDTO> notificationList = repository.selectNotification(email);
        return response;

    }

    @ResponseBody
    @GetMapping("/wishlist/size")
    public Map<String, Object> getWishlistSize(@SessionAttribute(value="email", required=false) String email){

        Map<String, Object> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        int wishlistSize = repository.selectWishlistSize(email);

        response.put("status", "fetch_success");
        response.put("message", "위시리스트 제품 수를 불러왔습니다.");
        response.put("wishlistSize", wishlistSize);
        System.out.println(response);
        return response;

    }

}
