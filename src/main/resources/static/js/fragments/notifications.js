let errorMessage = ''; // 전역에서 사용할 변수 선언

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
                        duration: 10000,
                        close: true,
                        gravity: "top",
                        position: "center",
                        style: {
                            background: "linear-gradient(to right, #00b09b, #96c93d)",
                        },
                        onClick: function () {
                            const modalElement = document.getElementById("notification-modal");
                            if (!modalElement.classList.contains("show")) {
                                const notificationModal = new bootstrap.Modal(modalElement);
                                notificationModal.show();
                            } else {
                                console.log("모달이 이미 열려 있습니다.");
                            }
                        }
                    }).showToast();

                    // isRead 업데이트
                    await markNotificationAsRead(notification.notificationId);
                }

                let status = notification.itemStatus?.trim().toUpperCase() || "OTHER";

                const categoryMapping = {
                    "낙찰": "BIDDING_END",
                    "내 수락 대기 중": "WAITING_FOR_MY_APPROVAL",
                    "상대방 수락 대기 중": "WAITING_FOR_OTHER_APPROVAL",
                    "거래 완료": "COMPLETED",
                    "취소된 거래": "CANCELLED",
                };

                status = categoryMapping[status] || status;

                if (Object.keys(categories).includes(status)) {
                    categories[status].push(notification);
                } else {
                    categories.OTHER.push(notification);
                }
            }

            // 카테고리별 UI 생성
            Object.keys(categories).forEach(category => {
                const categoryTitle = {
                    BIDDING_END: "<낙찰>",
                    WAITING_FOR_MY_APPROVAL: "<수락 대기 중>",
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
                            ["BIDDING_END", "WAITING_FOR_MY_APPROVAL", "WAITING_FOR_OTHER_APPROVAL"].includes(category)
                                ? `<button onclick="completeTransaction(${notification.itemId}, ${notification.userId})">거래수락</button>
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
            errorMessage = "알림 목록을 가져오는 중 오류가 발생했습니다.";
            console.error("Failed to fetch notifications:", response.statusText);
        }
    } catch (error) {
        errorMessage = "알림을 가져오는 동안 예기치 않은 오류가 발생했습니다.";
        console.error("Error fetching notifications:", error);
    }
}

async function markNotificationAsRead(notificationId) {
    try {
        const params = new URLSearchParams();
        params.append("notificationId", notificationId);
        params.append("isRead", true);

        const response = await fetch("/notifications/update-read-status", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: params.toString(),
        });

        if (response.ok) {
            console.log(`Notification ${notificationId} marked as read.`);
        } else {
            console.error(`Failed to update read status for notification ${notificationId}:`, response.statusText);
        }
    } catch (error) {
        console.error(`Error updating read status for notification ${notificationId}:`, error);
    }
}

function initializeWebSocket(userId) {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket 연결 성공");

        stompClient.subscribe('/topic/notifications', (message) => {
            const notifications = JSON.parse(message.body);

            if (notifications.userId === parseInt(userId)) {
                if (!notifications.isRead) {
                    Toastify({
                        text: notifications.message,
                        duration: 10000,
                        close: true,
                        gravity: "top",
                        position: "center",
                        style: {
                            background: "linear-gradient(to right, #00b09b, #96c93d)",
                        },
                        onClick: function () {
                            const modalElement = document.getElementById("notification-modal");
                            if (!modalElement.classList.contains("show")) {
                                const notificationModal = new bootstrap.Modal(modalElement);
                                notificationModal.show();
                            } else {
                                console.log("모달이 이미 열려 있습니다.");
                            }
                        }
                    }).showToast();

                    // isRead 업데이트
                    markNotificationAsRead(notification.notificationId);
                }
            }
        });
    }, (error) => {
        console.error("WebSocket 연결 실패:", error);
    });
}

async function completeTransaction(itemId, userId) {
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
            location.reload();
        } else {
            errorMessage = "거래 완료 처리 중 오류가 발생했습니다.";
            console.error("거래 완료 처리 중 오류 발생:", response.statusText);
        }
    } catch (error) {
        errorMessage = "거래 완료 요청 처리 중 예기치 않은 오류가 발생했습니다.";
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
        if (!response.ok) {
            errorMessage = "거래 포기 요청 처리 중 오류가 발생했습니다.";
            console.error("거래 포기 처리 중 오류 발생:", response.statusText);
        }
    } catch (error) {
        errorMessage = "거래 포기 요청 처리 중 예기치 않은 오류가 발생했습니다.";
        console.error("Error canceling transaction:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const notificationIcon = document.getElementById("notification-icon");
    const notificationList = document.getElementById("notification-list");
    const userIdElement = document.getElementById("userId");

    if (!notificationIcon || !notificationList || !userIdElement) {
        console.error("Required DOM elements not found!");
        return;
    }

    const userId = userIdElement.textContent.trim();

    initializeWebSocket(userId); // WebSocket 초기화
    fetchNotifications(); // 알림 목록 가져오기

    if (errorMessage) {
        alert(errorMessage); // 에러 메시지 표시
        errorMessage = ''; // 메시지 초기화
    }

    const notificationModal = new bootstrap.Modal(document.getElementById("notification-modal"));

    notificationIcon.addEventListener("click", () => {
        notificationModal.show(); // 모달 열기
        fetchNotifications(); // 알림 목록 새로 가져오기
    });
});
