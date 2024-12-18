const $tabs = document.querySelectorAll(".tab");
const tabLength = $tabs.length;

let $info = document.querySelector("#info_box");
let $pwd = document.querySelector("#passwd_box");
let $board = document.querySelector("#board_box");

let $update = document.querySelector("#update_btn");
let $address = document.querySelector("#address_btn");

const $tabWrap = document.getElementById("tab_wrap");
const $addressInfoWrap = document.getElementById("address_info_wrap");

$update.addEventListener("click", () => {

    const $infoInput = document.querySelectorAll(".info_input");
    const btnText = $update.textContent.trim();

    if (btnText === '정보일괄수정') {

        $tabWrap.style.pointerEvents = 'none';
        $addressInfoWrap.style.pointerEvents = 'none';


        $infoInput.forEach(element => {

            element.addEventListener('focus', () => {
                element.style.outline = '1px solid black';
            });
            element.addEventListener('blur', () => {
                element.style.outline = 'none';
            });

            element.removeAttribute("readonly");
            element.style.outline = 'none';
            $update.innerText = "정보수정완료";
        });
        $infoInput[0].focus();
    } else {
        if (checkNum()) {
            sendRequest();

            $tabWrap.style.pointerEvents = 'auto';
            $addressInfoWrap.style.pointerEvents = 'auto';

            $infoInput.forEach(element => {

                element.addEventListener('focus', () => {
                    element.style.outline = '0';
                });

                element.setAttribute("readonly", true);
                element.style.outline = '0';
                $update.innerText = "정보일괄수정";
            });
        }
    }
});

$address.addEventListener("click", () => {
    document.querySelector("#modal1").style.display = 'flex';
});

for (let i = 0; i < tabLength; i++) {
    $tabs[i].addEventListener("click", () => {
        $tabs[i].style.borderTop = '1px solid rgba(148, 147, 147, 0.4)';
        $tabs[i].style.borderLeft = '1px solid rgba(148, 147, 147, 0.4)';
        $tabs[i].style.borderRight = '1px solid rgba(148, 147, 147, 0.4)';
        $tabs[i].style.borderBottom = '1px solid white';
        for (let j = 0; j < tabLength; j++) {
            if (j === i) continue;
            else $tabs[j].style.border = '0px';
        }

        switch(i) {
            case 0:
                $info.style.display = 'block';
                $pwd.style.display = 'none';
                $board.style.display = 'none';
                break;
            case 1:
                $info.style.display = 'none';
                $pwd.style.display = 'block';
                $board.style.display = 'none';
                break;
            case 2:
                $info.style.display = 'none';
                $pwd.style.display = 'none';
                $board.style.display = 'block';
                break;
            default:
                break;
        }
    });
}

function checkNum() {

    const $infoInput = document.querySelectorAll(".info_input");

    for (let i = 0; i < 3; i++) {
        if ($infoInput[i].value.trim() === '') {
            $infoInput[i].focus();
            return false;
        };
    }
    return true;
}

function sendRequest(){
    let email = document.querySelector("#email").value;
    let name = document.querySelector("#name").value;
    let phone = document.querySelector("#phone").value;
    let birth = document.querySelector("#birth").value;
    let pcc = document.querySelector("#pcc").value;

    let data = {
        email: email,
        name: name,
        phoneNumber: phone,
        birthDate: birth,
        pccc: pcc
    };

    $.ajax({
        type: "POST",
        url: "/updateUserInfo",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function(response){
            alert(response.message);
        },
        error: function(xhr, status, error){
            console.error("Error:", error);
        }
    });
}