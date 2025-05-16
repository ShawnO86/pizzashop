package com.pizzashop.controllers;

import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.Order;
import com.pizzashop.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    private final long HEARTBEAT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30); // send heartbeat every 30 seconds

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

        // Send "connected" event on successful connection to client
        try {
            emitter.send(SseEmitter.event().data("Successfully connected to order notifications."));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        heartbeatScheduler.scheduleAtFixedRate(sendHeartbeat(emitter),
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);

        return emitter;
    }

    private Runnable sendHeartbeat(SseEmitter emitter) {
        return () -> {
            try {
                emitter.send(SseEmitter.event().data("ping, time ms -- " + System.currentTimeMillis()));
                System.out.println("Ping, time ms -- " + System.currentTimeMillis());
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        };
    }

    public void notifyNewOrder(OrderDTO order) {
        for (SseEmitter emitter : emitters) {
            try {
                String orderDTO_JSON = objectMapper.writeValueAsString(order);
                emitter.send(SseEmitter.event().name("new-order").data(orderDTO_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
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

}
