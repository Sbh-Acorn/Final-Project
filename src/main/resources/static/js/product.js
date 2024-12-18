let $range = document.getElementById("range");
let $leftThumb = document.querySelector(".thumb-left");
let $rightThumb = document.querySelector(".thumb-right");
let $rangeTrack = document.querySelector(".range-track");

let leftValue = 25;
let rightValue = 75;

const $value1 = document.getElementById("value1");
const $value2 = document.getElementById("value2");

function updateValues() {
  $value1.textContent = Math.round(leftValue);
  $value2.textContent = Math.round(rightValue);
}

function updateUI() {
    $rangeTrack.style.left = `${leftValue}%`;
    $rangeTrack.style.width = `${rightValue - leftValue}%`;

    $leftThumb.style.left = `${leftValue}%`;
    $rightThumb.style.left = `${rightValue}%`;
}

$leftThumb.addEventListener("mousedown", () => {
    const onMouseMove = (event) => {
        const rangeRect = range.getBoundingClientRect();
        const newLeftValue = Math.max(0, Math.min(rightValue - 15, ((event.clientX - rangeRect.left) / rangeRect.width) * 100));
        leftValue = newLeftValue;
        updateUI();
        updateValues();
    };

    document.addEventListener("mousemove", onMouseMove);
    document.addEventListener("mouseup", () => {
        document.removeEventListener("mousemove", onMouseMove);
    }, { once: true });
});

$rightThumb.addEventListener("mousedown", () => {
    const onMouseMove = (event) => {
        const rangeRect = range.getBoundingClientRect();
        const newRightValue = Math.min(100, Math.max(leftValue + 15, ((event.clientX - rangeRect.left) / rangeRect.width) * 100));
        rightValue = newRightValue;
        updateUI();
        updateValues();
    };

    document.addEventListener("mousemove", onMouseMove);
    document.addEventListener("mouseup", () => {
        document.removeEventListener("mousemove", onMouseMove);
    }, { once: true });
});

updateUI();



document.addEventListener('DOMContentLoaded', function() {
        const selectedUl = document.getElementById("selected_ul");

        // 필터 체크박스를 모두 가져옵니다.
        const filterCheckboxes = document.querySelectorAll(".filter_checkbox input");

        filterCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const label = this.nextElementSibling; // p 태그 (브랜드나 카테고리 이름)
                const value = label.innerText; // 선택한 항목의 텍스트

                if (this.checked) {
                    // 체크한 항목을 selected_li에 추가
                    const listItem = document.createElement("li");
                    listItem.classList.add("selected_li");

                    // 텍스트와 close 버튼 추가
                    listItem.innerHTML = `
                        <p class="selected_li_txt">${value}</p>
                        <img src="/img/close.svg" alt="Close" class="selected_li_close">
                    `;

                    // close 버튼에 이벤트 리스너 추가 (항목 제거)
                    listItem.querySelector(".selected_li_close").addEventListener('click', function() {
                        selectedUl.removeChild(listItem);
                        checkbox.checked = false; // 체크박스를 해제합니다.
                    });

                    selectedUl.appendChild(listItem); // ul에 추가
                } else {
                    // 체크를 해제한 항목은 selected_li에서 제거
                    const items = selectedUl.querySelectorAll(".selected_li_txt");
                    items.forEach(item => {
                        if (item.innerText === value) {
                            selectedUl.removeChild(item.parentElement); // li 제거
                        }
                    });
                }
            });
        });
    });