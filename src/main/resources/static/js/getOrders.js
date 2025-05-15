// todo :
//  -- : when new order comes in, add to end of Orders[] array (like a queue)
//  -- : orderId in DTO for setting isComplete to true..
//  -- : use REST controller to get all current orders on initial connect/reconnect, and update completed order ..

document.addEventListener('DOMContentLoaded', ()=> {
    const connectBtn = document.getElementById("connect-btn");
    const disconnectBtn = document.getElementById("disconnect-btn");
    const currentOrdersEndpoint = "/employees/getCurrentOrders";

    let eventSource;
    let orders = [];
    let isClosed = true;

    connectBtn.addEventListener("click", manualConnect);
    disconnectBtn.addEventListener("click", manualDisconnect);

    async function connect() {
        orders = await getCurrentOrders(currentOrdersEndpoint);

        eventSource  = new EventSource('/employees/subscribeOrders');

        eventSource.addEventListener('connected', (event) => {
            console.log('Server says:', event.data);
            isClosed = false;
        });

        eventSource.addEventListener('heartbeat', (event) => {
            console.log('Heartbeat:', event.data);
        });

        eventSource.addEventListener('new-order', (event) => {
            const newOrderMessage = JSON.parse(event.data);
            console.log('New Order Alert:', newOrderMessage);
            orders.push(newOrderMessage);
            // Display the notification to the employees (e.g., update a list, show an alert)
            //displayNewOrderNotification(newOrderMessage);
            console.log("orders after push: ", orders);
        });

        eventSource.onerror = (error) => {
            console.error('Error connecting to order notifications:', error);
            eventSource.close(); // Attempt to close and potentially reconnect
            if (!isClosed) {
                console.log("Attempting reconnect in 4 seconds...");
                setTimeout(connect, 4000);
            } else {
                console.log("Auto reconnect disabled, stream was closed intentionally.")
            }
        };
    }

    function manualConnect() {
        if (isClosed) {
            connect();
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








