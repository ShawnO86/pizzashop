document.addEventListener('DOMContentLoaded', ()=> {

    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");
    let errorMessageElement = document.querySelector(".error");

    menuItemsContainer.addEventListener("click", handleAddMenuItem);
    menuAmountContainer.addEventListener("click", handleRemoveMenuItem);

    if (errorMessageElement) {
        const errMsg = errorMessageElement.children.item(0).innerText;
        if (errMsg === "No menu items added!" || errMsg === "Menu items and quantity mismatch!" || errMsg === "There was an error.") {
            setTimeout(() => {
                errorMessageElement.remove();
                errorMessageElement = null;
            }, 5000);

        } else if (errMsg === "Item mismatch!" || errMsg === "Not enough inventory!"){
            let cartItemContainers = document.querySelectorAll(".cartItemContainer");
            cartItemContainers.forEach((el) => {
                setTimeout(() => {
                    el.remove();
                }, 5000);
            });
        }
    }

    function handleAddMenuItem(event) {
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
                alert("Cannot add " + quantity + " " + orderItemName + "! We have enough inventory for " + orderItemMax + "!");
            } else {
                createOrderItemAmountSelector(orderItemName, orderItemId, orderItemPrice, quantity, orderItemMax);
            }
        }
    }

    function handleRemoveMenuItem(event){
        event.preventDefault();
        if (event.target.classList.contains("remove-item")) {
            event.target.parentElement.remove();
        }
    }

    // todo: setup show/hide button for cart area, custom pizza form.
    //  --: change itemContainer to an HTML template literal to append to menuAmountContainer?

    function createOrderItemAmountSelector(menuItemName, menuItemId, menuItemPrice, orderInitQty, menuItemMaxQty) {
        const existingItem = document.querySelector(`[data-cart-item-id="${menuItemId}"]`);
        if (existingItem) {
            alert(menuItemName + " is already in the cart. You can adjust the quantity there.");
            return;
        }

        const itemContainer = document.createElement("div");

        itemContainer.innerHTML = `
        <div class="inline cartItem-Container" data-cart-item-id="${menuItemId}">
            <input type="hidden" value="${menuItemId}" name="menuItemsIdList" />
            <input type="hidden" value="${menuItemName}" name="menuDishNamesList" />
            <p>${menuItemName}</p>
            <label>Qty:
                <input type="number" value="${orderInitQty}" min="1" max="${menuItemMaxQty}" name="menuItemsAmountsList" required/>
            </label>
            <button class="remove-item">Remove Item</button>
        </div>`;

        menuAmountContainer.appendChild(itemContainer);
    }
});