// todo: setup show/hide button for cart area, custom pizza form.
//  -- test getting form data on click of '.addPizza-btn'
const toppingSelectionContainer = document.getElementById("pizza-topping-container");
const pizzaSizeSelectionContainer = document.getElementById("pizza-size-container");
const qtyInput = document.getElementById("pizza-qty-input");
const pizzaSubTotalDisplay = document.getElementById("pizza-price-per");
const pizzaQtyDisplay = document.getElementById("pizza-qty-display");
const pizzaTotalDisplay = document.getElementById("pizza-price-total");
const pizzaNameInput = document.getElementById("pizza-name-input");

const pizzaSizeSelectors = pizzaSizeSelectionContainer.querySelectorAll('input[type="radio"][name="pizzaSizeRadios"]');

const ingredientPriceElements = toppingSelectionContainer.querySelectorAll(".ingredient-data");
const extraIngredientPriceElements = toppingSelectionContainer.querySelectorAll(".extra-ingredient-data");

// todo : if 'edit' btn pressed populate pizza object with that data.
//  -- : send to main JSON as part of customPizzaDTOList
//  -- : set fields used in backend to exact names and change in script accordingly
const pizza = {
    "pizzaName" : "",
    "quantity" : 1,
    "price-per" : pizzaPriceMap["SMALL"].price,
    "total-price" : pizzaPriceMap["SMALL"].price,
    "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
    "toppings" : {},
    "extra-toppings" : {}
};

// gets initial price for pizza from Cheese Pizza Prices from template
// pizzaPriceMap contains map for {price:, maxQty:} to set price and max qty input
// setSizePriceSelector adds {ingredientAmount:, extraIngredientAmount:} to pizzaPriceMap to use outside pizzaSizeSelectors.forEach
setSizePriceSelector();
pizzaSubTotalDisplay.innerText = displayAsCurrency(pizza["price-per"]);
pizzaQtyDisplay.innerText = pizza["quantity"] + "x";
pizzaTotalDisplay.innerText = displayAsCurrency(pizza["total-price"]);
qtyInput.setAttribute("max", pizzaPriceMap['SMALL'].maxQty);

export function displayAsCurrency(amount, isTopping) {
    amount = amount.toString();
    const dollar = amount.slice(0, -2) === "" ? "0" : amount.slice(0, -2);
    const cents = amount.slice(-2);

    if (isTopping) {
        return "+ $" + dollar + "." + cents;
    }
    return "$" + dollar + "." + cents;
}

function setSizePriceSelector() {
    let sizePriceEl, sizeData;
    pizzaSizeSelectors.forEach(el => {
        const size = el.dataset.pizzaSize;
        pizzaPriceMap[size]["ingredientAmount"] = el.dataset.ingredientAmount;
        pizzaPriceMap[size]["extraIngredientAmount"] = el.dataset.extraIngredientAmount;
        sizePriceEl = pizzaSizeSelectionContainer.querySelector("." + size);
        sizeData = pizzaPriceMap[size];
        //  todo : check qty available for ingredients after submit and produce error message if needed.
        if (sizeData) {
            sizePriceEl.innerHTML = displayAsCurrency(sizeData.price, false);
        } else {
            sizePriceEl.innerHTML = "No data";
        }
    });
    // sets initial topping prices for small pizza
    const smallPizzaAmt = document.getElementById("small-radio").dataset.ingredientAmount;
    setToppingPriceDisplay(smallPizzaAmt, ingredientPriceElements, false);
    setToppingPriceDisplay(smallPizzaAmt, extraIngredientPriceElements, true);
}

function setToppingPriceDisplay(ingredientAmount, priceElements, isExtra) {
    let ingredientPricePer,ingredientPrice;
    priceElements.forEach(el => {
        ingredientPricePer = el.dataset.ingredientPrice;
        if (!isExtra) {
            ingredientPrice = ingredientPricePer * ingredientAmount;
        } else {
            ingredientPrice = ingredientPricePer * (ingredientAmount / 2);
        }
        el.innerText = displayAsCurrency(ingredientPrice, true);
    });
}

function updatePizzaPricePer() {
    // for one pizza... price for qty is totaled in number case
    const ingredientAmt = pizzaPriceMap[pizza["size-data"].size].ingredientAmount;
    const extraIngredientAmt = pizzaPriceMap[pizza["size-data"].size].extraIngredientAmount;
    let toppingPrice = 0;
    let extraToppingPrice = 0;

    for (const toppingName in pizza["toppings"]) {
        toppingPrice += parseInt(pizza["toppings"][toppingName].price);
    }
    for (const toppingName in pizza["extra-toppings"]) {
        extraToppingPrice += parseInt(pizza["extra-toppings"][toppingName].price);
    }
    toppingPrice = toppingPrice * ingredientAmt;
    extraToppingPrice = extraToppingPrice * extraIngredientAmt;

    pizza["price-per"] = toppingPrice + extraToppingPrice + pizza["size-data"].price;
    pizza["total-price"] = pizza["price-per"] * pizza["quantity"];
}

export function handlePizzaBuilderEvents(event) {
    const target = event.target;
    let isPriceChanged = false;
    if (target.matches('input[type="radio"], input[type="checkbox"], input[type="number"], button[type="submit"]')) {
        switch (target.type) {
            case "radio":
                const pizzaSize = target.dataset.pizzaSize;
                const ingredientAmt = pizzaPriceMap[pizzaSize].ingredientAmount;

                if (pizza["size-data"].size !== pizzaSize) {
                    qtyInput.setAttribute("max", pizzaPriceMap[pizzaSize].maxQty);

                    setToppingPriceDisplay(ingredientAmt, ingredientPriceElements, false);
                    setToppingPriceDisplay(ingredientAmt, extraIngredientPriceElements, true);

                    pizza["size-data"] = {"size" : pizzaSize, "price" : pizzaPriceMap[pizzaSize].price}

                    updatePizzaPricePer();
                    isPriceChanged = true;
                }

                break;
            case "checkbox":
                const toppingName = target.dataset.toppingName;
                const toppingPrice = target.dataset.toppingPrice;
                const toppingId = target.dataset.toppingId;
                const toppingType = target.dataset.toppingType;

                //adds pizza ingredient name: {price,id} to pizza object for display
                if (target.checked) {
                    if (toppingType === "regular") {
                        pizza["toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId}
                    } else {
                        pizza["extra-toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId}
                    }
                } else {
                    if (toppingType === "regular") {
                        delete pizza["toppings"][toppingName];
                    } else {
                        delete pizza["extra-toppings"][toppingName];
                    }
                }
                updatePizzaPricePer()
                isPriceChanged = true;

                break;
            case "number":
                //changes pizza obj qty
                if (qtyInput.value !== pizza.quantity) {
                    pizza["quantity"] = qtyInput.value;
                    pizza["total-price"] = pizza["quantity"] * pizza["price-per"];
                    isPriceChanged = true;
                }

                break;
            case "submit":
                console.log("submit button pressed, add to order.");
                pizza.pizzaName = pizzaNameInput.value;
                console.log(pizza);
                return pizza;
        }

        if (isPriceChanged) {
            pizzaSubTotalDisplay.innerText = displayAsCurrency(pizza["price-per"]);
            pizzaTotalDisplay.innerText = displayAsCurrency(pizza["total-price"]);
            pizzaQtyDisplay.innerText = pizza["quantity"] + "x";
        }
    }
}

// todo : create inputs and display elements for each pizza with remove and edit buttons,
//  -- : will require functions for removal and editing - must populate pizza object from this and check appropriate boxes.
//  -- : probably need to use "show/hide" function and pass in pizza object?
//  -- : for "edit pizza" - loop over form elements and add "checked" based on object - set this pizza obj to passed in value for events -- put in show/hide function?

export function createOrderItemAmountSelectorPizza(pizzaObject, container) {
    const itemContainer = document.createElement("div");

    itemContainer.classList.add("cartItem-container");
    itemContainer.setAttribute("data-item-name", pizzaObject.pizzaName);
    itemContainer.setAttribute("data-item-type", "pizza item");
    itemContainer.setAttribute("data-item-price", pizzaObject["price-per"]);

    const toppingString = Object.keys(pizzaObject.toppings).join(", ");
    const extraToppingArr = Object.keys(pizzaObject["extra-toppings"]);
    let extraToppingJoinString = [];
    let lightToppingJoinString = [];

    console.log(extraToppingArr);
    for (let i = 0; i < extraToppingArr.length; i++) {
        if (extraToppingArr[i] in pizzaObject.toppings || extraToppingArr[i] === "Mozzarella") {
            extraToppingJoinString.push(extraToppingArr[i]);
        } else {
            lightToppingJoinString.push(extraToppingArr[i]);
        }
    }

    itemContainer.innerHTML = `
            <h5 class="space-between">
                <span>${pizzaObject.pizzaName}</span>
                <span class="cart-item-price">${pizzaObject.quantity} x ${displayAsCurrency(pizzaObject["price-per"], false)}</span>
            </h5>
            <p>
            <small><strong>Toppings:</strong> ${toppingString}</small><br>
            ${extraToppingJoinString.length > 0 ? `<small><strong>Extra:</strong> ${extraToppingJoinString.join(", ")}</small><br>` : ""}
            ${lightToppingJoinString.length > 0 ? `<small><strong>Light:</strong> ${lightToppingJoinString.join(", ")}</small><br>` : ""}
            </p>
            <button class="edit-item">Edit</button><br>
            <label>Qty:
                <input type="number" value="${pizzaObject.quantity}" min="1" max="${pizzaPriceMap[pizzaObject["size-data"].size].maxQty}" required/>
            </label>
            <button class="remove-item">Remove Item</button>`;

    container.appendChild(itemContainer);
}