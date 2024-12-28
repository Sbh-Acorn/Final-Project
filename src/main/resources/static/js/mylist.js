
let $input = document.getElementById('coupon_input');

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


        // 페이지 로드 시 함수 호출
        totalItemPrice();
        OriginalTotalPrice();
        saleTotalPrice();
        calculateTotalPrice();
        formatPrices();



        // 쿠폰 적용 버튼 클릭 이벤트
        $('.apply_coupon_button').click(function () {
            coupon();
        });

        // 수량 변경 버튼 클릭 이벤트
        $('.count_wrap .count').click(function() {
            event.stopPropagation(); // 이벤트 전파 방지
            const action = $(this).hasClass('count_plus') ? 'plus' : 'minus'; // "+" 버튼은 'count_plus' 클래스, "-" 버튼은 'count_minus' 클래스
            const productId = $(this).data('product-id'); // 버튼에서 product-id 가져오기
            console.log(action, productId); // 디버깅
            changeQuantity(action, productId);
        });

        // 장바구니 항목 삭제 버튼 클릭 이벤트
        $('.close').click(function() {
            const productId = $(this).data('product-id');
            removeCartItem(productId);
        });

        // 쿠폰 적용 함수
        function coupon() {
//            alert('쿠폰버튼클릭');
            const discountPercent = parseFloat($('.coupon_input').val());

            if (isNaN(discountPercent) || discountPercent <= 0 || discountPercent > 100) {
                return;
            }

            $('.list').each(function () {
                const isExcludedVoucher = $(this).find('.coupon');

                if (isExcludedVoucher.text() === '* 쿠폰 적용 가능') {
                    const currentPriceText = $(this).find('.price').data('current-price');

                    if (currentPriceText) {
                        const currentPrice = parseFloat(currentPriceText.replace(/[^0-9.]/g, ''));

                        if (!isNaN(currentPrice)) {
                            const quantity = parseInt($(this).find('.count_num').text());
                            const discountedPrice = currentPrice * quantity * (1 - discountPercent / 100);
                            const formattedPrice = Math.round(discountedPrice);

                            // 할인된 가격을 표시
                            $(this).find('.price').text(`￦${formattedPrice.toLocaleString()}`);
                            // 총 가격 업데이트
                            saleTotalPrice();
                            calculateTotalPrice();
                        }
                    }
                }
            });
        }


        // 총 할인 가격 계산 함수
        function saleTotalPrice() {
            const saleTotalPriceElement = $('#total-sale_p');

            const originalTotalPriceText = $('#total_original_p').text().trim();
            const originalTotalPriceValue = parseFloat(originalTotalPriceText.replace(/[^0-9.]/g, ''));
            const originalTotalPrice = Math.round(originalTotalPriceValue);

            let currentTotalPrice = 0;
            $('.list').each(function() {
                const currentPriceText = $(this).find('.price').text().trim();
                const priceValue = parseFloat(currentPriceText.replace(/[^0-9.]/g, ''));
                const price = Math.round(priceValue);

                if (!isNaN(price)) {
                    currentTotalPrice += price;
                }
            });

            const saleTotalPrice = originalTotalPrice - currentTotalPrice;
            saleTotalPriceElement.text(saleTotalPrice.toLocaleString());
        }

        // 원래 총 가격 계산 함수
        function OriginalTotalPrice() {
            let originalTotal = 0;

            $('.list').each(function() {
                const originalPriceText = $(this).find('.original_price').text().trim();
                const priceText = $(this).find('.price').text().trim();
                const priceToUse = originalPriceText || priceText;
                const priceValue = parseFloat(priceToUse.replace(/[^0-9.]/g, ''));
                const price = Math.round(priceValue);

                if (!isNaN(price)) {
                    const quantity = parseInt($(this).find('.count_num').text());
                    originalTotal += price;
                }
            });

            $('#total_original_p').text(originalTotal.toLocaleString());
        }

        // 부가세 계산 함수
        function vat() {
            let currentTotalPrice = 0;
            $('.list').each(function() {
                const currentPriceText = $(this).find('.price').text().trim();
                const priceValue = parseFloat(currentPriceText.replace(/[^0-9.]/g, ''));
                const price = Math.round(priceValue);

                if (!isNaN(price)) {
                    currentTotalPrice += price;
                }
            });
            const vatAmount = 0.1; // 10% 부가세
            const vat = Math.round(vatAmount * currentTotalPrice);
            $('#total_VAT').text(vat.toLocaleString());
        }

        // 관세 계산 함수
        function tax() {
            let currentTotalPrice = 0;
            $('.list').each(function () {
                const isFta = $(this).find('.fta');

                if (isFta.text() === '* FTA 미적용') {
                    const currentPriceText = $(this).find('.price').text().trim();
                    const priceValue = parseFloat(currentPriceText.replace(/[^0-9.]/g, ''));
                    const price = Math.round(priceValue);

                    if (!isNaN(price)) {
                        currentTotalPrice += price;
                    }
                }
            });
            const taxAmount = 0.13; // 13% 관세
            const tax = Math.round(taxAmount * currentTotalPrice);
            $('#total_Tax').text(tax.toLocaleString());
        }

        function reset(selector){
        $(selector).text(0);
        }


        // 최종 금액 계산 함수
        function calculateTotalPrice() {
        let totalPrice = 0;
        let vatPrice = 0;
        let taxPrice = 0;

        // 원래가격 총합
        const originalTotalPriceText = $('#total_original_p').text().trim();
        const originalPriceValue = parseFloat(originalTotalPriceText.replace(/[^0-9.]/g, ''));
        const originalPrice = Math.round(originalPriceValue);

        // 할인가격 총합
        const saleTotalPriceText = $('#total-sale_p').text().trim();
        const salePriceValue = parseFloat(saleTotalPriceText.replace(/[^0-9.]/g, ''));
        const salePrice = Math.round(salePriceValue);

        // 배송비
        const shippingText = $('#shipping_p').text().trim();
        const shippingValue = parseFloat(shippingText.replace(/[^0-9.]/g, ''));
        const shipping = Math.round(shippingValue);

        // 총 상품 금액 - 총 할인 금액 계산
        const totalAfterDiscount = originalPrice - salePrice;

        console.log("taxBaseAmount : " + taxBaseAmount);

        // 총 상품 금액 - 총 할인 금액이 20만원을 초과하면 tax()와 vat() 함수 실행
        if (totalAfterDiscount > taxBaseAmount) {
            tax();
            vat();

            // 부가세 (total_VAT 요소에서 가져오기)
            const vatText = $('#total_VAT').text().trim();
            const vatValue = parseFloat(vatText.replace(/[^0-9.]/g, ''));
            vatPrice = Math.round(vatValue);

            // 관세 (total_Tax 요소에서 가져오기)
            const taxText = $('#total_Tax').text().trim();
            const taxValue = parseFloat(taxText.replace(/[^0-9.]/g, ''));
            taxPrice = Math.round(taxValue);
        }else{
            reset('#total_Tax');
            reset('#total_VAT');

        }

        // 최종 금액 = 원래가격 - 할인가격 + 배송비 + 부가세 + 관세
        totalPrice = originalPrice - salePrice + shipping + vatPrice + taxPrice;
        // 최종 금액 표시 (예시: #final_span에 최종 금액 표시)
        $('#final_span').text(totalPrice.toLocaleString());
    }

        // 가격 포맷팅 함수
        function formatPrices() {
            formatPriceElements('.price');
            formatPriceElements('.original_price');
        }

        // 가격 포맷팅 함수
        function formatPriceElements(selector) {
            $(selector).each(function() {
                const priceText = $(this).text().trim();
                const priceValue = parseFloat(priceText.replace(/[^0-9.]/g, ''));

                if (!isNaN(priceValue)) {
                    const formattedPrice = Math.round(priceValue).toLocaleString();
                    $(this).text(`￦${formattedPrice}`);
                }
            });
        }

        // 수량 변경 함수
        function changeQuantity(action, productId) {
            const quantityNum = $(`#count_num_${productId}`); // 수량을 표시하는 div 선택
            let currentQuantity = parseInt(quantityNum.text()); // 현재 수량 가져오기
            console.log("현재 수량" + currentQuantity);

            if (action === 'minus' && currentQuantity > 1) {
                quantityNum.text(currentQuantity - 1); // 수량 감소
            } else if (action === 'plus') {
                quantityNum.text(currentQuantity + 1); // 수량 증가
            }
            console.log("변경된 수량" + quantityNum.text());
            updateQuantity(productId, action); // 서버에 수량 업데이트 요청
        }

        // 수량에 따른 원래 가격, 현재 가격 계산 함수
        function totalItemPrice() {
            $('.list').each(function() {
                // 현재 가격 처리
                const currentPriceText = $(this).find('.price').data('current-price');
                if (currentPriceText) {
                    const currentPrice = parseFloat(currentPriceText.toString().replace(/[^0-9.]/g, ''));

                    if (!isNaN(currentPrice)) {
                        const quantity = parseInt($(this).find('.count_num').text());
                        const totalValue = currentPrice * quantity;

                        $(this).find('.price').text(`￦${totalValue.toLocaleString('ko-KR')}`);
                    }
                }

                // 원래 가격 처리
                const originalPriceText = $(this).find('.original_price').data('original-price');
                if (originalPriceText) {
                    const originalPrice = parseFloat(originalPriceText.toString().replace(/[^0-9.]/g, ''));

                    if (!isNaN(originalPrice)) {
                        const quantity = parseInt($(this).find('.count_num').text());
                        const totalValue = originalPrice * quantity;

                        $(this).find('.original_price').text(`￦${totalValue.toLocaleString()}`);
                    }
                } else {
                    $(this).find('.original_price').text('');
                }
            });
        }

        // 수량 업데이트 후 처리 함수
        function updateQuantity(productId, action) {
            $.ajax({
                url: 'view/updateQuantity',
                type: 'POST',
                data: { product_id: productId, action: action },
                success: function(response) {
                    totalItemPrice();
                    OriginalTotalPrice();
                    coupon();
                    saleTotalPrice();
                    calculateTotalPrice();
                    formatPrices();
//                    alert(response);
                },
                error: function(error) {
                    console.error('AJAX 요청 실패:', error);
//                    alert('장바구니 수량 업데이트 실패.');
                }
            });
        }

        // 장바구니 항목 삭제 함수
        function removeCartItem(productId) {
            $.ajax({
                url: 'view/remove',
                type: 'POST',
                data: { product_id: productId },
                success: function(response) {
                    $(`.close[data-product-id='${productId}']`).closest('.list').remove();
                    totalItemPrice();
                    OriginalTotalPrice();
                    coupon();
                    saleTotalPrice();
                    calculateTotalPrice();
                    formatPrices();
//                    alert(response);
                },
                error: function(error) {
                    console.error('장바구니 AJAX 요청 실패:', error);
//                    alert('장바구니 아이템 삭제 실패.');
                }
            });
        }

