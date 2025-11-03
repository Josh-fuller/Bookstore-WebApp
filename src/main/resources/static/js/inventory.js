document.addEventListener("DOMContentLoaded", () => {
    console.log("[inventory.js] loaded");

    const inventoryId = document.body.dataset.inventoryId;
    const tableBody = document.getElementById("booksBody");
    const form = document.getElementById("addBookForm");

    if (!inventoryId) {
        console.error("No inventory ID found in HTML");
        return;
    }

    const escapeHtml = (s) =>
        (s ?? "").toString()
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");

    async function loadBooks() {
        try {
            const res = await fetch(`/api/inventories/${inventoryId}`);
            if (!res.ok) throw new Error("Failed to load inventory");
            const data = await res.json();

            tableBody.innerHTML = "";
            data.books.forEach((b) => {
                const row = document.createElement("tr");
                row.dataset.bookId = b.id;
                row.innerHTML = `
          <td>${escapeHtml(b.bookTitle)}</td>
          <td>${escapeHtml(b.bookGenre)}</td>
          <td>${escapeHtml(b.bookPrice)}</td>
          <td>${escapeHtml(b.bookISBN)}</td>
          <td><button class="remove-btn" data-id="${b.id}">Remove</button></td>
        `;
                tableBody.appendChild(row);
            });
        } catch (err) {
            console.error("Error loading books:", err);
        }
    }

    async function addBook(book) {
        const res = await fetch(`/api/inventories/${inventoryId}/books`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(book),
        });
        if (!res.ok) throw new Error("Failed to add book");
        await loadBooks();
    }

    async function removeBook(id) {
        const res = await fetch(`/api/inventories/${inventoryId}/books/${id}`, {
            method: "DELETE",
        });
        if (!res.ok) throw new Error("Failed to delete book");
        await loadBooks();
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const fd = new FormData(form);
        const book = {
            bookTitle: fd.get("bookTitle"),
            bookGenre: fd.get("bookGenre"),
            bookPrice: parseFloat(fd.get("bookPrice")),
            bookISBN: fd.get("bookISBN"),
        };
        try {
            await addBook(book);
            form.reset();
        } catch (err) {
            console.error(err);
        }
    });

    tableBody.addEventListener("click", async (e) => {
        if (e.target.classList.contains("remove-btn")) {
            const id = e.target.dataset.id;
            try {
                await removeBook(id);
            } catch (err) {
                console.error(err);
            }
        }
    });

    loadBooks(); // Initial load
});
