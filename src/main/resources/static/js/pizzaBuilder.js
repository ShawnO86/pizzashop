// todo: setup show/hide button for cart area, custom pizza form.
//  -- test getting form data on click of '.addPizza-btn'
document.addEventListener('DOMContentLoaded', ()=> {

    const pizzaBuilderContainer = document.getElementById("pizza-builder-container");
    const toppingSelectionContainer = document.getElementById("pizza-topping-container");
    const pizzaSizeSelectionContainer = document.getElementById("pizza-size-container");
    const qtyInput = document.getElementById("pizza-qty-input");
    const pizzaSubmitBtn = document.getElementById("submit-pizza-btn");
    const pizzaPriceDisplay = document.getElementById("pizza-price");

    const pizzaSizeSelectors = pizzaSizeSelectionContainer.querySelectorAll('input[type="radio"][name="pizzaSizeRadios"]');

    //const toppingSelectors = toppingSelectionContainer.querySelectorAll('.topping-checkbox');
    //const extraToppingSelectors = toppingSelectionContainer.querySelectorAll('.extra-topping-checkbox');
    const ingredientPriceElements = toppingSelectionContainer.querySelectorAll(".ingredient-data");
    const extraIngredientPriceElements = toppingSelectionContainer.querySelectorAll(".extra-ingredient-data");

    // todo : if 'edit' btn pressed populate pizza object with that data.
    const pizza = {
        "name" : "",
        "quantity" : 1,
        "size-data" : {"size" : "SMALL", "price" : pizzaPriceMap["SMALL"].price},
        "topping-data" : {"toppings" : {}},
        "extra-topping-data" : {"toppings" : {}}
    };

    // gets initial price for pizza from Cheese Pizza Prices from template
    // pizzaPriceMap contains map for {price:, maxQty:} to set price and max qty input
    // setPriceSelector adds {ingredientAmount:} to pizzaPriceMap to use outside pizzaSizeSelectors.forEach
    setPriceSelector();
    qtyInput.setAttribute("max", pizzaPriceMap['SMALL'].maxQty);

    console.log(pizzaPriceMap);

    function displayAsCurrency(amount, isTopping) {
        amount = amount.toString();
        const dollar = amount.slice(0, -2) == "" ? "0" : amount.slice(0, -2);
        const cents = amount.slice(-2);

        if (isTopping) {
            return "+ $" + dollar + "." + cents;
        }
        return "$" + dollar + "." + cents;
    }

    function setPriceSelector() {
        let sizePriceEl, sizeData;
        pizzaSizeSelectors.forEach(el => {
            const size = el.dataset.pizzaSize;
            pizzaPriceMap[size]["ingredientAmount"] = el.dataset.ingredientAmount;
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
        setToppingPrices(smallPizzaAmt, ingredientPriceElements, false);
        setToppingPrices(smallPizzaAmt, extraIngredientPriceElements, true);
        pizzaPriceDisplay.innerText = displayAsCurrency(pizza["size-data"].price);
    }

    function setToppingPrices(ingredientAmount, priceElements, isExtra) {
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

    // todo : build object and inputs with name attribute for CustomPizzaDTO on click of add to order button
    //  -- : need to get amounts of pizzas added to order and same system as adding/removing menuItems.

    pizzaBuilderContainer.addEventListener("click", function (event) {
        const target = event.target;
        let isPizzaObjChanged = false;

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

                        setToppingPrices(ingredientAmt, ingredientPriceElements, false);
                        setToppingPrices(ingredientAmt, extraIngredientPriceElements, true);

                        // todo :  add pizza size to pizza object for display and to hidden inputs for submission??
                        pizza["size-data"] = {"size" : pizzaSize, "price" : pizzaPriceMap[pizzaSize].price}
                        isPizzaObjChanged = true;
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
                            pizza["topping-data"].toppings[toppingName] = {"price" : toppingPrice, "id" : toppingId}
                        } else {
                            pizza["extra-topping-data"].toppings[toppingName] = {"price" : toppingPrice, "id" : toppingId}
                        }
                    } else {
                        if (toppingType == "regular") {
                            delete pizza["topping-data"].toppings[toppingName];
                        } else {
                            delete pizza["extra-topping-data"].toppings[toppingName];
                        }
                    }

                    break;
                case "number":
                    //changes pizza obj qty
                    pizza.quantity = qtyInput.value;

                    break;
                case "submit":
                    // todo : send pizza obj to "cart" aka createOrderItemAmountSelector()
                    console.log("submit button pressed, add to order.");

                    break;
            }

            console.log(pizza);
            if (isPizzaObjChanged) {
                // todo : update pizza price...
            }
        }
    });

    // todo : create inputs and display elements for each pizza with remove and edit buttons,
    //  -- : will require functions for removal and editing - must populate pizza object from this and check appropriate boxes.
    function createOrderItemAmountSelector() { }
});