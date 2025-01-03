document.addEventListener("DOMContentLoaded", function () {
    const sortBy = document.getElementById("sortBy");
    const directionRadios = document.querySelectorAll("input[name='direction']");
    const statusSelect = document.getElementById("status");

    // 정렬 기준 변경 시
    sortBy.addEventListener("change", function () {
        document.getElementById("searchForm").submit();
    });

    // 정렬 방향 변경 시
    directionRadios.forEach(radio => {
        radio.addEventListener("change", function () {
            document.getElementById("searchForm").submit();
        });
    });

    // 상태 변경 시
    statusSelect.addEventListener("change", function () {
        document.getElementById("searchForm").submit();
    });

    // 장바구니 상태 확인 및 버튼 스타일 업데이트
    updateCartButtonStyles();
});

// 장바구니 버튼 스타일 업데이트 함수 추가
async function updateCartButtonStyles() {
    try {
        const response = await fetch('/api/currentUser');
        if (!response.ok) throw new Error('Failed to get current user');
        
        const currentUser = await response.json();
        const userId = currentUser.userId;
        
        // 현재 유저의 장바구니 데이터 가져오기
        const cartItems = JSON.parse(localStorage.getItem(userId)) || [];
        const cartItemIds = cartItems.map(item => item.itemId);
        
        // 모든 장바구니 버튼 순회
        document.querySelectorAll('.like-btn').forEach(button => {
            const itemCard = button.closest('.auction-card');
            const itemId = itemCard.id.split('-')[2];
            
            if (cartItemIds.includes(itemId)) {
                button.classList.remove('like-btn'); // 기존 클래스 제거
                button.classList.add('unlike-btn'); // 장바구니에 있는 경우 성공 스타일
            }
        });
    } catch (error) {
        console.error('장바구니 상태 업데이트 중 오류:', error);
    }
}

async function addToCart(event) {
    // 기본 동작 방지 (아이템 상세 페이지로 이동하지 않도록)
    event.preventDefault();

    // itemId는 버튼의 부모 요소 (card)에 대한 속성이나 다른 방법으로 추출할 수 있음
    const itemId = event.target.closest('.auction-card').id.split('-')[2];  // `item-card-${itemId}`에서 itemId 추출
    console.log(itemId);
    try {
        // 현재 유저 정보 가져오기
        const response = await fetch('/api/currentUser', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('현재 유저 정보를 가져오지 못했습니다.');
        }

        const currentUser = await response.json();
        const userId = currentUser.userId;  // 현재 유저 ID

        // 클릭한 아이템 정보 가져오기
        const item = document.querySelector(`#item-card-${itemId}`);
        const itemData = {
            itemId: itemId,
            title: item.dataset.title, // data-title 속성
            startingBid: item.dataset.startingBid,
            buyNowPrice: item.dataset.buyNowPrice,// data-starting-bid 속성
            status: item.dataset.status, // data-status 속성
            fileUrl: item.dataset.imgSrc
            // 필요한 아이템 정보 추가
        };

        // localStorage에 아이템을 저장 (유저 ID를 키로)
        let cart = JSON.parse(localStorage.getItem(userId)) || [];
        cart.push(itemData);
        localStorage.setItem(userId, JSON.stringify(cart));

        // 버튼 스타일 즉시 업데이트
        const button = event.target;
        button.classList.remove('like-btn');
        button.classList.add('unlike-btn'); // 장바구니에 있는 경우 성공 스타일


        alert('아이템이 장바구니에 추가되었습니다!');
        console.log("Local Storage Data:", localStorage.getItem(userId));
    } catch (error) {
        console.error('장바구니 추가 중 오류 발생:', error);
        alert('장바구니에 추가하는 데 문제가 발생했습니다.');
    }
}
document.addEventListener("DOMContentLoaded", () => {
    updateRemainingTimes(); // 초기 시간 계산
    setInterval(updateRemainingTimes, 10000); // 매분마다 업데이트
});

function updateRemainingTimes() {
    const timeElements = document.querySelectorAll('.time-remaining');

    timeElements.forEach(el => {
        const endTime = new Date(el.getAttribute('data-end-time')); // 종료 시간
        const currentTime = new Date(); // 현재 시간
        const remainingTime = endTime - currentTime; // 남은 시간 (밀리초)

        if (remainingTime <= 0) {
            el.textContent = "경매 종료";
            el.classList.add("text-danger"); // 경매 종료 시 빨간색 강조
        } else {
            el.textContent = formatRemainingTime(remainingTime);
        }
    });
}

function formatRemainingTime(ms) {
    const seconds = Math.floor(ms / 1000);
    const days = Math.floor(seconds / (3600 * 24));
    const hours = Math.floor((seconds % (3600 * 24)) / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);


    const parts = [];
    if (days > 0) parts.push(`${days}일`);
    if (hours > 0 || days > 0) parts.push(`${hours}시간`);
    if (minutes > 0 || hours > 0 || days > 0) parts.push(`${minutes}분`);


    return parts.join(" ");
}