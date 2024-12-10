package com.example.Caltizm.Service;


import com.example.Caltizm.DTO.BrandDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
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

        CompletableFuture<Void>[] futures = new CompletableFuture[brandURL.size()];
        int index = 0;

        for( String url : brandURL){
            futures[index++] = CompletableFuture.runAsync(() -> {
                try {
                    Document brandDoc = fetchWithRetry(url);
                    BrandDTO brand = extractBrand(brandDoc);
                    synchronized (brands) { // 동기화된 접근
                        brands.add(brand);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        CompletableFuture.allOf(futures).join();
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
                if (retries >= 3) {
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
