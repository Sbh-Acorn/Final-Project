package com.example.Caltizm.Service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class GetFTADataService {

    private static final String BASE_URL = "https://www.cultizm.com/kor/fta-items/";
    private static final int ITEMS_PER_PAGE = 36; // 페이지당 표시되는 상품 수

    public static int calculateTotalPages() {
        try {
            // 첫 페이지 로드
            Document firstPageDoc = fetchWithRetry(BASE_URL);

            // 총 아이템 수를 포함하는 태그 찾기
            Element totalItemsElement = firstPageDoc.selectFirst("p.emz-article-count");
            if (totalItemsElement == null) {
                System.out.println("총 아이템 정보를 찾을 수 없습니다.");
                return 0;
            }

            // 총 아이템 수 파싱
            int totalItems = Integer.parseInt(totalItemsElement.text().replaceAll("[^0-9]", ""));
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            System.out.println("총 아이템 수: " + totalItems);
            System.out.println("총 페이지 수: " + totalPages);
            return totalPages;

        } catch (IOException e) {
            System.out.println("카테고리 페이지를 로드하는 동안 오류 발생.");
            e.printStackTrace();
        }
        return 0;
    }

    // 페이지별 상품 URL 수집 메서드
    public static Set<String> collectProductURL(){
        int totalPages = calculateTotalPages();
        Set<String> productURL = new HashSet<>();
        CompletableFuture<Void>[] futures = new CompletableFuture[totalPages];

        for (int page = 1; page <= totalPages; page++) {
            int currentPage = page; // page 변수를 final처럼 사용하기 위해 복사
            futures[page - 1] = CompletableFuture.runAsync(() -> {
                String pageUrl = BASE_URL + "?p=" + currentPage;
                //                System.out.println("Scraping page: " + pageUrl);

                try {
                    // 페이지 로드
                    Document pageDoc = fetchWithRetry(pageUrl);

                    // 상품 링크 추출
                    Elements nameElments = pageDoc.select("a.product--title");
                    for (Element nameElement : nameElments) {
                        String productUrl = nameElement.absUrl("href");
                        synchronized (productURL) { // 여러 스레드에서 동시에 수정될 수 있기 때문에 synchronized 처리
                            productURL.add(productUrl);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error while fetching page " + currentPage);
                    e.printStackTrace();
                }
            });
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures).join();
        System.out.println("수집된 FTA제품 링크: " + productURL.size() + "개");
        return productURL;
    }

    // FTA 아이템 코드 추출
    public static Set<String> collectFTAItemCode(){
        Set<String> productURL = collectProductURL();

        Set<String> FTAItemCode = new HashSet<>();


        CompletableFuture<?>[] futures = new CompletableFuture[productURL.size()];
        int index = 0;
        for (String productUrl : productURL) {

            futures[index++] = CompletableFuture.runAsync(() -> {
                try {

                    Document productDoc = fetchWithRetry(productUrl);
                    Elements itemCodeElement = productDoc.select("span.entry--content.is--hidden");
                    String itemCode = itemCodeElement.text();
                    synchronized (FTAItemCode){
                        FTAItemCode.add(itemCode);
                    }


                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + productUrl);
                    e.printStackTrace();
                }
            });
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures).join();
        System.out.println("수집된 FTA제품 코드: " + FTAItemCode.size() + "개");
        System.out.println(FTAItemCode);
        return FTAItemCode;
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

    public static void main(String[] args) {
        GetFTADataService service = new GetFTADataService();
        collectFTAItemCode();
    }
}
