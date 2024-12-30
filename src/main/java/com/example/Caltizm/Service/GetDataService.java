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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        for (String url : baseUrls) {
            futureList.add(CompletableFuture.supplyAsync(() -> {
                Set<String> localBrandSet = new HashSet<>();
                try {
                    Document doc = fetchWithRetry(url);

                    Elements brandElements = doc.select(
                            "div.filter-panel.filter--multi-selection.filter-facet--value-list.facet--manufacturer label.filter-panel--label"
                    );
                    for (Element brandElement : brandElements) {
                        String brandName = brandElement.text();
                        String normalize = Normalizer.normalize(brandName, Normalizer.Form.NFD)
                                .replaceAll("\\p{M}", "-")
                                .replaceAll(" ", "-")
                                .replaceAll("[&’']", "-")
                                .replaceAll("([^a-zA-Z0-9\\s.-])", "")
                                .replaceAll("-{2,}", "-")
                                .replaceAll("-\\.$", ".")
                                .replaceAll("-$", "")
                                .toLowerCase();

                        localBrandSet.add(BASE_URL + normalize + "/");
                    }
                } catch (IOException e) {
                    System.out.println("Error fetching data for URL: " + url);
                    e.printStackTrace();
                }
                return localBrandSet;
            }).thenAccept(localBrandSet -> {
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

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        List<BrandDTO> sortedBrands = new ArrayList<>(brands);
        sortedBrands.sort((b1, b2) -> b2.getName().compareTo(b1.getName()));  // 내림차순 정렬
        System.out.println("수집된 브랜드 정보 : " + sortedBrands.size() + "개");
        return sortedBrands;
    }

    // 브랜드 데이터 추출 메서드
    public static BrandDTO extractBrand(Document brandDoc) {

        Elements nameElement = brandDoc.select("h1.panel--title.is--underline");
        String name = nameElement.text().replaceFirst("^Brand", "").trim();

        Elements imgElement = brandDoc.select("img.vendor--image");
        String logoImgURL = imgElement.attr("src");

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
                    Document productDoc = fetchWithRetry(url);
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

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 제품 링크: " + productUrls.size() + "개");
        return productUrls;
    }

    // 상품 세부 정보 수집 메서드
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

        Element brandElement = productDoc.selectFirst("a.product--supplier-link");
        String brand = brandElement.text();

        Element itemCodeElement = productDoc.selectFirst("span.entry--content.is--hidden");
        String product_id = itemCodeElement.text();

        Element nameElement = productDoc.selectFirst("h1.product--title");
        String name = nameElement.text();

        Element currentPriceElement = productDoc.selectFirst("span.price--content");
        String current_price = currentPriceElement.ownText();
        current_price = current_price.replaceAll("[^0-9.,]", "");
        current_price = current_price.replace(",", "");
        double currentPriceValue = Double.parseDouble(current_price);

        Element originalPriceElement = productDoc.selectFirst("span.price--line-through");
        String original_price = (originalPriceElement != null) ? originalPriceElement.text() : null;
        Double originalPriceValue = null;
        if (original_price != null) {
            original_price = original_price.replaceAll("[^0-9.,]", ""); // 숫자와 ',' 또는 '.'만 남김
            original_price = original_price.replace(",", ""); // 쉼표 제거
            originalPriceValue = Double.parseDouble(original_price); // String -> Double 변환
        } else{
        originalPriceValue = currentPriceValue;}


        Element detailElement = productDoc.selectFirst("div.product--description");
        String description = detailElement.text();

        Elements categoryElements = productDoc.select("span.breadcrumb--title[itemprop=name]");
        Element category1Element = categoryElements.get(0);
        String category1 = category1Element.text();

        Element category2Element = categoryElements.get(1);
        String category2 = (category2Element != null) ? category2Element.text() : null;

        Element category3Element = categoryElements.size() > 2 ? categoryElements.get(2) : null;
        String category3 = (category3Element != null) ? category3Element.text() : null;

        Element imageElement = productDoc.selectFirst("span.image--element");
        String image_url = imageElement.attr("data-img-original");

        Elements excludedVoucherElements = productDoc.select("span.entry--content");
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
            Document firstPageDoc = fetchWithRetry(FTA_URL);
            Element totalItemsElement = firstPageDoc.selectFirst("p.emz-article-count");

            if (totalItemsElement == null) {
                System.out.println("총 아이템 정보를 찾을 수 없습니다.");
                totalPages = 0;
            }

            int totalItems = Integer.parseInt(totalItemsElement.text().replaceAll("[^0-9]", ""));
            totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            System.out.println("총 FTA 아이템 수: " + totalItems);
            System.out.println("총 FTA 페이지 수: " + totalPages);


        } catch (IOException e) {
            System.out.println("카테고리 페이지를 로드하는 동안 오류 발생.");
            e.printStackTrace();
        }

        Set<String> FTAProductURL = ConcurrentHashMap.newKeySet();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            int currentPage = page; // page 변수를 final처럼 사용하기 위해 복사

            futureList.add(CompletableFuture.runAsync(() -> {
                String pageUrl = FTA_URL + "?p=" + currentPage;

                try {
                    Document pageDoc = fetchWithRetry(pageUrl);
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

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 FTA제품 링크: " + FTAProductURL.size() + "개");
        return FTAProductURL;
    }

    // FTA 아이템 코드 추출
    public static Set<String> collectFTAItemCode() {
        Set<String> FTAProductURL = collectFTAProductURL();
        Set<String> FTAItemCode = ConcurrentHashMap.newKeySet();
        List<CompletableFuture<Void>> futuresList = new ArrayList<>();

        for (String productUrl : FTAProductURL) {
            futuresList.add(CompletableFuture.runAsync(() -> {
                try {
                    Document productDoc = fetchWithRetry(productUrl);
                    Elements itemCodeElement = productDoc.select("span.entry--content.is--hidden");
                    String itemCode = itemCodeElement.text();
                    FTAItemCode.add(itemCode);
                } catch (IOException e) {
                    System.out.println("Error while fetching product page: " + productUrl);
                    e.printStackTrace();
                }
            }));
        }

        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
        System.out.println("수집된 FTA 제품 코드: " + FTAItemCode.size() + "개");

        return FTAItemCode;
    }


    public static List<String> collectBannerImage() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        List<String> imageUrls = new ArrayList<>();

        int retryCount = 3; // 최대 3번 재시도
        int attempt = 0;
        boolean success = false;

        while (attempt < retryCount && !success) {
            try {
                driver.get(BASE_URL);
                Thread.sleep(5000); // 페이지 로딩 대기
                List<WebElement> images = driver.findElements(By.cssSelector("div.emotion--row.row--1 img.banner-slider--image"));

                if (images.isEmpty()) {
                    throw new Exception("No banner images found.");
                }

                for (WebElement img : images) {
                    String srcset = img.getAttribute("srcset");
                    imageUrls.add(extract1920Image(srcset)); // 수정된 메서드 호출
                }

                success = true; // 작업 성공
            } catch (Exception e) {
                attempt++;
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt >= retryCount) {
                    System.out.println("Max retry attempts reached. Exiting...");
                } else {
                    try {
                        Thread.sleep(2000); // 재시도 전에 잠시 대기
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        driver.quit();
        return imageUrls;
    }


    private static String extract1920Image(String srcset) {
        // 정규식을 1920x720 이미지에 맞게 변경
        Pattern pattern = Pattern.compile("https?://[\\w./-]+1920x1920[\\w./-]* 1920w");
        Matcher matcher = pattern.matcher(srcset);

        if (matcher.find()) {
            return matcher.group().replace(" 1920w", "");
        }

        return null;
    }

    public static void saveImage(List<String> imageUrls, String directoryPath) {
        try {
            // 디렉토리 존재 여부 확인 및 생성
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                Files.createDirectories(Paths.get(directoryPath));
            } else {
                // 기존 파일 삭제
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                            System.out.println("Deleted old image: " + file.getName());
                        }
                    }
                }
            }

            // 새로운 이미지 저장
            int counter = 1; // 순서 번호
            for (String imageUrl : imageUrls) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String numberedFileName = counter + "_" + fileName; // 순서 번호 추가

                URL url = new URL(imageUrl);
                try (InputStream in = url.openStream();
                     FileOutputStream out = new FileOutputStream(new File(directoryPath, numberedFileName))) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    System.out.println("Saved image: " + numberedFileName);
                } catch (Exception e) {
                    System.err.println("Failed to save image: " + imageUrl);
                    e.printStackTrace();
                }
                counter++; // 순서 증가
            }
        } catch (Exception e) {
            System.err.println("Error creating directory or processing images:");
            e.printStackTrace();
        }
    }

    //에러페이지 재시도 로직
    public static Document fetchWithRetry(String url) throws IOException {
        int retries = 0;
        while (retries < 10) {
            try {
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000)
                        .get();
            } catch (IOException e) {
                retries++;
                System.out.println("Error fetching page (attempt " + retries + "): " + url);
                if (retries >= 10) {
                    throw new IOException("Failed to fetch page after " + retries + " attempts: " + url, e);
                }
                try {
                    Thread.sleep(2000);
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
