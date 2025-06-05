import {
    getCurrentOrders, appendOrderToUI, setOrderInProgress,
    setOrderComplete, getMenuItemRecipe, getCustomPizzaRecipe, buildRecipeDisplay, cancelOrderInProgress
} from "./getOrdersUtils.js";

document.addEventListener('DOMContentLoaded', ()=> {
    const SSE_ENDPOINT = '/employees/subscribeOrders';
    const connectBtn = document.getElementById("connect-btn");
    const disconnectBtn = document.getElementById("disconnect-btn");
    const orderDisplayContainer = document.getElementById("order-display");
    const orderContainer = document.getElementById("orders-container");
    const fulfilledContainer = document.getElementById("fulfilled-container");
    const recipeDialog = document.getElementById("recipe-dialog");
    const recipeContainer = document.getElementById("recipe-container");
    const closeRecipeBtn = document.getElementById("close-recipe-dialog");

    let eventSource;
    let orders = {};
    let fulfilled = {};
    let isClosed = true;

    connectBtn.addEventListener("click", manualConnect);
    disconnectBtn.addEventListener("click", manualDisconnect);
    closeRecipeBtn.addEventListener("click", () => {
        recipeDialog.close();
    })

    async function connect() {
        try {
            orders = await getCurrentOrders();
        } catch (e) {
            alert("Error fetching initial orders, please try again. Error message: " + e);
            return;
        }

        eventSource  = new EventSource(SSE_ENDPOINT);
        populateInitialUI();

        eventSource.onopen = () => {
            console.log("Notification stream open.")
            isClosed = false;
        };
        eventSource.onmessage = (event) => {
            console.log(event.data);
        };
        eventSource.onerror = (error) => {
            // if not manually closed, close stream and attempt to reconnect.
            if (!isClosed) {
                eventSource.close();
                console.error(error);
                console.log("Attempting reconnect in 3 seconds...");
                setTimeout(connect, 3000);
            } else {
                console.log("Auto reconnect disabled, stream was closed intentionally.")
            }
        };

        eventSource.addEventListener('new-order', (event) => {
            const newOrderMsg = JSON.parse(event.data);
            orders[newOrderMsg["orderID"]] = newOrderMsg;
            // Display the newOrderMessage to the employees
            appendOrderToUI(orders[newOrderMsg["orderID"]], orderContainer);
        });

        eventSource.addEventListener('order-in-progress', (event) => {
            //remove received order by id key, put into pendingOrders object with completedBy
            const fulfilledOrderMsg = JSON.parse(event.data);
            delete orders[fulfilledOrderMsg["orderID"]];
            fulfilled[fulfilledOrderMsg["orderID"]] = fulfilledOrderMsg;

            appendOrderToUI(fulfilled[fulfilledOrderMsg["orderID"]], fulfilledContainer);
            const removeEl = orderContainer.querySelector(`[data-order-id="${fulfilledOrderMsg["orderID"]}"]`)
            removeEl.remove();
        });

        eventSource.addEventListener('order-complete', (event) => {
            const completedOrderId = event.data;

            delete fulfilled[completedOrderId];
            const removeEl = fulfilledContainer.querySelector(`[data-order-id="${completedOrderId}"]`)
            removeEl.remove();
        });

        eventSource.addEventListener('cancel-in-progress', (event) => {
            const canceledOrderMsg = JSON.parse(event.data);
            delete fulfilled[canceledOrderMsg["orderID"]];
            orders[canceledOrderMsg["orderID"]] = canceledOrderMsg;

            const removeEl = fulfilledContainer.querySelector(`[data-order-id="${canceledOrderMsg["orderID"]}"]`)
            removeEl.remove();
            populateInitialUI()
        });
    }

    function manualConnect() {
        if (isClosed) {
            connect()
                .then(() => console.log('order notification stream connected.'));
        } else {
            alert("Notification stream is already active.");
        }
    }

    function manualDisconnect() {
        if (eventSource && eventSource.readyState !== EventSource.CLOSED) {
            eventSource.close();
            isClosed = true;
            console.log('Order notification stream closed.');
        } else {
            alert("Notification stream is already closed.");
        }
    }

    function populateInitialUI() {
        orderContainer.innerHTML = "";
        fulfilledContainer.innerHTML = "";
        for (let order in orders) {
            if (orders[order]["inProgress"]) {
                fulfilled[order] = orders[order];
            } else {
                appendOrderToUI(orders[order], orderContainer);
            }
        }
        if (Object.keys(fulfilled).length > 0) {
            for (let order in fulfilled) {
                if (orders[order]) {
                    delete orders[order];
                }
                appendOrderToUI(fulfilled[order], fulfilledContainer)
            }
        }
    }

    orderDisplayContainer.addEventListener("click", async (event) => {
        const target = event.target;
        const targetClasses = target.classList;
        if (targetClasses.contains("fulfill-btn")) {
            try {
                await setOrderInProgress(target.dataset.orderId);
            } catch (e) {
                console.error("Error during setOrderInProgress:", e);
            }
        } else if (targetClasses.contains("complete-btn")) {
            try {
                await setOrderComplete(target.dataset.orderId);
            } catch (e) {
                console.error("Error during setOrderComplete:", e);
            }
        } else if (targetClasses.contains("cancel-fulfillment-btn")) {
            try {
                await cancelOrderInProgress(target.dataset.orderId);
            } catch (e) {
                console.error("Error during cancelOrderInProgress:", e);
            }
        } else if (targetClasses.contains("get-menu-item-recipe-btn")) {
            try {
                const menuItemRecipe = await getMenuItemRecipe(target.dataset.itemId);
                buildRecipeDisplay(menuItemRecipe, recipeContainer);
                recipeDialog.showModal();
            } catch (e) {
                console.error("Error during getMenuItemRecipe:", e);
            }
        } else if (targetClasses.contains("get-custom-pizza-recipe-btn")) {
            try {
                const pizzaItemRecipe = await getCustomPizzaRecipe(target.dataset.itemId);
                buildRecipeDisplay(pizzaItemRecipe, recipeContainer);
                recipeDialog.showModal();
            } catch (e) {
                console.error("Error during getCustomPizzaRecipe:", e);
            }
        }
    });

});







