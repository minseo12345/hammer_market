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
    async function fetchNotifications() {


        const userIdElement = document.getElementById("userId");
        if (!userIdElement) {
            console.error("User ID element not found!");
            return;
        }

        const params = {
            userId: userIdElement.textContent.trim(),
        };

        try {
            const response = await fetch("/notifications/list", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(params),
            });

            if (response.ok) {
                const data = await response.json();

                // 기존 내용 제거
                notificationList.innerHTML = "";

                data.forEach(notification => {
                    const li = document.createElement("li");
                    console.log('알림아이콘 클릭 이벤트')
                    li.innerHTML = `
                        <p>${notification.message}</p>
                        <button onclick="completeTransaction(${notification.itemId})">거래완료</button>
                        <button onclick="cancelTransaction(${notification.itemId})">거래포기</button>
                    `;

                    notificationList.appendChild(li);
                });
            } else {
                console.error("Failed to fetch notifications:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching notifications:", error);
        }
    }

    // 모든 알림 읽음 처리
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
    });
});

function initializeWebSocket() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket 연결 성공");

        try {
            stompClient.subscribe('/topic/notifications', (message) => {

                console.log('웹소켓 stomp')
                const notificationIcon = document.getElementById("notification-icon");
                const notifications = JSON.parse(message.body);

                try {

                    const userIdElement = document.getElementById("userId");
                    if (!userIdElement) {
                        console.error("User ID element not found!");
                        return;
                    }

                    const currentUserId = userIdElement.textContent.trim();

                    if (notifications.userId === parseInt(currentUserId)) {
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
                                // 알림창 제거
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


function completeTransaction(itemId) {
    fetch(`/transaction/complete/${itemId}`, { method: "POST" })
        .then(response => {
            if (response.ok) {
                alert("거래가 완료되었습니다.");
                location.reload();
            } else {
                alert("거래 완료 처리 중 오류가 발생했습니다.");
            }
        })
        .catch(error => console.error("Error completing transaction:", error));
}

function cancelTransaction(itemId) {
    fetch(`/transaction/cancel/${itemId}`, { method: "POST" })
        .then(response => {
            if (response.ok) {
                alert("거래를 포기했습니다.");
                location.reload();
            } else {
                alert("거래 포기 처리 중 오류가 발생했습니다.");
            }
        })
        .catch(error => console.error("Error canceling transaction:", error));
}



