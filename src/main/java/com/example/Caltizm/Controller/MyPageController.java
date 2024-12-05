package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.MyPageResponseDTO;
import com.example.Caltizm.DTO.UserUpdateRequestDTO;
import com.example.Caltizm.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class MyPageController {

    @Autowired
    UserRepository repository;

    @GetMapping("/myPage")
    public String myPage(@SessionAttribute(value="userId", required=false) String userId, Model model){

        if(userId == null){
            return "redirect:/login";
        }

        MyPageResponseDTO user = repository.selectUserInfo(userId);

        if(user == null){
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        System.out.println(userId);
        System.out.println(user);

        return "myPage/myPage";

    }

    @PostMapping("/updateUserInfo")
    public String update(@ModelAttribute UserUpdateRequestDTO userUpdateRequestDTO){

        String phoneNumber = userUpdateRequestDTO.getPhoneNumber().replaceAll("-", "");

        String part1 = phoneNumber.substring(0, 3);
        String part2 = phoneNumber.substring(3, 7);
        String part3 = phoneNumber.substring(7);

        String newPhoneNumber = part1 + "-" + part2 + "-" + part3;

        userUpdateRequestDTO.setPhoneNumber(newPhoneNumber);

        System.out.println(userUpdateRequestDTO);

        int rRow = repository.updateUserInfo(userUpdateRequestDTO);
        System.out.println(rRow);

        return "redirect:/myPage";

    }

}
