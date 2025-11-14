document.addEventListener("DOMContentLoaded", () => {
    console.log("[inventory.js] loaded");

    const body = document.body;
    const isAdmin = body.dataset.isAdmin === "true";

    const tableBody = document.getElementById("booksBody");
    const addBookForm = document.getElementById("addBookForm");
    const searchForm = document.querySelector("form.search-bar");

    const titleInput   = searchForm?.querySelector('input[name="title"]');
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

        const titleTerm   = (titleInput?.value ?? "").trim().toLowerCase();
        const minPriceVal = minPriceInput?.value;
        const maxPriceVal = maxPriceInput?.value;
        const selectedGenre = genreSelect?.value ?? "";

        const minPrice = minPriceVal !== "" ? parseFloat(minPriceVal) : null;
        const maxPrice = maxPriceVal !== "" ? parseFloat(maxPriceVal) : null;

        const rows = tableBody.querySelectorAll("tr");

        rows.forEach((row) => {
            const rowTitle = (row.dataset.title || "").toLowerCase();
            const rowPriceRaw = row.dataset.price;
            const rowPrice = rowPriceRaw !== undefined ? parseFloat(rowPriceRaw) : NaN;
            const rowGenre = row.dataset.genre || "";

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
            const row = btn.closest("tr");
            const bookId = btn.getAttribute("data-book-id");
            const deleteUrl =
                btn.getAttribute("data-delete-url") || `/books/${bookId}/delete`;

            if (!bookId) {
                console.warn("Remove button without data-book-id");
                return;
            }

            if (!confirm("Remove this book?")) {
                return;
            }

            try {
                const res = await fetch(deleteUrl, { method: "DELETE" });

                if (!res.ok && res.status !== 204) {
                    throw new Error(`HTTP ${res.status}`);
                }

                // Remove table row
                row && row.remove();

                // Remove corresponding modal if it exists
                const modal = document.getElementById(`bookModal__${bookId}`);
                if (modal) modal.remove();

                applyFilters();
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
                        data-book-id="${book.id}"
                        data-delete-url="/books/${book.id}/delete">
                    Remove
                </button>
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

    // ---------- Initial wiring ----------

    // Hook remove buttons for rows rendered by Thymeleaf
    if (isAdmin && tableBody) {
        tableBody
            .querySelectorAll(".remove-book-btn")
            .forEach((btn) => hookRemoveButton(btn));
    }

    // Live filtering using the original form inputs
    if (titleInput)   titleInput.addEventListener("input", applyFilters);
    if (minPriceInput) minPriceInput.addEventListener("input", applyFilters);
    if (maxPriceInput) maxPriceInput.addEventListener("input", applyFilters);
    if (genreSelect)   genreSelect.addEventListener("change", applyFilters);

    // Add book via AJAX
    if (addBookForm) {
        addBookForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const formData = new FormData(addBookForm);
            const payload = Object.fromEntries(formData.entries());

            try {
                const res = await fetch(addBookForm.action, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json",
                    },
                    body: JSON.stringify(payload),
                });

                if (!res.ok) {
                    throw new Error(`HTTP ${res.status}`);
                }

                const created = await res.json();

                // Add row to table
                if (tableBody) {
                    const row = createBookRow(created);
                    tableBody.appendChild(row);
                }

                // Add modal
                const container = document.querySelector(".container");
                if (container) {
                    const modal = createBookModal(created);
                    container.appendChild(modal);
                }

                addBookForm.reset();
                applyFilters();
            } catch (err) {
                console.error("Add book error:", err);
                alert("Could not add book.");
            }
        });
    }

    // Run once in case title/min/max/genre have values from server-side search
    applyFilters();
});
