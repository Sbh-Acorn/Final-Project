package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.*;
import com.example.Caltizm.Repository.BoardRepository;
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
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/myPage")
    public String myPage(@SessionAttribute(value="email", required=false) String email, Model model){

        if(email == null){
            return "redirect:/login";
        }

        MyPageResponseDTO user = userRepository.selectUserInfo(email);

        if(user == null){
            return "redirect:/login";
        }

        List<AddressResponseDTO> addressList = userRepository.selectAddressAll(email);
        List<PostDTO> postList = boardRepository.selectAllByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("addressList", addressList);
        model.addAttribute("postList", postList);

        System.out.println(addressList);
        System.out.println(postList);

        System.out.println(email);
        System.out.println(user);

        return "myPage/mypage";

    }

    @ResponseBody
    @PatchMapping("/updateUserInfo")
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

        int rRow = userRepository.updateUserInfo(userUpdateDTO);
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

    @ResponseBody
    @PostMapping("/address/create")
    public Map<String, String> createAddress(@SessionAttribute(value="email", required=false) String email,
                                @RequestBody AddressRequestDTO addressRequestDTO){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        if(userRepository.checkMaxAddressLimit(email)){
            response.put("status", "max_limit");
            response.put("message", "최대 주소 개수 제한을 초과했습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println("addressRequestDTO: " + addressRequestDTO);
        addressRequestDTO.setEmail(email);
        System.out.println("addressRequestDTO: " + addressRequestDTO);

        int rRow = userRepository.insertAddress(addressRequestDTO);
        System.out.println(rRow);

        if(rRow != 1){
            response.put("status", "add_fail");
            response.put("message", "주소록 추가에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "add_success");
        response.put("message", "새로운 주소를 추가했습니다.");
        System.out.println(response);
        return response;

    }

    @ResponseBody
    @DeleteMapping("/address/delete/{id}")
    public Map<String, String> deleteAddress(@SessionAttribute(value="email", required=false) String email,
                                @PathVariable("id") String id){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        int rRow = userRepository.deleteAddress(id);
        System.out.println(rRow);

        if(rRow != 1){
            response.put("status", "delete_fail");
            response.put("message", "주소록 삭제에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "delete_success");
        response.put("message", "주소를 삭제했습니다.");
        System.out.println(response);
        return response;

    }

    @GetMapping("/address/update/{id}")
    public String addressForm2(@SessionAttribute(value="email", required=false) String email,
                               @PathVariable("id") String id, Model model){

        if(email == null){
            return "redirect:/login";
        }

        AddressResponseDTO address = userRepository.selectAddress(id);
        System.out.println(address);

        if(address == null){
            return "redirect:/myPage";
        }

        model.addAttribute("address", address);

        return "myPage/updateAddress";

    }

    @ResponseBody
    @PatchMapping("/address/update")
    public Map<String, String> updateAddress(@SessionAttribute(name="email", required=false) String email,
                                @RequestBody AddressResponseDTO addressResponseDTO){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        System.out.println(addressResponseDTO);
        addressResponseDTO.setEmail(email);
        System.out.println(addressResponseDTO);

        int rRow = userRepository.updateAddress(addressResponseDTO);
        System.out.println(rRow);

        if(rRow != 1){
            response.put("status", "update_fail");
            response.put("message", "주소록 수정에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "update_success");
        response.put("message", "주소를 수정했습니다.");
        System.out.println(response);
        return response;

    }

    @ResponseBody
    @PatchMapping("/changePassword")
    public Map<String, String> changePassword(@SessionAttribute(value="email", required=false) String email,
                                 @RequestBody PasswordFormDTO passwordFormDTO){

        Map<String, String> response = new HashMap<>();

        if(email == null){
            response.put("status", "session_invalid");
            response.put("message", "세션이 유효하지 않습니다.");
            System.out.println(response);
            return response;
        }

        LoginRequestDTO user = userRepository.selectUserLogin(email);
        if(user == null){
            response.put("status", "user_invalid");
            response.put("message", "사용자를 찾을 수 없습니다.");
            System.out.println(response);
            return response;
        }

        String newPassword1 = passwordFormDTO.getNewPassword1();
        String newPassword2 = passwordFormDTO.getNewPassword2();

        if(!newPassword1.equals(newPassword2)){
            response.put("status", "password_mismatch");
            response.put("message", "비밀번호 입력이 일치하지 않습니다.");
            System.out.println(response);
            return response;
        }

        if(newPassword1.equals(user.getPassword())){
            response.put("status", "password_same");
            response.put("message", "기존 비밀번호와 동일합니다.");
            System.out.println(response);
            return response;
        }

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setEmail(email);
        passwordUpdateDTO.setNewPassword(newPassword1);

        int rRow = userRepository.updatePassword(passwordUpdateDTO);
        System.out.println(rRow);

        if(rRow != 1){
            response.put("status", "update_fail");
            response.put("message", "비밀번호 변경에 실패했습니다.");
            System.out.println(response);
            return response;
        }

        response.put("status", "update_success");
        response.put("message", "비밀번호가 변경되었습니다.");
        System.out.println(response);
        return response;

    }

}
