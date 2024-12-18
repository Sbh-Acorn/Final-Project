package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.SignupRequestDTO;
import com.example.Caltizm.DTO.UserAddressDTO;
import com.example.Caltizm.Service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class signupController {

    @Autowired
    SignupService service;

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    @GetMapping("/signupTest")
    public String signup2() {return "auth/signupTest"; }

    @PostMapping("/signup")
    public  String register(@ModelAttribute SignupRequestDTO user,
                            @RequestParam("zip_code") List<String> zipCodes,
                            @RequestParam("checkPw") String checkPw,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        // 이메일 중복 검사
        if(service.userCheck(user.getEmail())) {
            redirectAttributes.addFlashAttribute("message", "중복된 이메일이 존재합니다.");
            return "redirect:/signup";
        }

        // 전화번호 중복 검사
        if(!service.telDupCheck(user.getPhone_number())) {
            redirectAttributes.addFlashAttribute("message", "중복된 전화번호가 존재합니다.");
            return "redirect:/signup";
        }

        // 전화번호 형식 검사
        if(!service.telEffCheck(user.getPhone_number())) {
            redirectAttributes.addFlashAttribute("message", "전화번호 형식이 올바르지 않습니다.");
            return "redirect:/signup";
        }

        // 비밀번호가 비밀번호 확인과 동일한지 확인
        if(!(user.getPassword().equals(checkPw))) {
            redirectAttributes.addFlashAttribute("message", "비밀번호가 일치하지 않습니다.");
            return "redirect:/signup";
        }

        System.out.println(user);

        service.registUser(user);
        String email = user.getEmail();

        // @RequestParam으로 받아올 시 문자열에 쉼표가 포함되면 나누어서 가져옴
        // 하나씩 가져오기 위해 request.getParameterValues() 사용
        List<String> addresses = List.of(request.getParameterValues("address"));
        List<String> details = List.of(request.getParameterValues("detail"));

        if(!addresses.isEmpty() && !zipCodes.isEmpty() && !details.isEmpty()
                && addresses.size() == zipCodes.size() && addresses.size() == details.size()){
            for(int i=0; i<addresses.size(); i++){
                UserAddressDTO addr = new UserAddressDTO();
                addr.setAddress(addresses.get(i));
                addr.setZip_code(zipCodes.get(i));
                addr.setDetail(details.get(i));
                System.out.println(addr);
                service.registUserAddr(addr, email);
            }
        }

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/login";
    }
}
