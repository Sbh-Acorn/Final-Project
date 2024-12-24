let $list = document.querySelectorAll(".board_list");
let $listWrap = document.querySelector(".board_list_wrap");
let $text = document.querySelectorAll('.board_list_p');

$list.forEach((list) => {
    let $type = list.querySelector('.board_list_p');
    if ($type && $type.textContent.trim() === '공지') {
        list.style.backgroundColor = 'rgba(231, 231, 231, 0.6)';
    }
});

$text.forEach((text) => {
    if (text.textContent.length > 20) {
        text.textContent = text.textContent.substring(0, 20) + '...';
    }
});

$listWrap.addEventListener('click', (event) => {
    let $list = event.target.closest('.board_list');
    if ($list) {
        const $link = $list.querySelector('a');
        if ($link) $link.click();
    }
});

// 테스트용 함수. 편하게 지워주세요.
for (let i = 0; i < 84; i++) {
    let list = document.createElement('li');
    list.className = 'board_list';
    list.innerHTML = `
        <p class="board_list_p">자유</p>
        <p class="board_list_p">자유로운123</p>
        <p class="board_list_p">울라숑</p>
        <p class="board_list_p">12.03</p>
        <p class="board_list_p">177</p>
        <p class="board_list_p">0</p>
        <a href='#'></a>
    `;
    $listWrap.appendChild(list);
}