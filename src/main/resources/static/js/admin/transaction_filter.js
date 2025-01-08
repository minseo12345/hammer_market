let originalRows = []; // 전역 변수로 선언

document.addEventListener("DOMContentLoaded", function () {
    const rows = Array.from(document.querySelectorAll("#transactionTable tr")); // 테이블 행 가져오기
    originalRows = rows; // 초기화
    filterTransactions(); // 초기 필터링 및 정렬 적용
});

function filterTransactions() {
    // 원본 데이터를 복사해서 사용
    const rows = [...originalRows];

    // 체크박스 상태 확인
    const isOngoingChecked = document.getElementById("statusONGOING").checked;
    const isCompletedChecked = document.getElementById("statusCOMPLETED").checked;
    const isBiddingEndChecked = document.getElementById("statusBIDDING_END").checked;

    // 선택된 상태를 배열에 저장
    const selectedStatuses = [];
    if (isOngoingChecked) {
        selectedStatuses.push("ONGOING", "WAITING_FOR_MY_APPROVAL", "WAITING_FOR_OTHER_APPROVAL");
    }
    if (isCompletedChecked) selectedStatuses.push("COMPLETED");
    if (isBiddingEndChecked) selectedStatuses.push("BIDDING_END");

    // 필터링: 체크박스 조건에 따라 행을 선택
    const filteredRows = rows.filter(row => {
        const rowStatus = row.getAttribute("data-status") || '';
        return selectedStatuses.length === 0 || selectedStatuses.includes(rowStatus);
    });

    // 정렬 조건 가져오기
    const sortFilter = document.getElementById("sortFilter").value;

    // 정렬 적용
    filteredRows.sort((a, b) => {
        const priceA = parseFloat(a.getAttribute("data-finalprice")) || 0;
        const priceB = parseFloat(b.getAttribute("data-finalprice")) || 0;
        const dateA = new Date(a.getAttribute("data-transactiondate"));
        const dateB = new Date(b.getAttribute("data-transactiondate"));

        switch (sortFilter) {
            case "latest": return dateB - dateA; // 최신 순
            case "oldest": return dateA - dateB; // 오래된 순
            case "highPrice": return priceB - priceA; // 금액 높은 순
            case "lowPrice": return priceA - priceB; // 금액 낮은 순
            default: return 0;
        }
    });

    // 테이블 다시 채우기
    const tableBody = document.getElementById("transactionTable");
    tableBody.innerHTML = ""; // 기존 내용 비우기
    filteredRows.forEach(row => tableBody.appendChild(row)); // 필터링 및 정렬된 행 추가
}
