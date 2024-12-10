package com.example.Caltizm.Service;

import com.example.Caltizm.DTO.ProductDTO;
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
public class GetProductDataService {


    private static final String BASE_URL = "https://www.cultizm.com/latest/";
    private static final int ITEMS_PER_PAGE = 36; // 페이지당 표시되는 상품 수


    // 총 페이지 수를 계산하는 메서드
    public static int calculateTotalPages() {
        try {
            // 첫 페이지 로드
            Document firstPageDoc = Jsoup.connect(BASE_URL).get();

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
    public static Set<String> collectProductUrlsAsync() {
        int totalPages = calculateTotalPages();
        Set<String> productUrls = new HashSet<>();
        CompletableFuture<Void>[] futures = new CompletableFuture[totalPages];

        for (int page = 1; page <= totalPages; page++) {
            int currentPage = page; // page 변수를 final처럼 사용하기 위해 복사
            futures[page - 1] = CompletableFuture.runAsync(() -> {
                String pageUrl = BASE_URL + "?p=" + currentPage;
                System.out.println("Scraping page: " + pageUrl);

                try {
                    // 페이지 로드
                    Document pageDoc = Jsoup.connect(pageUrl).get();

                    // 상품 링크 추출
                    Elements links = pageDoc.select("a.product--title");
                    for (Element link : links) {
                        String productUrl = link.absUrl("href");
                        synchronized (productUrls) { // 여러 스레드에서 동시에 수정될 수 있기 때문에 synchronized 처리
                            productUrls.add(productUrl);
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
        System.out.println("수집된 제품 링크: " + productUrls.size() + "개");
        return productUrls;
    }

    // 상품 세부 정보 수집 메서드 (비동기 처리)
    public static Set<ProductDTO> collectProductDetailsAsync() {
        Set<String> productUrls = collectProductUrlsAsync();

        Set<ProductDTO> products = new HashSet<>();
        CompletableFuture<?>[] futures = new CompletableFuture[productUrls.size()];
        int index = 0;
        for (String productUrl : productUrls) {

            futures[index++] = CompletableFuture.runAsync(() -> {
                try {

                    Document productDoc = Jsoup.connect(productUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .get();


                    ProductDTO product = extractProduct(productDoc);
                    System.out.println(product);
                    synchronized (products) {
                        products.add(product);
                    }

                    //                    Thread.sleep(1000);  // 1초 대기 후 다음 요청
                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + productUrl);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("에러페이지: " + productUrl);
                    e.printStackTrace();
                }
            });
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures).join();
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
        String itemCode = itemCodeElement.text();
        //상품 이름 추출
        Element nameElement = productDoc.selectFirst("h1.product--title");
        String name = nameElement.text();
        //현재 판매가 가격 추출
        Element currentPriceElement = productDoc.selectFirst("span.price--content.content--default");
        String currentPrice = currentPriceElement.text();
        currentPrice = currentPrice.replace("*", "").trim();
        //이전 판매 가격 추출
        Element originalPriceElement = productDoc.selectFirst("span.price--line-through");
        String originalPrice = (originalPriceElement != null) ? originalPriceElement.text() : null;
        if (originalPrice != null) {
            originalPrice = originalPrice.replace("*", "").trim();
        }
        //상세 내용 추출
        Element detailElement = productDoc.selectFirst("div.product--description");
        String detail = detailElement.text();
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
        String imageUrl = imageElement.attr("data-img-original");
        //쿠폰적용 여부
        // 모든 "span.entry--content" 요소를 선택
        Elements excludedVoucherElements = productDoc.select("span.entry--content");
        // 특정 메시지가 포함된 요소가 있는지 확인
        boolean excludedVoucher = excludedVoucherElements.stream()
                .anyMatch(element -> "This item is excluded from all vouchers".equals(element.text()));

        ProductDTO product = new ProductDTO(brand, itemCode, name, currentPrice, originalPrice, detail, category1, category2, category3, imageUrl, false, excludedVoucher);

        return product;

    }
}
