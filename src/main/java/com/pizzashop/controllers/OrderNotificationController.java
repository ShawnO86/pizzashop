package com.pizzashop.controllers;

import com.pizzashop.dao.CustomPizzaDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    private final MenuItemDAO menuItemDAO;
    private final CustomPizzaDAO customPizzaDAO;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private final long HEARTBEAT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30); // set heartbeat to every 30 seconds

    @Autowired
    public OrderNotificationController(ObjectMapper objectMapper, OrderDAO orderDAO, OrderService orderService,
                                       MenuItemDAO menuItemDAO, CustomPizzaDAO customPizzaDAO) {
        this.objectMapper = objectMapper;
        this.orderDAO = orderDAO;
        this.orderService = orderService;
        this.menuItemDAO = menuItemDAO;
        this.customPizzaDAO = customPizzaDAO;
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
        List<Order> currentOrders = orderDAO.findAllIncompleteJoinFetchUserDetails();
        for (Order currentOrder : currentOrders) {
            OrderDTO orderDTO = orderService.convertOrderToDTO(currentOrder, true);
            orders.put(orderDTO.getOrderID(), orderDTO);
        }

        return orders;
    }

    @GetMapping(value = "/showMenuItemRecipe", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String[]> showMenuItemRecipe(@RequestParam("menuItemId") int menuItemId) {
        Map<String, String[]> recipe = new HashMap<>();
        MenuItem menuItem = menuItemDAO.findByIdJoinFetchIngredients(menuItemId);
        List<MenuItemIngredient> menuItemIngredients = menuItem.getMenuItemIngredients();

        recipe.put("Name", new String[]{menuItem.getDishName()});
        recipe.put("Description", new String[]{menuItem.getDescription()});
        String[] ingredients = new String[menuItemIngredients.size()];

        for (int i = 0; i < ingredients.length; i++) {
            String ingredientAmtStr = menuItemIngredients.get(i).getQuantityUsed() + " " +
                    menuItemIngredients.get(i).getIngredient().getUnitOfMeasure() + " " +
                    menuItemIngredients.get(i).getIngredient().getIngredientName();
            ingredients[i] = ingredientAmtStr;
        }

        recipe.put("Ingredients", ingredients);
        return recipe;
    }

    @GetMapping(value = "/showCustomPizzaRecipe", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String[]> showCustomPizzaRecipe(@RequestParam("customPizzaId") int customPizzaId) {
        Map<String, String[]> recipe = new HashMap<>();
        CustomPizza customPizza = customPizzaDAO.findByIdJoinFetchIngredients(customPizzaId);
        List<CustomPizzaIngredient> customPizzaIngredients = customPizza.getCustomPizzaIngredients();

        String titleCaseSize = StringUtils.capitalize(customPizza.getSize().name().toLowerCase());
        String basePizzaName = titleCaseSize + " Cheese Pizza";
        MenuItem basePizza = menuItemDAO.findByNameJoinFetchIngredients(basePizzaName);
        List<MenuItemIngredient> menuItemIngredients = basePizza.getMenuItemIngredients();

        recipe.put("Name", new String[]{titleCaseSize + " " + customPizza.getName()});
        recipe.put("Description", new String[]{"Base pizza: " + basePizza.getDescription()});
        String[] basePizzaIngredients = new String[basePizza.getMenuItemIngredients().size()];
        String[] toppings = new String[customPizzaIngredients.size()];

        for (int i = 0; i < basePizzaIngredients.length; i++) {
            String ingredientAmtStr = menuItemIngredients.get(i).getQuantityUsed() + " " +
                    menuItemIngredients.get(i).getIngredient().getUnitOfMeasure() + " " +
                    menuItemIngredients.get(i).getIngredient().getIngredientName();
            basePizzaIngredients[i] = ingredientAmtStr;
        }
        recipe.put("Ingredients", basePizzaIngredients);

        for (int i = 0; i < toppings.length; i++) {
            String ingredientAmtStr = customPizzaIngredients.get(i).getQuantityUsed() + " " +
                    customPizzaIngredients.get(i).getIngredient().getUnitOfMeasure() + " " +
                    customPizzaIngredients.get(i).getIngredient().getIngredientName();
            toppings[i] = ingredientAmtStr;
        }
        recipe.put("Toppings", toppings);

        return recipe;
    }

    @PostMapping("/setInProgress")
    public ResponseEntity<String> setPendingOrder(@RequestParam("orderId") int orderId, @RequestParam("employeeName") String employeeName) {
        Order order = orderDAO.findByIdJoinFetchUserDetails(orderId);
        if (order == null) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } else if (order.getIn_progress()) {
            notifyOrderInProgress(orderService.convertOrderToDTO(order, true));
            return new ResponseEntity<>("Order already in progress", HttpStatus.CONFLICT);
        } else if (order.getIs_complete()) {
            notifyOrderComplete(orderId);
            return new ResponseEntity<>("Order already completed", HttpStatus.CONFLICT);
        }
        order.setFulfilled_by(employeeName);
        order.setIn_progress(true);

        orderDAO.update(order);
        notifyOrderInProgress(orderService.convertOrderToDTO(order, true));

        return new ResponseEntity<>("Order set inProgress", HttpStatus.OK);
    }

    @PostMapping("/setIsComplete")
    public ResponseEntity<String> setIsComplete(@RequestParam("orderId") int orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } else if (order.getIs_complete()) {
            notifyOrderComplete(orderId);
            return new ResponseEntity<>("Order already complete", HttpStatus.CONFLICT);
        }
        order.setIn_progress(false);
        order.setIs_complete(true);

        orderDAO.update(order);
        notifyOrderComplete(order.getId());

        return new ResponseEntity<>("Order set isComplete", HttpStatus.OK);
    }

    @PostMapping("/cancelInProgress")
    public ResponseEntity<String> cancelInProgress(@RequestParam("orderId") int orderId) {
        Order order = orderDAO.findByIdJoinFetchUserDetails(orderId);
        if (order == null) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } else if (order.getIs_complete()) {
            notifyOrderComplete(orderId);
            return new ResponseEntity<>("Order already completed", HttpStatus.CONFLICT);
        }
        order.setFulfilled_by(null);
        order.setIn_progress(false);

        orderDAO.update(order);
        notifyCancelInProgress(orderService.convertOrderToDTO(order, true));

        return new ResponseEntity<>("Order in progress reverted", HttpStatus.OK);
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

    private void notifyCancelInProgress(OrderDTO order) {
        for (SseEmitter emitter : this.emitters) {
            try {
                String orderDTO_JSON = objectMapper.writeValueAsString(order);
                emitter.send(SseEmitter.event().name("cancel-in-progress").data(orderDTO_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        }
    }

}