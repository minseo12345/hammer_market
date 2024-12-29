let currentBalance = 0;

const pointButtons = document.querySelectorAll('.charge-points button');
const nextButton = document.querySelector('.charge-footer .next');
const balanceDisplay = document.getElementById('balance');
const cancelButton = document.querySelector('.charge-footer .cancel');
const descriptionInput = document.getElementById('description');

// 포인트 버튼 클릭 이벤트
pointButtons.forEach(button => {
    button.addEventListener('click', () => {
        pointButtons.forEach(btn => btn.classList.remove('selected'));  // 기존 선택된 버튼 제거
        button.classList.add('selected');  // 클릭한 버튼 선택
        nextButton.disabled = false;  // '다음' 버튼 활성화
    });
});

// 충전 취소 버튼
cancelButton.addEventListener('click', () => {
    alert('충전을 취소했습니다.');
    pointButtons.forEach(btn => btn.classList.remove('selected'));  // 버튼 상태 초기화
    nextButton.disabled = true;  // '다음' 버튼 비활성화
    currentBalance = 0;  // 충전 취소 시 보유 포인트 초기화
    balanceDisplay.textContent = currentBalance.toLocaleString();  // 초기화된 값으로 표시
    descriptionInput.value = '';  // 메모 초기화
});

// 충전하기 버튼 클릭 이벤트
nextButton.addEventListener('click', () => {
    const selectedButton = document.querySelector('.charge-points button.selected');

    if (!selectedButton) {
        alert('포인트를 선택해 주세요!');
        return;  // 포인트가 선택되지 않았을 경우 처리
    }

    const selectedValue = parseInt(selectedButton.dataset.value, 10);

    if (isNaN(selectedValue)) {
        alert('유효한 포인트 값을 선택해 주세요!');
        return;  // 유효하지 않은 값일 경우 처리
    }

    // 현재 보유 포인트에 추가
    currentBalance += selectedValue;

    // 포인트 표시 업데이트
    balanceDisplay.textContent = currentBalance.toLocaleString();

    // 충전 완료 알림
    alert(`${selectedValue.toLocaleString()}P가 충전되었습니다! 현재 보유 포인트: ${currentBalance.toLocaleString()}P`);

    // description 값 가져오기
    const description = descriptionInput.value.trim() || '충전';  // 빈 값일 경우 기본값 '충전'으로 처리

    const userId = window.location.pathname.split("/")[3];

    // FormData 객체 생성
    const formData = new FormData();
    formData.append('pointAmount', selectedValue);
    formData.append('description', description);

    // FormData를 사용해 포인트 충전 API 호출
    fetch(`/points/charge/${userId}`, {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (!response.ok) {
                // 서버에서 오류가 발생한 경우
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text();
        })
        .then(html => {
            // 서버가 반환하는 HTML을 받아서 페이지에 삽입
            document.write(html);
            alert('포인트 충전이 완료되었습니다!');
        })
        .catch(error => {
            console.error('포인트 충전 중 오류 발생:', error);
            alert('포인트 충전 중 오류가 발생했습니다.');
        });

    // 상태 초기화
    pointButtons.forEach(btn => btn.classList.remove('selected'));
    nextButton.disabled = true;  // '다음' 버튼 비활성화
    descriptionInput.value = '';  // 메모 초기화
});
``