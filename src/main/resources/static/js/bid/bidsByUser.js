document.addEventListener("DOMContentLoaded", function() {
    const sortSelect = document.getElementById("sortSelect");
    const searchInput = document.getElementById("searchInput");

    // 정렬 기준 변경 시 페이지 이동
    sortSelect.addEventListener('change', function() {
        const value = sortSelect.value;
        console.log("Selected value: ", value); // 선택된 값 확인

        if (value) {
            const sortParam = value;
            console.log("Sort parameter: ", sortParam); // sortParam 확인

            // 페이지 번호는 URL 파라미터로 유지되고, 정렬 정보도 반영
            const userId = document.querySelector(".container").getAttribute("data-user-id");
            const currentPage = 0;  // 기본적으로 첫 페이지로 시작

            // 페이지 이동 시, 정렬 기준을 URL 파라미터에 포함
            const url = new URL(window.location.href);
            const params = new URLSearchParams(url.search);

            // itemName도 반영
            const itemName = searchInput.value;
            if (itemName) {
                params.set('itemName', itemName);  // 검색어를 itemName으로 변경
            } else {
                params.delete('itemName');  // itemName이 비어있으면 파라미터에서 삭제
            }

            // 정렬 기준과 itemName 파라미터 추가
            params.set('sort', sortParam);

            // 페이지 이동
            window.location.href = `/bid/user/${userId}?${params.toString()}`;
        }
    });

    // 엔터키로만 검색이 실행되도록 처리
    searchInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            // 기본 동작을 막음 (새로고침을 방지)
            event.preventDefault();

            const itemName = searchInput.value;
            const userId = document.querySelector(".container").getAttribute("data-user-id");
            const currentPage = 0; // 기본적으로 첫 페이지로 시작
            const sortParam = sortSelect.value; // 현재 선택된 정렬 기준

            // URL 파라미터 생성
            const url = new URL(window.location.href);
            const params = new URLSearchParams(url.search);

            if (itemName) {
                params.set('itemName', itemName);  // itemName으로 파라미터 설정
            } else {
                params.delete('itemName');  // itemName이 비어있으면 파라미터에서 삭제
            }

            if (sortParam) {
                params.set('sort', sortParam);
            }

            // 페이지 이동 시, 정렬 기준과 itemName도 URL 파라미터에 포함
            window.location.href = `/bid/user/${userId}?${params.toString()}`;
        }
    });
});
