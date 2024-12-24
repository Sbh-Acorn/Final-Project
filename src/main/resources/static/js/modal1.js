let $btn = document.querySelector(".modal_btn");
let $modalBoxs = document.querySelector(".modal_boxs");
let $closeModal = document.querySelector("#close");

$closeModal.addEventListener("click", () => {
    document.querySelector("#modal1").style.display = 'none';
});

checkBox();

// 여기 삭제하시면 됩니다!!!
//$btn.addEventListener("click", () => {
//
//    const newLi = document.createElement("li");
//    newLi.className = "modal_box";
//    newLi.innerHTML = `
//        <form action="">
//            <div class="modal_box_title">
//                <p class="box_title_txt">주소록</p>
//                <div class="close_wrap">
//                    <img src="/img/close.svg" alt="" class="close">
//                </div>
//            </div>
//            <div class="modal_box_items">
//                <p class="modal_box_item">Geombae-ro, Guri-si, Gyeonggi-do, Republic of Korea, KoreaKoreaKoreaKoreaKoreaKoreaKorea</p>
//                <p class="modal_box_item">우편번호 - 0123456</p>
//                <p class="modal_box_item">홍길시 홍길동 홍길로123 홍길아파트 홍길홍길</p>
//            </div>
//        </form>
//    `;
//
//    $modalBoxs.appendChild(newLi);
//    checkBox();
//});
// 여기입니다!

function deleteBox() {
    document.body.addEventListener("click", (event) => {
        if (event.target.classList.contains("close")) {
            const $box = event.target.closest(".modal_box");
            event.preventDefault();

            if(confirm("삭제하시겠습니까?")){
                let addressId = $box.dataset.addressId;

                $.ajax({
                    url: "/address/delete/" + addressId,
                    type: "DELETE",
                    success: function(response){
                        alert(response.message);
                        if(response.status === "delete_success"){
                            if($box){
                                $box.remove();
                                document.querySelector(`.info_wrap[data-address-id="${addressId}"]`).remove();
                                checkBox();
                            }
                        }
                    },
                    error: function(xhr, status, error){}
                });
            }

//            if ($box) {
//                $box.remove();
//            }
        }
        checkBox();
    });
}

function checkBox() {
    let $boxs = document.querySelectorAll(".modal_box");
    let $close = document.querySelectorAll(".close");
    let boxLength = $boxs.length;


    if (boxLength === 1) {
        $close.forEach((close) => {
            close.style.display = 'none';
        })
    } else {
        $close.forEach((close) => {
            close.style.display = 'block';
        })
    }
    if (boxLength === 3) {
        $btn.style.display = 'none';
    } else {
        $btn.style.display = 'block';
    }
}

deleteBox();