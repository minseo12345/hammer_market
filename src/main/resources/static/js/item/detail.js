document.addEventListener("DOMContentLoaded", () => {
    const submitButton = document.getElementById("submit-bid");
    const bidAmountInput = document.getElementById("bidAmount");
    const itemDetails = document.getElementById("item-details");
    const highestBidElement = document.getElementById("current-highest-bid");

    const itemId = itemDetails.getAttribute("data-item-id");

    submitButton.addEventListener("click", () => {
        const bidAmount = parseFloat(bidAmountInput.value);

        // 유효성 검사
        if (isNaN(bidAmount) || bidAmount <= 0) {
            alert("입찰 금액을 올바르게 입력해주세요.");
            return;
        }

        // 버튼 비활성화
        submitButton.disabled = true;

        fetch(`/items/detail/${itemId}/bid`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                itemId: itemId,
                userId: 1, // 사용자 ID는 하드코딩 (실제 환경에서는 변경 필요)
                bidAmount: bidAmount,
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
                    highestBidElement.textContent = `₩${data.highestBid}`;
                    alert("입찰 성공! 현재가가 갱신되었습니다.");
                } else {
                    throw new Error(data.error || "알 수 없는 오류");
                }
            })
            .catch((error) => {
                alert(error.message);
            })
            .finally(() => {
                // 버튼 다시 활성화
                submitButton.disabled = false;
            });
    });
});
