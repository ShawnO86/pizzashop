// todo: setup show/hide button for cart area, custom pizza form.
//  -- test getting form data on click of '.addPizza-btn'
document.addEventListener('DOMContentLoaded', ()=> {

    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");
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
    const pizza = {
        "name" : "",
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
    qtyInput.setAttribute("max", pizzaPriceMap['SMALL'].maxQty);

    console.log(pizzaPriceMap);

    function displayAsCurrency(amount, isTopping) {
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
            pizzaPriceMap[size]["extraIngredientAmount"] = el.dataset.ingredientAmount;
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

        pizzaSubTotalDisplay.innerText = displayAsCurrency(pizza["price-per"]);
        pizzaQtyDisplay.innerText = pizza["quantity"] + "x";
        pizzaTotalDisplay.innerText = displayAsCurrency(pizza["total-price"]);
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

    // todo : build object and inputs with name attribute for CustomPizzaDTO on click of add to order button
    //  -- : need to get amounts of pizzas added to order and same system as adding/removing menuItems.

    pizzaBuilderContainer.addEventListener("click", function (event) {
        const target = event.target;
        let isPriceChanged = false;
        console.log("target: " + target.type);

        if (target.matches('input[type="radio"], input[type="checkbox"], input[type="number"], input[type="submit"]')) {

            switch (target.type) {
                case "radio":
                    const pizzaSize = target.dataset.pizzaSize;
                    const ingredientAmt = pizzaPriceMap[pizzaSize].ingredientAmount;

                    console.log("pizzaPriceMap for selected size:\ninitial price (cents): " + pizzaPriceMap[pizzaSize].price + " amount available: " + pizzaPriceMap[pizzaSize].maxQty);
                    console.log("ingredient amount for selected size: " + pizzaPriceMap[pizzaSize].ingredientAmount);

                    if (pizza["size-data"].size != pizzaSize) {
                        console.log("changed size...");
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

                    console.log("checkbox: " + target.checked +
                        "\ningredient name: " + toppingName + "- price per: " + toppingPrice + "- id: " + toppingId);

                    //adds pizza ingredient name: {price,id} to pizza object for display, price total and id for hidden inputs for submission??
                    if (target.checked) {
                        if (toppingType == "regular") {
                            pizza["toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId}
                        } else {
                            pizza["extra-toppings"][toppingName] = {"price" : toppingPrice, "id" : toppingId}
                        }
                    } else {
                        if (toppingType == "regular") {
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
                    // todo : send pizza obj to "cart" via createOrderItemAmountSelector(),
                    //  -- : add name to order item obj
                    pizza["name"] = pizzaNameInput.value;
                    console.log("submit button pressed, add to order.");

                    break;
            }

            console.log(pizza);
            if (isPriceChanged) {
                // todo : update pizza pricePer...
                pizzaSubTotalDisplay.innerText = displayAsCurrency(pizza["price-per"]);
                pizzaTotalDisplay.innerText = displayAsCurrency(pizza["total-price"]);
                pizzaQtyDisplay.innerText = pizza["quantity"] + "x";
            }
        }
    });

    // todo : create inputs and display elements for each pizza with remove and edit buttons,
    //  -- : will require functions for removal and editing - must populate pizza object from this and check appropriate boxes.
    function createOrderItemAmountSelector() { }
});