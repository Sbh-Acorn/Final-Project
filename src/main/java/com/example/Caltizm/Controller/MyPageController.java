package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.*;
import com.example.Caltizm.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return "myPage/mypage";

    }

    @ResponseBody
    @PostMapping("/updateUserInfo")
    public Map<String, String> update(@SessionAttribute(value="email", required=false) String email,
                         @RequestBody UserUpdateFormDTO userUpdateFormDTO){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println(userUpdateFormDTO);

        String[] name = userUpdateFormDTO.getName().split(" ");
        if(name.length != 2){
            response.put("status", "invalid_input");
            response.put("message", "유효하지 않은 입력입니다.");
            System.out.println(response);
            return response;
        }
        String firstName = name[0];
        String lastName = name[1];

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(email, firstName, lastName,
                userUpdateFormDTO.getPhoneNumber(), userUpdateFormDTO.getBirthDate(),
                userUpdateFormDTO.getPccc() != null ? userUpdateFormDTO.getPccc() : null);
        System.out.println(userUpdateDTO);

        int rRow = repository.updateUserInfo(userUpdateDTO);
        System.out.println(rRow);

        if(rRow != 1){
            response.put("status", "update_fail");
            response.put("message", "정보가 수정되지 않았습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "update_success");
        response.put("message", "정보가 수정되었습니다.");
        System.out.println(response);
        return response;

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
        System.out.println(rRow);

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

        String newPassword1 = passwordFormDTO.getNewPassword1();
        String newPassword2 = passwordFormDTO.getNewPassword2();

        if(!newPassword1.equals(newPassword2)){
            System.out.println("새 비밀번호 불일치");
            return "redirect:/myPage";
        }

        if(newPassword1.equals(user.getPassword())){
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
