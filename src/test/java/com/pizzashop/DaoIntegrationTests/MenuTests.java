package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.config.SecConfig;
import com.pizzashop.controllers.RegistrationController;
import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.MenuItemService;
import com.pizzashop.services.OrderService;
import com.pizzashop.services.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
// needed because not within main com.pizzashop package
@ComponentScan(
        basePackages = "com.pizzashop",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                MenuItemDAO.class,
                IngredientDAO.class,
                OrderDAO.class,
                UserDAO.class,
                MenuItemService.class,
                OrderService.class,
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                UserRegistrationService.class,
                RegistrationController.class,
                SecConfig.class
        })
)
public class MenuTests {

    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private MenuItemDAO menuItemDAO;
    @Autowired
    private IngredientDAO ingredientDAO;
    @Autowired
    private UserDAO userDAO;

    private static final Map<String, Integer> sodaSizes = new HashMap<>();
    static {
        sodaSizes.put("Lg", 32);
        sodaSizes.put("Md", 24);
        sodaSizes.put("Sm", 16);
    }

    @BeforeEach
    void setUp() {
//        order = new Order(user, LocalDateTime.now());

        // Creating dishes and quantities are done once upon dish creation outside of testing.

        //create breadsticks dish and it's ingredients
        MenuItemDTO breadSticks = new MenuItemDTO("Bread sticks", "sticks of bread", MenuCategoryEnum.APP);
        Ingredient dough = new Ingredient("Dough", 50, 100);
        ingredientDAO.save(dough);
        // set ingredient quantities and save menuItem
        Map<Integer, Integer> breadSticksIngredientsQuantities = new HashMap<>();
        breadSticksIngredientsQuantities.put(dough.getId(), 1);
        breadSticks.setIngredientIdAmounts(breadSticksIngredientsQuantities);

        menuItemService.saveMenuItem(breadSticks);

        //create spaghetti dish and it's ingredients
        MenuItemDTO spaghetti = new MenuItemDTO("Spaghetti Bolognese", "Pasta with a meat and tomato sauce", MenuCategoryEnum.PASTA);
        Ingredient tomatoSauce = new Ingredient("Tomato sauce", 2000, 50);
        ingredientDAO.save(tomatoSauce);
        Ingredient pasta = new Ingredient("Pasta", 100, 50);
        ingredientDAO.save(pasta);
        Ingredient groundBeef = new Ingredient("Ground Beef", 50,300);
        ingredientDAO.save(groundBeef);
        // set ingredient quantities
        Map<Integer, Integer> spaghettiIngredientsQuantities = new HashMap<>();
        spaghettiIngredientsQuantities.put(tomatoSauce.getId(), 2);
        spaghettiIngredientsQuantities.put(pasta.getId(), 1);
        spaghettiIngredientsQuantities.put(groundBeef.getId(), 1);
        spaghetti.setIngredientIdAmounts(spaghettiIngredientsQuantities);

        menuItemService.saveMenuItem(spaghetti);

        //create soda and it's ingredients
        MenuItemDTO lgSoda = new MenuItemDTO("Lg Soda", "Large Soda", MenuCategoryEnum.DRINK);
        Ingredient soda = new Ingredient("Soda", 1280, 3);
        ingredientDAO.save(soda);
        // set ingredient quantities
        Map<Integer, Integer> sodaIngredientsQuantities = new HashMap<>();
        sodaIngredientsQuantities.put(soda.getId(), sodaSizes.get("Lg"));
        lgSoda.setIngredientIdAmounts(sodaIngredientsQuantities);

        menuItemService.saveMenuItem(lgSoda);
    }

    @Test
    public void openMenuTest() {
        List<MenuItem> menuItems = menuItemDAO.findAll();

        // look at menu items and mapped ingredients
        for (MenuItem menuItem : menuItems) {
            System.out.println("Menu Item Name: " + menuItem.getDishName());
            System.out.println("Category: " + menuItem.getMenuCategory() + "\nprice: " + menuItem.getPriceCents() + "\nIngredients:");
            for (MenuItemIngredient menuItemIngredient : menuItem.getMenuItemIngredients()) {
                Ingredient ingredient = menuItemIngredient.getIngredient();
                System.out.println(ingredient.getIngredientName() + " -- quantity: " + menuItemIngredient.getQuantityUsed());
            }
        }

        assertEquals(3, menuItems.size());
    }

    @Test
    public void createOrderTest() {
        User user = new User();
        user.setUsername("TestName");
        user.setPassword("TestPassword");
        user.setActive(true);
        UserDetail userDetail = new UserDetail();
        userDetail.setFirstName("TestFirstName");
        userDetail.setLastName("TestLastName");
        userDetail.setEmail("TestEmail");
        userDetail.setPhone("TestPhone");
        userDetail.setAddress("TestAddress");
        userDetail.setCity("TestCity");
        userDetail.setState("TestState");
        user.setUserDetail(userDetail);

        Role role = new Role(RoleEnum.ROLE_CUSTOMER);
        user.addRole(role);

        userDAO.save(user);

        List<MenuItem> menuItems = menuItemDAO.findAll();

        OrderDTO orderDTO  = new OrderDTO();
        for (MenuItem menuItem : menuItems) {
            orderDTO.addMenuItem(menuItem);
        }

        orderService.addOrderToDB(orderDTO, user.getUsername());

        Order checkOrder = orderDAO.findOrderById(1);

        List<Ingredient> ingredients = ingredientDAO.findAll();

        System.out.println(ingredients);

        //1122 cost of added menuItems
        assertEquals(1938, checkOrder.getFinal_price_cents());
    }
}
