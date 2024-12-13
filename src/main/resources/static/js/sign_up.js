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
        <div id="address_box" class="box">
            <p class="input">영문주소</p>
            <input type="text" style="display: none;">
        </div>
        <div id="postcode_box" class="box">
            <p class="input">우편번호</p>
            <input type="text" style="display: none;">
        </div>
        <div id="detailed_address_box" class="box">
            <input type="text" class="input" placeholder="상세주소를 입력해주세요">
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