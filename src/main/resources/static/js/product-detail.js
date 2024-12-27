let $wishlist = document.querySelector("#wishlist_btn");
let $bucket = document.querySelector("#bucket_btn");
let $count = document.querySelector("#count_wrap");

let $minus = document.querySelector("#count_minus");
let $plus = document.querySelector("#count_plus");
let $countNum = document.querySelector("#count_num");
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

        $("#bucket_btn").click(function() {
                    var productId = $(this).data("product-id");

                    console.log("상품 ID:", productId);

                    $.ajax({
                        url: '/cart/add',
                        type: 'POST',
                        data: {
                            product_id: productId
                        },
                        success: function(response) {
                            alert(response);
                        },
                        error: function(error) {
                            console.log("AJAX 요청 실패:", error);
                            alert("장바구니에 상품을 추가하는데 실패했습니다.");
                        }
                    });
                });


$bucket.addEventListener('click', () => {
    if ($count.style.display = 'none') {
        $count.style.display = 'flex';
        return;
    } else if ($count.style.display = 'flex') {
        // AJAX 코드 넣어주시면 됩니다.
        // 종료하면서 반드시 $count.style.display = 'none' 처리를 해주셔야 합니다.
    } else {
        return;
    }
});

$minus.addEventListener("click", () => {
    if (parseInt($countNum.textContent) === 1) return;
    else {
        $countNum.textContent = parseInt($countNum.textContent) - 1;
    }
});

$plus.addEventListener("click", () => {
    $countNum.textContent = parseInt($countNum.textContent) + 1;
});