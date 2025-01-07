let errorMessage = ''; // 전역에서 사용할 변수 선언

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


// 알림 목록 가져오기
async function fetchNotifications() {
    const notificationList = document.getElementById("notification-list");
    const userIdElement = document.getElementById("userId");

    if (!userIdElement || !notificationList) {
        console.error("Required elements not found!");
        return;
    }

    const params = { userId: userIdElement.textContent.trim() };
    console.log("Sending Request Params:", params);

    try {
        const response = await fetch("/notifications/list", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params),
        });

        console.log("Response Status:", response.status);

        if (response.ok) {
            const data = await response.json();
            console.log("Received Data:", data);
            // 기존 내용 제거
            notificationList.innerHTML = "";

            // 카테고리별 리스트 초기화
            const categories = {
                BIDDING_END: [],
                PARTIALLY_APPROVE: [],
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
                    "거래수락 대기": "PARTIALLY_APPROVE",
                    "완료된 거래": "COMPLETED",
                    "취소된 거래": "CANCELLED",
                };
                status = categoryMapping[status] || status;

                // 상태 매핑 결과 디버깅
                console.log("Notification Status:", notification.itemStatus);
                console.log("Mapped Status:", status);

                if (Object.keys(categories).includes(status)) {
                    categories[status].push(notification);
                } else {
                    categories.OTHER.push(notification);
                }
            }

            Object.keys(categories).forEach(category => {
                const categoryTitle = {
                    BIDDING_END: "낙찰",
                    PARTIALLY_APPROVE: "거래수락 대기",
                    COMPLETED: "완료된 거래",
                    CANCELLED: "취소된 거래",
                    OTHER: "기타 알림"
                }[category] || "기타 알림";

                const categoryClass = {
                    BIDDING_END: "list-group-item-primary",
                    PARTIALLY_APPROVE: "list-group-item-warning",
                    COMPLETED: "list-group-item-success",
                    CANCELLED: "list-group-item-danger",
                    OTHER: "list-group-item-secondary"
                }[category] || "list-group-item-secondary";

                // 새로운 컨테이너 생성
                const categoryContainer = document.createElement("div");
                categoryContainer.classList.add("category-container");

                // 카테고리 제목 생성
                const titleElement = document.createElement("div");
                titleElement.textContent = categoryTitle;
                titleElement.classList.add("category-bar", categoryClass);
                categoryContainer.appendChild(titleElement);

                // 알림 리스트 생성
                const itemList = document.createElement("ul");
                itemList.classList.add("list-group");

                if (categories[category].length > 0) {
                    categories[category].forEach(notification => {
                        const listItem = document.createElement("li");
                        listItem.classList.add("list-group-item");
                        listItem.innerHTML = `
                <p>${notification.message}</p>
                ${
                            ["BIDDING_END", "PARTIALLY_APPROVE"].includes(category)
                                ? `<button onclick="completeTransaction(${notification.itemId}, ${notification.userId})">거래수락</button>
                       <button onclick="cancelTransaction(${notification.itemId}, ${notification.userId})">거래포기</button>`
                                : ""
                        }
            `;
                        itemList.appendChild(listItem);
                    });
                } else {
                    const emptyMessage = document.createElement("li");
                    emptyMessage.classList.add("list-group-item");
                    emptyMessage.textContent = "알림이 없습니다.";
                    itemList.appendChild(emptyMessage);
                }

                categoryContainer.appendChild(itemList);

                // 알림 리스트를 최종 DOM에 추가
                const notificationList = document.getElementById("notification-list");
                notificationList.appendChild(categoryContainer);
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
                    const notificationList = document.getElementById("notification-list");
                    const li = document.createElement("li");
                    li.textContent = notifications.message;
                    notificationList.prepend(li);
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
    params.append("itemId", itemId); // transactionId → itemId로 변경
    params.append("userId", userId);

    try {
        const response = await fetch("/transaction/complete", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: params.toString(),
        });

        console.log("Complete Transaction Request:", { itemId, userId });
        fetchNotifications();

        if (response.ok) {
            const data = await response.json();
            console.log("Transaction Completed Successfully:", data);
            alert("거래 완료 요청이 접수되었습니다.");
            location.reload();
        } else {
            errorMessage = "거래 완료 처리 중 오류가 발생했습니다.";
            console.error("Transaction Completion Error:");
        }
    } catch (error) {
        errorMessage = "거래 완료 요청 처리 중 예기치 않은 오류가 발생했습니다.";
        console.error("Unexpected Error Completing Transaction:");
    }
}


async function cancelTransaction(itemId, userId) {
    try {
        const response = await fetch(`/transaction/cancel?itemId=${itemId}&userId=${userId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });
        fetchNotifications();
        if (!response.ok) {
            errorMessage = "거래 포기 요청 처리 중 오류가 발생했습니다.";
        }
    } catch (error) {
        errorMessage = "거래 포기 요청 처리 중 예기치 않은 오류가 발생했습니다.";
        console.error("Error canceling transaction:", error);
    }
}

