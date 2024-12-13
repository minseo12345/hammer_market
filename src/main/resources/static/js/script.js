// 현재 사용자 정보와 상태 변수
let stompClient = null;
let currentUser = null;
let currentChatRoomId = null;

// HTML 요소 참조
const userListEl = document.getElementById("users");
const roomListEl = document.getElementById("rooms");
const messagesEl = document.getElementById("messages");
const messageInputEl = document.getElementById("message-input");
const sendMessageBtn = document.getElementById("send-message");
const chatRoomTitleEl = document.getElementById("chat-room-title");

// WebSocket 연결 설정
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');

        // 메시지 구독 설정 (메시지 실시간 수신)
        stompClient.subscribe('/topic/messages', (message) => {
            const msg = JSON.parse(message.body);
            if (msg.chatRoomId === currentChatRoomId) {
                addMessage(msg.senderId, msg.content);
            } else {
                // 읽지 않은 메시지 수 실시간 업데이트
                updateUnreadCount(msg.chatRoomId);
            }
        });

        loadChatRooms(); // 채팅방 목록 불러오기
    });
}
function loadChatRooms() {
    fetch('/api/chatrooms')
        .then((response) => response.json())
        .then((chatRooms) => {
            roomListEl.innerHTML = ''; // 기존 목록 초기화
            chatRooms.forEach((room) => {
                createChatRoomElement(room);
            });
        });
}
// 채팅방 요소 생성
function createChatRoomElement(room) {
    const li = document.createElement('li');
    li.textContent = room.title;
    li.dataset.roomId = room.id;

    // UnreadCount 표시용 요소 추가
    const unreadSpan = document.createElement('span');
    unreadSpan.className = 'unread-count';
    unreadSpan.style.color = 'red';
    unreadSpan.style.fontWeight = 'bold';
    li.appendChild(unreadSpan);

    li.style.cursor = 'pointer';
    li.onclick = () => enterChatRoom(room);
    roomListEl.appendChild(li);

    // 초기 UnreadCount 불러오기
    fetch(`/api/chatrooms/${room.id}/unreadCount?userId=${currentUser.id}`)
        .then((res) => res.json())
        .then((unreadCount) => {
            updateUnreadUI(li, unreadCount);
        });
}

// UnreadCount UI 업데이트
function updateUnreadUI(roomElement, unreadCount) {
    const unreadSpan = roomElement.querySelector('.unread-count');
    unreadSpan.textContent = unreadCount > 0 ? ` (${unreadCount} unread)` : '';
}
// 특정 채팅방 UnreadCount 실시간 업데이트
function updateUnreadCount(chatRoomId) {
    const roomElement = document.querySelector(`[data-room-id="${chatRoomId}"]`);
    if (roomElement) {
        fetch(`/api/chatrooms/${chatRoomId}/unreadCount?userId=${currentUser.id}`)
            .then((res) => res.json())
            .then((unreadCount) => {
                updateUnreadUI(roomElement, unreadCount);
            });
    }
}
// 유저 목록 불러오기
function fetchUsers() {
    fetch('/api/users')
        .then((response) => response.json())
        .then((users) => {
            userListEl.innerHTML = '';
            users.forEach((user) => {
                const li = document.createElement('li');
                li.textContent = user.username;
                li.style.cursor = 'pointer';
                li.onclick = () => selectUser(user);
                userListEl.appendChild(li);
            });
        });
}

// 채팅방 입장
function enterChatRoom(room) {
    currentChatRoomId = room.id;
    chatRoomTitleEl.textContent = `Chat Title: ${room.title}`;
    messagesEl.innerHTML = ''; // 기존 메시지 초기화

    // 읽지 않은 메시지를 읽은 상태로 표시
    fetch(`/chat/${room.id}/read?userId=${currentUser.id}`, { method: 'POST' })
        .then(() => updateUnreadCount(room.id)); // UnreadCount 초기화

    fetch(`/chat/${room.id}`)
        .then((response) => response.json())
        .then((messages) => {
            messages.forEach((msg) => addMessage(msg.senderId, msg.content));
        });
}

// 유저 선택 후 채팅방 생성
function selectUser(user) {
    if (user.id === currentUser.id) {
        alert("You can't chat with yourself!");
        return;
    }
    // 서버에 채팅방 요청
    fetch(`/chat/${currentUser.id}/${user.id}`)
        .then((response) => response.json())
        .then((chatRoom) => {
            currentChatRoomId = chatRoom.id;
        })
        .catch((err) => console.error('Error fetching chat room:', err));
}

// 메시지를 채팅창에 추가
function addMessage(senderId, content) {
    const div = document.createElement('div');

    // 현재 사용자의 메시지인지 확인
    if (senderId === currentUser.id) {
        div.textContent = content;
        div.style.textAlign = 'right';
        div.style.backgroundColor = '#d1f7ff';
        div.style.padding = '5px 10px';
        div.style.marginBottom = '10px';
        div.style.borderRadius = '10px';
        div.style.maxWidth = '60%';
        div.style.marginLeft = 'auto';
    } else {
        div.textContent = content;
        div.style.textAlign = 'left';
        div.style.backgroundColor = '#f1f1f1';
        div.style.padding = '5px 10px';
        div.style.marginBottom = '10px';
        div.style.borderRadius = '10px';
        div.style.maxWidth = '60%';
    }

    messagesEl.appendChild(div);
    messagesEl.scrollTop = messagesEl.scrollHeight; // 스크롤 하단으로 이동
}

// 메시지 전송
sendMessageBtn.onclick = () => {
    const content = messageInputEl.value.trim();
    if (content && currentChatRoomId) {
        const message = {
            chatRoomId: currentChatRoomId,
            senderId: currentUser.id,
            content: content,
        };
        stompClient.send("/app/chat", {}, JSON.stringify(message));
        messageInputEl.value = '';
    }
};

// 로그인 및 초기화
function login() {
    fetch('/api/session-user')
        .then((response) => {
            if (!response.ok) {
                throw new Error('활성화된 세션이나 로그인된 유저가 없습니다.');
            }
            return response.json();
        })
        .then((user) => {
            currentUser = user;
            console.log('로그인 유저:', currentUser.username);
            fetchUsers(); // 유저 목록 로드
            connectWebSocket(); // WebSocket 연결
        })
        .catch((err) => {
            console.error('세션 정보 에러:', err);
            alert('로그인 필요');
            window.location.href = '/login';
        });
}

// 시작
login();
