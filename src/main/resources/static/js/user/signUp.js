// 폼 검증 함수
document.getElementById('signUpForm').addEventListener('submit', function(e) {
    e.preventDefault(); // 폼 기본 제출 동작을 막음

    // 모든 오류 메시지를 초기화
    const errorMessages = document.querySelectorAll('.error-message');
    errorMessages.forEach((msg) => msg.textContent = '');

    // 입력값 가져오기
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPw = document.getElementById('confirmPw').value.trim();
    const username = document.getElementById('username').value.trim();
    const phoneNumber = document.getElementById('phoneNumber').value.trim();

    // 이메일 확인
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (email === '') {
        document.getElementById('emailError').textContent = '이메일을 입력해 주세요';
        return;
    }

    if (!emailRegex.test(email)) {
        document.getElementById('emailError').textContent = '유효한 이메일 형식으로 입력해주세요.';
        return;
    }

    // 비밀번호 확인
    const passRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{8,20}$/;
    if (!passRegex.test(password)) {
        document.getElementById('passwordError').textContent = '비밀번호는 영문+숫자+특수문자 포함 8~20자여야 합니다.';
        return;
    }

    if (password !== confirmPw) {
        document.getElementById('confirmPwError').textContent = '비밀번호가 일치하지 않습니다.';
        return;
    }

    // 이름 검증 (2~30자 한글)
    const nameRegex = /^[a-zA-Z가-힣]{2,30}$/;
    if (!nameRegex.test(username)) {
        document.getElementById('usernameError').textContent = '이름은 2~30자 한글, 영문로 입력해야 합니다.';
        return;
    }

    // 전화번호 형식 검증 (010으로 시작하는 10~11자리 숫자)
    const phoneRegex = /^010\d{7,8}$/;
    if (!phoneRegex.test(phoneNumber)) {
        document.getElementById('phoneNumberError').textContent = '전화번호는 10~11자리 숫자여야 합니다.';
        return;
    }

    // 모든 검증 통과 시 폼 제출
    fetch('/api/signup', {
        method: 'POST',
        body: JSON.stringify({ email, password, username, phoneNumber }),
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            // 응답이 OK가 아니면, 에러 메시지 파싱
            return response.json().then(err => {
                throw new Error(err.message || '알 수 없는 오류가 발생했습니다.');
            });
        }
        // 응답이 성공적이면
        return response.json(); // JSON 형식으로 응답 받기
    })
    .then(data => {
        // 성공적인 응답을 받았을 때
        if (data.message) {
            alert(data.message); // 서버에서 보낸 메시지 출력
            if (data.message === "회원가입 성공") {
                // 회원가입이 성공하면 로그인 페이지로 리다이렉트
                window.location.href = '/login';
            }
        }
    })
    .catch(error => {
        // 에러 처리
        alert(error.message); // 에러 메시지 출력
    });
});
//    this.submit();
