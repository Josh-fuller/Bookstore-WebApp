document.addEventListener("DOMContentLoaded", () => {
    console.log("[cart.js] loaded");

    const cartBody      = document.getElementById("cartBody");
    const totalItemsEl  = document.getElementById("cartTotalItems");
    const totalPriceEl  = document.getElementById("cartTotalPrice");
    const emptyMsgEl    = document.getElementById("emptyCartMessage");
    const tableWrapper  = document.getElementById("cartTableWrapper");
    const actionsEl     = document.getElementById("cartActions");

    if (!cartBody) {
        // No cart table on this page
        return;
    }

    const formatPrice = (num) => {
        if (Number.isNaN(num)) return "$0.00";
        return `$${num.toFixed(2)}`;
    };

    function recalcCartSummary() {
        const rows = cartBody.querySelectorAll("tr");
        const itemCount = rows.length;
        let total = 0;

        rows.forEach((row) => {
            const raw = row.dataset.price;
            const value = raw !== undefined ? parseFloat(raw) : NaN;
            if (!Number.isNaN(value)) {
                total += value;
            }
        });

        if (totalItemsEl) {
            totalItemsEl.textContent = String(itemCount);
        }
        if (totalPriceEl) {
            totalPriceEl.textContent = formatPrice(total);
        }

        const isEmpty = itemCount === 0;

        if (emptyMsgEl)   emptyMsgEl.style.display   = isEmpty ? "" : "none";
        if (tableWrapper) tableWrapper.style.display = isEmpty ? "none" : "";
        if (actionsEl)    actionsEl.style.display    = isEmpty ? "none" : "";
    }

    function hookRemoveForm(form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const row = form.closest("tr");
            if (!row) return;

            const action = form.getAttribute("action");
            if (!action) return;

            if (!confirm("Remove this item from your cart?")) {
                return;
            }

            const btn = form.querySelector("button");
            const originalText = btn?.textContent;

            try {
                if (btn) {
                    btn.disabled = true;
                    btn.textContent = "Removing...";
                }

                const res = await fetch(action, {
                    method: "POST"
                });

                if (!res.ok) {
                    throw new Error(`HTTP ${res.status}`);
                }

                row.remove();
                recalcCartSummary();
            } catch (err) {
                console.error("Cart remove error:", err);
                alert("Could not remove item from cart.");
                if (btn) {
                    btn.disabled = false;
                    btn.textContent = originalText || "Remove";
                }
            }
        });
    }

    // Hook all remove forms in the cart table
    cartBody
        .querySelectorAll("form")
        .forEach((form) => hookRemoveForm(form));

    // Initial summary calculation
    recalcCartSummary();
});