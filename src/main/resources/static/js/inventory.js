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

    function showCartToast(message) {
        // Simple lightweight message; you can replace with Bootstrap toast later
        let box = document.getElementById("cart-toast-box");
        if (!box) {
            box = document.createElement("div");
            box.id = "cart-toast-box";
            box.style.position = "fixed";
            box.style.top = "10px";
            box.style.right = "10px";
            box.style.zIndex = "9999";
            document.body.appendChild(box);
        }

        const alert = document.createElement("div");
        alert.className = "alert alert-success py-1 px-2 mb-2";
        alert.textContent = message || "Added to cart!";
        box.appendChild(alert);

        setTimeout(() => {
            alert.remove();
            if (!box.hasChildNodes()) {
                box.remove();
            }
        }, 2500);
    }

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

    // ---------- Admin remove wiring ----------

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

    // ---------- Add-to-cart wiring (dynamic) ----------

    function hookAddToCartForm(form) {
        // Prevent double-binding the same form
        if (form.dataset.cartHooked === "true") {
            return;
        }
        form.dataset.cartHooked = "true";

        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const action = form.getAttribute("action");
            if (!action) {
                console.warn("Add-to-cart form without action");
                return;
            }

            try {
                const res = await fetch(action, {
                    method: "POST",
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                });

                if (!res.ok) {
                    throw new Error(`HTTP ${res.status}`);
                }

                showCartToast("Added to cart!");
                // If you later add a cart count badge in navbar, update it here.
            } catch (err) {
                console.error("Add to cart error:", err);
                alert("Could not add to cart.");
            }
        });
    }

    function wireAllAddToCartForms() {
        if (!tableBody || !isLoggedIn || isAdmin) return;
        const forms = tableBody.querySelectorAll('form[action^="/cart/add/"]');
        forms.forEach((f) => hookAddToCartForm(f));
    }

    // ---------- Row / modal creation ----------

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

        const stock = book.inventory ?? 0;
        const isOutOfStock = stock <= 0;

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
            <td>${stock}</td>
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
                ${
                    isOutOfStock
                        ? `<span class="text-muted">Out of stock</span>`
                        : `
                    <form method="post" action="/cart/add/${book.id}">
                        <button type="submit"
                                class="btn btn-sm btn-primary">
                            Add to Cart
                        </button>
                    </form>`
                    }
                </td>`
                    : ""
            }
        `;

        if (isAdmin) {
            const btn = tr.querySelector(".remove-book-btn");
            if (btn) hookRemoveButton(btn);
        } else if (isLoggedIn) {
            const form = tr.querySelector('form[action^="/cart/add/"]');
            if (form) hookAddToCartForm(form);
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
        <p><strong>Inventory:</strong> <span>${book.inventory ?? 0}</span></p>                   
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

    // ---------- API calls ----------

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

            // Reapply filters
            applyFilters();
            // No need to call wireAllAddToCartForms() here because createBookRow already hooks them
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

        const rawInventory = fd.get("inventory");
        const parsedInventory = rawInventory !== null ? parseInt(rawInventory, 10) : NaN;
        const inventory = Number.isNaN(parsedInventory) ? 5 : parsedInventory;


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
                : null,
            inventory: inventory
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

    // Hook add-to-cart forms for rows rendered by Thymeleaf (first paint)
    wireAllAddToCartForms();

    // Live filtering using the original form inputs
    if (titleInput)    titleInput.addEventListener("input", applyFilters);
    if (minPriceInput) minPriceInput.addEventListener("input", applyFilters);
    if (maxPriceInput) maxPriceInput.addEventListener("input", applyFilters);
    if (genreSelect)   genreSelect.addEventListener("change", applyFilters);

    // Add book via API + full reload (of table)
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