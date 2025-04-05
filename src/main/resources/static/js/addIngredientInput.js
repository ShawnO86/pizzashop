document.addEventListener('DOMContentLoaded', ()=> {
    const ingredientAmountsContainer = document.getElementById('ingredientAmountsContainer');
    const addIngredientAmountButton = document.getElementById('addIngredient');
    let ingredientInputFieldAmount = 1;

    addIngredientAmountButton.addEventListener('click', (event) => {
        event.preventDefault();
        // copies everything within 'ingredient-amount' div
        const newIngredientAmountDiv = ingredientAmountsContainer.querySelector('.ingredient-amount').cloneNode(true);
        const ingredientSelect = newIngredientAmountDiv.querySelector('select');
        const amountInput = newIngredientAmountDiv.querySelector('input[type="number"]');

        ingredientSelect.name = "ingredientIdAmountsKeys";

        if (amountInput != null) {
            amountInput.name = "ingredientIdAmountsValues";
        }


        ingredientAmountsContainer.appendChild(newIngredientAmountDiv);

        //to not allow removal of last input field.
        ingredientInputFieldAmount += 1;
    });

    ingredientAmountsContainer.addEventListener('click',  handleRemoveIngredient);

    function handleRemoveIngredient(event) {
        if (event.target.classList.contains('remove-item') && ingredientInputFieldAmount >= 2) {
            event.target.parentElement.remove();
            ingredientInputFieldAmount -= 1;
        }
    }
});