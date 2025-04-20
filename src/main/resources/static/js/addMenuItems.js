
    // todo : add menuItem displays but build a JSON for orderFormMain.js to send to Spring instead of sending form data

    export function handleAddMenuItem(event, menuAmountContainer) {
        event.preventDefault();
        if (event.target.classList.contains("addMenuItem-btn")) {
            const orderItemId = event.target.dataset.itemId;
            const orderItemName = event.target.dataset.itemName;
            const orderItemPrice = event.target.dataset.itemPrice;
            const orderItemMax = parseInt(event.target.dataset.itemMax, 10);
            const orderInitQty = event.target.parentElement.querySelector('input[type="number"]');
            const quantity = orderInitQty.value ? parseInt(orderInitQty.value, 10) : 1;

            if (quantity > orderItemMax) {
                orderInitQty.value = 1;
                alert("Cannot add " + quantity + " " + orderItemName + "! We only have enough inventory for " + orderItemMax + "!");
            } else {
                createOrderItemAmountSelector(orderItemName, orderItemId, orderItemPrice, quantity, orderItemMax, menuAmountContainer);
            }
        }
    }

    export function handleRemoveMenuItem(event){
        event.preventDefault();
        let id;
        if (event.target.classList.contains("remove-item")) {
            id = event.target.parentElement.dataset.cartItemId;
            event.target.parentElement.remove();
            return id;
        }
    }

    // todo: setup show/hide button for cart area, custom pizza form.

    function createOrderItemAmountSelector(menuItemName, menuItemId, menuItemPrice, orderInitQty, menuItemMaxQty, container) {
        const existingItem = container.querySelector(`[data-item-id="${menuItemId}"]`);
        const itemContainer = document.createElement("div");

        if (existingItem) {
            alert(menuItemName + " is already in the cart. You can adjust the quantity there.");
            return;
        }

        itemContainer.classList.add("cartItem-container");
        itemContainer.setAttribute("data-item-id", menuItemId);
        itemContainer.setAttribute("date-item-name", menuItemName);

        itemContainer.innerHTML = `
            <h5>${menuItemName}</h5>
            <label>Qty:
                <input type="number" value="${orderInitQty}" min="1" max="${menuItemMaxQty}" required/>
            </label>
            <button class="remove-item">Remove Item</button>`;

        container.appendChild(itemContainer);
    }
