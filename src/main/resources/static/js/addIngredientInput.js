
document.addEventListener('DOMContentLoaded', ()=> {

    const ingredientAmountsContainer = document.getElementById('ingredientAmountsContainer');
    const addIngredientAmountButton = document.getElementById('addIngredient');
    let ingredientInputFieldAmount = ingredientAmountsContainer.querySelectorAll('.ingredient-amount').length;

    addIngredientAmountButton.addEventListener('click', () => {
        const newIngredientAmountDiv = ingredientAmountsContainer.querySelector('.ingredient-amount').cloneNode(true);

        const ingredientSelect = newIngredientAmountDiv.querySelector('select');
        const amountInput = newIngredientAmountDiv.querySelector('input[type="number"]');

        ingredientSelect.name = "ingredientIdAmountsKeys";
        amountInput.name = "ingredientIdAmountsValues";

        ingredientAmountsContainer.appendChild(newIngredientAmountDiv);
        ingredientInputFieldAmount = ingredientAmountsContainer.querySelectorAll('.ingredient-amount').length;
        console.log(ingredientInputFieldAmount);
    });

    ingredientAmountsContainer.addEventListener('click',  handleRemoveIngredient);

    function handleRemoveIngredient(event) {
        if (event.target.classList.contains('remove-ingredient') && ingredientInputFieldAmount >= 2) {
            event.target.parentElement.remove();
            ingredientInputFieldAmount = ingredientAmountsContainer.querySelectorAll('.ingredient-amount').length;
        }
    }

});
