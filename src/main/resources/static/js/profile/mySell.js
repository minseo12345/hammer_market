document.addEventListener('DOMContentLoaded', function() {
        const sortSelect = document.getElementById('sortSelect');
        const bidTable = document.getElementById('bidTable');

        sortSelect.addEventListener('change', function() {
            const selectedStatus = this.value;
            const tbody = bidTable.querySelector('tbody');
            const rows = Array.from(tbody.querySelectorAll('tr'));

            // 모든 행 표시 초기화
            rows.forEach(row => row.style.display = '');

            if (selectedStatus) {
                // 선택된 상태와 일치하지 않는 행 숨기기
                rows.forEach(row => {
                    const status = row.querySelector('td:nth-child(5)').textContent.trim();
                    if (status !== selectedStatus) {
                        row.style.display = 'none';
                    }
                });
            }

            // 번호 재정렬
            let visibleIndex = 1;
            rows.forEach(row => {
                if (row.style.display !== 'none') {
                    row.querySelector('td:first-child').textContent = visibleIndex++;
                }
            });
        });

        // 초기 상태값 설정
        const urlParams = new URLSearchParams(window.location.search);
        const currentStatus = urlParams.get('status');
        if (currentStatus) {
            sortSelect.value = currentStatus;
            // 페이지 로드 시 초기 필터링 적용
            sortSelect.dispatchEvent(new Event('change'));
        }
    });