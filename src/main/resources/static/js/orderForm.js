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

    function createOrderItemAmountSelector(menuItemName, menuItemId, menuItemPrice, orderInitQty, menuItemMaxQty) {
        const existingItem = document.querySelector(`[data-cart-item-id="${menuItemId}"]`);
        if (existingItem) {
            alert(menuItemName + " is already in the cart. You can adjust the quantity there.");
            return;
        }

        const itemContainer = document.createElement("div");
        itemContainer.classList.add("inline", "cartItem-Container");
        itemContainer.dataset.cartItemId = menuItemId;

        const itemName = document.createElement("p");

        const itemIdHiddenInput = document.createElement("input");
        itemIdHiddenInput.setAttribute("type", "hidden");
        itemIdHiddenInput.setAttribute("value", menuItemId);
        itemIdHiddenInput.name = "menuItemsIdList";

        const itemDishNameHiddenInput = document.createElement("input");
        itemDishNameHiddenInput.setAttribute("type", "hidden");
        itemDishNameHiddenInput.setAttribute("value", menuItemName);
        itemDishNameHiddenInput.name = "menuDishNamesList";

        const itemAmount = document.createElement("input");
        itemAmount.setAttribute("type", "number");
        itemAmount.setAttribute("value", orderInitQty);
        itemAmount.setAttribute("min", "1");
        itemAmount.setAttribute("max", menuItemMaxQty);
        itemAmount.name = "menuItemsAmountsList";
        itemAmount.required = true;

        const itemAmountLabel = document.createElement("label");
        itemAmountLabel.innerText = "Qty"

        const removeItemBtn = document.createElement("button");
        removeItemBtn.innerText = "Remove Item";
        removeItemBtn.value = menuItemName;
        removeItemBtn.classList.add("remove-item");

        itemContainer.appendChild(itemIdHiddenInput);
        itemContainer.appendChild(itemDishNameHiddenInput);
        itemContainer.appendChild(itemName);
        itemContainer.appendChild(itemAmountLabel);
        itemContainer.appendChild(removeItemBtn);
        itemAmountLabel.appendChild(itemAmount);

        const itemContent = document.createTextNode(menuItemName);
        itemName.appendChild(itemContent);

        menuAmountContainer.appendChild(itemContainer);
    }
});