package com.example.Caltizm.Service;

import com.example.Caltizm.DTO.ProductDTO;
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
public class GetProductDataService {


    private static final String BASE_URL = "https://www.cultizm.com/Supplier";
    private static final int ITEMS_PER_PAGE = 36; // 페이지당 표시되는 상품 수


    //    브랜드 url 수집하기
    public static Set<String> collectUrls() {
        Set<String> urls = new HashSet<>();

        try {
            Document doc = fetchWithRetry(BASE_URL);
            Elements urlElements = doc.select("a.supplier--item--link");

            for (Element urlElement : urlElements) {
                String url = urlElement.attr("href");
                urls.add(url);
            }
            System.out.println("수집된 브랜드 링크: " + urls.size() + "개");
        } catch (IOException e) {
            System.out.println("Error while fetching page " + BASE_URL);
            e.printStackTrace();
        }
        return urls;
    }

    //    각 url 아이템 수 수집하여 페이지 수 계산하기
    //    상품링크 수집
    public static Set<String> collectPageUrls() {
        Set<String> urls = collectUrls();
        Set<String> pageUrls = new HashSet<>();

        // CompletableFuture 배열 대신 List 사용
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String url : urls) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 페이지 로드
                    Document pageDoc = fetchWithRetry(url);

                    // 총 아이템 수 추출
                    Element totalItemsElement = pageDoc.selectFirst("p.emz-article-count");
                    if (totalItemsElement == null) {
                        System.out.println("총 아이템 정보를 찾을 수 없습니다.");
                    } else {
                        // 총 아이템 수 파싱
                        int totalItems = Integer.parseInt(totalItemsElement.text().replaceAll("[^0-9]", ""));
                        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

                        // 각 페이지 URL 생성
                        for (int page = 1; page <= totalPages; page++) {
                            String pageUrl = url + "?p=" + page;
                            synchronized (pageUrls) {  // 동기화된 접근으로 여러 스레드에서 수정되는 문제 방지
                                pageUrls.add(pageUrl);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + url);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("에러페이지: " + url);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 페이지 링크 : " + pageUrls.size() + "개");
        return pageUrls;
    }



    // 페이지별 상품 URL 수집 메서드
    public static Set<String> collectProductUrlsAsync() {
        Set<String> pageUrls = collectPageUrls();

        Set<String> productUrls = new HashSet<>();
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();
        for (String url : pageUrls) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 페이지 로드
                    Document pageDoc = fetchWithRetry(url);

                    // 상품 링크 추출
                    Elements links = pageDoc.select("a.product--title");
                    for (Element link : links) {
                        String productUrl = link.absUrl("href");
                        synchronized (productUrls) { // 여러 스레드에서 동시에 수정될 수 있기 때문에 synchronized 처리
                            productUrls.add(productUrl);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error while fetching page " + url);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 제품 링크: " + productUrls.size() + "개");
        return productUrls;
    }

    // 상품 세부 정보 수집 메서드 (비동기 처리)
    public static Set<ProductDTO> collectProductDetailsAsync() {
        Set<String> productUrls = collectProductUrlsAsync();

        Set<ProductDTO> products = new HashSet<>();

        // CompletableFuture 배열 대신 List 사용
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String productUrl : productUrls) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 상품 페이지 로드
                    Document productDoc = fetchWithRetry(productUrl);

                    // ProductDTO 객체 추출
                    ProductDTO product = extractProduct(productDoc);

                    // 동기화된 접근으로 products에 추가
                    synchronized (products) {
                        products.add(product);
                    }

                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + productUrl);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Error occurred with product page: " + productUrl);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 상품: " + products.size() + "개");

        return products;
    }


    // 데이터 추출 메서드
    public static ProductDTO extractProduct(Document productDoc) {

        //브랜드 추출
        Element brandElement = productDoc.selectFirst("a.product--supplier-link");
        String brand = brandElement.text();
        //아이템 코드 추출
        Element itemCodeElement = productDoc.selectFirst("span.entry--content.is--hidden");
        String product_id = itemCodeElement.text();
        //상품 이름 추출
        Element nameElement = productDoc.selectFirst("h1.product--title");
        String name = nameElement.text();
        // 현재 판매가 가격 추출
        Element currentPriceElement = productDoc.selectFirst("span.price--content");
        String current_price = currentPriceElement.ownText();
        current_price = current_price.replaceAll("[^0-9.,]", ""); // 숫자와 ',' 또는 '.'만 남김
        current_price = current_price.replace(",", ""); // 쉼표 제거
        double currentPriceValue = Double.parseDouble(current_price); // String -> double 변환

        // 이전 판매 가격 추출
        Element originalPriceElement = productDoc.selectFirst("span.price--line-through");
        String original_price = (originalPriceElement != null) ? originalPriceElement.text() : null;
        Double originalPriceValue = null;
        if (original_price != null) {
            original_price = original_price.replaceAll("[^0-9.,]", ""); // 숫자와 ',' 또는 '.'만 남김
            original_price = original_price.replace(",", ""); // 쉼표 제거
            originalPriceValue = Double.parseDouble(original_price); // String -> Double 변환
        }
        //상세 내용 추출
        Element detailElement = productDoc.selectFirst("div.product--description");
        String description = detailElement.text();
        // 카테고리1 추출
        Elements categoryElements = productDoc.select("span.breadcrumb--title[itemprop=name]");
        Element category1Element = categoryElements.get(0);
        String category1 = category1Element.text();
        // 카테고리2 추출
        Element category2Element = categoryElements.get(1);
        String category2 = (category2Element != null) ? category2Element.text() : null;
        // 카테고리3 추출
        Element category3Element = categoryElements.size() > 2 ? categoryElements.get(2) : null;
        String category3 = (category3Element != null) ? category3Element.text() : null;
        //이미지URL 추출
        Element imageElement = productDoc.selectFirst("span.image--element");
        String image_url = imageElement.attr("data-img-original");
        //쿠폰적용 여부
        // 모든 "span.entry--content" 요소를 선택
        Elements excludedVoucherElements = productDoc.select("span.entry--content");
        // 특정 메시지가 포함된 요소가 있는지 확인
        boolean is_excludedVoucher = excludedVoucherElements.stream()
                .anyMatch(element -> "This item is excluded from all vouchers".equals(element.text()));

        ProductDTO product = new ProductDTO(brand, product_id, image_url, name, originalPriceValue, currentPriceValue, description,
                category1, category2, category3, is_excludedVoucher);


        return product;

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

//        Set<ProductDTO> productDTOs = collectProductDetailsAsync();
//        System.out.println(productDTOs);
    }
}

