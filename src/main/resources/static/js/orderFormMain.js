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

    let menuItems = {};
    let customPizzas = {};
    let pizzaCount = 0;
    let editingPizza = {};

    // populate cart if exist in session
    populateCartUI();
    console.log("menuItems:");
    console.log(menuItems);
    console.log("customPizzas:");
    console.log(customPizzas);

    let errorMessageElement = document.querySelector(".error");
    if (errorMessageElement) {
        const errMsg = errorMessageElement.children.item(0).innerText;
        if (errMsg === "No menu items added!" || errMsg === "Menu items and quantity mismatch!" || errMsg === "There was an error.") {
            setTimeout(() => {
                errorMessageElement.remove();
                errorMessageElement = null;
            }, 5000);

        } else if (errMsg === "Item mismatch!" || errMsg === "Not enough inventory!"){
            const cartItemContainers = document.querySelectorAll(".cartItemContainer");
            cartItemContainers.forEach((el) => {
                setTimeout(() => {
                    el.remove();
                }, 5000);
            });
        }
    }

    function saveCartObjectsToSession() {
        if (menuItems) {
            sessionStorage.setItem("menuItems", JSON.stringify(menuItems));
        }
        if (customPizzas) {
            sessionStorage.setItem("customPizzas", JSON.stringify(customPizzas));
        }
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
    }

    function populateCartUI() {
       // const
        // build cart item displays
        getCartObjectsFromSession();
        if (menuItems) {
            for (let menuItemId in menuItems) {
                const currentItem = menuItems[menuItemId];
                createOrderItemAmountSelectorMenu(currentItem.name, menuItemId, currentItem.price, currentItem.qty, currentItem.maxQty, menuAmountContainer);
            }
        }

        if (customPizzas) {
            for (let pizzaName in customPizzas) {
                const currentPizza = customPizzas[pizzaName];
                createOrderItemAmountSelectorPizza(currentPizza, menuAmountContainer);
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
            const menuItem = handleAddMenuItem(event, menuAmountContainer);
            if (menuItem) {
                menuItems[menuItem.menuItemId] = {
                    "name": menuItem.menuItemName, "qty": menuItem.orderInitQty, "maxQty": menuItem.menuItemMaxQty, "price": menuItem.menuItemPrice
                };
                saveCartObjectsToSession();
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
                createOrderItemAmountSelectorPizza(editingPizza, menuAmountContainer);
                customPizzas[editingPizza.pizzaName] = editingPizza;
                editingPizza = {};
            }
        }

        // radio, checkbox, input events of pizza builder
        const customPizzaData = handlePizzaBuilderEvents(event);
        // for adding pizza to order/cart
        if (customPizzaData) {
            // check if edited or not. -- if editing, remove customPizzaData.pizzaName from customPizzas to overwrite without duplicate keys
            if (customPizzaData[0]) {
                console.log("editing pizza:");
                console.log(editingPizza);
                delete customPizzas[editingPizza.pizzaName];
            }

            if (customPizzaData[1].pizzaName in customPizzas) {
                alert("Pizza name already in use. Please use another name.");
                return;
            }

            pizzaCount += 1;
            if (customPizzaData[1].pizzaName === "") {
                customPizzaData[1].pizzaName = "Unnamed Pizza " + pizzaCount;
            }

            const customPizza = {
                "pizzaName": customPizzaData[1].pizzaName,
                "quantity": customPizzaData[1].quantity,
                "price-per": customPizzaData[1]["price-per"],
                "total-price": customPizzaData[1]["total-price"],
                "size-data": {...customPizzaData[1]["size-data"]}, // Creates a copy of the nested objects as well so it's not referenced from others.
                "toppings": {...customPizzaData[1].toppings},
                "extra-toppings": {...customPizzaData[1]["extra-toppings"]}
            };

            createOrderItemAmountSelectorPizza(customPizza, menuAmountContainer);

            customPizzas[customPizza.pizzaName] = customPizza;
            saveCartObjectsToSession();
            console.log("customPizzas:");
            console.log(customPizzas);
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
                } else {
                    delete customPizzas[cartItemContainer.dataset.itemName];
                }

                saveCartObjectsToSession();

            }  else if (event.target.classList.contains("edit-item")) {
                console.log("edit item:");
                console.log(customPizzas[cartItemContainer.dataset.itemName]);
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
                } else {
                    customPizzas[cartItemContainer.dataset.itemName].quantity = parseInt(event.target.value);
                    qty = customPizzas[cartItemContainer.dataset.itemName].quantity;
                    price = customPizzas[cartItemContainer.dataset.itemName]["price-per"];
                    customPizzas[cartItemContainer.dataset.itemName]["total-price"] = price * qty;
                }

                cartItemContainer.querySelector(".cart-item-price").innerText = `${qty} x ${displayAsCurrency(price, false)}`;
                saveCartObjectsToSession();

            }

        } else if (event.target.type === "submit") {
            const order = {"menuItemList": [], "customPizzaList": []};
            console.log("submitting cart...");

            for (let menuItemId in menuItems) {
                order.menuItemList.push({
                    "menuItemID": menuItemId, "menuItemName": menuItems[menuItemId].name, "menuItemAmount": menuItems[menuItemId].qty
                });
            }

            for (let pizzaName in customPizzas) {
                const toppings = {};
                for (let topping in customPizzas[pizzaName].toppings) {
                    toppings[topping] = customPizzas[pizzaName].toppings[topping].id;
                }

                const extraToppings = {};
                for (let topping in customPizzas[pizzaName]["extra-toppings"]) {
                    extraToppings[topping] = customPizzas[pizzaName]["extra-toppings"][topping].id;
                }
                order.customPizzaList.push({
                    "pizzaName" : pizzaName,
                    "toppings": toppings,
                    "extraToppings": extraToppings,
                    "pizzaSize": customPizzas[pizzaName]["size-data"].size,
                    "quantity": customPizzas[pizzaName].quantity,
                    "pricePerPizza": customPizzas[pizzaName]["price-per"]
                });
            }

            console.log(order);
        }
    });


});