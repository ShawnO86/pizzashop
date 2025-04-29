import {displayAsCurrency} from './pizzaBuilder.js';

export function handleAddMenuItem(event, menuAmountContainer) {
    event.preventDefault();
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
        return createOrderItemAmountSelectorMenu(orderItemName, orderItemId, orderItemPrice, quantity, orderItemMax, menuAmountContainer);
    }
}

export function handleRemoveItem(event){
    event.preventDefault();
    event.target.parentElement.remove();
}

export function createOrderItemAmountSelectorMenu(menuItemName, menuItemId, menuItemPrice, orderInitQty, menuItemMaxQty, container) {
    const existingItem = container.querySelector(`[data-item-id="${menuItemId}"]`);
    const itemContainer = document.createElement("div");

    if (existingItem) {
        alert(menuItemName + " is already in the cart. You can adjust the quantity there.");
        return;
    }

    itemContainer.classList.add("cartItem-container");
    itemContainer.setAttribute("data-item-id", menuItemId);
    itemContainer.setAttribute("data-item-type", "menu item");
    itemContainer.setAttribute("data-item-price", menuItemPrice);

    const menuItemIndex = document.querySelectorAll(".cartItem-container[data-item-type='menu item']").length;

    itemContainer.innerHTML = `
        <h5 class="space-between">
            <span>${menuItemName}</span>
            <span class="cart-item-price">${orderInitQty} x ${displayAsCurrency(menuItemPrice, false)}</span>
        </h5>
        <label>Qty:
            <input type="number" value="${orderInitQty}" min="1" max="${menuItemMaxQty}" name="menuItemList[${menuItemIndex}].menuItemAmount" required />
        </label>
        <button class="remove-item">Remove Item</button>
        <input type="hidden" name="menuItemList[${menuItemIndex}].menuItemID" value="${menuItemId}" />
        <input type="hidden" name="menuItemList[${menuItemIndex}].menuItemName" value="${menuItemName}" />
        <input type="hidden" name="menuItemList[${menuItemIndex}].maxQty" value="${menuItemMaxQty}" />
        <input type="hidden" name="menuItemList[${menuItemIndex}].pricePerItem" value="${menuItemPrice}"/>
        `;

    container.appendChild(itemContainer);

    return {menuItemId, menuItemName, orderInitQty, menuItemMaxQty, menuItemPrice};
}