const loginForm = document.getElementById("loginForm");
loginForm.addEventListener("submit", (event) => {
    event.preventDefault(); // 폼 제출 기본 동작 방지

    // 입력된 데이터를 가져옴
    const email = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const rememberMe = document.getElementById('remember-me').checked ? 'true' : 'false';

    // 입력값 검증
    if (!email || !password) {
        alert("이메일과 비밀번호를 모두 입력해주세요.");
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert("올바른 이메일 형식을 입력해주세요.");
            return;
        }

    const loginData = {
        email: email,
        password: password,
        rememberMe: rememberMe
    };
    // fetch 함수로 POST 요청 보내기
    fetch("/jwt-login", {
        method: "POST", // HTTP 메서드
        headers: {
            "Content-Type": "application/json" // JSON 데이터 전송
        },
        body: JSON.stringify(loginData) // 객체를 JSON 문자열로 변환
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json()
    })
    .then((data) => {
        // 성공 시 필요한 추가 로직 (예: 토큰 저장)
        alert("로그인에 성공했습니다!");
        window.location.href = "/items/list"; // 성공 시 페이지 이동

    })
    .catch((error) => {
        console.error("로그인 실패:", error);
        alert(`로그인 실패: ${error.message}`);
    });
});
