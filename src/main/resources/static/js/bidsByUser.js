document.addEventListener("DOMContentLoaded", function() {
    // 페이지네이션 링크 클릭 시 페이지 이동 처리
    const paginationLinks = document.querySelectorAll('.page-link');
    paginationLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            let page = new URL(link.href).searchParams.get('page');
            if (page) {
                window.location.href = link.href;
            }
        });
    });
});
