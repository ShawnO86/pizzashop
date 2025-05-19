package com.pizzashop.services;

import com.pizzashop.dao.*;

import com.pizzashop.dto.*;

import com.pizzashop.entities.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final MenuItemDAO menuItemDAO;
    private final CustomPizzaDAO customPizzaDAO;
    private final IngredientDAO ingredientDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    private final MenuItemService menuItemService;

    private List<String> availabilityErrors;
    private List<String> priceErrors;

    @Autowired
    public OrderServiceImpl(MenuItemDAO menuItemDAO, CustomPizzaDAO customPizzaDAO, IngredientDAO ingredientDAO,
                            OrderDAO orderDAO, UserDAO userDAO, MenuItemService menuItemService) {
        this.menuItemDAO = menuItemDAO;
        this.customPizzaDAO = customPizzaDAO;
        this.ingredientDAO = ingredientDAO;
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.menuItemService = menuItemService;
    }

    //  build ingredientIdAmounts Map <IngredientID, newAmt> by using already fetched ingredients/menuItems and subtracting recipe * item qty
    //  reduce ingredient stock used in menuItems/customPizzas in submitOrder()
    //  update each Ingredient separately and menuItems amount available
    @Override
    @Transactional
    public Order submitOrder(OrderDTO order, String username) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Order orderEntity = new Order(user, now);
        orderEntity.setIs_complete(false);
        orderEntity.setFinal_price_cents(order.getTotalPrice());
        List<OrderMenuItem> orderMenuItems = new ArrayList<>();
        // <ingredientId, newAmtAvailable>
        Map<Integer, Integer> ingredientIdAmounts = new HashMap<>();

        if (order.getMenuItemList() != null && !order.getMenuItemList().isEmpty()) {
            Map<Integer, Integer> menuItemIdQty = new HashMap<>();
            for (OrderMenuItemDTO orderMenuItemDTO : order.getMenuItemList()) {
                menuItemIdQty.put(orderMenuItemDTO.getMenuItemID(), orderMenuItemDTO.getMenuItemAmount());
            }
            List<Integer> menuItemIDs = new ArrayList<>(menuItemIdQty.keySet());
            List<MenuItem> availableMenuItems = menuItemDAO.findAllAvailableIn(menuItemIDs);
            for (MenuItem menuItem : availableMenuItems) {
                OrderMenuItem orderMenuItem = new OrderMenuItem(orderEntity);
                orderMenuItem.setItemQuantity(menuItemIdQty.get(menuItem.getId()));
                orderMenuItem.setMenuItem(menuItem);

                orderMenuItems.add(orderMenuItem);

                // build ingredientIdAmounts Map for menuItems to reduce stock
                for (MenuItemIngredient menuItemIngredient : menuItem.getMenuItemIngredients()) {
                    Ingredient ingredient = menuItemIngredient.getIngredient();
                    // if no id in map, set id and current stock amt, then reduce that stock value later
                    if (!ingredientIdAmounts.containsKey(ingredient.getId())) {
                        ingredientIdAmounts.put(ingredient.getId(), ingredient.getCurrentStock());
                    }
                    int orderQuantity = menuItemIdQty.get(menuItemIngredient.getMenuItem().getId());
                    int newStock = ingredientIdAmounts.get(ingredient.getId()) - (menuItemIngredient.getQuantityUsed() * orderQuantity);
                    ingredientIdAmounts.put(ingredient.getId(), newStock);
                }
            }
        }

        if (order.getCustomPizzaList() != null && !order.getCustomPizzaList().isEmpty()) {
            for (CustomPizzaDTO customPizzaDTO : order.getCustomPizzaList()) {
                CustomPizza customPizza = new CustomPizza(
                        customPizzaDTO.getPizzaName(), customPizzaDTO.getPricePerPizza(), customPizzaDTO.getPizzaSize().getSize());
                List<CustomPizzaIngredient> customPizzaIngredients = new ArrayList<>();

                // set ingredients used in base pizza(s) for ingredientIdAmounts map
                MenuItem basePizza = menuItemDAO.findByName(customPizzaDTO.getPizzaSize().getSize().getPizzaName());
                for (MenuItemIngredient menuItemIngredient : basePizza.getMenuItemIngredients()) {
                    Ingredient ingredient = menuItemIngredient.getIngredient();
                    if (!ingredientIdAmounts.containsKey(ingredient.getId())) {
                        ingredientIdAmounts.put(ingredient.getId(), ingredient.getCurrentStock());
                    }
                    int newStock = ingredientIdAmounts.get(ingredient.getId()) - (menuItemIngredient.getQuantityUsed() * customPizzaDTO.getQuantity());
                    ingredientIdAmounts.put(ingredient.getId(), newStock);
                }

                // set ingredients used for toppings
                List<Integer> ingredientIDs = new ArrayList<>();
                List<Ingredient> ingredients;
                if (customPizzaDTO.getToppings() != null && !customPizzaDTO.getToppings().isEmpty()) {
                    for (ToppingDTO toppingDTO : customPizzaDTO.getToppings()) {
                        ingredientIDs.add(toppingDTO.getId());
                    }

                    ingredients = ingredientDAO.findAllIn(ingredientIDs);
                    for (Ingredient ingredient : ingredients) {
                        CustomPizzaIngredient customPizzaIngredient = new CustomPizzaIngredient(
                                customPizza, ingredient, false
                        );

                        customPizzaIngredient.setQuantityUsedBySize(customPizza.getSize());
                        customPizzaIngredients.add(customPizzaIngredient);

                        if (!ingredientIdAmounts.containsKey(ingredient.getId())) {
                            ingredientIdAmounts.put(ingredient.getId(), ingredient.getCurrentStock());
                        }

                        int newStock = ingredientIdAmounts.get(ingredient.getId()) - (customPizzaIngredient.getQuantityUsed() * customPizzaDTO.getQuantity());
                        ingredientIdAmounts.put(ingredient.getId(), newStock);
                    }
                }

                // set ingredients used for extra toppings
                List<Integer> extraIngredientIDs = new ArrayList<>();
                List<Ingredient> extraIngredients;
                if (customPizzaDTO.getExtraToppings() != null && !customPizzaDTO.getExtraToppings().isEmpty()) {
                    for (ToppingDTO toppingDTO : customPizzaDTO.getExtraToppings()) {
                        extraIngredientIDs.add(toppingDTO.getId());
                    }

                    extraIngredients = ingredientDAO.findAllIn(extraIngredientIDs);
                    for (Ingredient ingredient : extraIngredients) {
                        CustomPizzaIngredient customPizzaIngredient = new CustomPizzaIngredient(
                                customPizza, ingredient, true
                        );

                        customPizzaIngredient.setQuantityUsedBySize(customPizza.getSize());
                        customPizzaIngredients.add(customPizzaIngredient);

                        if (!ingredientIdAmounts.containsKey(ingredient.getId())) {
                            ingredientIdAmounts.put(ingredient.getId(), ingredient.getCurrentStock());
                        }

                        int newStock = ingredientIdAmounts.get(ingredient.getId()) - (customPizzaIngredient.getQuantityUsed() * customPizzaDTO.getQuantity());
                        ingredientIdAmounts.put(ingredient.getId(), newStock);
                    }
                }

                customPizza.setCustomPizzaIngredients(customPizzaIngredients);
                customPizzaDAO.save(customPizza);
                
                OrderMenuItem orderMenuItem = new OrderMenuItem(orderEntity);
                orderMenuItem.setItemQuantity(customPizzaDTO.getQuantity());
                orderMenuItem.setCustomPizza(customPizza);
                orderMenuItems.add(orderMenuItem);
            }
        }

        orderEntity.setOrderMenuItems(orderMenuItems);
        reduceInventory(ingredientIdAmounts);

        return orderDAO.save(orderEntity);
    }

    // todo: implement logger for this method
    @Transactional
    protected void reduceInventory(Map<Integer, Integer> ingredientIdAmounts) {
        List<Integer> ingredientIDs = new ArrayList<>(ingredientIdAmounts.keySet());
        List<Ingredient> ingredients = ingredientDAO.findAllInJoinFetchMenuItemIngredients(ingredientIDs);
        Set<MenuItem> affectedMenuItems = new HashSet<>();
        Map<Integer, List<MenuItemIngredient>> menuItemIngredientsByMenuItemId = new HashMap<>();
        System.out.println("*** Ingredient ID Amounts: " + ingredientIdAmounts);
        System.out.println("*** Affected Ingredients: " + ingredients);
        for (Ingredient ingredient : ingredients) {
            // set ingredient stock to reduced amount found in ingredientIdAmounts
            System.out.println("reducing stock for: " + ingredient);
            ingredient.setCurrentStock(ingredientIdAmounts.get(ingredient.getId()));
            List<MenuItemIngredient> menuItemIngredients = ingredient.getMenuItemIngredients();
            for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
                affectedMenuItems.add(menuItemIngredient.getMenuItem());
                // maps MenuItem id to new ArrayList if absent, adds MenuItemIngredients associated with it to list.
                menuItemIngredientsByMenuItemId.computeIfAbsent(menuItemIngredient.getMenuItem().getId(), k -> new ArrayList<>()).add(menuItemIngredient);
            }

            ingredientDAO.update(ingredient);
        }

        if (!affectedMenuItems.isEmpty()) {
            for (MenuItem menuItem : affectedMenuItems) {
                System.out.println("updating amount available for: " + menuItem);
                int newAmtAvailable = menuItemService.updateMenuItemAmountAvailableWithIngredients(menuItem, menuItemIngredientsByMenuItemId.get(menuItem.getId()));
                if (newAmtAvailable != menuItem.getAmountAvailable()) {
                    menuItem.setAmountAvailable(newAmtAvailable);
                    menuItemDAO.update(menuItem);
                }
            }
        }
    }

    @Override
    public Map<String, List<String>> submitOrderForValidation(OrderDTO order) {
        Map<String, List<String>> errors = new HashMap<>();
        availabilityErrors = new ArrayList<>();
        priceErrors = new ArrayList<>();

        if (order.getMenuItemList() != null && !order.getMenuItemList().isEmpty()) {
            validateMenuItems(order);
        }
        if (order.getCustomPizzaList() != null && !order.getCustomPizzaList().isEmpty()) {
            validatePizzaIngredients(order.getCustomPizzaList());
        }

        validateOrderPrice(order);

        errors.put("availabilityErrors", availabilityErrors);
        errors.put("priceErrors", priceErrors);

        return errors;
    }

    @Override
    public OrderDTO convertOrderToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderID(order.getId());
        orderDTO.setTotalPrice(order.getFinal_price_cents());
        orderDTO.setOrderDateTime(order.getOrder_date());

        if (order.getIn_progress()) {
            orderDTO.setInProgress(true);
        }
        if (order.getFulfilled_by() != null) {
            orderDTO.setEmployeeName(order.getFulfilled_by());
        }

        for (OrderMenuItem orderMenuItem : order.getOrderMenuItems()) {
            if (orderMenuItem.getMenuItem() != null) {
                MenuItem menuItem = orderMenuItem.getMenuItem();
                OrderMenuItemDTO orderMenuItemDTO = new OrderMenuItemDTO(
                        menuItem.getId(), menuItem.getDishName(), orderMenuItem.getItemQuantity(),
                        menuItem.getAmountAvailable(), menuItem.getPriceCents()
                );
                orderDTO.addMenuItem(orderMenuItemDTO);
            } else if (orderMenuItem.getCustomPizza() != null) {
                CustomPizza customPizza = orderMenuItem.getCustomPizza();
                CustomPizzaDTO customPizzaDTO = new CustomPizzaDTO(customPizza.getName(), orderMenuItem.getItemQuantity());
                customPizzaDTO.setCustomPizzaID(customPizza.getId());
                SizeDTO sizeDTO = new SizeDTO(customPizza.getSize(),
                        menuItemDAO.findByName(customPizza.getSize().getPizzaName()).getPriceCents());
                List<ToppingDTO> toppingDTOList = new ArrayList<>();
                List<ToppingDTO> extraToppingDTOList = new ArrayList<>();
                for (CustomPizzaIngredient customPizzaIngredient : customPizza.getCustomPizzaIngredients()) {
                    Ingredient ingredient = customPizzaIngredient.getIngredient();
                    ToppingDTO toppingDTO = new ToppingDTO(ingredient.getIngredientName(), ingredient.getId());
                    if (customPizzaIngredient.getIsExtra()) {
                        extraToppingDTOList.add(toppingDTO);
                    } else {
                        toppingDTOList.add(toppingDTO);
                    }
                }
                if (!toppingDTOList.isEmpty()) {
                    customPizzaDTO.setToppings(toppingDTOList);
                }
                if (!extraToppingDTOList.isEmpty()) {
                    customPizzaDTO.setExtraToppings(extraToppingDTOList);
                }
                customPizzaDTO.setPizzaSize(sizeDTO);
                customPizzaDTO.setPricePerPizza(customPizza.getPriceCents());
                customPizzaDTO.setTotalPizzaPrice(customPizza.getPriceCents() * orderMenuItem.getItemQuantity());

                orderDTO.addCustomPizza(customPizzaDTO);
            }

        }

        return orderDTO;
    }

    private void validateMenuItems(OrderDTO order) {
        // copy orderMenuItemDTOs to ArrayList Impl for making modifications to it
        List<OrderMenuItemDTO> orderMenuItemDTOs = new ArrayList<>(order.getMenuItemList());
        // gets list of menuItemIds to use IN clause for single query and compare with database records
        List<Integer> menuItemIds = new ArrayList<>();
        for (OrderMenuItemDTO orderMenuItemDTO : orderMenuItemDTOs) {
            menuItemIds.add(orderMenuItemDTO.getMenuItemID());
        }
        List<MenuItem> availableMenuItems = menuItemDAO.findAllAvailableIn(menuItemIds);
        Map<Integer, MenuItem> menuItemIdMap = new HashMap<>();
        for (MenuItem menuItem : availableMenuItems) {
            menuItemIdMap.put(menuItem.getId(), menuItem);
        }
        // uses a map for quick lookup time of found available menu items from database
        // adds error string and removes menuItem DTO element from end of sorted list using reverse loop
        // if no availability errors, checks for correct price and sets it
        for (int i = orderMenuItemDTOs.size() - 1; i >= 0; i--) {
            OrderMenuItemDTO currentMenuItemDTO = orderMenuItemDTOs.get(i);
            MenuItem currentMenuItem = menuItemIdMap.get(currentMenuItemDTO.getMenuItemID());

            if (currentMenuItem == null) {
                availabilityErrors.add(currentMenuItemDTO.getMenuItemName() + " is not available at this time.");
                orderMenuItemDTOs.remove(i);
            } else if (currentMenuItem.getAmountAvailable() < currentMenuItemDTO.getMenuItemAmount()) {
                availabilityErrors.add("Cannot add " + currentMenuItemDTO.getMenuItemAmount() + " " + currentMenuItemDTO.getMenuItemName() +
                        ", only " + currentMenuItem.getAmountAvailable() + " currently available. Your cart is updated to reflect available quantity.");
                currentMenuItemDTO.setMenuItemAmount(currentMenuItem.getAmountAvailable());
            } else if (currentMenuItem.getPriceCents() != currentMenuItemDTO.getPricePerItem()) {
                priceErrors.add(currentMenuItem.getDishName() + " does not match our price. Your cart is updated to reflect actual price.");
                currentMenuItemDTO.setPricePerItem(currentMenuItem.getPriceCents());
            }
        }
        order.setMenuItemList(orderMenuItemDTOs);
    }

    private void validatePizzaIngredients(List<CustomPizzaDTO> customPizzaDTOs) {
        //counts ingredients used, adds to hashmap with id as key, count as value
        //used for checking amounts with db.
        Map<Integer, Integer> ingredientsCount = new HashMap<>();

        List<ToppingDTO> toppings;
        List<ToppingDTO> extraToppings;
        PizzaSizeEnum size;

        for (CustomPizzaDTO customPizzaDTO : customPizzaDTOs) {
            toppings = customPizzaDTO.getToppings();
            extraToppings = customPizzaDTO.getExtraToppings();
            size = customPizzaDTO.getPizzaSize().getSize();
            if (toppings != null && !toppings.isEmpty()) {
                for (ToppingDTO topping : toppings) {
                    ingredientsCount.put(topping.getId(), size.getIngredientAmount() * customPizzaDTO.getQuantity());
                }
            }
            if (extraToppings != null && !extraToppings.isEmpty()) {
                for (ToppingDTO topping : extraToppings) {
                    ingredientsCount.put(topping.getId(), size.getExtraIngredientAmount() * customPizzaDTO.getQuantity());
                }
            }
        }

        checkIngredientsWithDB(ingredientsCount);
    }

    private void checkIngredientsWithDB(Map<Integer, Integer> ingredientsCount) {
        List<Integer> ingredientIds = new ArrayList<>(ingredientsCount.keySet());
        List<Ingredient> toppingIngredients = ingredientDAO.findAllIn(ingredientIds);
        //checks for smaller pizza available
        for (Ingredient ingredient : toppingIngredients) {
            if (ingredient.getCurrentStock() < ingredientsCount.get(ingredient.getId())) {
                List<String> pizzasAvailable = new ArrayList<>();
                String errorMessage = "Sorry, not enough " + ingredient.getIngredientName() + " in stock for this order";

                for (int i = 0; i < PizzaSizeEnum.values().length; i++) {
                    PizzaSizeEnum size = PizzaSizeEnum.values()[i];
                    int pizzaAmt = ingredient.getCurrentStock() / size.getIngredientAmount();
                    if (pizzaAmt > 0) {
                        pizzasAvailable.add(pizzaAmt + " " + size.name().toLowerCase());
                    }
                }

                if (!pizzasAvailable.isEmpty()) {
                    if (pizzasAvailable.size() > 1) {
                        pizzasAvailable.set(pizzasAvailable.size() - 1, "or " + pizzasAvailable.getLast());
                    }
                    errorMessage += ", we can make: " + String.join(", ", pizzasAvailable) + " pizza(s) with " + ingredient.getIngredientName() + ".";
                } else {
                    errorMessage += ", please choose another topping.";
                }

                availabilityErrors.add(errorMessage);
            }
        }
    }

    private void validateOrderPrice(OrderDTO order) {
        int pizzasPrice = 0;
        int menuPrice = 0;

        if (order.getMenuItemList() != null && !order.getMenuItemList().isEmpty()) {
            menuPrice = getMenuItemsPrice(order.getMenuItemList());
        }
        if (order.getCustomPizzaList() != null && !order.getCustomPizzaList().isEmpty()) {
            pizzasPrice = validatePizzaDTOPrices(order.getCustomPizzaList());
        }

        int orderPrice = menuPrice + pizzasPrice;
        if (order.getTotalPrice() != orderPrice) {
            order.setTotalPrice(orderPrice);
            priceErrors.add("Cart price does not match our calculated price. Your cart is updated to reflect actual price.");
        }
    }

    private int getMenuItemsPrice(List<OrderMenuItemDTO> orderMenuItemDTOs) {
        int price = 0;

        for (OrderMenuItemDTO orderMenuItemDTO : orderMenuItemDTOs) {
            price += orderMenuItemDTO.getMenuItemAmount() * orderMenuItemDTO.getPricePerItem();
        }

        return price;
    }

    private int validatePizzaDTOPrices(List<CustomPizzaDTO> customPizzaDTOs) {
        int totalPrice = 0;
        for (CustomPizzaDTO customPizzaDTO : customPizzaDTOs) {
            int pizzaPrice = 0;
            List<ToppingDTO> toppings = customPizzaDTO.getToppings();
            List<Integer> toppingIds = new ArrayList<>();
            if (toppings != null && !toppings.isEmpty()) {
                for (ToppingDTO topping : toppings) {
                    toppingIds.add(topping.getId());
                }
            }

            List<ToppingDTO> extraToppings = customPizzaDTO.getExtraToppings();
            List<Integer> extraToppingIds = new ArrayList<>();
            if (extraToppings != null && !extraToppings.isEmpty()) {
                for (ToppingDTO topping : extraToppings) {
                    extraToppingIds.add(topping.getId());
                }
            }

            List<Ingredient> toppingIngredients = ingredientDAO.findAllIn(toppingIds);
            List<Ingredient> extraIngredients = ingredientDAO.findAllIn(extraToppingIds);
            PizzaSizeEnum size = customPizzaDTO.getPizzaSize().getSize();
            String sizeTitleCase = StringUtils.capitalize(size.name().toLowerCase());
            int toppingAmount = size.getIngredientAmount();
            int extraToppingAmount = size.getExtraIngredientAmount();

            MenuItem basePizza = menuItemDAO.findByName(sizeTitleCase + " Cheese Pizza");
            if (basePizza == null) {
                availabilityErrors.add("Sorry, " + size.name().toLowerCase() + " pizza is not available at this time.");
                return 0;
            } else if (basePizza.getAmountAvailable() < customPizzaDTO.getQuantity()) {
                availabilityErrors.add("Sorry, not enough stock for " + customPizzaDTO.getQuantity() + " pizza(s), cart is updated to reflect amount available.");
                customPizzaDTO.setQuantity(basePizza.getAmountAvailable());
                return 0;
            }

            pizzaPrice = basePizza.getPriceCents();

            for (Ingredient ingredient : toppingIngredients) {
                pizzaPrice += ingredient.getCentsPricePer() * toppingAmount;
            }

            for (Ingredient ingredient : extraIngredients) {
                pizzaPrice += ingredient.getCentsPricePer() * extraToppingAmount;
            }

            if (pizzaPrice != customPizzaDTO.getPricePerPizza()) {
                customPizzaDTO.setPricePerPizza(pizzaPrice);
                priceErrors.add(customPizzaDTO.getPizzaName() + "'s price does not match our price for this order. It is updated to reflect actual price.");
            }
            if (pizzaPrice * customPizzaDTO.getQuantity() != customPizzaDTO.getTotalPizzaPrice()) {
                customPizzaDTO.setTotalPizzaPrice(pizzaPrice * customPizzaDTO.getQuantity());
                priceErrors.add(customPizzaDTO.getPizzaName() + "'s total price does not match our calculated price for this order. It is updated to reflect actual price.");
            }

            totalPrice += customPizzaDTO.getTotalPizzaPrice();
        }
        return totalPrice;
    }

}