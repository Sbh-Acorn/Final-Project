package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.SignupRequestDTO;
import com.example.Caltizm.DTO.UserAddressDTO;
import com.example.Caltizm.Service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class signupController {

    @Autowired
    SignupService service;

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public  String register(@ModelAttribute SignupRequestDTO user,
                            @ModelAttribute UserAddressDTO address) {

        System.out.println(user);
        System.out.println(address);

        service.registUser(user);
        String email = user.getEmail();

        service.registUserAddr(address,email);

        return "redirect:/login";
    }
}
