
// todo : import needed functions here to build a JSON payload and send to Spring.

import {handleAddMenuItem, handleRemoveMenuItem, createOrderItemAmountSelector} from "./addMenuItems.js";

document.addEventListener('DOMContentLoaded', ()=> {
    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");
    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");

/*  structure of cart objects for display and setting qty =>
    menuItems: {
        "id": {"name": "name", "qty": 0, "maxQty": 400},
        "id": {"name": "name", "qty": 0, "maxQty": 400},
        "id": {"name": "name", "qty": 0, "maxQty": 400}
    };

    customPizzas: {
        "name": {
            "quantity" : 1,
            "pizzaSize": "SMALL",
            "price-per" : pizzaPriceMap["SMALL"].price,
            "total-price" : pizzaPriceMap["SMALL"].price,
            "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
            "toppings" : {
                "name": {id: 0, price: 0.00},
                "name": {id: 0, price: 0.00}
                },
            "extra-toppings" : {
                "name": {id: 0, price: 0.00},
                "name": {id: 0, price: 0.00}
                }
        }
    };

    structure of order object for processing =>
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
            "quantity": 1
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
            "quantity": 1
            }
        ]
    }
    */

    let menuItems = {};
    let customPizzas = {};

    // populate cart if exist in session
    populateCartUI();
    console.log("menuItems:");
    console.log(menuItems);
    console.log("customPizzas:");
    console.log(customPizzas);

    const order = {"menuItemDTOList": [], "customPizzaDTOList": []};

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
        }
    }

    function populateCartUI() {
        getCartObjectsFromSession();
        if (menuItems) {
            for (let menuItemId in menuItems) {
                const currentItem = menuItems[menuItemId];
                createOrderItemAmountSelector(currentItem.name, menuItemId, currentItem.price, currentItem.qty, currentItem.maxQty, menuAmountContainer);
            }
        }
    }

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

    menuAmountContainer.addEventListener("click", (event) => {
        const cartItemContainer = event.target.closest('.cartItem-container');
        console.log(event.target.type)
        if (cartItemContainer) {
            const id = cartItemContainer.dataset.itemId;
            if (event.target.classList.contains("remove-item")) {
                handleRemoveMenuItem(event);
                delete menuItems[id];
            } else if (event.target.type == "number") {
                menuItems[id].qty = event.target.value;
            }
            saveCartObjectsToSession();
            return;
        }

        if (event.target.type == "submit") {
            //  todo : build order object
            console.log("submitting cart...");

            for (let menuItemId in menuItems) {
                order.menuItemDTOList.push({
                    "menuItemID": menuItemId, "menuItemName": menuItems[menuItemId].name, "menuItemAmount": menuItems[menuItemId].qty
                });
            }
            // same for custom pizzas...
        }

    });
});