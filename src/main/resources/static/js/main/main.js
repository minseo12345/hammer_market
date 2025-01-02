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

function TransactionPage() {
    window.location.href = '/transactions';
}

document.addEventListener("DOMContentLoaded", () => {
    const userIdElement = document.getElementById("userId");
    if (userIdElement) {
        const userId = userIdElement.textContent.trim();
        initializeNotifications(userId); // 알림 초기화
    }
});



