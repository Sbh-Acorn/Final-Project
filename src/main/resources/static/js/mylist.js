let $count = document.querySelectorAll(".count_wrap");
let $input = document.getElementById('coupon_input');

$count.forEach((item) => {
    let $minus = item.querySelector(".count_minus");
    let $plus = item.querySelector(".count_plus");
    let $countNum = item.querySelector(".count_num");

    $minus.addEventListener("click", () => {
        event.preventDefault();
        if (parseInt($countNum.textContent) === 1) return;
        else {
            $countNum.textContent = parseInt($countNum.textContent) - 1;
        }
    });

    $plus.addEventListener("click", () => {
        event.preventDefault();
        $countNum.textContent = parseInt($countNum.textContent) + 1;
    });
});


$input.addEventListener('keydown', (event) => {
    // 숫자 키, 백스페이스, 방향키만 허용
    if (
        !(
            (event.key >= '0' && event.key <= '9') || // 숫자 키
            event.key === 'Backspace' || // 백스페이스
            event.key === 'ArrowLeft' || // 왼쪽 화살표
            event.key === 'ArrowRight' || // 오른쪽 화살표
            event.key === 'Tab' // 탭 키
        )
    ) {
        event.preventDefault();
    }
});