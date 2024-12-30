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
            data.forEach(notification => {
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
            });

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
    const notificationList = document.getElementById("notification-list");
    const closeModalButton = document.getElementById("close-modal-button");
    const markReadButton = document.getElementById("mark-read-button");

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

    // 알림 목록 가져오기

    /*// 모든 알림 읽음 처리
    markReadButton.addEventListener("click", async () => {
        const userIdElement = document.getElementById("userId");
        if (!userIdElement) {
            console.error("User ID element not found!");
            return;
        }

        try {
            const response = await fetch("/notifications/mark-read", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userId: userIdElement.textContent.trim() }),
            });

            if (response.ok) {
                alert("모든 알림이 읽음 처리되었습니다.");
                fetchNotifications();
            } else {
                console.error("Failed to mark notifications as read.");
            }
        } catch (error) {
            console.error("Error marking notifications as read:", error);
        }
    });*/
});

function initializeWebSocket() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket 연결 성공");

        try {
            stompClient.subscribe('/topic/notifications', (message) => {

                console.log('웹소켓 구독!')
                // const notificationIcon = document.getElementById("notification-icon");
                const notifications = JSON.parse(message.body);

                try {

                    const userIdElement = document.getElementById("userId");
                    if (!userIdElement) {
                        console.error("아이디 조회에 실패했습니다.");
                        return;
                    }

                    fetchNotifications(); // 알림 목록 가져오기

                    const currentUserId = userIdElement.textContent.trim();

                    if (notifications.userId === parseInt(currentUserId)) {
                        //toast 알림
                        Toastify({
                            text: notifications.message,
                            duration: -1,
                            // destination: "https://github.com/apvarun/toastify-js",
                            newWindow: true,
                            close: true,
                            gravity: "top",
                            position: "center",
                            // stopOnFocus: true, // Prevents dismissing of toast on hover
                            style: {
                                background: "linear-gradient(to right, #00b09b, #96c93d)",
                            },
                            onClick: function () {
                                // 알림 클릭 시 모달 열기
                                const notificationModal = document.getElementById("notification-modal");
                                notificationModal.style.display = "block"; // 모달 열기

                                fetchNotifications(); // 알림 목록 가져오기
                                /*// 알림창 제거
                                if (toastElement && toastElement.parentNode) {
                                    toastElement.parentNode.removeChild(toastElement);
                                }*/
                            },
                        }).showToast();
                    }

                    /*if (Array.isArray(notifications)) {
                        const hasUnread = notifications.some(notification => !notification.isRead);
                        if (hasUnread) {
                            notificationIcon.src = "/img/bell2.png";
                            notificationIcon.alt = "알림 아이콘(새 알림 있음)";
                        } else {
                            notificationIcon.src = "/img/bell1.png";
                            notificationIcon.alt = "알림 아이콘(다 읽음)";
                        }
                    } else if (notifications.isRead !== undefined) {
                        // 단일 객체 처리 (예: 메시지가 배열이 아닐 경우)
                        if (!notifications.isRead) {
                            notificationIcon.src = "/img/bell2.png";
                            notificationIcon.alt = "알림 아이콘(새 알림 있음)";
                        } else {
                            notificationIcon.src = "/img/bell1.png";
                            notificationIcon.alt = "알림 아이콘(다 읽음)";
                        }
                    } else {
                        console.log("WebSocket 메시지 형식이 올바르지 않습니다:", notifications.message);
                    }*/
                } catch (error) {
                    console.log("WebSocket 메시지 처리 중 오류 발생:", error, "수신된 메시지:", notifications.message);
                }
            });

        } catch (error) {
            console.error("WebSocket 구독 중 오류 발생:", error);
        }
    }, (error) => {
        console.error("WebSocket 연결 실패:", error);
    });
}


async function completeTransaction(itemId, userId) {
    // URL-encoded 형식으로 데이터 생성
    const params = new URLSearchParams();
    params.append("transactionId", itemId);
    params.append("userId", userId);
    /*console.log("Transaction ID:", itemId);
    console.log("User ID:", userId);*/

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

/*
// 1. 읽지 않은 알림 가져오기
async function fetchUnreadNotificationsForToast(userId) {
    try {
        const response = await fetch(`/notifications/unread?userId=${userId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
        });

        if (response.ok) {
            const unreadNotifications = await response.json();
            displayUnreadNotificationsAsToast(unreadNotifications);
        } else {
            console.error("Failed to fetch unread notifications:", response.statusText);
        }
    } catch (error) {
        console.error("Error fetching unread notifications:", error);
    }
}

// 읽지 않은 알림을 Toast로 표시
function displayUnreadNotificationsAsToast(notifications) {
    notifications.forEach(notification => {
        Toastify({
            text: notification.message,
            duration: -1, // 알림 유지
            close: true,
            gravity: "top",
            position: "center",
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            },
            onClick: function () {
                const notificationModal = document.getElementById("notification-modal");
                if (notificationModal) {
                    notificationModal.style.display = "block"; // 알림 클릭 시 모달 열기
                }
            },
        }).showToast();
    });
}


// 3. 로그인 후 초기화
function initializeNotifications(userId) {
    initializeWebSocket(); // WebSocket 초기화
    fetchUnreadNotificationsForToast(userId); // 로그인 시 읽지 않은 알림 표시
}

document.addEventListener("DOMContentLoaded", () => {
    const userIdElement = document.getElementById("userId");
    if (userIdElement) {
        const userId = userIdElement.textContent.trim();
        initializeNotifications(userId); // 알림 초기화
    }
});
*/

