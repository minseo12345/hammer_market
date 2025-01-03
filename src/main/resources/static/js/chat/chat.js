// 현재 사용자 정보와 상태 변수
let stompClient = null;
let currentUser = null;
let currentChatRoomId = null;
let reconnectAttempts = 0; // 재연결 시도 횟수
const maxReconnectAttempts = 5; // 최대 재연결 시도 횟수
const reconnectInterval = 1000; // 초기 재연결 간격 (1초)

// HTML 요소 참조
const sellerRoomsEl = document.getElementById("seller-rooms");
const buyerRoomsEl = document.getElementById("buyer-rooms");
const messagesEl = document.getElementById("messages");
const messageInputEl = document.getElementById("message-input");
const sendMessageBtn = document.getElementById("send-message");
const chatRoomTitleEl = document.getElementById("chat-room-title");

// Notification 권한 요청
function requestNotificationPermission() {
    if ("Notification" in window && Notification.permission !== "granted") {
        Notification.requestPermission().then((permission) => {
            if (permission === "granted") {
                console.log("Notification permission granted.");
            }
        });
    }
}

// Notification 전송
function showNotification(title, options) {
    if ("Notification" in window && Notification.permission === "granted") {
        new Notification(title, options);
    }
}

// WebSocket 연결 설정
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect(
        {},
        () => {
            console.log('Connected to WebSocket');
            reconnectAttempts = 0; // 연결 성공 시 재시도 횟수 초기화

            // 메시지 구독 설정 (메시지 실시간 수신)
            stompClient.subscribe('/topic/messages', (message) => {
                const msg = JSON.parse(message.body);
                if (msg.chatRoomId === currentChatRoomId) {
                    console.log("message arrived")
                    addMessage(msg.senderId, msg.content);
                } else {
                    // 읽지 않은 메시지 수 실시간 업데이트
                    console.log("chatRoomId :", msg.chatRoomId);
                    updateUnreadCount(msg.chatRoomId);

                    // Notification 전송
                    showNotification("새 메시지", {
                        body: `새로운 메세지 : ${msg.content}`,
                        icon: "/img/chat.png",
                    });
                }
            });
            loadChatRooms(); // 채팅방 목록 불러오기
        },
        (error) => {
            console.error('WebSocket connection error:', error);
            attemptReconnect(); // 연결 실패 시 재연결 시도
        }
    );

    // 연결 해제 이벤트 핸들러
    socket.onclose = () => {
        console.warn('WebSocket connection closed.');
        attemptReconnect(); // 연결 해제 시 재연결 시도
    };
}

// 재연결 시도 로직
function attemptReconnect() {
    if (reconnectAttempts < maxReconnectAttempts) {
        const delay = reconnectInterval * Math.pow(2, reconnectAttempts); // 지수적 백오프
        console.log(`Reconnecting in ${delay / 1000} seconds...`);
        setTimeout(() => {
            reconnectAttempts++;
            connectWebSocket(); // 재연결 시도
        }, delay);
    } else {
        console.error('Max reconnect attempts reached. Connection failed.');
        alert('서버와의 연결이 불안정합니다. 잠시 후 다시 시도해주세요.');
    }
}
// 채팅방 목록 불러오기
function loadChatRooms() {
    fetch('/api/chat/chatrooms')
        .then((response) => response.json())
        .then((chatRooms) => {
            // 기존 목록 초기화
            sellerRoomsEl.innerHTML = '';
            buyerRoomsEl.innerHTML = '';
            chatRooms.forEach((room) => {
                createChatRoomElement(room);
            });
        });
}
// 채팅방 요소 생성
function createChatRoomElement(room) {
    const li = document.createElement('li');
    li.textContent = currentUser.userId === room.sellerId ? room.sellerTitle : room.buyerTitle;
    li.dataset.roomId = room.id;

    // UnreadCount 표시용 요소 추가
    const unreadSpan = document.createElement('span');
    unreadSpan.className = 'unread-count';
    unreadSpan.style.color = 'red';
    unreadSpan.style.fontWeight = 'bold';
    li.appendChild(unreadSpan);

    li.style.cursor = 'pointer';
    li.onclick = () => enterChatRoom(room);

    // 채팅방이 판매자 관련인지 구매자 관련인지 구분
    if (currentUser.userId === room.sellerId) {
        sellerRoomsEl.appendChild(li);
    } else if (currentUser.userId === room.buyerId) {
        buyerRoomsEl.appendChild(li);
    }

    // 초기 UnreadCount 불러오기
    fetch(`/api/chat/chatrooms/${room.id}/unreadCount?userId=${currentUser.userId}`)
        .then((res) => res.json())
        .then((unreadCount) => {
            updateUnreadUI(li, unreadCount);
        });
}

// UnreadCount UI 업데이트
function updateUnreadUI(roomElement, unreadCount) {
    const unreadSpan = roomElement.querySelector('.unread-count');
    unreadSpan.textContent = unreadCount > 0 ? ` (${unreadCount} !)` : '';
}
// 특정 채팅방 UnreadCount 실시간 업데이트
function updateUnreadCount(chatRoomId) {
    const roomElement = document.querySelector(`[data-room-id="${chatRoomId}"]`);
    if (roomElement) {
        fetch(`/api/chat/chatrooms/${chatRoomId}/unreadCount?userId=${currentUser.userId}`)
            .then((res) => res.json())
            .then((unreadCount) => {
                updateUnreadUI(roomElement, unreadCount);
            });
    } else {
        loadChatRooms();
    }
}

// 채팅방 입장
function enterChatRoom(room) {
    currentChatRoomId = room.id;
    chatRoomTitleEl.textContent = currentUser.userId === room.sellerId
        ? `Title: ${room.sellerTitle}`
        : `Title: ${room.buyerTitle}`;
    messagesEl.innerHTML = ''; // 기존 메시지 초기화

    // 읽지 않은 메시지를 읽은 상태로 표시
    fetch(`/api/chat/${room.id}/read?userId=${currentUser.userId}`, { method: 'POST' })
        .then(() => updateUnreadCount(room.id)); // UnreadCount 초기화

    fetch(`/api/chat/${room.id}`)
        .then((response) => response.json())
        .then((messages) => {
            messages.forEach((msg) => addMessage(msg.senderId, msg.content));
        });
}

// 유저 선택 후 채팅방 생성
function selectUser(user) {
    if (user.userId === currentUser.userId) {
        alert("You can't chat with yourself!");
        return;
    }
    fetch('/api/chat/createChatRoom', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            buyerId: currentUser.userId,
            sellerId: user.userId,
        }),
    })
        .then((chatRoom) => {
            console.log('Chat room created or retrieved:', chatRoom);
            loadChatRooms(); // 채팅방 목록 갱신
        })
        .catch((err) => console.error('Error fetching chat room:', err));
}

// 메시지를 채팅창에 추가
function addMessage(senderId, content) {
    const div = createMessageElement(senderId, content);
    messagesEl.appendChild(div);
    fetch(`/api/chat/${currentChatRoomId}/read?userId=${currentUser.userId}`, { method: 'POST' })
        .then(() => console.log("message add"));
    messagesEl.scrollTop = messagesEl.scrollHeight; // 스크롤 하단으로 이동
}

// 메시지 DOM 요소 생성
function createMessageElement(senderId, content) {
    const div = document.createElement('div');
    div.textContent = content;
    // CSS 클래스 추가
    if (senderId === currentUser.userId) {
        div.classList.add('message', 'message-sent');
    } else {
        div.classList.add('message', 'message-received');
    }
    return div;
}

// Enter 키로 메시지 전송
messageInputEl.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        sendMessage();
    }
});

// 메시지 전송 기능
function sendMessage() {
    const content = messageInputEl.value.trim();
    // 메시지가 비어있을 경우
    if (content === "") {
        alert("메세지를 입력해주세요.");
        return;
    }

    // 메시지가 100자 이상일 경우
    if (content.length > 100) {
        alert("메세지는 100자 까지만 가능합니다.");
        return;
    }
    if (content && currentChatRoomId) {
        const message = {
            chatRoomId: currentChatRoomId,
            senderId: currentUser.userId,
            content: content,
        };
        stompClient.send("/app/chat", {}, JSON.stringify(message));
        messageInputEl.value = '';
        console.log("message send")
    }
}
// 전송 버튼 클릭
sendMessageBtn.onclick = sendMessage;

// 로그인 및 초기화
function login() {
    fetch('/api/currentUser')
        .then((response) => {
            if (!response.ok) {
                throw new Error('활성화된 세션이나 로그인된 유저가 없습니다.');
            }
            return response.json();
        })
        .then((user) => {
            currentUser = user;
            console.log('로그인 유저:', currentUser.username);
            connectWebSocket(); // WebSocket 연결
            requestNotificationPermission(); // Notification 권한 요청
        })
        .catch((err) => {
            console.error('세션 정보 에러:', err);
            alert('로그인 필요');
            window.location.href = '/login';
        });
}
// 시작
login();
