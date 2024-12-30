document.getElementById('plus_btn').addEventListener('click', function () {

    const Count = document.querySelectorAll('.address_bigbox').length;

    if (Count >= 3) {
        alert('더 이상 생성이 불가능합니다');
        return;
    }

    const newBigBox = document.createElement('div');
    newBigBox.classList.add('bigbox');
    newBigBox.classList.add('address_bigbox');
    newBigBox.id = `address_box_box_${Date.now()}`; // 고유 ID 생성

    // 2. 새로운 .bigbox 내부 콘텐츠 추가
    newBigBox.innerHTML = `
        <div id="address_box" class="box" onclick="execDaumPostcode(event)">
            <p class="input addressEnglishText">영문주소</p>
            <input type="text" name="address" class="addressEnglish" style="display: none;">
        </div>
        <div id="postcode_box" class="box" onclick="execDaumPostcode(event)">
            <p class="input postcodeText">우편번호</p>
            <input type="text" name="zip_code" class="postcode" style="display: none;">
        </div>
        <div id="detailed_address_box" class="box">
            <input type="text" name="detail" class="input detailAddress" placeholder="상세주소를 입력해주세요" required>
        </div>
        <div class="close_wrap">
            <img src="/img/close.svg" alt="닫기 버튼" class="close">
        </div>
    `;

    const parentElement = document.getElementById('plus_btn').parentNode;

    parentElement.insertBefore(newBigBox, document.getElementById('plus_btn'));

    const closeBtn = newBigBox.querySelector('.close');
    closeBtn.addEventListener('click', function () {
        newBigBox.remove();
    });
});

function validateForm(){
    let lastName = document.querySelector("#last_name").value;
    let firstName = document.querySelector("#first_name").value;
    let email = document.querySelector("#email").value;
    let phoneNumber = document.querySelector("#phone").value;
    let birthDate = document.querySelector("#birth").value;
    let pccc = document.querySelector("#pcc").value;

    let addressEnglishList = document.querySelectorAll(".addressEnglish");
    let postcodeList = document.querySelectorAll(".postcode");
    let detailAddressList = document.querySelectorAll(".detailAddress");

    if(lastName.includes(" ") || lastName.trim() === "" || firstName.includes(" ") || firstName.trim() === ""){
        alert("이름이 유효하지 않습니다.");
        return false;
    }

    if(!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}(\.[A-Za-z]{2,})*$/.test(email)){
        alert("이메일이 유효하지 않습니다.");
        return false;
    }

    if(!/^010\d{8}$/.test(phoneNumber)){
        alert("전화번호가 유효하지 않습니다.");
        return false;
    }

    if(!checkValidDate(birthDate)){
        alert("생년월일이 유효하지 않습니다.");
        return false;
    }

    if(addressEnglishList.length === 0 || postcodeList.length === 0 || detailAddressList.length === 0){
        alert("주소가 유효하지 않습니다.");
        return false;
    }

    for(let i=0; i<addressEnglishList.length; i++){
        if(addressEnglishList[i].value.trim() === ""){
            alert("주소가 유효하지 않습니다.");
            return false;
        }
    }

    for(let i=0; i<postcodeList.length; i++){
        if(postcodeList[i].value.trim() === ""){
            alert("주소가 유효하지 않습니다.");
            return false;
        }
    }

    for(let i=0; i<detailAddressList.length; i++){
        if(detailAddressList[i].value.trim() === ""){
            alert("주소가 유효하지 않습니다.");
            return false;
        }
    }

    if(pccc.trim() !== ""){
        // 개인통관고유부호 검사
        if(!/^P\d{12}$/.test(pccc)){
            alert("개인통관고유번호가 유효하지 않습니다.");
            return false;
        }
    }

    return true;
}

function checkValidDate(value) {
	var result = true;
	try {
	    var date = value.split("-");
	    var y = parseInt(date[0], 10),
	        m = parseInt(date[1], 10),
	        d = parseInt(date[2], 10);

	    var dateRegex = /^(?=\d)(?:(?:31(?!.(?:0?[2469]|11))|(?:30|29)(?!.0?2)|29(?=.0?2.(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))(?:\x20|$))|(?:2[0-8]|1\d|0?[1-9]))([-.\/])(?:1[012]|0?[1-9])\1(?:1[6-9]|[2-9]\d)?\d\d(?:(?=\x20\d)\x20|$))?(((0?[1-9]|1[012])(:[0-5]\d){0,2}(\x20[AP]M))|([01]\d|2[0-3])(:[0-5]\d){1,2})?$/;
	    result = dateRegex.test(d+'-'+m+'-'+y);
	} catch (err) {
		result = false;
	}
    return result;
}