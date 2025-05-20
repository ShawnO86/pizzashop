const CURRENT_ORDERS_ENDPOINT = "/employees/getCurrentOrders";
// needs menuItem id, name params
const MENU_ITEM_RECIPE_ENDPOINT = "/employees/showMenuItemRecipe";
// needs customPizza id param
const CUSTOM_PIZZA_RECIPE_ENDPOINT = "/employees/showCustomPizzaRecipe";
// needs order ID, employeeName
const SET_IN_PROGRESS_ENDPOINT = "/employees/setInProgress";
const SET_COMPLETE_ENDPOINT = "/employees/setIsComplete";

const EMPLOYEE_NAME = document.getElementById("employee-name").innerText;
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

export async function getCurrentOrders() {
    try {
        const response = await fetch(CURRENT_ORDERS_ENDPOINT);
        const result = await response.json();
        console.log("current orders", result);
        return result;
    } catch (e) {
        console.error(e.message);
    }
}

export async function setOrderInProgress(orderId) {
    const data = new URLSearchParams();
    data.append('orderId', orderId);
    data.append('employeeName', EMPLOYEE_NAME);
    try {
        const response = await fetch(SET_IN_PROGRESS_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken
            },
            body: data.toString()
        });
        if (!response.ok) {
            console.error(response)
            alert(await response.text());
        }
    } catch (e) {
        console.error(e);
    }
}

export async function setOrderComplete(orderId) {
    const data = new URLSearchParams();
    data.append('orderId', orderId);
    try {
        const response = await fetch(SET_COMPLETE_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken
            },
            body: data.toString()
        });
        if (!response.ok) {
            console.error(response)
            alert(await response.text());
        }
    } catch (e) {
        console.error(e);
    }
}

export async function getMenuItemRecipe(menuItemId) {
    try {
        const response = await fetch(MENU_ITEM_RECIPE_ENDPOINT + "?menuItemId=" + menuItemId);
        const result = await response.json();
        console.log("fetching menu item recipe...");
        return result;
    } catch (e) {
        console.error(e.message);
    }
}

export async function getCustomPizzaRecipe(customPizzaId) {
    try {
        const response = await fetch(CUSTOM_PIZZA_RECIPE_ENDPOINT + "?customPizzaId=" + customPizzaId);
        const result = await response.json();
        console.log("fetching custom pizza recipe...");
        return result;
    } catch (e) {
        console.error(e.message);
    }
}

// todo: -> add cancel fulfillment btn and related endpoints/fetch?
export function appendOrderToUI(order, container) {
    const orderTemplate = document.createElement("div");
    let menuItemHTML, customPizzaHTML;
    // todo: format date, topping arrays, add 'get recipe' button,
    //  --: will call respective /getRecipe?id=itemId..
    if (order["menuItemList"] !== null) {
        menuItemHTML = buildMenuItemHTML(order['menuItemList']);
    }
    if (order["customPizzaList"] !== null) {
        customPizzaHTML = buildCustomPizzaHTML(order['customPizzaList']);
    }

    const orderDateReceived = new Date(order['orderDateTime']);
    const orderDateFormatted = [orderDateReceived.getMonth(), orderDateReceived.getDate(), orderDateReceived.getHours(), orderDateReceived.getMinutes()]

    orderTemplate.innerHTML = `
                <div class="order" data-order-id="${order['orderID']}">
                    <details>
                        <summary class="order-summary space-between">
                            <span>Order ID: ${order['orderID']}</span>
                            <span>Received on: ${orderDateFormatted[0]}/${orderDateFormatted[1]} @ ${orderDateFormatted[2]}:${orderDateFormatted[3]}</span>
                        </summary>
                        ${menuItemHTML ? menuItemHTML : ""}
                        ${menuItemHTML && customPizzaHTML ? `<br>` : ""}
                        ${customPizzaHTML ? customPizzaHTML : ""}
                        <div>
                            ${order["employeeName"] === EMPLOYEE_NAME ? `<button class="complete-btn" data-order-id="${order['orderID']}">Complete Order</button>` : ''}
                            ${order["inProgress"] !== true ? `<button class="fulfill-btn" data-order-id="${order['orderID']}">Fulfill Order</button>` : `<p>Being fulfilled by: ${order["employeeName"]}</p>`}
                        </div>
                    </details>
                    <hr>
                </div>
            `;

    container.appendChild(orderTemplate);
}

function buildMenuItemHTML(menuItems) {
    let menuItemsTemplate = "<h3>Menu Items</h3><hr>";
    for (let menuItem of menuItems) {
        menuItemsTemplate += `
            <p class="space-between"><strong>${menuItem['menuItemName']} x ${menuItem['menuItemAmount']}</strong>
                <button class="get-menu-item-recipe-btn" data-item-id="${menuItem["menuItemID"]}">Get Recipe</button>
            </p>
        `;
    }

    return menuItemsTemplate;
}

function buildCustomPizzaHTML(customPizzas) {
    let customPizzasTemplate = "<h3>Custom Pizzas</h3><hr>";
    for (let customPizza of customPizzas) {
        customPizzasTemplate += `
            <p class="space-between"><strong>${customPizza['pizzaSize']['size']} ${customPizza['pizzaName']} x ${customPizza['quantity']}</strong>
                <button class="get-custom-pizza-recipe-btn" data-item-id="${customPizza["customPizzaID"]}">Get Recipe</button>
            </p>
        `;
    }

    return customPizzasTemplate;
}

// todo: build dialog box with recipe and close button
export function buildRecipeDisplay(item, container) {
    console.log("in build recipe display:", item);

    const ingredientsHTML = buildIngredientsHTML(item["Ingredients"]);
    let toppingsHTML = "";
    if (item["Toppings"]) {
        toppingsHTML = buildIngredientsHTML(item["Toppings"]);
    }

    container.innerHTML = `
        <h3>${item["Name"][0]}</h3>
        <p>${item["Description"][0]}</p>
        <h4>Ingredients</h4>
        <ul>
            ${ingredientsHTML}
        </ul>
        ${toppingsHTML !== "" ? `
        <h4>Toppings</h4>
        <ul>
        ${toppingsHTML}
        </ul>
        ` : ""}
    `;


}

function buildIngredientsHTML(ingredients) {
    let ingredientList = "";
    for (let ingredient of ingredients) {
        ingredientList += `<li>${ingredient}</li>`
    }
    return ingredientList;
}






