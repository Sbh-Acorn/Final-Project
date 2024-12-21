package com.example.Caltizm.Service;


import com.example.Caltizm.DTO.BrandDTO;
import com.example.Caltizm.DTO.ProductDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GetDataService {

    private static final String BASE_URL = "https://www.cultizm.com/kor/";
    private static final String BRAND_URL1 = "https://www.cultizm.com/kor/clothing/";
    private static final String BRAND_URL2 = "https://www.cultizm.com/kor/footwear/";
    private static final String BRAND_URL3 = "https://www.cultizm.com/kor/accessories/";
    private static final String FTA_URL = "https://www.cultizm.com/kor/fta-items/";
    private static final int ITEMS_PER_PAGE = 36; // 페이지당 표시되는 상품 수


    //브랜드 데이터 수집---------------------------------------------------------------------------------------------------

    //브랜드 링크 수집
    public static Set<String> collectBrandURL() {
        List<String> baseUrls = List.of(BRAND_URL1, BRAND_URL2, BRAND_URL3);

        Set<String> brand = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 각 URL을 비동기로 처리
        for (String url : baseUrls) {
            futureList.add(CompletableFuture.supplyAsync(() -> {
                Set<String> localBrandSet = new HashSet<>();
                try {
                    // fetchWithRetry로 Document 가져오기
                    Document doc = fetchWithRetry(url);

                    // 필요한 요소 추출
                    Elements brandElements = doc.select(
                            "div.filter-panel.filter--multi-selection.filter-facet--value-list.facet--manufacturer label.filter-panel--label"
                    );
                    for (Element brandElement : brandElements) {
                        // 이름 추출 및 공백을 하이픈으로 변환
                        String brandName = brandElement.text();
                        String normalize = Normalizer.normalize(brandName, Normalizer.Form.NFD)
                                .replaceAll("\\p{M}", "-")
                                .replaceAll(" ", "-")                 // 공백을 하이픈으로 변경
                                .replaceAll("[&’']", "-")             // &, ’, '를 하이픈으로 변경
                                .replaceAll("([^a-zA-Z0-9\\s.-])", "")// 알파벳, 숫자, 공백, 하이픈(-), 점(.) 외 문자 제거
                                .replaceAll("-{2,}", "-")             // 연속된 하이픈을 하나의 하이픈으로 축소
                                .replaceAll("-\\.$", ".")             // 하이픈과 점이 함께 있으면 하이픈 제거
                                .replaceAll("-$", "")                 // 문자열 끝에 하이픈이 있으면 제거
                                .toLowerCase();                       // 소문자로 변환


                        localBrandSet.add(BASE_URL + normalize + "/");
                    }
                } catch (IOException e) {
                    System.out.println("Error fetching data for URL: " + url);
                    e.printStackTrace();
                }
                return localBrandSet;
            }).thenAccept(localBrandSet -> {
                // 각 CompletableFuture의 결과를 brand Set에 추가
                brand.addAll(localBrandSet);
            }));
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 브랜드 링크 : " + brand.size() + "개");

        return brand;
    }

    //브랜드 정보 수집
    public static List<BrandDTO> collectBrandInfo() {
        Set<String> brandUrls = collectBrandURL();
        Set<BrandDTO> brands = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (String brandUrl : brandUrls) {
            futureList.add(CompletableFuture.runAsync(() -> {
                try {
                    Document doc = fetchWithRetry(brandUrl);

                    BrandDTO brand = extractBrand(doc);
                    brands.add(brand);
                } catch (IOException e) {
                    System.out.println("Error fetching data for URL: " + brandUrl);
                    e.printStackTrace();
                }
            }));
        }
        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        List<BrandDTO> sortedBrands = new ArrayList<>(brands);
        sortedBrands.sort((b1, b2) -> b2.getName().compareTo(b1.getName()));  // 내림차순 정렬
        System.out.println("수집된 브랜드 정보 : " + sortedBrands.size() + "개");
        return sortedBrands;
    }

    // 브랜드 데이터 추출 메서드
    public static BrandDTO extractBrand(Document brandDoc) {

        //이름 추출
        Elements nameElement = brandDoc.select("h1.panel--title.is--underline");
        String name = nameElement.text().replaceFirst("^Brand", "").trim();
        //로고 이미지 링크 추출
        Elements imgElement = brandDoc.select("img.vendor--image");
        String logoImgURL = imgElement.attr("src");
        //브랜드 소개글 추출
        Elements profileElement = brandDoc.select("div.vendor--text");
        String profile = profileElement.text();

        BrandDTO brand = new BrandDTO(name, logoImgURL, profile);

        return brand;
    }


    //상품 데이터 수집---------------------------------------------------------------------------------------------------

    // 브랜드 페이지 링크 수집
    public static Set<String> collectPageURL() {
        Set<String> brandUrls = collectBrandURL();
        Set<String> pageUrls = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (String brandUrl : brandUrls) {
            futureList.add(CompletableFuture.runAsync(() -> {
                try {
                    Document pageDoc = fetchWithRetry(brandUrl);
                    Element totalItemsElement = pageDoc.selectFirst("p.emz-article-count");
                    if (totalItemsElement != null) {
                        int totalItems = Integer.parseInt(totalItemsElement.text().replaceAll("[^0-9]", ""));
                        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
                        for (int page = 1; page <= totalPages; page++) {
                            pageUrls.add(brandUrl + "?p=" + page);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error fetching product page: " + brandUrl);
                    e.printStackTrace();
                }
            }));
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 페이지 링크 : " + pageUrls.size() + "개");
        return pageUrls;
    }

    // 페이지별 상품 URL 수집 메서드
    public static Set<String> collectProductURL() {
        Set<String> pageUrls = collectPageURL();

        Set<String> productUrls = ConcurrentHashMap.newKeySet();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (String url : pageUrls) {
            futureList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 페이지 로드
                    Document productDoc = fetchWithRetry(url);

                    // 상품 링크 추출
                    Elements links = productDoc.select("a.product--title");
                    for (Element link : links) {
                        String productUrl = link.absUrl("href");
                        productUrls.add(productUrl);

                    }
                } catch (IOException e) {
                    System.out.println("Error while fetching page " + url);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 제품 링크: " + productUrls.size() + "개");
        return productUrls;
    }

    // 상품 세부 정보 수집 메서드 (비동기 처리)
    public static Set<ProductDTO> collectProductInfo() {
        Set<String> productUrls = collectProductURL();
        Set<ProductDTO> products = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (String productUrl : productUrls) {
            futureList.add(CompletableFuture.runAsync(() -> {
                try {
                    Document productDoc = fetchWithRetry(productUrl);
                    ProductDTO product = extractProduct(productDoc);
                    products.add(product);
                } catch (IOException e) {
                    System.out.println("Error fetching product page: " + productUrl);
                    e.printStackTrace();
                }
            }));
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
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
        } else{
            originalPriceValue = currentPriceValue;
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


    //FTA상품 데이터 수집-------------------------------------------------------------------------------------------------

    //FTA 페이지 링크 수집
    public static Set<String> collectFTAProductURL() {
        int totalPages = 0;
        try {
            // 첫 페이지 로드
            Document firstPageDoc = fetchWithRetry(FTA_URL);

            // 총 아이템 수를 포함하는 태그 찾기
            Element totalItemsElement = firstPageDoc.selectFirst("p.emz-article-count");
            if (totalItemsElement == null) {
                System.out.println("총 아이템 정보를 찾을 수 없습니다.");
                totalPages = 0;
            }

            // 총 아이템 수 파싱
            int totalItems = Integer.parseInt(totalItemsElement.text().replaceAll("[^0-9]", ""));
            totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            System.out.println("총 FTA 아이템 수: " + totalItems);
            System.out.println("총 FTA 페이지 수: " + totalPages);


        } catch (IOException e) {
            System.out.println("카테고리 페이지를 로드하는 동안 오류 발생.");
            e.printStackTrace();
        }

        Set<String> FTAProductURL = ConcurrentHashMap.newKeySet();

        // CompletableFuture를 저장할 List
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            int currentPage = page; // page 변수를 final처럼 사용하기 위해 복사

            // 비동기 작업을 CompletableFuture로 처리하고 List에 추가
            futureList.add(CompletableFuture.runAsync(() -> {
                String pageUrl = FTA_URL + "?p=" + currentPage;

                try {
                    // 페이지 로드
                    Document pageDoc = fetchWithRetry(pageUrl);

                    // 상품 링크 추출
                    Elements nameElements = pageDoc.select("a.product--title");
                    for (Element nameElement : nameElements) {
                        String productUrl = nameElement.absUrl("href");
                        FTAProductURL.add(productUrl);

                    }

                } catch (IOException e) {
                    System.out.println("Error while fetching page " + currentPage);
                    e.printStackTrace();
                }
            }));
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        System.out.println("수집된 FTA제품 링크: " + FTAProductURL.size() + "개");
        return FTAProductURL;
    }

    // FTA 아이템 코드 추출
    public static Set<String> collectFTAItemCode() {
        Set<String> FTAProductURL = collectFTAProductURL();
        Set<String> FTAItemCode = ConcurrentHashMap.newKeySet();

        // CompletableFuture 배열 대신 List 사용
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String productUrl : FTAProductURL) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    // 상품 페이지 로드
                    Document productDoc = fetchWithRetry(productUrl);

                    // 아이템 코드 추출
                    Elements itemCodeElement = productDoc.select("span.entry--content.is--hidden");
                    String itemCode = itemCodeElement.text();

                    FTAItemCode.add(itemCode);

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


    //메인 배너 이미지 추출
    public static List<String> collectBannerImage() {
        // Selenium WebDriver 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless 모드로 실행 (브라우저 창을 띄우지 않음)
        WebDriver driver = new ChromeDriver(options);

        List<String> imageUrls = new ArrayList<>();  // 이미지 URL을 저장할 리스트

        try {
            // URL로 웹 페이지 열기
            driver.get(BASE_URL); // 실제 URL을 사용하세요

            // 페이지 로딩 대기 (동적 콘텐츠가 로드될 때까지 기다림)
            Thread.sleep(5000); // 5초 대기

            // div.emotion--row.row--1 내부에서 img.banner-slider--image 요소 찾기
            List<WebElement> images = driver.findElements(By.cssSelector("div.emotion--row.row--1 img.banner-slider--image"));

            // 각 이미지를 처리
            for (WebElement img : images) {
                String srcset = img.getAttribute("srcset");

                // 1920w 이미지만 추출하여 리스트에 추가
                imageUrls.add(extract1920wImage(srcset));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }

        return imageUrls;  // 추출된 1920w 이미지 URL 리스트 반환
    }

    private static String extract1920wImage(String srcset) {
        // 1920w 이미지를 추출하는 정규식 (1920w를 제외한 URL만 추출)
        Pattern pattern = Pattern.compile("https?://[\\w./-]+1920x1920[\\w./-]* 1920w");
        Matcher matcher = pattern.matcher(srcset);

        // 첫 번째 매칭된 결과를 찾고, 1920w를 제거하여 URL 반환
        if (matcher.find()) {
            return matcher.group().replace(" 1920w", "");  // 1920w 제거
        }

        return null;  // 1920w 이미지가 없으면 null 반환
    }



    //에러페이지 재시도 로직
    public static Document fetchWithRetry(String url) throws IOException {
        int retries = 0;
        while (retries < 10) {
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
//        GetDataService service = new GetDataService();
//        //        Set<String> products =  collectFTAItemCode();
//        //        System.out.println(products);
//        List<String> bannerImages = service.collectBannerImage();
//        System.out.println(bannerImages);

//      ㄹ
    }
}
