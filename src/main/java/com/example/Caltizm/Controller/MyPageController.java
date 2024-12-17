package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.*;
import com.example.Caltizm.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        System.out.println(addressList);

        System.out.println(email);
        System.out.println(user);

        return "myPage/myPage";

    }

    @GetMapping("/myPage_ui")
    public String myPage2() {
        return "myPage/myPage_ui";
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

    @GetMapping("/address/create")
    public String addressForm(@SessionAttribute(value="email", required=false) String email){

        if(email == null){
            return "redirect:/login";
        }

        return "myPage/addAddress";

    }

    @PostMapping("/address/create")
    public String createAddress(@SessionAttribute(value="email", required=false) String email,
                                @ModelAttribute AddressRequestDTO addressRequestDTO){

        if(email == null){
            return "redirect:/login";
        }

        addressRequestDTO.setEmail(email);
        System.out.println("addressRequestDTO: " + addressRequestDTO);

        int rRow = repository.insertAddress(addressRequestDTO);
        System.out.println(rRow);

        return "redirect:/myPage";

    }

    @GetMapping("/address/delete/{id}")
    public String deleteAddress(@SessionAttribute(value="email", required=false) String email,
                                @PathVariable("id") String id){

        if(email == null){
            return "redirect:/login";
        }

        int rRow = repository.deleteAddress(id);
        System.out.println(rRow);

        return "redirect:/myPage";

    }

    @GetMapping("/address/update/{id}")
    public String addressForm2(@SessionAttribute(value="email", required=false) String email,
                               @PathVariable("id") String id, Model model){

        if(email == null){
            return "redirect:/login";
        }

        AddressResponseDTO address = repository.selectAddress(id);
        System.out.println(address);

        if(address == null){
            return "redirect:/myPage";
        }

        model.addAttribute("address", address);

        return "myPage/updateAddress";

    }

    @PostMapping("/address/update/{id}")
    public String updateAddress(@PathVariable("id") String id,
                                @SessionAttribute(name="email", required=false) String email,
                                @ModelAttribute AddressResponseDTO addressResponseDTO){

        System.out.println(addressResponseDTO);

        addressResponseDTO.setAddressId(id);
        addressResponseDTO.setEmail(email);

        System.out.println(addressResponseDTO);

        int rRow = repository.updateAddress(addressResponseDTO);

        return "redirect:/myPage";

    }

    @PostMapping("/changePassword")
    public String changePassword(@SessionAttribute(value="email", required=false) String email,
                                 @ModelAttribute PasswordFormDTO passwordFormDTO){

        if(email == null){
            return "redirect:/login";
        }

        LoginRequestDTO user = repository.selectUserLogin(email);
        if(user == null){
            return "redirect:/login";
        }

        String currentPassword = passwordFormDTO.getCurrentPassword();
        String newPassword1 = passwordFormDTO.getNewPassword1();
        String newPassword2 = passwordFormDTO.getNewPassword2();

        if(!currentPassword.equals(user.getPassword())){
            System.out.println("현재 비밀번호 불일치");
            return "redirect:/myPage";
        }

        if(!newPassword1.equals(newPassword2)){
            System.out.println("새 비밀번호 불일치");
            return "redirect:/myPage";
        }

        if(currentPassword.equals(newPassword1)){
            System.out.println("비밀번호가 변경 전과 동일");
            return "redirect:/myPage";
        }

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setEmail(email);
        passwordUpdateDTO.setNewPassword(newPassword1);

        repository.updatePassword(passwordUpdateDTO);
        System.out.println("비밀번호 변경 완료");

        return "redirect:/myPage";

    }

}
