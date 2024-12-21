let $range = document.getElementById("range");
let $leftThumb = document.querySelector(".thumb-left");
let $rightThumb = document.querySelector(".thumb-right");
let $rangeTrack = document.querySelector(".range-track");

const $value1 = document.getElementById("value1");
const $value2 = document.getElementById("value2");

// 최소값과 최대값 초기화
let leftValue = 0; // 최소값
let rightValue = parseFloat($value2.textContent.trim()); // 최대값

// 범위 변경 단위 및 최소 간격 설정
const STEP = 10000;
const MIN_GAP = 500000; // 최소 간격

function roundToStep(value, maxValue) {
  let roundedValue = Math.round(value / STEP) * STEP;
  return Math.min(roundedValue, maxValue); // 최대값 초과 방지
}

function updateValues() {
  // leftValue는 STEP 크기 배수로 반올림하고, rightValue는 최대값에 도달할 때만 반올림
  $value1.textContent = roundToStep(leftValue, rightValue).toLocaleString();

  // rightValue가 최대값에 도달했을 때만 반올림
  const formattedRightValue = (rightValue === parseFloat($range.max)) ? rightValue.toLocaleString() : rightValue.toLocaleString();
  $value2.textContent = formattedRightValue;
}

function updateUI() {
  const rangeMin = 0;
  const rangeMax = parseFloat($range.max);

  $rangeTrack.style.left = `${((leftValue - rangeMin) / (rangeMax - rangeMin)) * 100}%`;
  $rangeTrack.style.width = `${((rightValue - leftValue) / (rangeMax - rangeMin)) * 100}%`;

  $leftThumb.style.left = `${((leftValue - rangeMin) / (rangeMax - rangeMin)) * 100}%`;
  $rightThumb.style.left = `${((rightValue - rangeMin) / (rangeMax - rangeMin)) * 100}%`;
}

// 왼쪽 thumb 이벤트 처리 추가
$leftThumb.addEventListener("mousedown", () => {
  const onMouseMove = (event) => {
    const rangeRect = $range.getBoundingClientRect();
    const rangeMin = 0;
    const rangeMax = parseFloat($range.max);

    // 왼쪽 thumb의 이동 범위는 오른쪽 thumb의 위치를 넘지 않도록 제한
    const newLeftValue = Math.min(
      rightValue - MIN_GAP, // 오른쪽 thumb을 넘지 않도록 설정
      Math.max(
        rangeMin,
        rangeMin +
          (((event.clientX - rangeRect.left) / rangeRect.width) * (rangeMax - rangeMin))
      )
    );

    // 왼쪽 thumb 이동 값 설정
    leftValue = roundToStep(newLeftValue, rightValue);
    updateUI();
    updateValues();
  };

  document.addEventListener("mousemove", onMouseMove);
  document.addEventListener(
    "mouseup",
    () => {
      document.removeEventListener("mousemove", onMouseMove);
    },
    { once: true }
  );
});


$rightThumb.addEventListener("mousedown", () => {
  const onMouseMove = (event) => {
    const rangeRect = $range.getBoundingClientRect();
    const rangeMin = 0;
    const rangeMax = parseFloat($range.max);

    const newRightValue = Math.min(
      rangeMax,
      Math.max(
        leftValue + MIN_GAP,
        rangeMin +
          (((event.clientX - rangeRect.left) / rangeRect.width) * (rangeMax - rangeMin))
      )
    );

    // 오른쪽 thumb가 최대값에 도달했을 때만 반올림을 적용
    rightValue = (newRightValue === rangeMax) ? rangeMax : roundToStep(newRightValue, rangeMax);
    updateUI();
    updateValues();
  };

  document.addEventListener("mousemove", onMouseMove);
  document.addEventListener(
    "mouseup",
    () => {
      document.removeEventListener("mousemove", onMouseMove);
    },
    { once: true }
  );
});


updateUI();





$(document).ready(function () {

    formatPrices();

    // FTA와 TAX 체크 제한
    $('#tax, #not_tax').change(function () {
        if ($(this).is('#tax')) {
            $('#not_tax').prop('checked', false);
        } else {
            $('#tax').prop('checked', false);
        }
    });

    $('#fta, #not_fta').change(function () {
        if ($(this).is('#fta')) {
            $('#not_fta').prop('checked', false);
        } else {
            $('#fta').prop('checked', false);
        }
    });

    $('#filter_btn').click(() => {
        filter();
    });

    const selectedUl = document.getElementById("selected_ul");

    const filterCheckboxes = document.querySelectorAll(".filter_checkbox input");

    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const label = this.nextElementSibling;
            const value = label.innerText;

            if (this.checked) {
                // 추가
                const listItem = document.createElement("li");
                listItem.classList.add("selected_li");

                listItem.innerHTML = `
                    <p class="selected_li_txt">${value}</p>
                    <img src="/img/close.svg" alt="Close" class="selected_li_close">
                `;

                // 삭제 버튼 클릭 시 항목 제거
                listItem.querySelector(".selected_li_close").addEventListener('click', function () {
                    selectedUl.removeChild(listItem);
                    checkbox.checked = false;
                });

                selectedUl.appendChild(listItem);
            } else {
                // 체크 해제 시 항목 제거
                const items = selectedUl.querySelectorAll(".selected_li_txt");
                items.forEach(item => {
                    if (item.innerText === value) {
                        selectedUl.removeChild(item.parentElement);
                    }
                });
            }
        });
    });
});

    function formatPrices() {
        // .current_price 요소 포맷
        $(".current_price").each(function() {
            var priceText = $(this).text().trim();
            var priceNumber = parseFloat(priceText.replace(/[^0-9.]/g, ''));

            if (!isNaN(priceNumber)) {
                var formattedPrice = Math.round(priceNumber).toLocaleString();
                $(this).text("￦" + formattedPrice);
            }
        });

        // .original_price 요소 포맷
        $(".original_price").each(function() {
            var priceText = $(this).text().trim();
            var priceNumber = parseFloat(priceText.replace(/[^0-9.]/g, ''));

            if (!isNaN(priceNumber)) {
                var formattedPrice = Math.round(priceNumber).toLocaleString();
                $(this).text(" (￦" + formattedPrice + ")");
            }
        });

        // #value2 (max_price_in_won) 포맷
        var value2 = $("#value2").text().trim();
        var value2Number = parseFloat(value2.replace(/[^0-9.]/g, ''));
        if (!isNaN(value2Number)) {
            var formattedValue2 = Math.round(value2Number).toLocaleString();
            $("#value2").text(formattedValue2);
        }
    }


 function filter() {
     const selectedBrands = [];
     const selectedCategories = [];
     const minPriceText = $('#value1').text().trim(); // 최저가격
     let minPrice = parseFloat(minPriceText.replace(/[^0-9]/g, ''));
     const maxPriceText = $('#value2').text().trim(); // 최고가격
     let maxPrice = parseFloat(maxPriceText.replace(/[^0-9]/g, ''));
     let isTax = $('#tax').prop('checked'); // TAX 체크 여부
     let isNotTax = $('#not_tax').prop('checked'); // NOT TAX 체크 여부
     let isFta = $('#fta').prop('checked'); // FTA 체크 여부
     let isNotFta = $('#not_fta').prop('checked'); // NOT FTA 체크 여부

     // 체크된 브랜드 수집
     $('.filter_wrap ul.filter_checkbox input[type="checkbox"]:checked').each(function () {
         const label = $(this).closest('label').find('p').text(); // label 내부의 텍스트 가져오기
         const parent = $(this).closest('.filter_wrap').prev('p.filter_txt').text().trim(); // 상위 텍스트

         // 브랜드와 카테고리를 구분해서 배열에 추가
         if (parent === 'BRAND') {
             selectedBrands.push(label);
         } else if (parent === 'CATEGORY') {
             selectedCategories.push(label);
         }
     });

     // JSON 데이터 생성
     const filterData = {
         brands: selectedBrands,
         categories: selectedCategories,
         price: {
             min: minPrice,
             max: maxPrice
         },
         tax: isTax ? 'TAX' : isNotTax ? 'NOT TAX' : null, // TAX 여부
         fta: isFta ? 'FTA' : isNotFta ? 'NOT FTA' : null  // FTA 여부
     };

        console.log(filterData);
     // AJAX로 서버에 필터 데이터 전송
//     $.ajax({
//         url: '/product/filter',
//         type: 'POST',
//         contentType: 'application/json',
//         data: JSON.stringify(filterData),
//         success: function(response) {
//             // 필터링된 데이터를 받아서 페이지 갱신
//             const productsContainer = $('#item_box_wrap');
//             productsContainer.empty();  // 기존 제품 목록 비우기
//
//             // 필터링된 제품 목록을 업데이트
//             response.forEach(product => {
//                 productsContainer.append(`
//                     <li class="item_box">
//                         <a href="/product/${product.product_id}">
//                             <img src="${product.image_url}" alt="Image" class="item_img">
//                             <p class="item_brand">${product.brand}</p>
//                             <p class="item_name">${product.name}</p>
//                             <p class="item_price">
//                                 <span class="current_price">${product.current_price}</span>
//                                 ${product.original_price ? `<span class="original_price">${product.original_price}</span>` : ''}
//                             </p>
//                         </a>
//                     </li>
//                 `);
//             });
//         },
//         error: function(xhr, status, error) {
//             console.error("필터링 요청 오류:", error);
//         }
//     });
 }




