let currentBalance = parseInt(document.querySelector('.balance-info').dataset.currentPoint, 10) || 0;

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
    alert('환전을 취소했습니다.');
    pointButtons.forEach(btn => btn.classList.remove('selected'));
    nextButton.disabled = true;
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

    if (selectedValue > currentBalance) {
        alert('현재 보유 포인트보다 많은 금액을 환전할 수 없습니다!');
        return;
    }

    currentBalance -= selectedValue;
    balanceDisplay.textContent = currentBalance.toLocaleString();

    alert(`${selectedValue.toLocaleString()}P를 환전 하시겠습니까?`);

    const description = descriptionInput.value.trim() || '환전';

    const userId = window.location.pathname.split("/")[3];

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
            alert('포인트 환전이 완료되었습니다!');
        })
        .catch(error => {
            console.error('포인트 환전 중 오류 발생:', error);
            alert('포인트 환전 중 오류가 발생했습니다.');
        });

    pointButtons.forEach(btn => btn.classList.remove('selected'));
    nextButton.disabled = true;
    descriptionInput.value = '';
});
