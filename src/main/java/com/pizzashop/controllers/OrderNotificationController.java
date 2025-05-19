package com.pizzashop.controllers;

import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.Order;
import com.pizzashop.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/employees")
public class OrderNotificationController {

    private final ObjectMapper objectMapper;
    private final OrderDAO orderDAO;
    private final OrderService orderService;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private final long HEARTBEAT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30); // set heartbeat to every 30 seconds

    @Autowired
    public OrderNotificationController(ObjectMapper objectMapper, OrderDAO orderDAO, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderDAO = orderDAO;
        this.orderService = orderService;
    }

    @GetMapping(value = "/subscribeOrders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeOrders() {
        // sets sse timeout to infinite (-1L), heartbeat can detect closed connection to trigger an emitter removal
        // manually closing will also trigger a removal
        SseEmitter emitter = new SseEmitter(-1L);
        // add emitter to list on opened connection
        this.emitters.add(emitter);
        // remove emitter from list on closed connection, error
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError((throwable) -> this.emitters.remove(emitter));

        // Send initial event on successful connection
        try {
            emitter.send(SseEmitter.event().data("Successfully connected to order notifications."));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // schedule heartbeat to prevent any network timeouts and to trigger emitter removal if no connection and no new order events.
        heartbeatScheduler.scheduleAtFixedRate(sendHeartbeat(emitter),
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);

        return emitter;
    }

    @GetMapping(value = "/getCurrentOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, OrderDTO> getCurrentOrders() {
        Map<Integer, OrderDTO> orders = new HashMap<>();
        List<Order> currentOrders = orderDAO.findAllIncomplete();
        for (Order currentOrder : currentOrders) {
            OrderDTO orderDTO = orderService.convertOrderToDTO(currentOrder);
            orders.put(orderDTO.getOrderID(), orderDTO);
        }

        return orders;
    }

    @PostMapping("/setInProgress")
    public ResponseEntity<String> setPendingOrder(@RequestParam("orderId") int orderId, @RequestParam("employeeName") String employeeName) {
        //System.out.println("request received, orderId: " + orderId + ", employeeName: " + employeeName);
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            System.out.println("Order not found");
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } else if (order.getIn_progress()) {
            System.out.println("Order already in progress");
            notifyOrderInProgress(orderService.convertOrderToDTO(order));
            return new ResponseEntity<>("Order already in progress", HttpStatus.CONFLICT);
        } else if (order.getIs_complete()) {
            System.out.println("Order already completed");
            notifyOrderComplete(orderId);
            return new ResponseEntity<>("Order already completed", HttpStatus.CONFLICT);
        }
        order.setFulfilled_by(employeeName);
        order.setIn_progress(true);

        orderDAO.update(order);
        notifyOrderInProgress(orderService.convertOrderToDTO(order));

        return new ResponseEntity<>("Order set inProgress", HttpStatus.OK);
    }

    @PostMapping("/setIsComplete")
    public ResponseEntity<String> setIsComplete(@RequestParam("orderId") int orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } else if (order.getIs_complete()) {
            System.out.println("Order already complete");
            notifyOrderComplete(orderId);
            return new ResponseEntity<>("Order already in complete", HttpStatus.CONFLICT);
        }
        order.setIn_progress(false);
        order.setIs_complete(true);

        orderDAO.update(order);
        notifyOrderComplete(order.getId());

        return new ResponseEntity<>("Order set isComplete", HttpStatus.OK);
    }


    private Runnable sendHeartbeat(SseEmitter emitter) {
        return () -> {
            try {
                emitter.send(SseEmitter.event().data("ping, server time ms -- " + System.currentTimeMillis()));
            } catch (IOException e) {
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        };
    }

    public void notifyNewOrder(OrderDTO order) {
        //send new order to each emitter
        for (SseEmitter emitter : this.emitters) {
            try {
                String orderDTO_JSON = objectMapper.writeValueAsString(order);
                emitter.send(SseEmitter.event().name("new-order").data(orderDTO_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        }
    }

    private void notifyOrderInProgress(OrderDTO order) {
        //send order in progress to each emitter
        for (SseEmitter emitter : this.emitters) {
            try {
                String orderDTO_JSON = objectMapper.writeValueAsString(order);
                emitter.send(SseEmitter.event().name("order-in-progress").data(orderDTO_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        }
    }

    private void notifyOrderComplete(int orderID) {
        //send completed order id to each emitter
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event().name("order-complete").data(orderID));
            } catch (IOException e) {
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        }
    }

}