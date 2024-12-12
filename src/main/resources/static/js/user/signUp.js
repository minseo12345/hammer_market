document.querySelector('.cancelBtn').addEventListener('click', () => location.href = '/user/login');


    
document.getElementById('signUpForm');.addEventListener('submit', async function(e) {
    e.preventDefault(); // 기본 제출 동작 막기

    // 데이터 객체 생성
    const formData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        userName: document.getElementById('userName').value,
        phoneNumber: document.getElementById('phoneNumber').value
    };

    try {
        const response = await axios.post(API_ENDPOINTS.USER.SIGN_UP, formData, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 200) {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/login'; // 로그인 페이지로 리다이렉트
        }
    } catch (error) {
        console.error('Error:', error);
        alert('회원가입 중 오류가 발생했습니다.');
    }
});