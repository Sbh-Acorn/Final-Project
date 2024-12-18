let $wishlist = document.querySelector("#wishlist_btn");
let $bucket = document.querySelector("#bucket_btn");

const $productInfo = document.querySelector("#product-info");
const $dataCode = $productInfo.getAttribute("data-code");
const $userId = $productInfo.getAttribute("data-userid");

function sendData(action) {
    $.ajax({
        url: "/api/user-action",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            userid: $userId,   // 사용자 ID
            code: $dataCode,   // 상품 코드
            action: action    // "wishlist" 또는 "bucket"
        }),
        success: function(response) {
            console.log(`성공: ${action}에 추가되었습니다.`, response);
            alert(`상품이 ${action === "wishlist" ? "위시리스트" : "장바구니"}에 추가되었습니다.`);
        },
        error: function(error) {
            console.error(`오류: ${action} 처리 실패`, error);
        }
    });
}


        $("#bucket_btn").click(function() {
            // 상품 ID를 버튼의 data-product-id 속성에서 가져오기
            var productId = $(this).data("product-id");

            console.log("상품 ID:", productId);  // 디버깅을 위한 로그 추가

            $.ajax({
                url: '/cart/add',
                type: 'POST',
                data: {
                    product_id: productId  // 상품 ID만 전달
                },
                success: function(response) {
                    alert("장바구니에 상품이 추가되었습니다!");
                    // 필요에 따라 UI를 업데이트하거나 리다이렉트 할 수 있음
                },
                error: function(error) {
                    console.log("AJAX 요청 실패:", error);  // 에러 메시지 로그 출력
                    alert("장바구니에 상품을 추가하는데 실패했습니다.");
                }
            });
        });


        $("#wishlist_btn").click(function(){
            let productId = $(this).data("product-id");

            $.ajax({
                url: "/wishlist/add",
                type: "POST",
                data: {
                    productId: productId
                },
                success: function(response){
                    alert(response.message);
                },
                error: function(xhr, status, error){
                    console.log("Error:", error);
                }
            });
        });
    });

// 이벤트 핸들러 등록
//$wishlist.addEventListener("click", function() {
//    sendData("wishlist"); // 위시리스트에 추가
//});

//$bucket.addEventListener("click", function() {
//    sendCartData("bucket"); // 장바구니에 추가
//});