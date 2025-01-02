// 요소 선택
let $alarm = document.querySelector("#header_alarm");
let $profile = document.querySelector("#header_profile");

let $alarm_drop = document.querySelector("#header_alarm_dropdown");
let $profile_drop = document.querySelector("#header_profile_dropdown");
let $profile_drop2 = document.querySelector("#header_profile_dropdown2");
let $drop = document.querySelector(".dropdown");

let $searchInput = document.querySelector("#header_input");
let $searchResults = document.createElement("ul"); // 검색 결과 목록을 위한 <ul> 생성
$searchResults.id = "search_results";
$searchResults.className = "search-dropdown";
$searchInput.parentNode.appendChild($searchResults);
let $headerList = document.querySelectorAll(".header_list");

// 알람 드롭다운 이벤트
$alarm.addEventListener("click", () => {
    $drop.classList.remove("active");
    $alarm_drop.classList.toggle("active");
});

// 프로필 드롭다운 이벤트
$profile.addEventListener("click", () => {
    $alarm_drop.classList.remove("active");
    $drop.classList.toggle("active");
});

$searchInput.addEventListener("input", () => {
    let query = $searchInput.value.trim();

    if (query.length >= 2) {
        fetch(`/search?query=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                $searchResults.innerHTML = "";
                if (data.length > 0) {
                    data.forEach(product => {
                        let listItem = document.createElement("li");
                        listItem.className = "search-result-item";

                        let link = document.createElement("a");
                        link.href = `/product/${product.product_id}`;
                        link.textContent = product.name;

                        listItem.appendChild(link);
                        $searchResults.appendChild(listItem);
                    });
                } else {
                    $searchResults.innerHTML = "<li class='search-result-empty'>검색 결과가 없습니다.</li>";
                }
            })
            .catch(error => {
                console.error("Error fetching search results:", error);
                $searchResults.innerHTML = "<li class='search-result-error'>검색 중 오류가 발생했습니다.</li>";
            });
    } else {
        $searchResults.innerHTML = "";
    }
});

// 외부 클릭 시 검색 결과 숨기기
document.addEventListener("click", (e) => {
    if (!$searchInput.contains(e.target) && !$searchResults.contains(e.target)) {
        $searchResults.innerHTML = ""; // 검색 결과 숨기기
    }
});

document.addEventListener("DOMContentLoaded", loadNotification);
document.addEventListener("DOMContentLoaded", loadWishlistSize);

function loadWishlistSize(){
    $.ajax({
        url: "/wishlist/size",
        type: "GET",
        success: function(response){
            if(response.status === "fetch_success"){
                if(response.wishlistSize > 0){
                    $("#wishlist_size").text(response.wishlistSize);
                    $("#wishlist_small_icon").addClass("active");
                }
            }
        },
        error: function(xhr, status, error){
            console.log("Error", error);
        }
    });
}

function loadNotification(){
    $.ajax({
        url: "/notification",
        type: "GET",
        success: function(response){
            if(response.status === "session_invalid"){
                $("#header_alarm_dropdown").html("<li class='header_alarm_dropdown_list' style='text-decoration: none; cursor: auto'>로그인이 필요합니다.</li>");
                return;
            }
            if(response.status === "fetch_success"){
                console.log(response);
                if(!response.notificationList || response.notificationList.length === 0){
                    $("#header_alarm_dropdown").html("<li class='header_alarm_dropdown_list' style='text-decoration: none; cursor: auto'>알림이 없습니다.</li>");
                    return;
                }
                $("#alarm_small_icon").addClass("active");
                let notificationHtml = "";
                response.notificationList.forEach((notification) => {
                    notificationHtml += `
                        <li class="header_alarm_dropdown_list"
                        onclick="readNotification(this)"
                        data-notification-id="${notification.notificationId}"
                        data-product-id="${notification.productId}">
                            [${notification.productName}] 제품의 가격이 하락했습니다.<br>
                            이전 가격: ￦${notification.previousPrice.toLocaleString('ko-KR')}<br>
                            현재 가격: ￦${notification.currentPrice.toLocaleString('ko-KR')}<br>
                            ${notification.createdAt}
                        </li>
                    `;
                });
                $("#header_alarm_dropdown").html(notificationHtml);
            }
        },
        error: function(xhr, status, error){
            console.log("Error", error);
        }
    });
}

function readNotification(element){
    let notificationId = element.dataset.notificationId;
    let productId = element.dataset.productId;
    $.ajax({
        url: "/notification/read",
        type: "POST",
        data: {
            notificationId: notificationId
        },
        success: function(response){
            if(response.status === "update_success"){
                if(element){
                    element.remove();
                    checkNotification();
                }
                window.location.href = "/product/" + productId;
            }
        },
        error: function(xhr, status, error){
            console.log("Error:", error);
        }
    });
}

function checkNotification(){
    let notifications = document.querySelectorAll(".header_alarm_dropdown_list");
        if(!notifications || notifications.length === 0){
            $("#alarm_small_icon").removeClass("active");
            $("#header_alarm_dropdown").html("<li class='header_alarm_dropdown_list' style='text-decoration: none; cursor: auto'>알림이 없습니다.</li>");
            return;
        }
}


$headerList.forEach((list) => {
    list.addEventListener("click", () => {
        const listId = list.id;

        const anchorId = listId.replace('header_', '') + '_anchor'; // "header_all" -> "all_anchor"
        const anchor = document.getElementById(anchorId);

        if (anchor) {
            anchor.click(); // a 태그 클릭 이벤트 트리거
        } else {
            console.error(`Anchor tag with id "${anchorId}" not found.`);
        }
    })
});

// 장바구니 수량 업데이트 함수
function updateCartQuantity() {
    $.ajax({
        url: '/cart/quantity', // 장바구니 수량을 가져오는 API
        type: 'GET',
        success: function(response) {
            var quantity = response.quantity;

            // 수량에 따라 동그라미 표시 여부 결정
            if (quantity > 0) {
                $("#cart_quantity").text(quantity).show(); // 수량이 0 이상일 때 표시
            } else {
                $("#cart_quantity").hide(); // 수량이 0일 때 숨김
            }
        },
        error: function(error) {
            console.log("장바구니 수량 요청 실패:", error);
        }
    });
}

// 페이지 로드 시 장바구니 수량을 초기화
$(document).ready(function() {
    updateCartQuantity();
});

// Enter 키로 전체 검색 페이지 이동
$searchInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        const query = $searchInput.value.trim(); // 검색어 가져오기
        if (query.length >= 2) {
            // 검색어가 유효하면 페이지 이동
            const encodedQuery = encodeURIComponent(query); // 쿼리 파라미터로 변환
            console.log(encodedQuery)
            window.location.href = `/searchResultProduct?query=${encodedQuery}`;
        } else {
            // 검색어가 비어있거나 짧으면 알림 표시
            alert("검색어를 2글자 이상 입력해주세요!");
        }
    }
});