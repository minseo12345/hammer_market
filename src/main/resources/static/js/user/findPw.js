document.getElementById('sendCtfNoBtn').addEventListener('click', function (event) {
    event.preventDefault();

    const userName = document.getElementById('userName').value;
    const email = document.getElementById('email').value;

    fetch('/api/sendCtfNo', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ username: userName, email: email }) // JSON 데이터 전송
    })
    .then(function (response) {
        if (response.ok) {
            return response.json(); // 응답 본문을 JSON으로 파싱
        } else {
            return response.json().then(function (error) {
                throw new Error(error.message || '오류가 발생했습니다.');
            });
        }
    })
    .then(function (result) {
        // 성공적으로 인증번호가 전송된 경우
        alert('인증번호가 이메일로 발송되었습니다.');

        // 인증번호 입력란 활성화
        document.getElementById('comCtfNo').disabled = false;
        // 인증번호 발송 버튼 비활성화
        document.getElementById('sendCtfNoBtn').disabled = true;
        // 인증번호 확인 버튼 활성화
        document.getElementById('comCtfNoBtn').disabled = false;
        // 이름과 이메일 입력란 비활성화
        document.getElementById('userName').disabled = true;
        document.getElementById('email').disabled = true;

        // 인증번호 제한 시간 5분 (300초) 카운트다운 시작
        startCountdown(300);
    })
    .catch(function (error) {
        console.error('Error:', error);
        // 에러 메시지를 표시
        alert(error.message || '서버와의 통신 중 오류가 발생했습니다.');
    });
});


document.getElementById('comCtfNoBtn').addEventListener('click', function (event) {
    event.preventDefault();

    const userName = document.getElementById('userName').value;
    const email = document.getElementById('email').value;
    const ctfNo = document.getElementById('comCtfNo').value;

    fetch('/api/comCtfNo', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ username: userName, email: email, ctfNo: ctfNo }) // JSON 데이터 전송
    })
    .then(function (response) {
        if (response.ok) {
            return response.json(); // 응답 본문을 JSON으로 파싱
        } else {
            return response.json().then(function (error) {
                throw new Error(error.message || '오류가 발생했습니다.');
            });
        }
    })
    .then(function (result) {
        // 정상적으로 인증이 성공한 경우
        console.log("message"+result.message);
        window.location.href = `/login/changePw?username=${encodeURIComponent(userName)}&email=${encodeURIComponent(email)}`;
    })
    .catch(function (error) {
        console.error('Error:', error);
        // 에러 메시지를 표시
        alert(error.message || '서버와의 통신 중 오류가 발생했습니다.');
    });
});

function startCountdown(seconds) {
    const countdownDisplay = document.createElement('div');
    countdownDisplay.id = 'countdownTimer';
    document.body.appendChild(countdownDisplay);

    let timeLeft = seconds;
    const countdownInterval = setInterval(function () {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        countdownDisplay.textContent = `남은 시간: ${minutes}분 ${seconds}초`;

        if (timeLeft <= 0) {
            clearInterval(countdownInterval);
            countdownDisplay.textContent = '시간 초과';
            document.getElementById('comCtfNo').disabled = true; // 시간 초과 시 인증번호 입력 비활성화
        } else {
            timeLeft--;
        }
    }, 1000); // 1초마다 타이머 업데이트
}