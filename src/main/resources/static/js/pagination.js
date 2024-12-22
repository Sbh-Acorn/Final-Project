let $list2 = document.querySelectorAll('.board_list');
let $left = document.querySelector("#left_wrap");
let $right = document.querySelector("#right_wrap");
let $pageWrap = document.querySelector(".page_wrap");

// 총 리스트 개수
let totalList = $list2.length;
// 한 페이지에 표시될 리스트의 개수
const MAX_LIST_LENGTH = 14;
// 총 페이지 개수
let totalPage = Math.ceil(totalList / MAX_LIST_LENGTH);
// 총 페이지 그룹 개수
let totalGroup = Math.ceil(totalPage / 5);

// 현재 페이지
let currentPage = 1;
let currentGroup = 1;

// 현재 페이지 표시
function renderList(page) {
    // 1 이면 0 으로 나옴
    let start = (page - 1) * MAX_LIST_LENGTH;
    // 1 이면 14 으로 나옴
    let last = Math.min(page * MAX_LIST_LENGTH, $list2.length);;

    for (let i = start; i < last; i++) {
        $list2[i].style.display = 'flex';
    }
}

// 이전 페이지 제거
function removeList(page) {
    // 1 이면 0 으로 나옴
    let start = (page - 1) * MAX_LIST_LENGTH;
    // 1 이면 14 으로 나옴
    let last = Math.min(page * MAX_LIST_LENGTH, $list2.length);;

    for (let i = start; i < last; i++) {
        $list2[i].style.display = 'none';
    }
}

// 페이지 로딩시 페이지 개수 생성
for (let i = 2; i <= totalPage; i++) {
    const newPage = document.createElement('li');
    newPage.className = 'page';
    newPage.textContent = i;

    $pageWrap.insertBefore(newPage, $right);
    groupCheck();
}

// 초기 페이지 설정
renderList(currentPage);

// 왼쪽 클릭시 1칸 이동
$left.addEventListener("click", () => {
    removeList(currentPage);
    removePageActive();
    currentPage--;
    groupCheck();
    if (currentPage === 0) currentPage = 1;
    renderList(currentPage);
    addPageActive();
});

// 오른쪽 클릭시 1칸 이동
$right.addEventListener("click", () => {
    removeList(currentPage);
    removePageActive();
    currentPage++;
    groupCheck();
    if (currentPage > totalPage) currentPage = totalPage;
    renderList(currentPage);
    addPageActive();
});

// 클릭된 요소로 이동
$pageWrap.addEventListener("click", (event) => {
    // 클릭된 요소가 .page 클래스인지 확인
    if (event.target.classList.contains("page")) {
        const pageContent = event.target.textContent.trim();

        if (pageContent) {
            const pageNumber = parseInt(pageContent, 10);
            removeList(currentPage);
            removePageActive();

            currentPage = pageNumber;
            renderList(currentPage);
            event.target.classList.add('active');
        }
    }
});

// active 효과 추가
function addPageActive() {
    let $page = document.querySelectorAll('.page');
    $page[currentPage].classList.add('active');
}

// active 효과 제거
function removePageActive(page) {
    let $page = document.querySelectorAll('.page');
    $page[currentPage].classList.remove('active');
}

// page 3, 17, 23
function groupCheck() {
    let start = (currentGroup - 1) * 5 + 1;
    let end = Math.min((currentGroup) * 5, totalPage);

    // 현재 페이지가 그룹 범위를 벗어났다면 그룹 변경
    if (currentPage < start) {
        currentGroup--;
        if (currentGroup < 1) currentGroup = 1;
    } else if (currentPage > end) {
        currentGroup++;
        if (currentGroup > totalGroup) currentGroup = totalGroup;
    }

    groupChange();
}

function groupChange() {
    let start = (currentGroup - 1) * 5 + 1;
    let end = Math.min((currentGroup) * 5, totalPage);

    // 모든 페이지 버튼 숨김
    const $pages = document.querySelectorAll(".page");
    $pages.forEach((page) => {
        const pageNumber = parseInt(page.textContent.trim(), 10);

        if (pageNumber >= start && pageNumber <= end) {
            page.style.display = "flex";
        } else {
            page.style.display = "none";
        }
    });

    // 좌우 이동 버튼은 항상 보이도록 유지
    $left.style.display = "flex";
    $right.style.display = "flex";
}