// // 사용자 ID 설정 및 관리
// let currentUserId = null;
//
// // 로컬 스토리지에 사용자별 장바구니 저장
// const saveCart = (cart) => {
//     if (!currentUserId) {
//         alert("Please set a User ID first.");
//         return;
//     }
//     localStorage.setItem(`cart_${currentUserId}`, JSON.stringify(cart));
// };
//
// // 로컬 스토리지에서 사용자별 장바구니 가져오기
// const getCart = () => {
//     if (!currentUserId) {
//         alert("Please set a User ID first.");
//         return [];
//     }
//     const cart = localStorage.getItem(`cart_${currentUserId}`);
//     return cart ? JSON.parse(cart) : [];
// };
//
// // 장바구니 UI 업데이트
// const updateCartUI = () => {
//     const cart = getCart();
//     const cartEl = document.getElementById("cart");
//     cartEl.innerHTML = ""; // 기존 장바구니 초기화
//
//     cart.forEach((item, index) => {
//         const li = document.createElement("li");
//         li.textContent = `${item.name} - $${item.price} x ${item.quantity}`;
//         const removeButton = document.createElement("button");
//         removeButton.textContent = "Remove";
//         removeButton.onclick = () => removeFromCart(index);
//         li.appendChild(removeButton);
//         cartEl.appendChild(li);
//     });
// };
//
// // 장바구니에 아이템 추가
// const addToCart = (product) => {
//     const cart = getCart();
//     const existingItem = cart.find((item) => item.id === product.id);
//     if (existingItem) {
//         existingItem.quantity += 1; // 수량 증가
//     } else {
//         cart.push({ ...product, quantity: 1 });
//     }
//     saveCart(cart);
//     updateCartUI();
// };
//
// // 장바구니에서 아이템 제거
// const removeFromCart = (index) => {
//     const cart = getCart();
//     cart.splice(index, 1); // 해당 인덱스 아이템 제거
//     saveCart(cart);
//     updateCartUI();
// };
//
// // 장바구니 초기화
// const clearCart = () => {
//     if (!currentUserId) {
//         alert("Please set a User ID first.");
//         return;
//     }
//     localStorage.removeItem(`cart_${currentUserId}`);
//     updateCartUI();
// };
//
// // 사용자 ID 설정
// document.getElementById("setUser").onclick = () => {
//     const userIdInput = document.getElementBy
