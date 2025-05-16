// todo :
//  -- : orderId in DTO for setting isComplete to true ..
//  -- : use REST controller to get all current orders on initial connect/reconnect, and to update completed order ..

document.addEventListener('DOMContentLoaded', ()=> {
    const connectBtn = document.getElementById("connect-btn");
    const disconnectBtn = document.getElementById("disconnect-btn");
    const orderContainer = document.getElementById("orders-container");


    const currentOrdersEndpoint = "/employees/getCurrentOrders";

    let eventSource;
    //when new order comes in, add to end of orders[] array (like a queue) after getCurrentOrders() finishes
    //will have to linear search for order in orders[] when done and tell server order it is complete by id.
    //populate page with fetched orders first, then add to display as they come in.

    let orders = {};
    let isClosed = true;

    let messageCount= 0;

    connectBtn.addEventListener("click", manualConnect);
    disconnectBtn.addEventListener("click", manualDisconnect);

    async function connect() {
        orders = await getCurrentOrders(currentOrdersEndpoint);
        eventSource  = new EventSource('/employees/subscribeOrders');

        populateInitialUI();
        //todo : will need to build template literals for orders to display

        eventSource.onopen = () => {
            console.log("Notification stream open.")
            isClosed = false;
        };
        eventSource.onmessage = (event) => {
            messageCount += 1;
            console.log(event.data + " count: " + messageCount);
        };

        eventSource.addEventListener('new-order', (event) => {
            const newOrderMessage = JSON.parse(event.data);
            console.log('New Order Alert:', newOrderMessage);

            orders[newOrderMessage["orderID"]] = newOrderMessage;
            // Display the newOrderMessage to the employees

            console.log("orders after update: ", orders);

        });

        eventSource.onerror = (error) => {
            console.error('Error connecting to order notifications:', error);
             // Attempt to close and potentially reconnect
            if (!isClosed) {
                eventSource.close();
                console.log("Attempting reconnect in 3 seconds...");
                setTimeout(connect, 3000);
            } else {
                console.log("Auto reconnect disabled, stream was closed intentionally.")
            }
        };
    }

    function manualConnect() {
        if (isClosed) {
            connect().then(() => console.log('getCurrentOrders() finished, order notification stream opened.'));
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
        for (let order in orders) {
            console.log("building this order's display:", order);
            appendOrderToUI(orders[order]);
        }
    }

    function appendOrderToUI(order) {
        const orderTemplate = document.createElement("div");

        // todo: format date,

        orderTemplate.innerHTML = `
                <div class="order" data-order-id="${order['orderID']}">
                    <details>
                        <summary class="order-summary">
                            <span>Order ID: ${order["orderID"]}</span>
                            <span>-- Time: ${new Date(order["orderDateTime"]).toString()}</span>
                        </summary>
                        
                        <p>blahhhh</p>
                    </details>
                    <hr>
                </div>
            `;

        orderContainer.appendChild(orderTemplate);
    }


});






async function getCurrentOrders(endpoint) {
    try {
        const response = await fetch(endpoint);
        const result = await response.json();
        console.log("current orders", result);
        return result;
    } catch (e) {
        console.error(e.message);
    }
}
