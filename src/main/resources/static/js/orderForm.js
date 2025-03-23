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
                createOrderItemAmountSelector(event.target.name);
                menuItemNamesAndQuantities[orderItemName] = 1;
            } else {
                alert(orderItemName + " already part of order. Select a quantity if you want more of this item.")
            }
        }
    }

    function handleRemoveMenuItem(event){
        event.preventDefault();
        //console.log(event.target.value);
        if (event.target.classList.contains("remove-item")) {
            event.target.parentElement.remove();
            delete menuItemNamesAndQuantities[event.target.value];
        }
    }

    function createOrderItemAmountSelector(orderItemName) {
        const itemContainer = document.createElement("div");
        itemContainer.classList.add("inline");

        const itemName = document.createElement("p");

        const itemNameHiddenInput = document.createElement("input");
        itemNameHiddenInput.setAttribute("type", "hidden");
        itemNameHiddenInput.setAttribute("value", orderItemName);
        itemNameHiddenInput.name = "menuItemsNamesList";

        const itemAmount = document.createElement("input");
        itemAmount.setAttribute("type", "number");
        itemAmount.setAttribute("value", "1")
        itemAmount.setAttribute("min", "1");
        itemAmount.name = "menuItemsAmountsList";
        itemAmount.required = true;

        const removeItemBtn = document.createElement("button");
        removeItemBtn.innerText = "Remove Item";
        removeItemBtn.value = orderItemName;
        removeItemBtn.classList.add("remove-item");

        itemContainer.appendChild(itemNameHiddenInput);
        itemContainer.appendChild(itemName);
        itemContainer.appendChild(itemAmount);
        itemContainer.appendChild(removeItemBtn);

        const itemContent = document.createTextNode(orderItemName);
        itemName.appendChild(itemContent);

        menuAmountContainer.appendChild(itemContainer);
    }

});