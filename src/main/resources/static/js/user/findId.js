document.getElementById('findIdForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const userName = document.getElementById('userName').value;
    const phoneNumber = document.getElementById('phoneNumber').value;

    fetch('/api/findId', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: userName, phoneNumber: phoneNumber })
    })
    .then(function (response) {
        if (response.ok) {
            return response.json();
        } else {
           return response.text().then(function (text) {
               const error = text ? JSON.parse(text) : { message: '서버에 오류가 발생했습니다.' };
               throw new Error(error.message);
           });
       }
    })
    .then(function (result) {
        // 성공적으로 userId를 찾았을 때 표시
        document.getElementById('userId').textContent = result.message;
        document.getElementById('resultContainer').style.display = 'block';
    })
    .catch(function (error) {
        console.error('Error:', error);
        // 에러 메시지를 표시
        document.getElementById('userId').textContent = error.message;
        document.getElementById('resultContainer').style.display = 'block';
    });
});