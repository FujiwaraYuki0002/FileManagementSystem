/**
 * ページング処理
 */

class Paginator {
    constructor(data, itemsPerPage, updateTableBodyCallback, tableBodyId, selectedId) {
        this.data = data;  // ユーザーデータ
        this.itemsPerPage = itemsPerPage;  // 1ページに表示するアイテム数
        this.currentPage = 1;  // 初期ページ
        this.totalPages = Math.ceil(this.data.length / this.itemsPerPage);  // 総ページ数
        this.updateTableBodyCallback = updateTableBodyCallback;  // 表示を更新するコールバック関数
        this.tableBodyId = tableBodyId;  // 更新対象のtbodyのID
        this.selectedId = selectedId; // 選択されたユーザーIDを保持
    }

    // ページ更新
    updatePage(page) {
        // ページの範囲をチェックして更新
        this.currentPage = Math.min(Math.max(page, 1), this.totalPages);  // 範囲外のページは制限
        this.updateTableBodyCallback(this.tableBodyId, this.data, this.currentPage, this.itemsPerPage);  // 表示更新
        this.updatePagingControls();  // ページ情報の更新
    }

    // ページネーションコントロールの更新
    updatePagingControls() {
        // ページ番号を表示
        document.getElementById('pageNumber').textContent = this.currentPage;
        document.getElementById('totalPages').textContent = this.totalPages;

        // 「最初のページ」「前のページ」ボタンの非活性化
            const firstPageButton = document.getElementById('firstPage');
            const prevPageButton = document.getElementById('prevPage');

            if (this.currentPage === 1) {
                firstPageButton.disabled = true;
                prevPageButton.disabled = true;
            } else {
                firstPageButton.disabled = false;
                prevPageButton.disabled = false;
            }

            // 「次のページ」「最後のページ」ボタンの非活性化
            const nextPageButton = document.getElementById('nextPage');
            const lastPageButton = document.getElementById('lastPage');

            if (this.currentPage === this.totalPages) {
                nextPageButton.disabled = true;
                lastPageButton.disabled = true;
            } else {
                nextPageButton.disabled = false;
                lastPageButton.disabled = false;
            }
    }

    // ページネーションボタンのイベントリスナー設定
    addPaginationButtonListeners() {
        document.getElementById('prevPage').addEventListener('click', () => {
            this.updatePage(this.currentPage - 1);
        });

        document.getElementById('nextPage').addEventListener('click', () => {
            this.updatePage(this.currentPage + 1);
        });

        document.getElementById('firstPage').addEventListener('click', () => {
            this.updatePage(1);
        });

        document.getElementById('lastPage').addEventListener('click', () => {
            this.updatePage(this.totalPages);
        });
    }
}
