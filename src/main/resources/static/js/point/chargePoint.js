document.addEventListener('DOMContentLoaded', () => {
    const balanceElement = document.getElementById('balance');
    let currentBalance = balanceElement.textContent.replace(',', '').replace('P', '').trim();  // balanceElement에서 값 가져오기

    const pointButtons = document.querySelectorAll('.charge-points button');
    const nextButton = document.querySelector('.charge-footer .next');
    const cancelButton = document.querySelector('.charge-footer .cancel');
    const descriptionInput = document.getElementById('description');

    function resetState() {
        pointButtons.forEach(btn => btn.classList.remove('selected'));
        nextButton.disabled = true;
        descriptionInput.value = '';
    }

    pointButtons.forEach(button => {
        button.addEventListener('click', () => {
            pointButtons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');
            nextButton.disabled = false;
        });
    });

    cancelButton.addEventListener('click', () => {
        alert('충전을 취소했습니다.');
        resetState();
    });

    nextButton.addEventListener('click', () => {
        const selectedButton = document.querySelector('.charge-points button.selected');
        if (!selectedButton) {
            alert('충전할 포인트를 선택해주세요.');
            return;
        }

        const selectedValue = selectedButton.dataset.value;
        if (!confirm(`${selectedValue.toLocaleString()}P를 충전하시겠습니까?`)) {
            return;
        }

        processCharge(selectedValue);
    });

    function processCharge(selectedValue) {
        currentBalance = (parseInt(currentBalance.replace(',', '').replace('P', '').trim()) + parseInt(selectedValue)).toLocaleString();
        balanceElement.textContent = `${currentBalance}P`;

        const description = descriptionInput.value.trim() || '충전';
        const userId = window.location.pathname.split("/")[3];  // 사용자 ID 가져오기

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
                document.write(html);  // 서버에서 받은 응답 처리
                alert(`${selectedValue.toLocaleString()}P 충전이 완료되었습니다.`);
            })
            .catch(error => {
                console.error('충전 중 오류:', error);
                alert('충전 요청이 실패했습니다. 다시 시도해주세요.');
            });

        resetState();
    }
});
