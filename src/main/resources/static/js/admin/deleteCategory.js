function deleteCategory(button) {
    const categoryId = button.getAttribute("data-category-id"); // data-category-id 속성 값 읽기
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/api/admin/categories/${categoryId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    location.reload(); // 성공 시 페이지 새로고침
                } else {
                    alert("삭제 실패: " + response.status);
                }
            })
            .catch(error => {
                console.error("삭제 중 오류 발생:", error);
                alert("삭제 요청 중 오류가 발생했습니다.");
            });
    }
}
