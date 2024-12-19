document.getElementById('.cancelBtn').addEventListener('click', () => location.href = '/login');
const signupForm = document.getElementById("signup-form");
signupForm.addEventListener("submit", (event) => {
    event.preventDefault(); // 폼 제출 기본 동작 방지

    // 입력된 데이터를 가져옴
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const username = document.getElementById("userName").value;
    const phone = document.getElementById("phoneNumber").value;

    // 서버로 보낼 데이터 객체 생성
    const userData = {
        email: email,
        password: password,
        userName: username,
        phoneNumber: phone
    };

    // fetch 함수로 POST 요청 보내기
    fetch("/api/join", {
        method: "POST", // HTTP 메서드
        headers: {
            "Content-Type": "application/json" // JSON 데이터 전송
        },
        body: JSON.stringify(userData) // 객체를 JSON 문자열로 변환
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json(); // 서버에서 반환된 JSON 처리
        })
        .then(data => {
            window.location.href = "/login";
        })
        .catch(error => {
            console.error("회원가입 실패:", error);
            alert("회원가입 중 오류가 발생했습니다.");
        });
});
