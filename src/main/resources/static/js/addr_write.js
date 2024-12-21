let $btn1 = document.querySelector("#btn1");

$btn1.addEventListener("click", () => {
    const btnText = $btn1.textContent.trim();

    if (btnText === "주소록 수정") {
        method1();
        $btn1.innerText = `주소록 저장`;
    } else if (btnText === "주소록 저장") {
        method2();
        $btn1.innerText = `주소록 수정`;
    }
})

function method1() {
    let $addr = document.querySelector("#addr_p");
    let $postcode = document.querySelector("#postcode_p");
    let $detailed = document.querySelector("#detailed_p");

    $addr.style.cursor = 'pointer';
    $postcode.style.cursor = 'pointer';
    $detailed.removeAttribute("readonly");

    $detailed.focus();

    $addr.addEventListener("click", execDaumPostcode);
    $postcode.addEventListener("click", execDaumPostcode);
}

function method2() {
    let $addr = document.querySelector("#addr_p");
    let $postcode = document.querySelector("#postcode_p");
    let $detailed = document.querySelector("#detailed_p");

    $addr.style.cursor = 'auto';
    $postcode.style.cursor = 'auto';
    $detailed.setAttribute("readonly", true);

    $addr.removeEventListener("click", execDaumPostcode);
    $postcode.removeEventListener("click", execDaumPostcode);
}

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }

            } else {
                document.getElementById("extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById("postcodeInput").value = data.zonecode;
            document.getElementById("postcode_p").innerHTML = data.zonecode;
            document.getElementById("addressEnglishInput").value = data.addressEnglish;
            document.getElementById("addr_p").innerHTML = data.addressEnglish;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("detailed_p").focus();
        }
    }).open();
}

$(document).ready(function(){
    $("#btn2").click(function(){
        let addressId = $("#addressId").val();
        let address = $("#addressEnglishInput").val();
        let zipCode = $("#postcodeInput").val();
        let detail = $("#detailed_p").val();
        console.log(addressId, address, zipCode, detail);

        if(addressId.trim() === "" || address.trim() === "" || zipCode.trim() === "" || detail.trim() == ""){
            alert("모든 필드를 채워주세요.");
            return;
        }

        let data = {
            addressId: addressId,
            address: address,
            zipCode: zipCode,
            detail: detail
        };

        $.ajax({
            url: "/address/update",
            type: "PATCH",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function(response){
                alert(response.message);
                if(response.status === "update_success"){
                    window.location.href = "/myPage";
                }
            },
            error: function(xhr, status, error){
                console.log("Error:", error);
            }
        });
    });
});