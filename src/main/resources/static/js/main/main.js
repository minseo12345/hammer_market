function AdminPage() {
    window.location.href = '/admin';
}

function BidPage(userId) {
    window.location.href = '/bid/user/' + userId;
}

function ChatPage() {
    window.location.href = '/chat';
}

function ItemPage() {
    window.location.href = '/items/list';
}

document.addEventListener("DOMContentLoaded", function () {
    // 알림 아이콘 클릭 시 모달 표시
    const notificationModal = document.getElementById("notification-modal");
    const notificationIcon = document.getElementById("notification-icon");
    const closeModalButton = document.getElementById("close-modal-button");
    const markReadButton = document.getElementById("mark-read-button");

    // 알림 모달 열기
    if (notificationIcon) {
        notificationIcon.addEventListener("click", function () {
            notificationModal.style.display = "block";
            fetch("/notifications", { method: "GET" })
                .then((response) => response.text())
                .then((html) => {
                    document.getElementById("notification-list").innerHTML = html;
                });
        });
    }

    // 알림 모달 닫기
    if (closeModalButton) {
        closeModalButton.addEventListener("click", function () {
            notificationModal.style.display = "none";
        });
    }

    // 모든 알림 읽음 처리
    if (markReadButton) {
        markReadButton.addEventListener("click", function () {
            fetch("/notifications/mark-read", { method: "POST" })
                .then((response) => {
                    if (response.ok) {
                        alert("모든 알림이 읽음 처리되었습니다.");
                        notificationModal.style.display = "none";
                    }
                });
        });
    }
});
