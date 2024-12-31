let currentBalance = 0;

const pointButtons = document.querySelectorAll('.charge-points button');
const nextButton = document.querySelector('.charge-footer .next');
const balanceDisplay = document.getElementById('balance');
const cancelButton = document.querySelector('.charge-footer .cancel');
const descriptionInput = document.getElementById('description');

pointButtons.forEach(button => {
    button.addEventListener('click', () => {
        pointButtons.forEach(btn => btn.classList.remove('selected'));
        button.classList.add('selected');
        nextButton.disabled = false;
    });
});


cancelButton.addEventListener('click', () => {
    alert('충전을 취소했습니다.');
    pointButtons.forEach(btn => btn.classList.remove('selected'));
    nextButton.disabled = true;
    currentBalance = 0;
    balanceDisplay.textContent = currentBalance.toLocaleString();
    descriptionInput.value = '';
});

nextButton.addEventListener('click', () => {
    const selectedButton = document.querySelector('.charge-points button.selected');

    if (!selectedButton) {
        alert('포인트를 선택해 주세요!');
        return;
    }

    const selectedValue = parseInt(selectedButton.dataset.value, 10);

    if (isNaN(selectedValue)) {
        alert('유효한 포인트 값을 선택해 주세요!');
        return;
    }

    currentBalance += selectedValue;

    balanceDisplay.textContent = currentBalance.toLocaleString();

    alert(`${selectedValue.toLocaleString()}P가 충전되었습니다! 현재 보유 포인트: ${currentBalance.toLocaleString()}P`);

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
            alert('포인트 충전이 완료되었습니다!');
        })
        .catch(error => {
            console.error('포인트 충전 중 오류 발생:', error);
            alert('포인트 충전 중 오류가 발생했습니다.');
        });


    pointButtons.forEach(btn => btn.classList.remove('selected'));
    nextButton.disabled = true;
    descriptionInput.value = '';
});
