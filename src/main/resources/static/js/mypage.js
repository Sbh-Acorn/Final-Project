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

    if(name.split(" ").length !== 2){
        alert("이름이 유효하지 않습니다.");
        window.location.reload();
        return;
    }

    if(!/^010\d{8}$/.test(phone)){
        alert("전화번호가 유효하지 않습니다.");
        window.location.reload();
        return;
    }

    let minDate = new Date("1925-01-01");
    let maxDate = new Date("2006-12-31");
    let userBirth = new Date(birth);
    if(userBirth < minDate || userBirth > maxDate){
        alert("생년월일이 유효하지 않습니다.");
        window.location.reload();
        return;
    }

    if(!checkValidDate(birth)){
        alert("생년월일이 유효하지 않습니다.");
        window.location.reload();
        return;
    }

    if(pcc.trim() !== ""){
        // 개인통관고유부호 검사
        if(!/^P\d{12}$/.test(pcc)){
            alert("개인통관고유번호가 유효하지 않습니다.");
            window.location.reload();
            return;
        }
    }

    let data = {
        email: email,
        name: name,
        phoneNumber: phone,
        birthDate: birth,
    };
    if(pcc.trim() !== ""){
        data.pccc = pcc;
    }

    $.ajax({
        type: "PATCH",
        url: "/updateUserInfo",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function(response){
            alert(response.message);
            if(response.status !== "update_success"){
                window.location.href = "/myPage";
            }
        },
        error: function(xhr, status, error){
            console.error("Error:", error);
        }
    });
}

document.querySelector("#pwd_btn").addEventListener("click", function(){
    let pwd = document.querySelector("#pwd").value;
    let pwdCheck = document.querySelector("#pwd_check").value;

    if(pwd.trim() === "" || pwdCheck.trim() === ""){
        alert("모든 필드를 채워주세요.");
        return;
    }

    let data = {
        newPassword1: pwd,
        newPassword2: pwdCheck
    };

    $.ajax({
        url: "/changePassword",
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