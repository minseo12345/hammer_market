document.addEventListener('DOMContentLoaded', () => {
    const notificationIcon = document.getElementById('notification-icon');
    const notificationCount = document.getElementById('notification-count');
    const notificationModal = document.getElementById('notification-modal');
    const notificationList = document.getElementById('notification-list');
    const closeModalButton = document.getElementById('close-modal');

    // 알림 아이콘 클릭 시 모달 열기
    notificationIcon.addEventListener('click', () => {
        notificationModal.style.display = 'block';
        fetchNotifications();
    });

    // 모달 닫기 버튼 클릭 시 모달 닫기
    closeModalButton.addEventListener('click', () => {
        notificationModal.style.display = 'none';
    });

    // 알림 데이터를 서버에서 가져오기
    async function fetchNotifications() {
        try {
            const response = await fetch(`/notifications/list`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `userId=${userId}`, // `userId`는 서버에서 제공하는 사용자 ID
            });

            if (response.ok) {
                const html = await response.text();
                notificationList.innerHTML = html;

                // 알림을 읽음 상태로 변경
                markNotificationsAsRead();
            } else {
                console.error('Failed to fetch notifications:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching notifications:', error);
        }
    }

    // 알림을 읽음 상태로 변경
    async function markNotificationsAsRead() {
        try {
            const response = await fetch(`/notifications/markAsRead`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `userId=${userId}`,
            });

            if (response.ok) {
                notificationIcon.classList.remove('unread');
            } else {
                console.error('Failed to mark notifications as read:', response.statusText);
            }
        } catch (error) {
            console.error('Error marking notifications as read:', error);
        }
    }
});
