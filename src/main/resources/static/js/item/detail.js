document.addEventListener("DOMContentLoaded", () => {
    const bidForm = document.getElementById("bid-form");
    const itemDetails = document.getElementById("item-details");


    const itemId = itemDetails.getAttribute("data-item-id");


    bidForm.addEventListener("submit", (event) => {
        event.preventDefault();

        const userId = 1; // 사용자 ID는 하드코딩 (실제 환경에서는 변경 필요)
        const bidAmount = parseFloat(document.getElementById("bidAmount").value);
        const bidTime = new Date().toISOString();  // 현재 시간을 ISO 형식으로 생성

        fetch(`/items/detail/${itemId}/bid`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                itemId: itemId,
                userId: userId,
                bidAmount: bidAmount,
                bidTime: bidTime,
            }),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("입찰에 실패했습니다.");
                }
                return response.json();
            })
            .then((data) => {
                if (data.success) {
                    // 현재가 업데이트
                    const highestBidElement = document.getElementById("current-highest-bid");
                    highestBidElement.textContent = `₩${data.highestBid}`;

                    alert("입찰 성공! 현재가가 갱신되었습니다.");
                } else {
                    throw new Error(data.error || "알 수 없는 오류");
                }
            })
            .catch((error) => {
                alert(error.message);
            });
    });
});