package com.example.Caltizm.Service;


import com.example.Caltizm.DTO.BrandDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class GetBrandDataService {

    private static final String BASE_URL = "https://www.cultizm.com/Supplier";


    // 브랜드별 URL 수집 메서드
    public static Set<String> collectBrandURL(String BASE_URL) throws IOException {
        Set<String> brandURL = new HashSet<>();

        Document doc = fetchWithRetry(BASE_URL);
        Elements linkElements = doc.select("a.supplier--item--link");

        for (Element linkElement : linkElements) {
            String link = linkElement.attr("href");
            brandURL.add(link);
            //            System.out.println("수집한 링크 : " + link);
        }

        return brandURL;
    }
    // 브랜드 정보 수집 메서드
    public static Set<BrandDTO> collectBrand() throws IOException {
        Set<String> brandURL = collectBrandURL(BASE_URL);
        Set<BrandDTO> brands = new TreeSet<>(); // 순서대로 저장되게끔

        // CompletableFuture 배열 대신 List 사용
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String url : brandURL) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 페이지 로드
                    Document brandDoc = fetchWithRetry(url);

                    // BrandDTO 객체 추출
                    BrandDTO brand = extractBrand(brandDoc);

                    // 동기화된 접근으로 brands에 추가
                    synchronized (brands) {
                        brands.add(brand);
                    }
                } catch (IOException e) {
                    System.out.println("Error while fetching page " + url);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();

        return brands;
    }




    // 데이터 추출 메서드
    public static BrandDTO extractBrand(Document brandDoc) {

        //이름 추출
        Elements nameElement = brandDoc.select("h1.panel--title.is--underline");
        String name = nameElement.text().replace("Brand ", "").trim();
        //로고 이미지 링크 추출
        Elements imgElement = brandDoc.select("img.vendor--image");
        String logoImgURL = imgElement.attr("src");
        //브랜드 소개글 추출
        Elements profileElement = brandDoc.select("div.vendor--text");
        String profile = profileElement.text();

        BrandDTO brand = new BrandDTO(name, logoImgURL, profile);

        return brand;
    }

    //에러페이지 재시도 로직
    public static Document fetchWithRetry(String url) throws IOException {
        int retries = 0;
        while (retries < 3) {
            try {
                // Jsoup로 URL을 요청
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000) // 타임아웃 설정
                        .get();
            } catch (IOException e) {
                retries++;
                System.out.println("Error fetching page (attempt " + retries + "): " + url);
                if (retries >= 10) {
                    throw new IOException("Failed to fetch page after " + retries + " attempts: " + url, e);
                }
                try {
                    Thread.sleep(2000); // 2초 대기 후 재시도
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        throw new IOException("Failed to fetch page: " + url);
    }
}
