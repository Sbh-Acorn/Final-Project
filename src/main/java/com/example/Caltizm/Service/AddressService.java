package com.example.Caltizm.Service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddressService {

    public static void main(String[] args) {

        String apiKey = "devU01TX0FVVEgyMDI0MTIwOTE1MzE1NjExNTMxMDM=";

        // String apiUrl = "https://business.juso.go.kr/addrlink/addrEngUrl.do";
        String apiUrl = "https://business.juso.go.kr/addrlink/addrEngApi.do";

        Map<String, String> postData = new HashMap<>();
        postData.put("confmKey", apiKey);
        // postData.put("returnUrl", "http://localhost:8080/engAddr");
        // postData.put("resultType", "2");

        postData.put("currentPage", "1");
        postData.put("countPerPage", "22");
        postData.put("keyword", "한국지역정보개발원");
        postData.put("resultType", "json");

        System.out.println(postData);

        String responseBody = post(apiUrl, postData);

        System.out.println(responseBody);


//        String responseBody =

//        HttpURLConnection conn = connect(apiUrl);


    }

    private static String post(String apiUrl, Map<String, String> postData){

        HttpURLConnection conn = connect(apiUrl);

        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = mapToJson(postData);

            System.out.println("JSON Payload: " + jsonInputString);

            try (OutputStream os = conn.getOutputStream()){
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

//            try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream())){
//                dataOutputStream.writeBytes(jsonInputString);
//                dataOutputStream.flush();
//            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){
                return readBody(conn.getInputStream());
            } else{
                return readBody(conn.getErrorStream());
            }

        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            conn.disconnect();
        }

    }

    private static String mapToJson(Map<String, String> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonBuilder.append("\"")
                    .append(entry.getKey())
                    .append("\": \"")
                    .append(entry.getValue())
                    .append("\", ");
        }

        // 마지막 쉼표 제거
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 2);
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결에 실패했습니다.  : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){

        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while((line = lineReader.readLine()) != null){
                responseBody.append(line);
            }

            return responseBody.toString();

        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }

    }

}
