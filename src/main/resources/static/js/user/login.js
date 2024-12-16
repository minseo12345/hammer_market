//const loginForm = document.getElementById("loginForm");
//loginForm.addEventListener("submit", (event) => {
//    event.preventDefault(); // 폼 제출 기본 동작 방지
//
//    // 입력된 데이터를 가져옴
//    const email = document.getElementById("userEmail").value;
//    const password = document.getElementById("password").value;
//
//    // 서버로 보낼 데이터 객체 생성
//    const loginData = {
//        email: email,
//        password: password,
//    };
//    console.log(loginData);
//    // fetch 함수로 POST 요청 보내기
//    fetch("/api/jwt-login", {
//        method: "POST", // HTTP 메서드
//        headers: {
//            "Content-Type": "application/json" // JSON 데이터 전송
//        },
//        body: JSON.stringify(loginData) // 객체를 JSON 문자열로 변환
//    })
//        .then(response => {
//            if (!response.ok) {
//                throw new Error(`HTTP error! status: ${response.status}`);
//            }
//            return response.json(); // 서버에서 반환된 JSON 처리
//        })
//        .then(data => {
//            window.location.href = "/login";
//        })
//        .catch(error => {
//            console.error("로그인 실패:", error);
//            alert("로그인 중 오류가 발생했습니다.");
//        });
//});
