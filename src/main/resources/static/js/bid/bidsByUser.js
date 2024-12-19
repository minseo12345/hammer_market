document.addEventListener("DOMContentLoaded", function() {
    // .container 요소에서 data-user-id 값을 가져오기
    const container = document.querySelector(".container");
    const userId = container.getAttribute("data-user-id"); // data-user-id 값을 읽음

    console.log("userId:", userId);  // userId 값을 제대로 출력하는지 확인

    const sortSelect = document.getElementById("sortSelect");

    sortSelect.addEventListener('change', function() {
        const value = sortSelect.value;
        console.log("Selected value: ", value); // 선택된 값 확인
        console.log("userId", userId);  // userId 출력 확인

        if (value) {
            const currentPage = 0;  // 기본적으로 첫 페이지로 시작
            const sortParam = value;  // 정렬 기준 (예: myPrice_asc)

            // 페이지 번호는 URL 파라미터로 유지되고, 정렬 정보도 반영
            window.location.href = `/bid/user/${userId}?page=${currentPage}&sort=${sortParam}`;  // 동적으로 userId 반영
        }
    });
});
