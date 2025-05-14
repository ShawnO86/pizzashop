package com.pizzashop.controllers;

import com.pizzashop.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/employees")
public class OrderNotificationController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private final long HEARTBEAT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(60); // heartbeat every 60 seconds
    private final long SSE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5); // sse timeout 5 minutes

    @Autowired
    public OrderNotificationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/subscribeOrders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeOrders() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        // add emitter to list on opened connection
        this.emitters.add(emitter);

        // remove emitter from list on closed connection
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError((throwable) -> this.emitters.remove(emitter));

        // Send "connected" event on successful connection to client
        try {
            emitter.send(SseEmitter.event().name("connected").data("Successfully subscribed to receive order notifications."));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        heartbeatScheduler.scheduleAtFixedRate(sendHeartbeat(emitter),
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);

        return emitter;
    }

    private Runnable sendHeartbeat(SseEmitter emitter) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.remove(emitter);
                }
            }
        };
    }

    public void notifyNewOrder(OrderDTO order) {
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

}
