package com.example.Caltizm.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /*
    // 인증번호 생성
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // 6자리 랜덤 숫자 생성
        return String.valueOf(code);
    }
    */

    public void sendEmail(String toEmail, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("macdonald481203@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject( toEmail + "'s password");
        helper.setText("<h1>Your passowrd is: " + password + "</h1>", true);

        mailSender.send(message);
    }
}
