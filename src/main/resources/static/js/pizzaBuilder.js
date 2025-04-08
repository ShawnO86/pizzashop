// todo: setup show/hide button for cart area, custom pizza form.
//  -- test getting form data on click of '.addPizza-btn'
document.addEventListener('DOMContentLoaded', ()=> {

    const pizzaBuilderContainer = document.getElementById("pizzaBuilder-container");

    const toppingSelectionContainer = pizzaBuilderContainer.querySelector(".pizza-topping-container");
    const pizzaSizeSelectionContainer = pizzaBuilderContainer.querySelector(".pizza-size-container");
    const pizzaSubmitBtn = pizzaBuilderContainer.querySelector("#submit-pizza-btn");

    const pizzaSizeSelectors = pizzaSizeSelectionContainer.querySelectorAll('input[type="radio"][name="pizzaItemSizeList"]');

    const ingredientPriceElements = toppingSelectionContainer.querySelectorAll(".ingredient-data");
    const extraIngredientPriceElements = toppingSelectionContainer.querySelectorAll(".extra-ingredient-data");

    // gets price for pizza from Cheese Pizza Prices
    // pizzaPriceMap contains arrays for [price, amount available] to set price and max qty input
    console.log(pizzaPriceMap);
    pizzaSizeSelectors.forEach(el => {
        console.log(el.dataset.pizzaSize);
        let sizePriceEl = el.parentElement.querySelector(".pizza-size-price");

        // todo : display price as currency ($dollars.cents)
        //  -- set qty input field max to amt specified in map for size
        //  -- check qty available for ingredients after submit and produce error message if needed.
        switch (el.dataset.pizzaSize) {
            case 'SMALL':
                sizePriceEl.innerHTML = pizzaPriceMap.SMALL[0];
                break;
            case 'MEDIUM':
                sizePriceEl.innerHTML = pizzaPriceMap.MEDIUM[0];
                break;
            case 'LARGE':
                sizePriceEl.innerHTML = pizzaPriceMap.LARGE[0];
                break;
        }
    });

    // todo : set initial price of toppings, set initial size prices from cheese pizza prices - 'pizza-size-price' class

    pizzaBuilderContainer.addEventListener("click", function (event) {
        const target = event.target;
        if (target.type === "radio") {
            console.log(target.dataset.pizzaSize);
            console.log(target.parentElement)
            console.log(target.dataset.ingredientAmount);
            console.log("initial price: " + target.dataset.sizePrice);

            // todo : set topping prices when size radio button pressed,
            //  -- ingredientPrice is price from ingredientCost * markupMulti, ingredientAmount is amount used for size of pizza
            //  -- extraIngredientPrice is ingredientPrice * (ingredientAmount / 2)

            ingredientPriceElements.forEach(el => {
                console.log(el.dataset.ingredientName + " price per: " + el.dataset.ingredientPrice +
                    "price for " + target.dataset.pizzaSize + ":" + (el.dataset.ingredientPrice * target.dataset.ingredientAmount))
                el.innerText = "$" + (el.dataset.ingredientPrice * target.dataset.ingredientAmount) / 100 + '0';
            })

        } /*else if (target.type === "checkbox") {
            console.log(target);
            // todo :  add to pizza item object or something
        }*/
    })


});