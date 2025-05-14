

// todo : create functions to handle events for new orders, connect button, disconnect button
//  -- : when new order comes in, add to end of Orders[] array (like a queue) or an Orders{} object with Id as the key, DTO as value?
//  -- : or have field in DTO for orderId? To be able to set isComplete to true..

const eventSource = new EventSource('/employees/subscribeOrders');

eventSource.onopen = () => {
    console.log('Connected to order notifications.');
};

eventSource.onmessage = (event) => {
    console.log('Received message:', event.data);
};

eventSource.addEventListener('connected', (event) => {
    console.log('Server says:', event.data);
});

eventSource.addEventListener('heartbeat', (event) => {
    console.log('Heartbeat data:', event.data);
});

eventSource.addEventListener('new-order', (event) => {
    const newOrderMessage = JSON.parse(event.data);
    console.log('New Order Alert:', newOrderMessage);
    // Display the notification to the employees (e.g., update a list, show an alert)
    //displayNewOrderNotification(newOrderMessage);
});

eventSource.onerror = (error) => {
    console.error('Error connecting to order notifications:', error);
    eventSource.close(); // Attempt to close and potentially reconnect
};


