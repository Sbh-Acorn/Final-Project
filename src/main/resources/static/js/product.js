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