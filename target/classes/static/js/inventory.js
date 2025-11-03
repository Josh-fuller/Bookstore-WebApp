// /static/js/inventory.js
(function () {
    console.log("[inventory.js] loaded");

    const root = document.getElementById("inventory-root");
    if (!root) {
        console.error("inventory-root not found");
        return;
    }

    const inventoryId = root.dataset.inventoryId;
    const tbody = document.getElementById("booksBody");
    const form = document.getElementById("addBookForm");

    // Utilities
    function rowHtml(b) {
        const price = (b.bookPrice ?? "").toString();
        return `
      <tr data-book-id="${b.id}">
        <td>${escapeHtml(b.bookTitle)}</td>
        <td>${escapeHtml(b.bookGenre)}</td>
        <td>${escapeHtml(price)}</td>
        <td>${escapeHtml(b.bookISBN)}</td>
        <td>
          <button type="button" class="remove-btn" data-book-id="${b.id}">Remove</button>
        </td>
      </tr>
    `;
    }

    function escapeHtml(s) {
        return (s ?? "").toString()
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }

    function bindRemoveHandlers() {
        tbody.querySelectorAll(".remove-btn").forEach(btn => {
            btn.onclick = async (e) => {
                const bookId = e.currentTarget.dataset.bookId;
                await removeBook(bookId);
            };
        });
    }

    // API calls (REST controller you already have)
    async function fetchInventory() {
        const res = await fetch(`/api/inventories/${inventoryId}`);
        if (!res.ok) throw new Error(`Failed to fetch inventory ${inventoryId}`);
        return res.json();
    }

    async function addBook(book) {
        // POST /api/inventories/{id}/books
        const res = await fetch(`/api/inventories/${inventoryId}/books`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(book)
        });
        if (!res.ok) throw new Error("Failed to add book");
        return res.json(); // returns updated inventory
    }

    async function removeBook(bookId) {
        // DELETE /api/inventories/{inventoryId}/books/{bookId}
        const res = await fetch(`/api/inventories/${inventoryId}/books/${bookId}`, {
            method: "DELETE"
        });
        if (!res.ok) {
            console.error("Failed to remove book", await res.text());
            return;
        }
        // Optimistic DOM update
        const row = tbody.querySelector(`tr[data-book-id="${bookId}"]`);
        if (row) row.remove();
    }

    // Rendering
    async function renderBooks() {
        try {
            const inventory = await fetchInventory();
            const books = (inventory.books || []).slice()
                .sort((a, b) => (a.bookTitle ?? "").localeCompare(b.bookTitle ?? "", undefined, {sensitivity: "base"}));

            tbody.innerHTML = books.map(rowHtml).join("");
            bindRemoveHandlers();
        } catch (err) {
            console.error("renderBooks error:", err);
            tbody.innerHTML = `<tr><td colspan="5">Failed to load books.</td></tr>`;
        }
    }

    // Intercept the form to add via AJAX (no full reload)
    form?.addEventListener("submit", async (e) => {
        e.preventDefault();
        const fd = new FormData(form);

        const book = {
            bookTitle: fd.get("bookTitle")?.toString().trim(),
            bookGenre: fd.get("bookGenre")?.toString().trim(),
            bookPrice: fd.get("bookPrice") ? Number(fd.get("bookPrice")) : null,
            bookISBN: fd.get("bookISBN")?.toString().trim()
        };

        // basic guard (match your server-side checks)
        if (!book.bookTitle) return;

        try {
            await addBook(book);
            // Re-render list (ensures IDs/prices are exactly what DB saved)
            await renderBooks();
            form.reset();
        } catch (err) {
            console.error("addBook error:", err);
            // Optional: show a small inline error
        }
    });

    // Initial load
    renderBooks();
})();
