const toppingSelectionContainer = document.getElementById("pizza-topping-container");
const pizzaSizeSelectionContainer = document.getElementById("pizza-size-container");
const qtyInput = document.getElementById("pizza-qty-input");
const pizzaSubTotalDisplay = document.getElementById("pizza-price-per");
const pizzaQtyDisplay = document.getElementById("pizza-qty-display");
const pizzaTotalDisplay = document.getElementById("pizza-price-total");
const pizzaNameInput = document.getElementById("pizza-name-input");
const pizzaSubmitBtn = document.getElementById("pizza-submit-btn");

const ingredientPriceElements = toppingSelectionContainer.querySelectorAll(".ingredient-data");
const extraIngredientPriceElements = toppingSelectionContainer.querySelectorAll(".extra-ingredient-data");

let pizza = {
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
    const pizzaSizeSelectors = pizzaSizeSelectionContainer.querySelectorAll('input[type="radio"][name="pizzaSizeRadios"]');
    let sizePriceEl, sizeData;
    pizzaSizeSelectors.forEach(el => {
        const size = el.dataset.pizzaSize;
        pizzaPriceMap[size]["ingredientAmount"] = el.dataset.ingredientAmount;
        pizzaPriceMap[size]["extraIngredientAmount"] = el.dataset.extraIngredientAmount;
        sizePriceEl = pizzaSizeSelectionContainer.querySelector("." + size);
        sizeData = pizzaPriceMap[size];
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

function handleRadioChange(pizzaSize) {
    const ingredientAmt = pizzaPriceMap[pizzaSize].ingredientAmount;
    qtyInput.setAttribute("max", pizzaPriceMap[pizzaSize].maxQty);

    setToppingPriceDisplay(ingredientAmt, ingredientPriceElements, false);
    setToppingPriceDisplay(ingredientAmt, extraIngredientPriceElements, true);
}


export function handlePizzaBuilderEvents(event) {
    const target = event.target;
    let isPriceChanged = false;
    console.log(target.id);
    if (target.matches('input[type="radio"], input[type="checkbox"], input[type="number"], button[type="submit"]')) {
        switch (target.type) {
            case "radio":
                const pizzaSize = target.dataset.pizzaSize;

                if (pizza["size-data"].size !== pizzaSize) {
                    handleRadioChange(pizzaSize);

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

                updatePizzaPricePer();
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
                if (target.dataset.addType === "add") {
                    pizza.pizzaName = pizzaNameInput.value;
                    return [false, pizza];
                } else if (target.dataset.addType === "update") {
                    pizza.pizzaName = pizzaNameInput.value;
                    return [true, pizza];
                }
        }
    }

    if (isPriceChanged) {
        updateFormPrice();
    }
}

// no pizza object param will reset form
export function populateBuilderForm(pizzaObject = {}) {
    const pizzaToppingCheckboxes = toppingSelectionContainer.querySelectorAll(
        'input[type="checkbox"][name="toppingCheckboxes"]');
    const pizzaExtraToppingCheckboxes = toppingSelectionContainer.querySelectorAll(
        'input[type="checkbox"][name="extraToppingCheckboxes"]');

    pizzaObject = setPizzaObject(pizzaObject);

    pizzaNameInput.value = pizzaObject["pizzaName"];

    checkSizeRadio(pizzaObject["size-data"].size);
    checkToppingBoxes(pizzaObject["toppings"], pizzaToppingCheckboxes);
    checkToppingBoxes(pizzaObject["extra-toppings"], pizzaExtraToppingCheckboxes);
    qtyInput.value = pizzaObject.quantity;
    updateFormPrice();
}

function updateFormPrice() {
    pizzaSubTotalDisplay.innerText = displayAsCurrency(pizza["price-per"]);
    pizzaTotalDisplay.innerText = displayAsCurrency(pizza["total-price"]);
    pizzaQtyDisplay.innerText = pizza["quantity"] + "x";
}

function checkSizeRadio(size) {
    const radioBtn = document.getElementById(size.toLowerCase() + "-radio");
    radioBtn.checked = true;
    handleRadioChange(size);
}

function checkToppingBoxes(toppings, checkboxes) {
    checkboxes.forEach(el => {
        if (toppings[el.dataset.toppingName]) {
            el.checked = true;
        } else {
            el.checked = false;
        }
    })
}

function setPizzaObject(pizzaObject) {
    const defaultPizza = {
        "pizzaName" : "",
        "quantity" : 1,
        "price-per" : pizzaPriceMap["SMALL"].price,
        "total-price" : pizzaPriceMap["SMALL"].price,
        "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
        "toppings" : {},
        "extra-toppings" : {}
    };

    if (Object.keys(pizzaObject).length > 0) {
        pizza = pizzaObject;
        pizzaSubmitBtn.dataset.addType = "update";
    } else {
        pizza = defaultPizza;
        pizzaSubmitBtn.dataset.addType = "add";
    }

    return pizza;
}

export function createOrderItemAmountSelectorPizza(pizzaObject, container) {
    const itemContainer = document.createElement("div");

    itemContainer.classList.add("cartItem-container");
    itemContainer.setAttribute("data-item-name", pizzaObject.pizzaName);
    itemContainer.setAttribute("data-item-type", "pizza item");
    itemContainer.setAttribute("data-item-price", pizzaObject["price-per"]);

    const toppingJoinString = Object.keys(pizzaObject.toppings);
    const extraToppingArr = Object.keys(pizzaObject["extra-toppings"]);
    let extraToppingJoinString = [];
    let lightToppingJoinString = [];

    for (let i = 0; i < extraToppingArr.length; i++) {
        if (extraToppingArr[i] in pizzaObject.toppings || extraToppingArr[i] === "Mozzarella") {
            extraToppingJoinString.push(extraToppingArr[i]);
        } else {
            lightToppingJoinString.push(extraToppingArr[i]);
        }
    }

    // todo : updating pizza display

    const toppingAmount = toppingJoinString.length + lightToppingJoinString.length;

    let titleCasedSize = pizzaObject["size-data"].size.toLowerCase();
    titleCasedSize = titleCasedSize.charAt(0).toUpperCase() + titleCasedSize.slice(1);

    itemContainer.innerHTML = `
            <h5 class="space-between">
                <span>${pizzaObject.pizzaName}</span>
                <span class="cart-item-price">${pizzaObject.quantity} x ${displayAsCurrency(pizzaObject["price-per"], false)}</span>
            </h5>
            <div>
            <small><strong>${titleCasedSize} ${toppingAmount} ${toppingAmount <= 1 ? `topping` : `toppings`}</strong></small><br>
            ${toppingJoinString.length > 0 ? `<small><strong>Toppings:</strong> ${toppingJoinString.join(", \n")}</small><br>` : ""}
            ${lightToppingJoinString.length > 0 ? `<small><strong>Light:</strong> ${lightToppingJoinString.join(", ")}</small><br>` : ""}
            ${extraToppingJoinString.length > 0 ? `<small><strong>Extra:</strong> ${extraToppingJoinString.join(", ")}</small><br>` : ""}
            </div>
            <button class="edit-item">Edit</button><br>
            <label>Qty:
                <input type="number" value="${pizzaObject.quantity}" min="1" max="${pizzaPriceMap[pizzaObject["size-data"].size].maxQty}" required/>
            </label>
            <button class="remove-item">Remove Item</button>`;

    container.appendChild(itemContainer);
}