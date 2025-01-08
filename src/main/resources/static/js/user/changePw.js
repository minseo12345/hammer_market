document.getElementById('changePwForm').addEventListener('submit', function (event) {
    event.preventDefault();  // 폼 제출 시 페이지 새로고침 방지

    // 폼 필드 값 가져오기
    const email = document.getElementById('email').value;
    const newPassword = document.getElementById('newPassword').value;
    const newConfirmPw = document.getElementById('newConfirmPw').value;

    // 비밀번호 확인 일치 여부 체크
    const passRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{8,20}$/;
    if (!passRegex.test(newPassword)) {
        document.getElementById('passwordError').textContent = '비밀번호는 영문+숫자+특수문자 포함 8~20자여야 합니다.';
        return;
    }

    if (newPassword !== newConfirmPw) {
        document.getElementById('confirmPwError').textContent = '비밀번호가 일치하지 않습니다.';
        return;
    }

    // 서버에 보낼 데이터 생성
    const data = {
        email: email,
        password: newPassword
    };

    // fetch를 사용하여 서버로 POST 요청
    fetch('/api/changePw', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)  // JSON으로 요청 본문 전송
    })
    .then(function (response) {  // 응답을 JSON으로 파싱
        if (response.ok) {
            // 비밀번호 변경 성공 시
            alert('비밀번호가 성공적으로 변경되었습니다. 다시 로그인 해주세요');
            window.location.href = '/login';

        } else {
            // 비밀번호 변경 실패 시
            alert('비밀번호 변경에 실패했습니다. ' + response.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('서버와의 통신 중 오류가 발생했습니다.');
    });
});