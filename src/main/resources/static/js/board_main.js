let $list = document.querySelectorAll(".board_list");
let $listWrap = document.querySelector(".board_list_wrap");
let $text = document.querySelectorAll('.board_list_p');

$list.forEach((list) => {
    let $type = list.querySelector('.board_list_p');
    if ($type && $type.textContent.trim() === '공지사항') {
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

