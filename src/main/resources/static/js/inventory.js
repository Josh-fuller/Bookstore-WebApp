document.addEventListener("DOMContentLoaded", () => {
    console.log("[inventory.js] loaded");

    const body        = document.body;
    const isAdmin     = body.dataset.isAdmin === "true";
    const isLoggedIn  = body.dataset.isLoggedIn === "true";
    const inventoryId = body.dataset.inventoryId;

    if (!inventoryId) {
        console.error("No inventory ID on <body>");
    }

    const tableBody   = document.getElementById("booksBody");
    const addBookForm = document.getElementById("addBookForm");
    const searchForm  = document.querySelector("form.search-bar");

    const titleInput    = searchForm?.querySelector('input[name="title"]');
    const minPriceInput = searchForm?.querySelector('input[name="minPrice"]');
    const maxPriceInput = searchForm?.querySelector('input[name="maxPrice"]');
    const genreSelect   = searchForm?.querySelector('select[name="genre"]');

    // ---------- Helpers ----------

    const escapeHtml = (s) =>
        (s ?? "").toString()
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");

    const formatPrice = (v) => {
        const num = Number(v);
        if (Number.isNaN(num)) return "";
        return `$${num.toFixed(2)}`;
    };

    function applyFilters() {
        if (!tableBody) return;

        const titleTerm     = (titleInput?.value ?? "").trim().toLowerCase();
        const minPriceVal   = minPriceInput?.value;
        const maxPriceVal   = maxPriceInput?.value;
        const selectedGenre = genreSelect?.value ?? "";

        const minPrice = minPriceVal !== "" ? parseFloat(minPriceVal) : null;
        const maxPrice = maxPriceVal !== "" ? parseFloat(maxPriceVal) : null;

        const rows = tableBody.querySelectorAll("tr");

        rows.forEach((row) => {
            const rowTitle    = (row.dataset.title || "").toLowerCase();
            const rowPriceRaw = row.dataset.price;
            const rowPrice    = rowPriceRaw !== undefined ? parseFloat(rowPriceRaw) : NaN;
            const rowGenre    = row.dataset.genre || "";

            let visible = true;

            if (titleTerm && !rowTitle.includes(titleTerm)) {
                visible = false;
            }
            if (minPrice !== null && !Number.isNaN(rowPrice) && rowPrice < minPrice) {
                visible = false;
            }
            if (maxPrice !== null && !Number.isNaN(rowPrice) && rowPrice > maxPrice) {
                visible = false;
            }
            if (selectedGenre && rowGenre !== selectedGenre) {
                visible = false;
            }

            row.style.display = visible ? "" : "none";
        });
    }

    function hookRemoveButton(btn) {
        btn.addEventListener("click", async () => {
            const bookId = btn.getAttribute("data-book-id");
            if (!bookId) {
                console.warn("Remove button without data-book-id");
                return;
            }

            if (!confirm("Remove this book?")) {
                return;
            }

            try {
                await removeBook(bookId);
                await loadBooks();
            } catch (err) {
                console.error("Remove book error:", err);
                alert("Could not remove book.");
            }
        });
    }

    function createBookRow(book) {
        const tr = document.createElement("tr");
        tr.setAttribute("data-book-id", book.id);
        tr.dataset.title = book.bookTitle || "";
        tr.dataset.price = book.bookPrice ?? "";
        tr.dataset.genre = book.bookGenre || "";

        const priceText = formatPrice(book.bookPrice);
        const description = book.bookDescription || "";
        const shortDesc =
            description.length > 60
                ? `${description.slice(0, 60)}…`
                : description;

        tr.innerHTML = `
            <td>
                <a href="#" class="text-decoration-none"
                   data-bs-toggle="modal" data-bs-target="#bookModal__${book.id}">
                    ${
            book.bookCoverURL
                ? `<img src="${escapeHtml(
                    book.bookCoverURL
                )}" class="cover-img" alt="Cover" />`
                : ""
        }
                </a>
            </td>
            <td>
                <a href="#" class="fw-semibold text-primary text-decoration-none"
                   data-bs-toggle="modal" data-bs-target="#bookModal__${book.id}">
                    ${escapeHtml(book.bookTitle || "")}
                </a>
            </td>
            <td>${escapeHtml(book.bookAuthor || "")}</td>
            <td>${escapeHtml(book.bookGenre || "")}</td>
            <td>${priceText}</td>
            <td>${escapeHtml(shortDesc)}</td>
            ${
                        isAdmin
                            ? `
                    <td>
                        <button type="button"
                                class="btn btn-sm btn-danger remove-book-btn"
                                data-book-id="${book.id}">
                            Remove
                        </button>
                    </td>`
                            : isLoggedIn
                                ? `
                    <td>
                        <form method="post" action="/cart/add/${book.id}">
                            <button type="submit"
                                    class="btn btn-sm btn-primary">
                                Add to Cart
                            </button>
                        </form>
                    </td>`
                                : ""
                    }
            `;


        if (isAdmin) {
            const btn = tr.querySelector(".remove-book-btn");
            if (btn) hookRemoveButton(btn);
        }

        return tr;
    }

    function createBookModal(book) {
        const div = document.createElement("div");
        div.className = "modal fade";
        div.id = `bookModal__${book.id}`;
        div.tabIndex = -1;
        div.setAttribute("aria-hidden", "true");

        div.innerHTML = `
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${escapeHtml(
            book.bookTitle || ""
        )}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body row">
                        <div class="col-md-4 text-center mb-3">
                            ${
            book.bookCoverURL
                ? `<img src="${escapeHtml(
                    book.bookCoverURL
                )}" alt="Cover" class="img-fluid rounded shadow-sm" />`
                : ""
        }
                        </div>
                        <div class="col-md-8">
                            <p><strong>Author:</strong> <span>${escapeHtml(
            book.bookAuthor || ""
        )}</span></p>
                            <p><strong>Publisher:</strong> <span>${escapeHtml(
            book.bookPublisher || ""
        )}</span></p>
                            <p><strong>Genre:</strong> <span>${escapeHtml(
            book.bookGenre || ""
        )}</span></p>
                            <p><strong>Price:</strong> <span>${formatPrice(
            book.bookPrice
        )}</span></p>
                            <p><strong>ISBN:</strong> <span>${escapeHtml(
            book.bookISBN || ""
        )}</span></p>
                            <hr>
                            <p>${escapeHtml(book.bookDescription || "")}</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        ${
            book.bookCoverURL
                ? `<a href="${escapeHtml(
                    book.bookCoverURL
                )}" target="_blank" class="btn btn-outline-primary">View Full Cover</a>`
                : ""
        }
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        `;
        return div;
    }

    // ---------- API calls (same endpoints as your working version) ----------

    async function loadBooks() {
        if (!inventoryId || !tableBody) return;

        try {
            const res = await fetch(`/api/inventories/${inventoryId}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const data = await res.json();
            const books = data.books || [];

            // Rebuild table body
            tableBody.innerHTML = "";

            books.forEach((b) => {
                const row = createBookRow(b);
                tableBody.appendChild(row);

                // Make sure each book has a modal; if Thymeleaf already rendered it, skip
                if (!document.getElementById(`bookModal__${b.id}`)) {
                    const container = document.querySelector(".container");
                    if (container) {
                        const modal = createBookModal(b);
                        container.appendChild(modal);
                    }
                }
            });

            applyFilters();
        } catch (err) {
            console.error("Error loading books:", err);
        }
    }

    async function addBookViaApi() {
        if (!inventoryId) {
            throw new Error("No inventoryId available for addBookViaApi");
        }
        const fd = new FormData(addBookForm);

        const genreValues = fd.getAll("bookGenre");
        const genres = genreValues.join(", ");

        const rawIsbn = fd.get("bookISBN") || "";
        const normalizedIsbn = rawIsbn.trim().replaceAll("-", "").replace(/\s+/g, "");

        const bookPayload = {
            bookTitle: fd.get("bookTitle"),
            bookAuthor: fd.get("bookAuthor"),
            bookPublisher: fd.get("bookPublisher"),
            bookISBN: rawIsbn,
            bookPrice: parseFloat(fd.get("bookPrice")),
            bookGenre: genres,
            bookDescription: fd.get("bookDescription"),
            bookCoverURL: normalizedIsbn
                ? `https://covers.openlibrary.org/b/isbn/${normalizedIsbn}-L.jpg`
                : null
        };

        const res = await fetch(`/api/inventories/${inventoryId}/books`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(bookPayload)
        });

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }
        // We don't trust the response shape; we'll just reload via GET.
    }

    async function removeBook(bookId) {
        if (!inventoryId) {
            throw new Error("No inventoryId available for removeBook");
        }
        const res = await fetch(`/api/inventories/${inventoryId}/books/${bookId}`, {
            method: "DELETE"
        });
        if (!res.ok && res.status !== 204) {
            throw new Error(`HTTP ${res.status}`);
        }
    }

    // ---------- Initial wiring ----------

    // Hook remove buttons for rows rendered by Thymeleaf (first paint)
    if (isAdmin && tableBody) {
        tableBody
            .querySelectorAll(".remove-book-btn")
            .forEach((btn) => hookRemoveButton(btn));
    }

    // Live filtering using the original form inputs
    if (titleInput)    titleInput.addEventListener("input", applyFilters);
    if (minPriceInput) minPriceInput.addEventListener("input", applyFilters);
    if (maxPriceInput) maxPriceInput.addEventListener("input", applyFilters);
    if (genreSelect)   genreSelect.addEventListener("change", applyFilters);

    // Add book via API + full reload
    if (addBookForm) {
        addBookForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            try {
                await addBookViaApi();
                await loadBooks();
                addBookForm.reset();
                applyFilters();
            } catch (err) {
                console.error("Add book error:", err);
                alert("Could not add book.");
            }
        });
    }

    // Initial load from API to sync everything
    loadBooks();
});
