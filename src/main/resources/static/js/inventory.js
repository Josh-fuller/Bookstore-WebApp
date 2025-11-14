document.addEventListener("DOMContentLoaded", () => {
    console.log("[inventory.js] loaded");

    const body = document.body;
    const inventoryId = body.dataset.inventoryId;
    const isAdmin = body.dataset.isAdmin === "true";

    const tableBody = document.getElementById("booksBody");
    const form = document.getElementById("addBookForm");

    if (!inventoryId) {
        console.error("No inventory ID found on <body>");
        return;
    }

    const escapeHtml = (s) =>
        (s ?? "").toString()
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");

    const formatPrice = (n) => {
        const num = Number(n);
        if (Number.isNaN(num)) return "";
        return "$" + num.toFixed(2);
    };

    const abbreviate = (text, maxLen = 60) => {
        if (!text) return "";
        const s = text.toString();
        if (s.length <= maxLen) return s;
        return s.slice(0, maxLen - 3) + "...";
    };

    // ---------- RENDER TABLE FROM INVENTORY JSON ----------

    function renderBooks(books) {
        tableBody.innerHTML = "";

        books.forEach((b) => {
            const row = document.createElement("tr");
            row.dataset.bookId = b.id;

            const coverUrl = b.bookCoverURL || "";
            const modalId = `bookModal__${b.id}`;

            // only include remove button cell if admin
            const removeCellHtml = isAdmin
                ? `
                    <td>
                        <button type="button"
                                class="btn btn-sm btn-danger remove-book-btn"
                                data-book-id="${b.id}">
                            Remove
                        </button>
                    </td>
                  `
                : "";

            row.innerHTML = `
                <td>
                    <a href="#" class="text-decoration-none"
                       data-bs-toggle="modal" data-bs-target="#${modalId}">
                        <img src="${escapeHtml(coverUrl)}"
                             class="cover-img" alt="Cover">
                    </a>
                </td>
                <td>
                    <a href="#" class="fw-semibold text-primary text-decoration-none"
                       data-bs-toggle="modal" data-bs-target="#${modalId}">
                        ${escapeHtml(b.bookTitle)}
                    </a>
                </td>
                <td>${escapeHtml(b.bookAuthor)}</td>
                <td>${escapeHtml(b.bookGenre)}</td>
                <td>${formatPrice(b.bookPrice)}</td>
                <td>${escapeHtml(abbreviate(b.bookDescription))}</td>
                ${removeCellHtml}
            `;
            tableBody.appendChild(row);
        });
    }

    async function loadBooks() {
        try {
            const res = await fetch(`/api/inventories/${inventoryId}`);
            if (!res.ok) throw new Error("Failed to load inventory");
            const data = await res.json();
            renderBooks(data.books || []);
        } catch (err) {
            console.error("Error loading books:", err);
        }
    }

    // ---------- ADD BOOK (via API) ----------

    async function addBookFromForm() {
        const fd = new FormData(form);

        // multi-select genres -> comma-separated string
        const genreValues = fd.getAll("bookGenre");
        const genres = genreValues.join(", ");

        const rawIsbn = fd.get("bookISBN") || "";
        const normalizedIsbn = rawIsbn.trim().replaceAll("-", "").replace(/\s+/g, "");

        const book = {
            bookTitle: fd.get("bookTitle"),
            bookAuthor: fd.get("bookAuthor"),
            bookPublisher: fd.get("bookPublisher"),
            bookISBN: rawIsbn,
            bookPrice: parseFloat(fd.get("bookPrice")),
            bookGenre: genres,
            bookDescription: fd.get("bookDescription"),
            // mirror the auto-cover logic from your controller
            bookCoverURL: normalizedIsbn
                ? `https://covers.openlibrary.org/b/isbn/${normalizedIsbn}-L.jpg`
                : null
        };

        const res = await fetch(`/api/inventories/${inventoryId}/books`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(book)
        });

        if (!res.ok) {
            throw new Error("Failed to add book");
        }

        // Reload table from updated inventory
        await loadBooks();
    }

    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            try {
                await addBookFromForm();
                form.reset();
            } catch (err) {
                console.error("Add book failed:", err);
                alert("Failed to add book. Check console for details.");
            }
        });
    }

    // ---------- REMOVE BOOK (via API) ----------

    async function removeBook(bookId) {
        const res = await fetch(`/api/inventories/${inventoryId}/books/${bookId}`, {
            method: "DELETE"
        });
        if (!res.ok) {
            throw new Error("Failed to delete book");
        }
        await loadBooks();
    }

    tableBody.addEventListener("click", async (e) => {
        // non-admins won't have buttons anyway, but double-check
        if (!isAdmin) return;

        const btn = e.target.closest(".remove-book-btn");
        if (!btn) return;

        const id = btn.dataset.bookId;
        if (!id) return;

        const confirmDelete = confirm("Are you sure you want to remove this book?");
        if (!confirmDelete) return;

        try {
            await removeBook(id);
        } catch (err) {
            console.error("Remove book failed:", err);
            alert("Failed to remove book. Check console for details.");
        }
    });

    // ---------- INITIAL LOAD ----------

    loadBooks();
});
