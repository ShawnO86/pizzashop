import {
    handleAddMenuItem,
    handleRemoveItem,
    createOrderItemAmountSelectorMenu,
} from "./addMenuItems.js";
import {
    displayAsCurrency,
    handlePizzaBuilderEvents,
    createOrderItemAmountSelectorPizza,
    populateBuilderForm
} from "./pizzaBuilder.js";

document.addEventListener('DOMContentLoaded', ()=> {
    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");
    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");
    const menuItemsDisplay = document.getElementById("menuItems-display");
    const customPizzasDisplay = document.getElementById("customPizzas-display");
    const cartTotalElement = document.getElementById("order-total");
    const cartTotalInputElement = document.getElementById("order-total-input");

    let cartErrorElement = document.getElementById("cart-error");
    let validationErrorElement = document.getElementById("validation-errors");
    let menuItems = {};
    let customPizzas = {};
    let editingPizza = {};
    let pizzaCount = 0;
    let cartTotal = 0;

// populate cart objects in session with orderDTO if returned with error,
// cart objects will be overwritten with orderDTO if it's there.
    // todo: more robust error removal after setTime for each kind or not removing at all just format UI better?.
    if (cartErrorElement || validationErrorElement) {
        if (cartErrorElement) {
            removeErrorElement(cartErrorElement, 5000);
        }
        if (validationErrorElement) {
            removeErrorElement(validationErrorElement, 8000);
        }

        parseThymeleafItems();
    } else {
        // populate cart if exist in session
        console.log("outside of parse thymeleaf:", orderDTO);
        populateCartUI();
        updateCartTotal();
    }

    console.log("menuItems:");
    console.log(menuItems);
    console.log("customPizzas:");
    console.log(customPizzas);

    function removeErrorElement(element, time) {
        setTimeout(() => {
            element.remove();
            element = null;
        }, time);
    }

    function updateCartTotal() {
        cartTotal = 0;

        for (const menuItemId in menuItems) {
            cartTotal += menuItems[menuItemId].price * menuItems[menuItemId].qty;
        }
        for (const pizzaName in customPizzas) {
            cartTotal += customPizzas[pizzaName]["total-price"] * customPizzas[pizzaName]["quantity"];
        }

        cartTotalElement.innerText = displayAsCurrency(cartTotal, false);
        cartTotalInputElement.value = cartTotal;
    }

    function saveMenuObjectsToSession() {
        if (menuItems) {
            console.log("save menu items...");
            sessionStorage.setItem("menuItems", JSON.stringify(menuItems));
        }
        updateCartTotal();
    }

    function savePizzaObjectsToSession() {
        if (customPizzas) {
            console.log("save pizza items...");
            sessionStorage.setItem("customPizzas", JSON.stringify(customPizzas));
        }
        updateCartTotal();
    }

    function getCartObjectsFromSession() {
        const sessionMenuItems = sessionStorage.getItem("menuItems");
        const sessionCustomPizzas = sessionStorage.getItem("customPizzas");
        if (sessionMenuItems) {
            menuItems = JSON.parse(sessionMenuItems);
        }
        if (sessionCustomPizzas) {
            customPizzas = JSON.parse(sessionCustomPizzas);
            // for unnamed pizza counter
            for (let pizzaName in customPizzas) {
                pizzaName = pizzaName.split(" ")
                const pizzaNumber = Number(pizzaName[pizzaName.length - 1]);
                if (!Number.isNaN(pizzaNumber) && pizzaNumber > pizzaCount) {
                    pizzaCount = pizzaNumber;
                }
            }
        }
        updateCartTotal();
    }

    function parseThymeleafItems() {
        console.log("parse thymeleaf: ", orderDTO);
        if (orderDTO.menuItems) {
            menuItems = {};
            for (const menuItem of orderDTO.menuItems) {
                console.log("menuItem", menuItem, "\n", menuItem["menuItemID"])
                menuItems[menuItem["menuItemID"]] = {
                    "maxQty": menuItem["maxQty"],
                    "name": menuItem["menuItemName"],
                    "price": menuItem["pricePerItem"],
                    "qty": menuItem["menuItemAmount"]
                }
            }
            saveMenuObjectsToSession();
        }
        if (orderDTO.pizzaItems) {
            customPizzas = {};
            for (const pizzaItem of orderDTO.pizzaItems) {
                console.log("menuItem", pizzaItem)
                customPizzas[pizzaItem["pizzaName"]] = {
                    "extra-toppings": {...pizzaItem["extraToppings"]},
                    "toppings": {...pizzaItem["toppings"]},
                    "size-data": {...pizzaItem["pizzaSize"]},
                    "quantity": pizzaItem["quantity"],
                    "price-per": pizzaItem["pricePerPizza"],
                    "total-price": pizzaItem["totalPizzaPrice"],
                    "pizza-name": pizzaItem["pizzaName"]
                }
            }
            savePizzaObjectsToSession();
        }
        populateCartUI();
        updateCartTotal();
    }

    function populateCartUI() {
        // build cart item displays
        menuItemsDisplay.innerHTML = "";
        customPizzasDisplay.innerHTML = "";

        getCartObjectsFromSession();
        if (menuItems) {
            for (let menuItemId in menuItems) {
                const currentItem = menuItems[menuItemId];
                createOrderItemAmountSelectorMenu(currentItem.name, menuItemId, currentItem.price, currentItem.qty, currentItem.maxQty, menuItemsDisplay);
            }
        }

        if (customPizzas) {
            for (let pizzaName in customPizzas) {
                const currentPizza = customPizzas[pizzaName];
                createOrderItemAmountSelectorPizza(currentPizza, customPizzasDisplay);
            }
        }
    }

    function disableEditBtns() {
        const editBtns = menuAmountContainer.querySelectorAll(".edit-item");
        const builderBtn = document.getElementById("open-pizza-builder-btn");
        editBtns.forEach(el =>{
            el.disabled = true;
        })
        builderBtn.disabled = true;
    }

    function enableEditBtns() {
        const editBtns = menuAmountContainer.querySelectorAll(".edit-item");
        const builderBtn = document.getElementById("open-pizza-builder-btn");
        editBtns.forEach(el =>{
            el.disabled = false;
        })
        builderBtn.disabled = false;
    }

    // for adding cart items
    menuItemsContainer.addEventListener("click", (event) => {
        if (event.target.classList.contains("addMenuItem-btn")) {
            const menuItem = handleAddMenuItem(event, menuItemsDisplay);
            if (menuItem) {
                menuItems[menuItem.menuItemId] = {
                    "name": menuItem.menuItemName, "qty": menuItem.orderInitQty, "maxQty": menuItem.menuItemMaxQty, "price": menuItem.menuItemPrice
                };
                saveMenuObjectsToSession();
            }
            console.log(menuItems);

        } else if (event.target.id === "open-pizza-builder-btn") {
            console.log("open pizza builder..");
            populateBuilderForm();
            pizzaBuilderContainer.classList.remove("hide-area");
            disableEditBtns();
        }
    });

    // for adding new pizza items
    pizzaBuilderContainer.addEventListener("click", (event) => {
        if (event.target.id === "pizza-cancel-btn") {
            pizzaBuilderContainer.classList.add("hide-area");
            enableEditBtns();
            if (editingPizza.hasOwnProperty("toppings")) {
                createOrderItemAmountSelectorPizza(editingPizza, customPizzasDisplay);
                customPizzas[editingPizza["pizza-name"]] = editingPizza;
                editingPizza = {};
            }
        }

        // radio, checkbox, input events of pizza builder
        const customPizzaData = handlePizzaBuilderEvents(event);
        // for adding pizza to order/cart
        if (customPizzaData) {
            // check if edited or not. -- if editing, remove customPizzaData.pizzaName from customPizzas to overwrite without duplicate keys
            if (customPizzaData[0]) {
                delete customPizzas[editingPizza["pizza-name"]];
            }

            if (customPizzaData[1]["pizza-name"] in customPizzas) {
                alert("Pizza name already in use. Please use another name.");
                return;
            }

            pizzaCount += 1;
            if (customPizzaData[1]["pizza-name"] === "") {
                customPizzaData[1]["pizza-name"] = "Unnamed Pizza " + pizzaCount;
            }

            const customPizza = {
                "pizza-name": customPizzaData[1]["pizza-name"],
                "quantity": customPizzaData[1].quantity,
                "price-per": customPizzaData[1]["price-per"],
                "total-price": customPizzaData[1]["total-price"],
                "size-data": {...customPizzaData[1]["size-data"]}, // Creates a copy of the nested objects as well so it's not referenced from others.
                "toppings": {...customPizzaData[1].toppings},
                "extra-toppings": {...customPizzaData[1]["extra-toppings"]}
            };

            createOrderItemAmountSelectorPizza(customPizza, customPizzasDisplay);

            customPizzas[customPizza["pizza-name"]] = customPizza;
            savePizzaObjectsToSession();

            pizzaBuilderContainer.classList.add("hide-area");
            editingPizza = {};
            enableEditBtns();
        }
    });

    // for removing, updating qty, or submitting added cart items
    menuAmountContainer.addEventListener("click", (event) => {
        const cartItemContainer = event.target.closest('.cartItem-container');
        if (cartItemContainer) {
            const type = cartItemContainer.dataset.itemType;

            if (event.target.classList.contains("remove-item")) {
                 handleRemoveItem(event);

                if (type === "menu item") {
                    delete menuItems[cartItemContainer.dataset.itemId];
                    saveMenuObjectsToSession();
                } else {
                    delete customPizzas[cartItemContainer.dataset.itemName];
                    savePizzaObjectsToSession();
                }
                populateCartUI();

            }  else if (event.target.classList.contains("edit-item")) {
                editingPizza = customPizzas[cartItemContainer.dataset.itemName];
                handleRemoveItem(event);

                pizzaBuilderContainer.classList.remove("hide-area");
                populateBuilderForm(customPizzas[cartItemContainer.dataset.itemName]);
                disableEditBtns();
                delete customPizzas[cartItemContainer.dataset.itemName];

            } else if (event.target.type === "number") {
                let qty, price;
                if (type === "menu item") {
                    menuItems[cartItemContainer.dataset.itemId].qty = parseInt(event.target.value);
                    qty = menuItems[cartItemContainer.dataset.itemId].qty;
                    price = menuItems[cartItemContainer.dataset.itemId].price;
                    saveMenuObjectsToSession();
                } else {
                    customPizzas[cartItemContainer.dataset.itemName].quantity = parseInt(event.target.value);
                    qty = customPizzas[cartItemContainer.dataset.itemName].quantity;
                    price = customPizzas[cartItemContainer.dataset.itemName]["price-per"];
                    customPizzas[cartItemContainer.dataset.itemName]["total-price"] = price * qty;
                    savePizzaObjectsToSession();
                }

                cartItemContainer.querySelector(".cart-item-price").innerText = `${qty} x ${displayAsCurrency(price, false)}`;
            }

        }
    });

});