document.addEventListener('DOMContentLoaded', () => {
    const balanceElement = document.getElementById('balance');
    let currentBalance = balanceElement.textContent.replace(',', '').replace('P', '').trim();  // balanceElement에서 값 가져오기

    const pointButtons = document.querySelectorAll('.charge-points button');
    const nextButton = document.getElementById('next');
    const cancelButton = document.getElementById('cancel');
    const descriptionInput = document.getElementById('description');

    // 상태 초기화 함수
    function resetState() {
        pointButtons.forEach(btn => btn.classList.remove('selected'));
        nextButton.disabled = true;
        descriptionInput.value = '';
    }

    // 포인트 선택 시 처리
    pointButtons.forEach(button => {
        button.addEventListener('click', () => {
            pointButtons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');
            nextButton.disabled = false;
        });
    });

    // 취소 버튼 클릭 처리
    cancelButton.addEventListener('click', () => {
        alert('환전을 취소했습니다.');
        resetState();
    });

    // 환전 버튼 클릭 처리
    nextButton.addEventListener('click', () => {
        const selectedButton = document.querySelector('.charge-points button.selected');
        if (!selectedButton) {
            alert('환전할 포인트를 선택해주세요.');
            return;
        }

        const selectedValue = selectedButton.dataset.value;
        if (parseInt(selectedValue) > parseInt(currentBalance.replace(',', '').replace('P', '').trim())) {
            alert('잔액 부족! 환전할 포인트를 다시 선택해주세요.');
            return;
        }

        if (!confirm(`${selectedValue.toLocaleString()}P를 환전하시겠습니까?`)) {
            return;
        }

        // 환전 처리 로직
        processExchange(selectedValue);
    });

    // 환전 처리 함수
    function processExchange(selectedValue) {
        currentBalance = (parseInt(currentBalance.replace(',', '').replace('P', '').trim()) - parseInt(selectedValue)).toLocaleString();
        balanceElement.textContent = `${currentBalance}P`;  // 새 잔액 갱신

        const description = descriptionInput.value.trim() || '환전';
        const userId = window.location.pathname.split("/")[3];
            console.log(userId)

        const formData = new FormData();
        formData.append('pointAmount', selectedValue);
        formData.append('description', description);

        fetch(`/points/currency/${userId}`, {
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
                alert(`${selectedValue.toLocaleString()}P 환전이 완료되었습니다.`);
            })
            .catch(error => {
                console.error('환전 중 오류:', error);
                alert('환전 요청이 실패했습니다. 다시 시도해주세요.');
            });

        resetState();
    }
});
