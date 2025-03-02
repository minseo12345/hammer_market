
document.addEventListener("DOMContentLoaded", () => {
    getCurrentUserId().then((userId) => {
        syncWithServer(userId).then(() => loadCart(userId));
    });
});

function getCurrentUserId() {
    return fetch('/api/currentUser')
        .then((response) => {
            if (!response.ok) throw new Error('활성화된 세션이나 로그인된 유저가 없습니다.');
            return response.json();
        })
        .then((user) => {
            return user.userId;
        })
        .catch((err) => {
            console.error('세션 정보 에러:', err);
            alert('로그인 필요');
            window.location.href = '/login';
        });
}

function loadCart(userId) {
    const cartItems = JSON.parse(localStorage.getItem(userId)) || [];
    const cartContainer = document.getElementById("cartItems");
    cartContainer.innerHTML = ""; // Clear the list before adding items

    cartItems.forEach((item) => {
        // Create a card for each item
        const cardCol = document.createElement("div");
        cardCol.classList.add("col-lg-3", "col-md-4", "col-sm-6", "mb-4"); // One row fits 4 cards on large screens

        const card = document.createElement("a");
        card.href = `/items/detail/${item.itemId}`;
        card.classList.add("card", "auction-card", "h-100"); // Full height card

        card.dataset.title = item.title;
        card.dataset.startingBid = item.startingBid;
        card.dataset.buyNowPrice = item.buyNowPrice;
        card.dataset.endTime = item.endTime;
        card.dataset.status = item.status;
        card.dataset.imgSrc = item.fileUrl;

        const cardImg = document.createElement("img");
        cardImg.classList.add("card-img-top");
        cardImg.src = item.fileUrl; // Assuming each item has an image URL
        cardImg.alt = "상품 이미지";

        const cardBody = document.createElement("div");
        cardBody.classList.add("card-body");

        const cardTitle = document.createElement("h5");
        cardTitle.classList.add("card-title");
        cardTitle.textContent = item.title;

        const cardText = document.createElement("div");
        cardText.classList.add("card-text");

        const bidInfo = document.createElement("p");
        bidInfo.innerHTML = `
    <strong>시작가:</strong> ${Math.floor(item.startingBid).toLocaleString()+'p'}<br>
    <strong>즉시 구매가:</strong> ${item.buyNowPrice != null ? Math.floor(item.buyNowPrice).toLocaleString() + 'p' : '즉시구매불가'}<br>
    <strong>상태:</strong> ${
            item.status === 'ONGOING' ? '진행중' :
                item.status === 'BIDDING_END' ? '낙찰' :
                    item.status === 'COMPLETED' ? '거래완료' :
                        item.status === 'CANCELLED' ? '거래취소' :
                            item.status === 'PARTIALLY_APPROVE' ? '거래수락 대기' : '알 수 없음'
        }
`;

        // Add "Remove from Cart" button
        const removeButton = document.createElement("button");
        removeButton.classList.add("btn", "btn-danger", "mt-2");
        removeButton.textContent = "삭제";
        removeButton.onclick = (e) => {
            e.preventDefault();
            removeFromCart(item.itemId, userId);
        };

        // Append elements
        cardText.appendChild(bidInfo);
        cardText.appendChild(removeButton);

        cardBody.appendChild(cardTitle);
        cardBody.appendChild(cardText);

        card.appendChild(cardImg);
        card.appendChild(cardBody);

        cardCol.appendChild(card);
        cartContainer.appendChild(cardCol);
    });
}


function removeFromCart(itemId, userId) {
    let cartItems = JSON.parse(localStorage.getItem(userId)) || [];
    cartItems = cartItems.filter(item => item.itemId !== itemId);
    localStorage.setItem(userId, JSON.stringify(cartItems));
    loadCart(userId);
}

async function syncWithServer(userId) {
    try {
        // Fetch local cart items
        const localCart = JSON.parse(localStorage.getItem(userId)) || [];
        console.log("Local Cart Items before Sync:", localCart);
        const localCartMap = new Map(localCart.map(item => [item.itemId, item]));

        // Send item IDs of the local cart to the server
        const localItemIds = Array.from(localCartMap.keys());

        const response = await fetch("/api/sync-cart", { // Replace with the actual API URL
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ userId, itemIds: localItemIds }), // Send userId and itemIds to server
        });

        if (!response.ok) throw new Error("Failed to sync cart with server.");

        const serverItems = await response.json();

        // Update only the items that exist in local storage
        serverItems.forEach(serverItem => {
            if (localCartMap.has(serverItem.itemId)) {
                localCartMap.set(serverItem.itemId, {
                    ...localCartMap.get(serverItem.itemId),
                    ...serverItem, // Update with the latest server data
                });
            }
        });

        // Save updated cart back to localStorage
        const updatedCart = Array.from(localCartMap.values());
        localStorage.setItem(userId, JSON.stringify(updatedCart));

        console.log("Cart synced successfully with server.");
    } catch (error) {
        console.error("Error syncing with server:", error);
    }
}