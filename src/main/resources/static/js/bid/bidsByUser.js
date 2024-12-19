document.addEventListener("DOMContentLoaded", function() {
    const sortSelect = document.getElementById("sortSelect");

    sortSelect.addEventListener('change', function() {
        const value = sortSelect.value;
        console.log("Selected value: ", value); // 여기서 선택된 값 확인
        if (value) {
            const [column, order] = value.split('_'); // 'myPrice'와 'asc' 또는 'desc'를 분리
            console.log("Column: ", column, "Order: ", order); // 여기서 column과 order 확인
            sortTable(column, order);
        }
    });

    // 테이블 정렬 함수
    function sortTable(column, order) {
        const table = document.getElementById("bidTable");
        if (!table) return;
        const tbody = table.querySelector("tbody");
        const rows = Array.from(tbody.getElementsByTagName("tr"));

        let index = -1;
        if (column === 'myPrice') {
            index = 3;
        } else if (column === 'currentPrice') {
            index = 4;
        }

        // 정렬
        rows.sort((rowA, rowB) => {
            const cellA = rowA.cells[index].textContent.trim().replace(' 원', '').replace(/,/g, '');
            const cellB = rowB.cells[index].textContent.trim().replace(' 원', '').replace(/,/g, '');
            const valueA = parseFloat(cellA);
            const valueB = parseFloat(cellB);

            if (order === 'asc') {
                return valueA - valueB;
            } else {
                return valueB - valueA;
            }
        });

        rows.forEach(row => tbody.appendChild(row));
    }
});
