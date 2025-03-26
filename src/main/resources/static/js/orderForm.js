document.addEventListener('DOMContentLoaded', ()=> {

    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");

    let menuItemNamesAndQuantities = {};

    menuItemsContainer.addEventListener("click", handleAddMenuItem);
    menuAmountContainer.addEventListener("click", handleRemoveMenuItem);


    function handleAddMenuItem(event) {
        event.preventDefault();
        if (event.target.classList.contains("addMenuItem-btn")) {
            let orderItemName = event.target.name;
            if (!menuItemNamesAndQuantities[orderItemName]) {
                let orderInitQty = event.target.parentElement.querySelector('input[type="number"]').value;
                createOrderItemAmountSelector(event.target.name, orderInitQty);
                menuItemNamesAndQuantities[orderItemName] = orderInitQty;
            } else {
                alert(orderItemName + " is already part of this order. You must increase the qty if you want to add more.")
            }
        }
    }

    function handleRemoveMenuItem(event){
        event.preventDefault();
        if (event.target.classList.contains("remove-item")) {
            event.target.parentElement.remove();
            delete menuItemNamesAndQuantities[event.target.value];
        }
    }

    function createOrderItemAmountSelector(orderItemName, orderInitQty) {
        // [id, dishName]
        const orderItemId_Name = orderItemName.split(" ");

        const menuItemId = orderItemId_Name[0]
        orderItemId_Name.shift();
        const menuItemName = orderItemId_Name.join(" ");

        const itemContainer = document.createElement("div");
        itemContainer.classList.add("inline");

        const itemName = document.createElement("p");

        const itemNameHiddenInput = document.createElement("input");
        itemNameHiddenInput.setAttribute("type", "hidden");
        itemNameHiddenInput.setAttribute("value", menuItemId);
        itemNameHiddenInput.name = "menuItemsNamesList";

        const itemAmount = document.createElement("input");
        itemAmount.setAttribute("type", "number");
        itemAmount.setAttribute("value", orderInitQty)
        itemAmount.setAttribute("min", "1");
        itemAmount.name = "menuItemsAmountsList";
        itemAmount.required = true;

        const itemAmountLabel = document.createElement("label")
        itemAmountLabel.innerText = "Qty"

        const removeItemBtn = document.createElement("button");
        removeItemBtn.innerText = "Remove Item";
        removeItemBtn.value = orderItemName;
        removeItemBtn.classList.add("remove-item");

        itemContainer.appendChild(itemNameHiddenInput);
        itemContainer.appendChild(itemName);
        itemContainer.appendChild(itemAmountLabel);
        itemContainer.appendChild(removeItemBtn);
        itemAmountLabel.appendChild(itemAmount);

        const itemContent = document.createTextNode(menuItemName);
        itemName.appendChild(itemContent);

        menuAmountContainer.appendChild(itemContainer);
    }

});