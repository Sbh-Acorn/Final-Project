let $rec = document.querySelectorAll('.box_item');
let $navList = document.querySelectorAll('.nav_list');

$rec.forEach((text) => {
    if (text.textContent.length > 18) {
        text.textContent = text.textContent.substring(0, 18) + '...';
    }
});

$navList.forEach((list) => {
    list.addEventListener('click', (event) => {
        let $activeList = document.querySelector('.nav_list.active');
        $activeList?.classList.remove('active');
        if (event.currentTarget === list) {
            list.classList.add('active');
        }
    })
});

// right 박스의 위치를 조정하기 위해 main태그의 높이가 중요한 상황
// 이를 위해 main태그의 높이를 동적으로 조정하기 위해 해당 코드를 사용
window.addEventListener('load', () => {
    const textBox = document.querySelector('.text_box');
    const main = document.querySelector('main');

    if (textBox && main) {
        const newHeight = textBox.offsetHeight - 300;  // .text_box의 실제 높이에서 300을 뺀 값
        main.style.height = `${newHeight}px`;
    }
});