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
                            HttpServletRequest request) {

        System.out.println(user);

        service.registUser(user);
        String email = user.getEmail();

        // @RequestParam으로 받아올 시 문자열에 쉼표가 포함되면 나누어서 가져옴
        // 하나씩 가져오기 위해 request.getParameterValues() 사용
        List<String> addresses = List.of(request.getParameterValues("address"));
        List<String> details = List.of(request.getParameterValues("detail"));

        System.out.println(addresses);
        System.out.println(addresses.size());
        System.out.println(zipCodes);
        System.out.println(zipCodes.size());
        System.out.println(details);
        System.out.println(details.size());

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

        return "redirect:/login";
    }
}
