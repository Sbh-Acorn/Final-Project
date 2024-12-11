package com.example.Caltizm.Service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public static Set<String> collectProductURL() {
        int totalPages = calculateTotalPages(); // 총 페이지 수 계산
        Set<String> productURL = new HashSet<>();

        // CompletableFuture를 저장할 List
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            int currentPage = page; // page 변수를 final처럼 사용하기 위해 복사

            // 비동기 작업을 CompletableFuture로 처리하고 List에 추가
            futures.add(CompletableFuture.runAsync(() -> {
                String pageUrl = BASE_URL + "?p=" + currentPage;

                try {
                    // 페이지 로드
                    Document pageDoc = fetchWithRetry(pageUrl);

                    // 상품 링크 추출
                    Elements nameElements = pageDoc.select("a.product--title");
                    for (Element nameElement : nameElements) {
                        String productUrl = nameElement.absUrl("href");

                        // 여러 스레드에서 동시에 수정될 수 있기 때문에 synchronized 처리
                        synchronized (productURL) {
                            productURL.add(productUrl);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error while fetching page " + currentPage);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        System.out.println("수집된 FTA제품 링크: " + productURL.size() + "개");
        return productURL;
    }

    // FTA 아이템 코드 추출
    public static Set<String> collectFTAItemCode() {
        Set<String> productURL = collectProductURL();
        Set<String> FTAItemCode = new HashSet<>();

        // CompletableFuture 배열 대신 List 사용
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String productUrl : productURL) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 상품 페이지 로드
                    Document productDoc = fetchWithRetry(productUrl);

                    // 아이템 코드 추출
                    Elements itemCodeElement = productDoc.select("span.entry--content.is--hidden");
                    String itemCode = itemCodeElement.text();

                    // 동기화된 접근으로 여러 스레드에서 수정되는 문제 방지
                    synchronized (FTAItemCode) {
                        FTAItemCode.add(itemCode);
                    }
                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + productUrl);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 FTA 제품 코드: " + FTAItemCode.size() + "개");

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

    public static void main(String[] args) {
        GetFTADataService service = new GetFTADataService();
        collectFTAItemCode();
    }
}
