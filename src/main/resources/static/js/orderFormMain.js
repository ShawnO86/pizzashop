
// todo : import needed functions here to build a JSON payload and send to Spring.

import {handleAddMenuItem, handleRemoveMenuItem} from "./addMenuItems.js";

document.addEventListener('DOMContentLoaded', ()=> {
    const menuItemsContainer = document.getElementById("menuItems-container");
    const menuAmountContainer = document.getElementById("menuAmount-container");
    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");


    // todo : separate order building object and JSON to be sent for easy setting qty?
    //  -- : add/remove to menuItemDTOList array from listeners or use different listener for 'submit order' btn?
    //  -- : need to get quantities after cart is submitted but before backend processing.

/*  structure of cart obj =>
    const cart = {
        "menuItems": {
            "name": {
                "id": 0,
                "qty": 0
            }
        },
        "customPizzas": {
            "name": {
                "quantity" : 1,
                "pizzaSize": "SMALL",
                "price-per" : pizzaPriceMap["SMALL"].price,
                "total-price" : pizzaPriceMap["SMALL"].price,
                "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
                "toppings" : {},
                "extra-toppings" : {}
            }
        }
    }*/
    const cart = {
        "menuItems": {},
        "customPizzas": {}
    }

    const order = {
        "menuItemDTOList": [],
        "customPizzaDTOList": []
    }

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

    menuItemsContainer.addEventListener("click", (event) => {

        handleAddMenuItem(event, menuAmountContainer);

        //order.menuItemDTOList.push();

        console.log(menuAmountContainer);
    });

    menuAmountContainer.addEventListener("click", (event) => {
        const delId = handleRemoveMenuItem(event);

/*        for (let i = 0; i < order["menuItemDTOList"].length; i++) {
            if (order.menuItemDTOList[i].menuItemID === delId) {
                order.menuItemDTOList.splice(i, 1);
            }
        }*/
        console.log(order);
    });

});