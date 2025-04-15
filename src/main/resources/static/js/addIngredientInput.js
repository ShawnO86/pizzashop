document.addEventListener('DOMContentLoaded', ()=> {
    const ingredientAmountsContainer = document.getElementById('ingredientAmountsContainer');
    const addIngredientAmountButton = document.getElementById('addIngredient');

    const initialIngredientAmountDivs = ingredientAmountsContainer.querySelectorAll('.ingredient-amount');
    let ingredientInputFieldAmount = initialIngredientAmountDivs == null ? 1 : initialIngredientAmountDivs.length;

    addIngredientAmountButton.addEventListener('click', (event) => {
        event.preventDefault();
        // copies everything within 'ingredient-amount' div
        const newIngredientAmountDiv = ingredientAmountsContainer.querySelector('.ingredient-amount').cloneNode(true);
        const ingredientSelect = newIngredientAmountDiv.querySelector('select');
        const amountInput = newIngredientAmountDiv.querySelector('input[type="number"]');
        amountInput.value = 1;

        ingredientSelect.name = "ingredientIdAmountsKeys";

        if (amountInput) {
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