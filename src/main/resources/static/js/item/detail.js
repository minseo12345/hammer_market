document.addEventListener('DOMContentLoaded', function() {
    var itemStatus = document.getElementById('item-details').getAttribute('data-item-status');
    console.log(itemStatus)
    var bidAmountInput = document.getElementById('bidAmount');
    var submitBidButton = document.getElementById('submit-bid');
    var buyNowButton = document.getElementById('buy-now');
    var auctionEndedMessage = document.createElement('p');

    if (itemStatus === 'BIDDING_END') {
        bidAmountInput.disabled = true;
        bidAmountInput.value = '';
        bidAmountInput.placeholder = '경매 종료';
        auctionEndedMessage.textContent = '경매 종료';
        auctionEndedMessage.style.color = 'red';
        bidAmountInput.parentElement.appendChild(auctionEndedMessage);
        submitBidButton.disabled = true;
        buyNowButton.disabled = true;
    }

    submitBidButton.addEventListener('click', function(event) {
        if (itemStatus === 'BIDDING_END') {
            event.preventDefault();
            alert('경매가 종료되었습니다.');
            return;
        }

        var bidAmount = bidAmountInput.value;
        var buyNowPrice = document.getElementById('buy-now-price').textContent.trim();

        if (!bidAmount) {
            event.preventDefault();
            alert('입찰 금액을 입력해주세요.');
            return;
        }

        if (Number(bidAmount) > Number(buyNowPrice)) {
            event.preventDefault();
            alert('입찰 금액이 즉시 구매가보다 높습니다. 다시 시도해주세요.');
        } else {
            var form = document.getElementById('bid-form');
            var formData = new FormData(form);

            var xhr = new XMLHttpRequest();
            xhr.open('POST', '/bid', true);

            xhr.onload = function() {
                if (xhr.status === 200) {
                    alert('입찰 성공! 현재가가 갱신되었습니다.');
                    location.reload();  // 성공하면 페이지 새로고침
                } else {
                    var errorMessage = xhr.responseText || '입찰 실패! 다시 시도해주세요.';
                    alert(errorMessage);
                }
            };

            xhr.onerror = function() {
                alert('서버와의 연결에 실패했습니다. 다시 시도해주세요.');
            };

            xhr.send(formData);
        }
    });

    buyNowButton.addEventListener('click', function(event) {
        if (itemStatus === 'BIDDING_END') {
            event.preventDefault();
            alert('경매가 종료되었습니다.');
            return;
        }

        var buyNowPrice = document.getElementById('buy-now-price').textContent.trim();
        bidAmountInput.value = buyNowPrice;

        var form = document.getElementById('bid-form');
        var formData = new FormData(form);

        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/bid/buy/now', true);

        xhr.onload = function() {
            if (xhr.status === 200) {
                alert('즉시 구매가로 입찰 성공! 현재가가 갱신되었습니다.');
                location.reload();
            } else {
                var errorMessage = xhr.responseText || '입찰 실패! 다시 시도해주세요.';
                alert(errorMessage);
            }
        };

        xhr.onerror = function() {
            alert('서버와의 연결에 실패했습니다. 다시 시도해주세요.');
        };

        xhr.send(formData);
    });
});
