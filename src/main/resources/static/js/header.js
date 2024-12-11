let $alarm = document.querySelector("#header_alarm");
let $profile = document.querySelector("#header_profile");
let $bucket = document.querySelector("#header_bucket");

let $alarm_drop = document.querySelector("#header_alarm_dropdown");
let $profile_drop = document.querySelector("#header_profile_dropdown");

$alarm.addEventListener("click", () => {
    $profile_drop.classList.remove("active");
    $alarm_drop.classList.toggle("active");
});

$profile.addEventListener("click", () => {
    $alarm_drop.classList.remove("active");
    $profile_drop.classList.toggle("active");
});
