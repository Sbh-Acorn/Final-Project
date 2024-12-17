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
}

function method2() {
    let $addr = document.querySelector("#addr_p");
    let $postcode = document.querySelector("#postcode_p");
    let $detailed = document.querySelector("#detailed_p");

    $addr.style.cursor = 'auto';
    $postcode.style.cursor = 'auto';
    $detailed.setAttribute("readonly", true);
}