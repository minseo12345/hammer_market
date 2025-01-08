document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        let isValid = true;

        // 제목 검증
        const title = document.getElementById('title');
        const titleError = document.getElementById('titleError');
        if (title.value.trim().length < 2) {
            titleError.textContent = '제목은 최소 2자 이상이어야 합니다.';
            titleError.style.color = 'red';
            isValid = false;
        } else {
            titleError.textContent = '';
        }

        // 설명 검증
        const description = document.getElementById('description');
        const descriptionError = document.getElementById('descriptionError');
        if (description.value.trim().length < 10) {
            descriptionError.textContent = '설명은 최소 10자 이상이어야 합니다.';
            descriptionError.style.color = 'red';
            isValid = false;
        } else {
            descriptionError.textContent = '';
        }

        // 시작가 검증
        const startingBid = document.getElementById('startingBid');
        const startingBidError = document.getElementById('startingBidError');
        if (startingBid.value <= 0) {
            startingBidError.textContent = '시작가는 0보다 커야 합니다.';
            startingBidError.style.color = 'red';
            isValid = false;
        } else if (startingBid.value < 100) {
            startingBidError.textContent = '시작가는 100원 이상이어야 합니다.';
            startingBidError.style.color = 'red';
            isValid = false;
        } else {
            startingBidError.textContent = '';
        }

        // 즉시 구매가 검증 (입력된 경우에만)
        const buyNowPrice = document.getElementById('buyNowPrice');
        const buyNowPriceError = document.getElementById('buyNowPriceError');
        if (buyNowPrice.value !== '') {
            if (parseInt(buyNowPrice.value) <= parseInt(startingBid.value)) {
                buyNowPriceError.textContent = '즉시 구매가는 시작가보다 커야 합니다.';
                buyNowPriceError.style.color = 'red';
                isValid = false;
            } else {
                buyNowPriceError.textContent = '';
            }
        }

        // 카테고리 검증
        const categoryId = document.getElementById('categoryId');
        const categoryError = document.getElementById('categoryError');
        if (!categoryId.value) {
            categoryError.textContent = '카테고리를 선택해주세요.';
            categoryError.style.color = 'red';
            isValid = false;
        } else {
            categoryError.textContent = '';
        }

        // 이미지 파일 검증
        const image = document.getElementById('image');
        const imageError = document.getElementById('imageError');
        if (image.files.length > 0) {
            const file = image.files[0];
            const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
            if (!validTypes.includes(file.type)) {
                imageError.textContent = '이미지 파일만 업로드 가능합니다. (JPEG, PNG, JPG)';
                imageError.style.color = 'red';
                isValid = false;
            } else if (file.size > 10 * 1024 * 1024) { // 10MB 제한
                imageError.textContent = '파일 크기는 10MB 이하여야 합니다.';
                imageError.style.color = 'red';
                isValid = false;
            } else {
                imageError.textContent = '';
            }
        }

        // 모든 검증을 통과하면 폼 제출
        if (isValid) {
            form.submit();
        }
    });
});