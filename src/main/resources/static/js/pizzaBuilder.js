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
    "pizza-name" : "",
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
    const dollar = Math.floor(amount / 100);
    const cents = amount % 100;
    const formattedCents = cents < 10 ? "0" + cents : cents;
    let formattedAsCurrency = "$" + dollar + "." + formattedCents;

    if (isTopping) {
        formattedAsCurrency = "+ " + formattedAsCurrency;
    }
    return formattedAsCurrency;
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

function updateFormPriceDisplay() {
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
        "pizza-name" : "",
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

qtyInput.addEventListener("change", () => {
    pizza["quantity"] = qtyInput.value;
    pizza["total-price"] = pizza["quantity"] * pizza["price-per"];
    updateFormPriceDisplay();
})

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

                    pizza["size-data"] = {"size" : pizzaSize, "price" : pizzaPriceMap[pizzaSize].price};

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
                        pizza["toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId};
                    } else {
                        pizza["extra-toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId};
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
                if (qtyInput.value !== pizza.quantity) {
                    pizza["quantity"] = qtyInput.value;
                    pizza["total-price"] = pizza["quantity"] * pizza["price-per"];
                    isPriceChanged = true;
                }
                break;
            case "submit":
                if (target.dataset.addType === "add") {
                    pizza["pizza-name"] = pizzaNameInput.value;
                    return [false, pizza];
                } else if (target.dataset.addType === "update") {
                    pizza["pizza-name"] = pizzaNameInput.value;
                    return [true, pizza];
                }
        }
    }

    if (isPriceChanged) {
        updateFormPriceDisplay();
    }
}

// no pizza object param will reset form
export function populateBuilderForm(pizzaObject = {}) {
    const pizzaToppingCheckboxes = toppingSelectionContainer.querySelectorAll(
        'input[type="checkbox"][name="toppingCheckboxes"]');
    const pizzaExtraToppingCheckboxes = toppingSelectionContainer.querySelectorAll(
        'input[type="checkbox"][name="extraToppingCheckboxes"]');

    pizzaObject = setPizzaObject(pizzaObject);

    pizzaNameInput.value = pizzaObject["pizza-name"];

    checkSizeRadio(pizzaObject["size-data"].size);
    checkToppingBoxes(pizzaObject["toppings"], pizzaToppingCheckboxes);
    checkToppingBoxes(pizzaObject["extra-toppings"], pizzaExtraToppingCheckboxes);
    qtyInput.value = pizzaObject.quantity;
    updateFormPriceDisplay();
}

function createOrderItemToppingHTML(pizzaIndex, toppings, toppingType) {
    const toppingStr = toppingType === "extra" ? "extraToppings" : "toppings";
    let toppingsHTML = "";
    let toppingIndex = 0;
    for (const toppingName in toppings) {
        toppingsHTML += `
            <input type="hidden" name="customPizzaList[${pizzaIndex}].${toppingStr}[${toppingIndex}].name" value="${toppingName}">
            <input type="hidden" name="customPizzaList[${pizzaIndex}].${toppingStr}[${toppingIndex}].id" value="${toppings[toppingName].id}">
        `;
        toppingIndex++;
    }
    return toppingsHTML;
}

export function createOrderItemAmountSelectorPizza(pizzaObject, container) {
    const itemContainer = document.createElement("div");
    const pizzaIndex = document.querySelectorAll(".cartItem-container[data-item-type='pizza item']").length;

    itemContainer.classList.add("cartItem-container");
    itemContainer.setAttribute("data-item-name", pizzaObject["pizza-name"]);
    itemContainer.setAttribute("data-item-type", "pizza item");
    itemContainer.setAttribute("data-item-price", pizzaObject["price-per"]);
    itemContainer.setAttribute("data-item-index", `customPizzaList[${pizzaIndex}]`)

    const toppingJoinArr = Object.keys(pizzaObject.toppings);
    const extraToppingArr = Object.keys(pizzaObject["extra-toppings"]);
    let extraToppingJoinString = [];
    let lightToppingJoinString = [];
    let toppingHTML = "";
    let extraToppingHTML = "";

    if (toppingJoinArr.length > 0) {
        console.log("normal topping")
        toppingHTML = createOrderItemToppingHTML(pizzaIndex, pizzaObject.toppings, "normal");
    }

    if (extraToppingArr.length > 0) {
        console.log("extra topping")
        extraToppingHTML = createOrderItemToppingHTML(pizzaIndex, pizzaObject["extra-toppings"], "extra");
    }

    for (let i = 0; i < extraToppingArr.length; i++) {
        if (extraToppingArr[i] in pizzaObject.toppings || extraToppingArr[i] === "Mozzarella") {
            extraToppingJoinString.push(extraToppingArr[i]);
        } else {
            lightToppingJoinString.push(extraToppingArr[i]);
        }
    }

    const toppingAmount = toppingJoinArr.length + lightToppingJoinString.length;
    let titleCasedSize = pizzaObject["size-data"].size.toLowerCase();
    titleCasedSize = titleCasedSize.charAt(0).toUpperCase() + titleCasedSize.slice(1);

    itemContainer.innerHTML = `
            <h5 class="space-between">
                <span>${pizzaObject["pizza-name"]}</span>
                <span class="cart-item-price">${pizzaObject.quantity} x ${displayAsCurrency(pizzaObject["price-per"], false)}</span>
            </h5>
            <div>
            <small><strong>${titleCasedSize} ${toppingAmount} Topping:</strong></small><br>
            ${toppingJoinArr.length > 0 ? `<small><strong>Normal:</strong> ${toppingJoinArr.join(", \n")}</small><br>` : ""}
            ${lightToppingJoinString.length > 0 ? `<small><strong>Light:</strong> ${lightToppingJoinString.join(", ")}</small><br>` : ""}
            ${extraToppingJoinString.length > 0 ? `<small><strong>Extra:</strong> ${extraToppingJoinString.join(", ")}</small><br>` : ""}
            </div>
            <button type="button" class="edit-item">Edit</button><br>
            <label>Qty:
                <input type="number" value="${pizzaObject.quantity}" min="1" max="${pizzaPriceMap[pizzaObject["size-data"].size].maxQty}" required/>
            </label>
            <button type="button" class="remove-item">Remove Item</button>
            <input type="hidden" name="customPizzaList[${pizzaIndex}].pizzaName" value="${pizzaObject["pizza-name"]}" />
            <input type="hidden" name="customPizzaList[${pizzaIndex}].pizzaSize.size" value="${pizzaObject["size-data"].size}" />
            <input type="hidden" name="customPizzaList[${pizzaIndex}].pizzaSize.price" value="${pizzaObject["size-data"].price}" />
            <input type="hidden" name="customPizzaList[${pizzaIndex}].quantity" value="${pizzaObject.quantity}" />
            <input type="hidden" name="customPizzaList[${pizzaIndex}].pricePerPizza" value="${pizzaObject["price-per"]}" />
            <input type="hidden" name="customPizzaList[${pizzaIndex}].totalPizzaPrice" value="${pizzaObject["total-price"]}" />
            ${toppingHTML}
            ${extraToppingHTML}
            `;

    container.appendChild(itemContainer);
}