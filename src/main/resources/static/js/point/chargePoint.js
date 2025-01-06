document.addEventListener('DOMContentLoaded', () => {
    const balanceElement = document.getElementById('balance');
    let currentBalance = balanceElement.textContent.replace(',', '').replace('P', '').trim();

    const pointButtons = document.querySelectorAll('.charge-points button');
    const nextButton = document.querySelector('.charge-footer .next');
    const cancelButton = document.querySelector('.charge-footer .cancel');
    const descriptionInput = document.getElementById('description');

    function resetState() {
        pointButtons.forEach(btn => btn.classList.remove('selected'));
        nextButton.disabled = true;  // 포인트를 선택하지 않으면 버튼 비활성화
        descriptionInput.value = '';
    }

    pointButtons.forEach(button => {
        button.addEventListener('click', () => {
            pointButtons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');
            nextButton.disabled = false;  // 포인트 선택 시 버튼 활성화
        });
    });

    cancelButton.addEventListener('click', () => {
        const userId = window.location.pathname.split("/")[3];
        if (confirm('충전을 취소하고 이전 페이지로 돌아가시겠습니까?')) {
            window.location.href = `/points/select/${userId}`;
        }
    });


    nextButton.addEventListener('click', () => {
        const selectedButton = document.querySelector('.charge-points button.selected');

        // 포인트를 선택하지 않으면 경고 알림
        if (!selectedButton) {
            alert('충전할 포인트를 선택해주세요.');
            return;  // 선택이 없으면 함수 종료
        }

        const selectedValue = selectedButton.dataset.value;

        if (!confirm(`${Number(selectedValue).toLocaleString()}P를 충전하시겠습니까?`)) {
            return;  // 확인을 취소하면 충전하지 않음
        }

        processCharge(selectedValue);
    });

    function processCharge(selectedValue) {
        // 보유 포인트에 충전 금액 더하기
        currentBalance = (parseInt(currentBalance.replace(',', '').replace('P', '').trim()) + parseInt(selectedValue)).toLocaleString();
        balanceElement.textContent = `${currentBalance}`;  // 업데이트된 보유 포인트 표시

        const description = descriptionInput.value.trim() || '충전';
        const userId = window.location.pathname.split("/")[3];

        const formData = new FormData();
        formData.append('pointAmount', selectedValue);
        formData.append('description', description);

        fetch(`/points/charge/${userId}`, {
            method: 'POST',
            body: formData,
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text);
                    });
                }
                return response.text();
            })
            .then(html => {
                document.write(html);
                alert(`${Number(selectedValue).toLocaleString()}P 충전이 완료되었습니다.`);
            })
            .catch(error => {
                console.error('충전 중 오류:', error);
                alert('충전 요청이 실패했습니다. 다시 시도해주세요.');
            });

        resetState();
    }
});
