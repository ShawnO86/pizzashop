const BASE_ENDPOINT = "/employees"
const CURRENT_ORDERS_ENDPOINT = BASE_ENDPOINT + "/getCurrentOrders";
// needs menuItem id, name params
const MENU_ITEM_RECIPE_ENDPOINT = BASE_ENDPOINT + "/showMenuItemRecipe";
// needs customPizza id param
const CUSTOM_PIZZA_RECIPE_ENDPOINT = BASE_ENDPOINT + "/showCustomPizzaRecipe";
// needs order ID, employeeName
const SET_IN_PROGRESS_ENDPOINT = BASE_ENDPOINT + "/setInProgress";
const SET_COMPLETE_ENDPOINT = BASE_ENDPOINT + "/setIsComplete";
const CANCEL_IN_PROGRESS_ENDPOINT = BASE_ENDPOINT + "/cancelInProgress";

const EMPLOYEE_NAME = document.getElementById("employee-name").innerText;
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

export async function getCurrentOrders() {
    try {
        const response = await fetch(CURRENT_ORDERS_ENDPOINT);
        const result = await response.json();
        console.log("fetched current orders", result);
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

export async function cancelOrderInProgress(orderId) {
    const data = new URLSearchParams();
    data.append('orderId', orderId);
    try {
        const response = await fetch(CANCEL_IN_PROGRESS_ENDPOINT, {
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

export function appendOrderToUI(order, container) {
    const orderTemplate = document.createElement("div");
    let menuItemHTML, customPizzaHTML, userdetailHTML;

    if (order["menuItemList"] !== null) {
        menuItemHTML = buildMenuItemHTML(order['menuItemList']);
    }
    if (order["customPizzaList"] !== null) {
        customPizzaHTML = buildCustomPizzaHTML(order['customPizzaList']);
    }

    userdetailHTML = buildUserDetailHTML(order["userDetail"]);

    const orderDateReceived = new Date(order['orderDateTime']);
    const orderDateFormatted = [orderDateReceived.getMonth() + 1, orderDateReceived.getDate(), orderDateReceived.getHours(), orderDateReceived.getMinutes()]

    orderTemplate.classList.add("order");
    orderTemplate.setAttribute("data-order-id", order['orderID']);

    orderTemplate.innerHTML = `
        <details>
            <summary class="order-summary space-between">
                <span>Order ID: ${order['orderID']}</span>
                <span>Received on: ${orderDateFormatted[0]}/${orderDateFormatted[1]} @ ${orderDateFormatted[2]}:${orderDateFormatted[3]}</span>
            </summary>
            ${menuItemHTML ? menuItemHTML : ""}
            ${customPizzaHTML ? customPizzaHTML : ""}
            ${userdetailHTML}
            <div>
                ${order["employeeName"] === EMPLOYEE_NAME ? `
                    <button class="complete-btn" data-order-id="${order['orderID']}">Complete Order</button>
                    <button class="cancel-fulfillment-btn" data-order-id="${order['orderID']}">Cancel In Progress</button>` : ''}
                ${order["inProgress"] !== true ? `<button class="fulfill-btn" data-order-id="${order['orderID']}">Fulfill Order</button>` : `<p>Being fulfilled by: ${order["employeeName"]}</p>`}
            </div>
        </details>
        <hr>
    `;

    container.appendChild(orderTemplate);
}

function buildMenuItemHTML(menuItems) {
    let menuItemsTemplate = "<div><h3>Menu Items</h3>";
    for (let menuItem of menuItems) {
        menuItemsTemplate += `
            <p class="space-between-dashed"><strong>${menuItem['menuItemName']} x ${menuItem['menuItemAmount']}</strong>
                <button class="get-menu-item-recipe-btn" data-item-id="${menuItem['menuItemID']}">Get Recipe</button>
            </p>
        `;
    }

    return menuItemsTemplate + "</div>";
}

function buildCustomPizzaHTML(customPizzas) {
    let customPizzasTemplate = "<div><h3>Custom Pizzas</h3>";
    for (let customPizza of customPizzas) {
        customPizzasTemplate += `
            <p class="space-between-dashed"><strong>${customPizza['pizzaSize']['size']} ${customPizza['pizzaName']} x ${customPizza['quantity']}</strong>
                <button class="get-custom-pizza-recipe-btn" data-item-id="${customPizza["customPizzaID"]}">Get Recipe</button>
            </p>
        `;
    }

    return customPizzasTemplate + "</div>";
}

function buildUserDetailHTML(userdetail) {
    return `<div><h3>Customer Details</h3>
        <p class="space-between-dashed"><span>Name:</span> <span>${userdetail['firstName']} ${userdetail['lastName']}</span></p>
        <p class="space-between-dashed"><span>Phone:</span> <span>${userdetail['phone']}</span></p>
        <p class="space-between-dashed"><span>Address:</span> <span>${userdetail['address']} ${userdetail['city']}, ${userdetail['state']}</span></p>
        <p class="space-between-dashed"><span>Email:</span> <span>${userdetail['email']}</span></p>
    </div>`;
}

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
        <h4>Base Ingredients</h4>
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


