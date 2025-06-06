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
    const formSubmitBtn = document.getElementById("cart-submit-btn");
    const builderBtn = document.getElementById("open-pizza-builder-btn");
    const pizzaBuilder = document.querySelector(".pizza-builder");
    const cartContainer = document.getElementById("cart-container");

    let cartErrorElement = document.getElementById("cart-errors");
    let availabilityErrorElement = document.getElementById("availability-errors");
    let priceErrorElement = document.getElementById("price-errors");

    let menuItems = {};
    let customPizzas = {};
    let editingPizza = {};
    let pizzaCount = 0;
    let cartTotal = 0;

    if (openPizzaBuilder) {
        setTimeout(() => {
            openPizzaBuilderForm();
        }, 150);
    }

// populate cart objects in session with orderDTO if returned with error,
// cart objects will be overwritten with orderDTO if it's there.
    if (cartErrorElement || availabilityErrorElement || priceErrorElement) {
        if (cartErrorElement) {
            removeErrorElement(cartErrorElement, 8000);
            if (cartErrorElement.innerText === "Your current order is being processed!") {
                if (sessionStorage.getItem("menuItems")) {
                    sessionStorage.removeItem("menuItems");
                }
                if (sessionStorage.getItem("customPizzas")) {
                    sessionStorage.removeItem("customPizzas");
                }
            }
        }
        if (availabilityErrorElement) {
            removeErrorElement(availabilityErrorElement, 10000);
        }
        if (priceErrorElement) {
            removeErrorElement(availabilityErrorElement, 10000);
        }
        parseThymeleafItems();
    } else {
        // populate cart if exist in session
        populateCartUI();
        updateCartTotal();
    }

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
            cartTotal += customPizzas[pizzaName]["total-price"];
        }

        cartTotalElement.innerText = displayAsCurrency(cartTotal, false);
        cartTotalInputElement.value = cartTotal;
    }

    function saveMenuObjectsToSession() {
        if (menuItems) {
            sessionStorage.setItem("menuItems", JSON.stringify(menuItems));
        }
        updateCartTotal();
    }

    function savePizzaObjectsToSession() {
        if (customPizzas) {
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
        if (orderDTO.menuItems) {
            menuItems = {};
            for (const menuItem of orderDTO.menuItems) {
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

    function updateCartItemTotal(type, container, event) {
        let qty, price;
        if (type === "menu item") {
            menuItems[container.dataset.itemId].qty = parseInt(event.target.value);
            qty = menuItems[container.dataset.itemId].qty;
            price = menuItems[container.dataset.itemId].price;
            saveMenuObjectsToSession();
        } else {
            customPizzas[container.dataset.itemName].quantity = parseInt(event.target.value);
            qty = customPizzas[container.dataset.itemName].quantity;
            price = customPizzas[container.dataset.itemName]["price-per"];
            customPizzas[container.dataset.itemName]["total-price"] = price * qty;
            const hiddenQtyInput = container.querySelector(`[name="${container.dataset.itemIndex}.quantity"]`);
            hiddenQtyInput.value = customPizzas[container.dataset.itemName].quantity;
            savePizzaObjectsToSession();
        }

        container.querySelector(".cart-item-price").innerText = `${qty} x ${displayAsCurrency(price, false)}`;
    }

    function openPizzaBuilderForm() {
        populateBuilderForm();
        pizzaBuilder.showModal();
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
                cartContainer.showModal();
            }
        }
    });

    // for adding new pizza items
    pizzaBuilderContainer.addEventListener("click", (event) => {
        if (event.target.id === "pizza-cancel-btn") {
            pizzaBuilder.close();
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

            pizzaBuilder.close();
            editingPizza = {};
            cartContainer.showModal();
        }
    });

    // for removing, updating qty, or submitting added cart items
    menuAmountContainer.addEventListener("click", (event) => {
        const cartItemContainer = event.target.closest('.cartItem-container');
        console.log(event.target)
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
                populateBuilderForm(customPizzas[cartItemContainer.dataset.itemName]);
                pizzaBuilder.showModal();
                delete customPizzas[cartItemContainer.dataset.itemName];

            } else if (event.target.type === "number") {
                updateCartItemTotal(type, cartItemContainer, event);
            }
        }
    });

    menuAmountContainer.addEventListener('keydown', (event) => {
        if (event.key === "Enter") {
            if (event.target.type === "number") {
                const cartItemContainer = event.target.closest('.cartItem-container');
                const type = cartItemContainer.dataset.itemType;
                updateCartItemTotal(type, cartItemContainer, event);
            }
            event.preventDefault(); // Prevent default Enter action in number inputs within the form (Submit)
        }
    });

    if (formSubmitBtn) {
        formSubmitBtn.addEventListener("click", () => {
            menuAmountContainer.submit();
        });
    }

    builderBtn.addEventListener('click', () => {
        openPizzaBuilderForm();
    })

    const showCartBtn = document.getElementById("show-cart-btn");
    const hideCartBtn = document.getElementById("hide-cart-btn");
    showCartBtn.addEventListener('click', () => {
        cartContainer.showModal();
    });
    hideCartBtn.addEventListener('click', () => {
        cartContainer.close();
    })


});