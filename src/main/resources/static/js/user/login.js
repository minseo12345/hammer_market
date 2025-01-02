const loginForm = document.getElementById("loginForm");
loginForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const email = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const rememberMe = document.getElementById('remember-me').checked ? 'true' : 'false';

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

    fetch("/jwt-login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData),
        credentials: 'include'  // 쿠키를 포함하기 위해 추가
    })
    .then((response) => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text);
            });
        }
        return response.text();  // JSON이 아닌 text로 변경
    })
    .then((data) => {
        alert("로그인에 성공했습니다!");
        window.location.href = "/items/list";  // 경로 수정
    })
    .catch((error) => {
        console.error("로그인 실패:", error);
        alert(`로그인 실패: ${error.message}`);
    });
});