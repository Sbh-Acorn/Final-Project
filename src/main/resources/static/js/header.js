// 요소 선택
let $alarm = document.querySelector("#header_alarm");
let $profile = document.querySelector("#header_profile");
let $bucket = document.querySelector("#header_bucket");

let $alarm_drop = document.querySelector("#header_alarm_dropdown");
let $profile_drop = document.querySelector("#header_profile_dropdown");
let $profile_drop2 = document.querySelector("#header_profile_dropdown2");
let $drop = document.querySelector(".dropdown");

let $searchInput = document.querySelector("#header_input");
let $searchResults = document.createElement("ul"); // 검색 결과 목록을 위한 <ul> 생성
$searchResults.id = "search_results";
$searchResults.className = "search-dropdown";
$searchInput.parentNode.appendChild($searchResults);

// 알람 드롭다운 이벤트
$alarm.addEventListener("click", () => {
    $drop.classList.remove("active");
    $alarm_drop.classList.toggle("active");
});

// 프로필 드롭다운 이벤트
$profile.addEventListener("click", () => {
    $alarm_drop.classList.remove("active");
    $drop.classList.toggle("active");
});

// 검색창 이벤트: 입력 감지 및 AJAX 요청
$searchInput.addEventListener("input", () => {
    let query = $searchInput.value.trim();

    if (query.length >= 2) { // 2글자 이상일 때만 요청
        fetch(`/search?query=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) throw new Error("Network response was not ok");
                return response.json();
            })
            .then(data => {
                $searchResults.innerHTML = ""; // 이전 검색 결과 초기화
                if (data.length > 0) {
                    data.forEach(product => {
                        let listItem = document.createElement("li");
                        listItem.className = "search-result-item";

                        let link = document.createElement("a");
                        link.href = `/productDetail?name=${encodeURIComponent(product.name)}`;
                        link.textContent = product.name;

                        // 클릭 이벤트 추가
                        link.addEventListener("click", (e) => {
                            e.preventDefault();
                            window.location.href = link.href;
                        });

                        listItem.appendChild(link);
                        $searchResults.appendChild(listItem);
                    });
                } else {
                    $searchResults.innerHTML = "<li class='search-result-empty'>검색 결과가 없습니다.</li>";
                }
            })
            .catch(error => {
                console.error("Error fetching search results:", error);
                $searchResults.innerHTML = "<li class='search-result-error'>검색 중 오류가 발생했습니다.</li>";
            });
    } else {
        $searchResults.innerHTML = ""; // 검색어가 짧으면 초기화
    }
});

// 외부 클릭 시 검색 결과 숨기기
document.addEventListener("click", (e) => {
    if (!$searchInput.contains(e.target) && !$searchResults.contains(e.target)) {
        $searchResults.innerHTML = ""; // 검색 결과 숨기기
    }
});
