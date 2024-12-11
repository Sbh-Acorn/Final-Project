let $banner = document.getElementById('banner');
let $prevBtn = document.getElementById('prev_btn_wrap');
let $nextBtn = document.getElementById('next_btn_wrap');
let $banners = document.querySelectorAll('.banner_img_wrap');
let $randomText = document.getElementById('random_text');

let bannerCount = $banners.length;

let currentIndex = 0;

const texts = [
    "Welcome to the First Slide",
    "Explore the Second Slide",
    "Here Comes the Third Slide",
    "Enjoy the Fourth Slide"
];

function moveBanner(index) {
    const offset = -index * 25;
    $banner.style.transform = `translateX(${offset}%)`;
    $banner.style.transition = 'transform 0.5s ease';

    $randomText.textContent = texts[index % bannerCount];
}

// 자동 이동 기능
let autoSlideInterval = setInterval(() => {
    currentIndex = (currentIndex + 1) % bannerCount;
    moveBanner(currentIndex);
}, 3000);

// 버튼 클릭 시 동작 및 자동 이동 초기화
function resetAutoSlide() {
    clearInterval(autoSlideInterval);
    autoSlideInterval = setInterval(() => {
        currentIndex = (currentIndex + 1) % bannerCount;
        moveBanner(currentIndex);
    }, 3000);
}

$banner.addEventListener("mouseenter", () => {
    clearInterval(autoSlideInterval);
});

$banner.addEventListener("mouseleave", () => {
    resetAutoSlide();
});

$prevBtn.addEventListener("click", () => {
    if (currentIndex == 0) {
        currentIndex = 3;
    } else {
        currentIndex = currentIndex - 1;
    }
    moveBanner(currentIndex);
    resetAutoSlide();
});

$nextBtn.addEventListener("click", () => {
    if (currentIndex == 3) {
        currentIndex = 0;
    } else {
        currentIndex = currentIndex + 1;
    }
    moveBanner(currentIndex);
    resetAutoSlide();
});