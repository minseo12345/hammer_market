// 알림 목록 가져오기
async function fetchNotifications() {
    const notificationList = document.getElementById("notification-list");
    const userIdElement = document.getElementById("userId");
    if (!userIdElement || !notificationList) {
        console.error("Required elements not found!");
        return;
    }

    const params = { userId: userIdElement.textContent.trim() };

    try {
        const response = await fetch("/notifications/list", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params),
        });

        if (response.ok) {
            const data = await response.json();

            // 기존 내용 제거
            notificationList.innerHTML = "";

            // 카테고리별 리스트 초기화
            const categories = {
                BIDDING_END: [],
                WAITING_FOR_MY_APPROVAL: [],
                WAITING_FOR_OTHER_APPROVAL: [],
                COMPLETED: [],
                CANCELLED: [],
                OTHER: []
            };

            // 알림 분류
            for (const notification of data) {
                if (!notification.isRead) {
                    // Toastify 표시
                    Toastify({
                        text: notification.message,
                        duration: -1,
                        close: true,
                        gravity: "top",
                        position: "center",
                        style: {
                            background: "linear-gradient(to right, #00b09b, #96c93d)",
                        },
                        onClick: function () {
                            const notificationModal = document.getElementById("notification-modal");
                            if (notificationModal) {
                                notificationModal.style.display = "block";
                            }
                        },
                    }).showToast();

                    // isRead 업데이트
                    try {
                        // URL-encoded 형식으로 데이터 생성
                        const params = new URLSearchParams();
                        params.append("notificationId", notification.notificationId);
                        params.append("isRead", true);

                        const response = await fetch("/notifications/update-read-status", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/x-www-form-urlencoded",
                            },
                            body: params.toString(), // URL-encoded 데이터 전송
                        });

                        if (response.ok) {
                            console.log(`Notification ${notification.notificationId} marked as read.`);
                        } else {
                            console.error(`Failed to update read status for notification ${notification.notificationId}:`, response.statusText);
                        }
                    } catch (updateError) {
                        console.error(`Failed to update read status for notification ${notification.notificationId}:`, updateError);
                    }
                }

                let status = notification.itemStatus?.trim() || "OTHER";

                if (status === "PARTIALLY_COMPLETED") {
                    status = notification.modifiedBy.includes(userId)
                        ? "WAITING_FOR_OTHER_APPROVAL"
                        : "WAITING_FOR_MY_APPROVAL";
                }

                if (categories[status]) {
                    categories[status].push(notification);
                } else {
                    categories.OTHER.push(notification);
                }
            }

            // 카테고리별 UI 생성
            Object.keys(categories).forEach(category => {
                const categoryTitle = {
                    BIDDING_END: "<낙찰>",
                    WAITING_FOR_MY_APPROVAL: "<내 수락 대기 중>",
                    WAITING_FOR_OTHER_APPROVAL: "<상대방 수락 대기 중>",
                    COMPLETED: "<거래완료>",
                    CANCELLED: "<취소된거래>",
                    OTHER: "<기타 알림>"
                }[category] || "<기타 알림>";

                const categoryElement = document.createElement("div");
                categoryElement.innerHTML = `<h3>${categoryTitle}</h3>`;
                notificationList.appendChild(categoryElement);

                if (categories[category].length > 0) {
                    categories[category].forEach(notification => {
                        const li = document.createElement("li");
                        li.innerHTML = `
                <p>${notification.message}</p>
                ${
                            category === "BIDDING_END" || category === "WAITING_FOR_MY_APPROVAL"
                                ? `<button onclick="completeTransaction(${notification.itemId}, ${notification.userId})">거래수락</button>
                                    <button onclick="cancelTransaction(${notification.itemId}, ${notification.userId})">거래포기</button>`
                                : category === "WAITING_FOR_OTHER_APPROVAL" 
                                    ? `<button disabled>거래수락</button>
                                        <button onclick="cancelTransaction(${notification.itemId}, ${notification.userId})">거래포기</button>`
                                    : ""
                        }
            `;
                        notificationList.appendChild(li);
                    });
                } else {
                    const emptyMessage = document.createElement("p");
                    emptyMessage.textContent = "알림이 없습니다.";
                    categoryElement.appendChild(emptyMessage);
                }
            });
        } else {
            console.error("Failed to fetch notifications:", response.statusText);
        }
    } catch (error) {
        console.error("Error fetching notifications:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const notificationModal = document.getElementById("notification-modal");
    const notificationIcon = document.getElementById("notification-icon");
    // const notificationList = document.getElementById("notification-list");
    const closeModalButton = document.getElementById("close-modal-button");

    if (!notificationIcon || !notificationModal || !closeModalButton) {
        console.error("Required DOM elements not found!");
        return;
    }

    // WebSocket 초기화 및 구독
    initializeWebSocket();

    // 알림 모달 열기
    notificationIcon.addEventListener("click", () => {
        if (notificationModal.style.display === "block") {
            notificationModal.style.display = "none";
        } else {
            notificationModal.style.display = "block";
            fetchNotifications();
        }
    });

    // 알림 모달 닫기
    closeModalButton.addEventListener("click", () => {
        notificationModal.style.display = "none";
    });

});
function initializeWebSocket(userId) {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log("WebSocket 연결 성공");

        stompClient.subscribe('/topic/notifications', (message) => {
            console.log('웹소켓 구독 이벤트 발생');
            const notifications = JSON.parse(message.body);

            if (notifications.userId === parseInt(userId)) {
                // Toastify로 실시간 알림 표시
                Toastify({
                    text: notifications.message,
                    duration: -1,
                    close: true,
                    gravity: "top",
                    position: "center",
                    style: {
                        background: "linear-gradient(to right, #00b09b, #96c93d)",
                    },
                    onClick: function () {
                        const notificationModal = document.getElementById("notification-modal");
                        if (notificationModal) {
                            notificationModal.style.display = "block";
                        }
                    },
                }).showToast();
            }
        });
    }, (error) => {
        console.error("WebSocket 연결 실패:", error);
    });
}

async function completeTransaction(itemId, userId) {
    // URL-encoded 형식으로 데이터 생성
    const params = new URLSearchParams();
    params.append("transactionId", itemId);
    params.append("userId", userId);

    try {
        const response = await fetch("/transaction/complete", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: params.toString(),
        });

        fetchNotifications();

        if (response.ok) {
            alert("거래 완료 요청이 접수되었습니다.");
            // location.reload();
        } else {
            console.error("거래 완료 처리 중 오류 발생:", response.statusText);
        }
    } catch (error) {
        console.error("Error completing transaction:", error);
    }
}

async function cancelTransaction(itemId, userId) {
    try {
        const response = await fetch(`/transaction/cancel?transactionId=${itemId}&userId=${userId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });

        fetchNotifications();

        if (response.ok) {
            alert("거래 포기 요청이 접수되었습니다.");
            location.reload();
        } else {
            console.error("거래 포기 처리 중 오류 발생:", response.statusText);
        }
    } catch (error) {
        console.error("Error canceling transaction:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const userIdElement = document.getElementById("userId");
    if (userIdElement) {
        const userId = userIdElement.textContent.trim();
        initializeWebSocket(userId); // 알림 초기화
        fetchNotifications();
    }
});


