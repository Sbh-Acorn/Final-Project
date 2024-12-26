            const urlParams = new URLSearchParams(window.location.search);
                  let $range = document.getElementById("range");

            // URL에서 파라미터 추출
            const sortedBrands = urlParams.get('brands') ? urlParams.get('brands').split(',') : [];
            const sortedCategories = urlParams.get('categories') ? urlParams.get('categories').split(',') : [];
            const sortedMinPrice = urlParams.has('minPrice') && !isNaN(urlParams.get('minPrice')) ? parseFloat(urlParams.get('minPrice')) : 0;
            const sortedMaxPrice = urlParams.has('maxPrice') && !isNaN(urlParams.get('maxPrice')) ? parseFloat(urlParams.get('maxPrice')) : parseFloat($range.max);
            const sortedIsTax = urlParams.get('tax') && urlParams.get('tax') !== 'null' ? urlParams.get('tax') : null; // null 제외

            console.log(sortedMinPrice);
            console.log(sortedMaxPrice);

                // 브랜드 체크박스 상태 반영
                sortedBrands.forEach(brand => {
                    $('input[name="brands"][value="' + brand + '"]').prop('checked', true);
                });

                // 카테고리 체크박스 상태 반영
                sortedCategories.forEach(category => {
                    $('input[name="categories"][value="' + category + '"]').prop('checked', true);
                });

                // TAX 체크박스 상태 반영
                if (sortedIsTax === 'TAX') {
                    $('#tax').prop('checked', true);
                } else if (sortedIsTax === 'NOT TAX') {
                    $('#not_tax').prop('checked', true);
                } else {
                    $('#tax, #not_tax').prop('checked', false); // null인 경우, 체크해제
                }



// 필터 함수
    function filter() {
        const selectedBrands = [];
        const selectedCategories = [];
        const minPrice = parseFloat($('#value1').text().trim().replace(/[^0-9]/g, ''));
        const maxPrice = parseFloat($('#value2').text().trim().replace(/[^0-9]/g, ''));
        const isTax = $('#tax').prop('checked') ? 'TAX' : $('#not_tax').prop('checked') ? 'NOT TAX' : null;

        $('.filter_wrap ul.filter_checkbox input[type="checkbox"]:checked').each(function () {
            const label = $(this).closest('label').find('p').text();
            const parent = $(this).closest('.filter_wrap').prev('p.filter_txt').text().trim();

            if (parent === 'BRAND') {
                selectedBrands.push(label);
            } else if (parent === 'CATEGORY') {
                selectedCategories.push(label);
            }
        });

        const params = new URLSearchParams({
            brands: selectedBrands.join(','),
            categories: selectedCategories.join(','),
            minPrice: minPrice,
            maxPrice: maxPrice,
            tax: isTax,
        });

        window.location.href = `/product/filter?${params.toString()}`;
    }

    // 특정 value를 가진 li 삭제하는 함수
    function removeLiByValue(value) {
        const selectedUl = document.getElementById("selected_ul");
        const items = selectedUl.querySelectorAll(".selected_li_txt");
        items.forEach(item => {
            if (item.innerText === value) {
                selectedUl.removeChild(item.parentElement);  // item의 부모 요소(li)를 삭제
            }
        });
    }

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
    }



    formatPrices();

        $('#filter_btn').click(() => {
            filter();
        });



    // 체크박스 상태에 따라 li 초기화
    const selectedUl = document.getElementById("selected_ul");
    const filterCheckboxes = document.querySelectorAll(".filter_checkbox input");

    filterCheckboxes.forEach(checkbox => {
        if (checkbox.checked) {
            const label = checkbox.nextElementSibling;
            const value = label.innerText;

            // 이미 추가된 li가 없으면 새로 추가
            if (![...selectedUl.children].some(li => li.innerText.includes(value))) {
                const listItem = document.createElement("li");
                listItem.classList.add("selected_li");
                listItem.innerHTML = `
                    <p class="selected_li_txt">${value}</p>
                    <img src="/img/close.svg" alt="Close" class="selected_li_close">
                `;
                listItem.querySelector(".selected_li_close").addEventListener('click', function () {
                    selectedUl.removeChild(listItem);
                    checkbox.checked = false;
                });
                selectedUl.appendChild(listItem);
            }
        }

        // 체크박스 변경 이벤트 추가
        checkbox.addEventListener('change', function () {
            const label = this.nextElementSibling;
            const value = label.innerText;

            if (this.checked) {
                const listItem = document.createElement("li");
                listItem.classList.add("selected_li");
                listItem.innerHTML = `
                    <p class="selected_li_txt">${value}</p>
                    <img src="/img/close.svg" alt="Close" class="selected_li_close">
                `;
                listItem.querySelector(".selected_li_close").addEventListener('click', function () {
                    selectedUl.removeChild(listItem);
                    checkbox.checked = false;
                });
                selectedUl.appendChild(listItem);
            } else {
                const items = selectedUl.querySelectorAll(".selected_li_txt");
                items.forEach(item => {
                    if (item.innerText === value) {
                        selectedUl.removeChild(item.parentElement);
                    }
                });
            }
        });
    });

    $('#tax, #not_tax').change(function () {
        const label = $(this).closest('label').text().trim(); // input의 부모 label을 찾아 텍스트 가져오기
        const value = label ? label : null; // 텍스트가 없으면 null로 설정

        if (value) { // value가 유효한 경우에만 처리
            if (this.checked) {
                // 이미 추가된 li가 있는지 확인
                const exists = [...selectedUl.children].some(li => li.innerText.includes(value));
                if (!exists) {
                    const listItem = document.createElement("li");
                    listItem.classList.add("selected_li");
                    listItem.innerHTML = `
                        <p class="selected_li_txt">${value}</p>
                        <img src="/img/close.svg" alt="Close" class="selected_li_close">
                    `;
                    listItem.querySelector(".selected_li_close").addEventListener('click', function () {
                        selectedUl.removeChild(listItem);
                        $(`#${$(this).data('name')}`).prop('checked', false);
                    });
                    selectedUl.appendChild(listItem);
                }

                // 반대 체크박스 해제 및 해당 li 제거
                if (this.id === 'tax') {
                    $('#not_tax').prop('checked', false);
                    removeLiByValue('NOT TAX'); // 반대 항목 li 제거
                } else if (this.id === 'not_tax') {
                    $('#tax').prop('checked', false);
                    removeLiByValue('TAX'); // 반대 항목 li 제거
                }
            } else {
                // 체크 해제 시 해당 li 삭제
                removeLiByValue(value);
            }
        }
    });


        let $leftThumb = document.querySelector(".thumb-left");
        let $rightThumb = document.querySelector(".thumb-right");
        let $rangeTrack = document.querySelector(".range-track");

        // 조건에 따라 leftValue 설정
        let leftValue =
            sortedMinPrice === 0 ? 0: (sortedMinPrice / parseFloat($range.max)) * 100;

        let rightValue =
            sortedMaxPrice === parseFloat($range.max) ? 100 :(sortedMaxPrice / parseFloat($range.max)) * 100 ; // 최대값은 기본적으로 100%


        const $value1 = document.getElementById("value1");
        const $value2 = document.getElementById("value2");
        const minDifference = 500000; // 50만원

        function updateValues() {
        // 최소값 출력: 10,000 단위로 반올림
        const minValue = Math.round((leftValue / 100) * parseFloat($range.max) / 10000) * 10000;

        // 최대값 출력
        const maxValue = rightValue === 100
            ? parseFloat($range.max)
            : Math.round((rightValue / 100) * parseFloat($range.max) / 10000) * 10000;

        // 값 표시
        $value1.textContent = minValue.toLocaleString();
        $value2.textContent = maxValue.toLocaleString();
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
                const newLeftValue = Math.max(
                    0,
                    Math.min(
                        rightValue - ((minDifference / parseFloat($range.max)) * 100),
                        ((event.clientX - rangeRect.left) / rangeRect.width) * 100
                    )
                );
                leftValue = newLeftValue;
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
                const rangeRect = range.getBoundingClientRect();
                const newRightValue = Math.min(
                    100,
                    Math.max(
                        leftValue + ((minDifference / parseFloat($range.max)) * 100),
                        ((event.clientX - rangeRect.left) / rangeRect.width) * 100
                    )
                );
                rightValue = newRightValue;
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

        // 초기화
        updateUI();
        updateValues();