document.getElementById('signUpForm').addEventListener('submit', function(e) {
    e.preventDefault(); // 폼 기본 제출 동작을 막음

    // 모든 오류 메시지를 초기화
    const errorMessages = document.querySelectorAll('.error-message');
    errorMessages.forEach((msg) => msg.textContent = '');

    // 입력값 가져오기
    const email = document.getElementById('email').value.trim();
    const username = document.getElementById('name').value.trim();
    const phoneNumber = document.getElementById('phoneNumber').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPw = document.getElementById('passwordConfirm').value.trim();

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

    // 모든 검증이 통과되면 서버로 데이터 전송
    const userData = {
        name: username,
        phoneNumber: phoneNumber,
        password: password,
        passwordConfirm: confirmPw
    };

    fetch('/profile/edit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)  // FormData 대신 JSON으로 전송
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || '서버 응답이 실패했습니다.');
            });
        }
        // 성공 메시지 표시
        alert('프로필이 성공적으로 수정되었습니다.');
        window.location.href = '/profile/myProfile'
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || '프로필 수정 중 오류가 발생했습니다.');
    });
});