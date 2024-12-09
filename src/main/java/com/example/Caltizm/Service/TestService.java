package com.example.Caltizm.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class TestService {

    public static void main(String[] args) throws IOException {

        String currentPage = "1";
        String countPerPage = "22";
        String resultType = "json";
        String confmKey = "devU01TX0FVVEgyMDI0MTIwOTE1MzE1NjExNTMxMDM=";
        String keyword = "한국지역정보개발원";
        String apiUrl = "https://business.juso.go.kr/addrlink/addrEngApi.do?currentPage="+currentPage
                +"&countPerPage="+countPerPage+"&keyword="+ URLEncoder.encode(keyword,"UTF-8")
                +"&confmKey="+confmKey+"&resultType="+resultType;
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String tempStr = null;
        while(true){
            tempStr = br.readLine();
            if(tempStr == null) break;
            sb.append(tempStr);
        }
        String result = sb.toString();
        System.out.println(result);
        br.close();
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        response.getWriter().write(sb.toString());

    }

}
