
// todo : import needed functions here to build a JSON payload and send to Spring.

import {
    handleAddMenuItem,
    handleRemoveItem,
    createOrderItemAmountSelectorMenu,
} from "./addMenuItems.js";
import {
    displayAsCurrency,
    handlePizzaBuilderEvents,
    createOrderItemAmountSelectorPizza
} from "./pizzaBuilder.js";

document.addEventListener('DOMContentLoaded', ()=> {
    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");
    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");

/*  structure of cart objects for display and setting qty =>
    menuItems: {
        "id": {"name": "name", "qty": 0, "maxQty": 400, "price": 0.00},
        "id": {"name": "name", "qty": 0, "maxQty": 400, "price": 0.00},
        "id": {"name": "name", "qty": 0, "maxQty": 400, "price": 0.00}
    };

    customPizzas: {
        "name": {
            "pizzaName": "name",
            "quantity" : 1,
            "price-per" : pizzaPriceMap["SMALL"].price,
            "total-price" : pizzaPriceMap["SMALL"].price,
            "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
            "toppings" : {
                {id: 0, name: ""},
                {id: 0, name: ""}
                },
            "extra-toppings" : {
                {id: 0, name: ""},
                {id: 0, name: ""}
                }
        }
    };

    structure of order object for processing,
    receipt page will also use this object for display? =>
    order = {
        "menuItemDTOList": [
            {"menuItemID": 1, "menuItemName": "name", "menuItemAmount": 0},
            {"menuItemID": 2, "menuItemName": "name", "menuItemAmount": 0},
            {"menuItemID": 3, "menuItemName": "name", "menuItemAmount": 0}
        ],
        "customPizzaDTOList": [
            {
            "pizzaName": name,
            "toppings": {
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1}
                },
            "extraToppings": {
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1}
                },
            "pizzaSize": SIZE,
            "pricePer": 0.00,
            "quantity": 1,
            "totalPrice": 0.00
            },
            {
            "pizzaName": name,
            "toppings": {
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1}
                },
            "extraToppings": {
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1},
                {toppingName: "name", toppingId: 1}
                },
            "pizzaSize": SIZE,
            "pricePer": 0.00,
            "quantity": 1,
            "totalPrice": 0.00
            }
        ]
    }
    */

    let menuItems = {};
    let customPizzas = {};
    let pizzaCount = 0;

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

    // for adding new menu items
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
        }
    });

    // for adding new pizza items
    pizzaBuilderContainer.addEventListener("click", (event) => {
        // radio, checkbox, input events of pizza builder
        const customPizzaData = handlePizzaBuilderEvents(event);
        // for adding pizza to order/cart
        if (customPizzaData) {
            if (customPizzaData.pizzaName in customPizzas) {
                alert("Pizza name already in use. Use another name for this pizza.");
                return;
            }

            pizzaCount += 1;
            if (customPizzaData.pizzaName === "") {
                customPizzaData.pizzaName = "Unnamed Pizza " + pizzaCount;
            }

            const customPizza = {
                "pizzaName": customPizzaData.pizzaName,
                "quantity": customPizzaData.quantity,
                "price-per": customPizzaData["price-per"],
                "total-price": customPizzaData["total-price"],
                "size-data": {...customPizzaData["size-data"]}, // Creates a copy of the objects so it's not referenced from others.
                "toppings": {...customPizzaData.toppings},
                "extra-toppings": {...customPizzaData["extra-toppings"]}
            };

            createOrderItemAmountSelectorPizza(customPizza, menuAmountContainer);

            customPizzas[customPizza.pizzaName] = customPizza;
            saveCartObjectsToSession();
            console.log("customPizzas:");
            console.log(customPizzas);
        }
    });

    // for removing, updating qty, or submitting added cart items
    menuAmountContainer.addEventListener("click", (event) => {
        const cartItemContainer = event.target.closest('.cartItem-container');
        if (cartItemContainer) {
            const type = cartItemContainer.dataset.itemType;
            console.log(event.target.type)
            if (event.target.classList.contains("remove-item")) {
                handleRemoveItem(event);
                if (type === "menu item") {
                    delete menuItems[cartItemContainer.dataset.itemId];
                } else {
                    delete customPizzas[cartItemContainer.dataset.itemName];
                }
                saveCartObjectsToSession();
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
                }

                cartItemContainer.querySelector(".cart-item-price").innerText = `${qty} x ${displayAsCurrency(price, false)}`;
                saveCartObjectsToSession();
            }
            return;
        }

        if (event.target.type === "submit") {
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
                    "quantity": customPizzas[pizzaName].quantity
                });
            }

            console.log(order);
        }
    });


});