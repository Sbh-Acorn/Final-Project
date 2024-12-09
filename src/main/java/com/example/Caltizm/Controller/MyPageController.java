package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.AddressResponseDTO;
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

import java.util.List;

@Controller
public class MyPageController {

    @Autowired
    UserRepository repository;

    @GetMapping("/myPage")
    public String myPage(@SessionAttribute(value="email", required=false) String email, Model model){

        if(email == null){
            return "redirect:/login";
        }

        MyPageResponseDTO user = repository.selectUserInfo(email);

        if(user == null){
            return "redirect:/login";
        }

        List<AddressResponseDTO> addressList = repository.selectAddressAll(email);

        model.addAttribute("user", user);
        model.addAttribute("addressList", addressList);

        System.out.println(email);
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

    @GetMapping("/addr")
    public String addr(){
        return "myPage/addr";
    }

    @GetMapping("/addrPopup")
    public String addrPopup(){
        return "myPage/addrPopup";
    }

}
