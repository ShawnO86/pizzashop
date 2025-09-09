package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.config.AppRunner;
import com.pizzashop.config.SecConfig;
import com.pizzashop.controllers.ManagementController;
import com.pizzashop.controllers.OrderController;
import com.pizzashop.controllers.OrderNotificationController;
import com.pizzashop.controllers.RegistrationController;
import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.MenuItemIngredientDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.*;
import com.pizzashop.entities.*;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.services.MenuItemService;
import com.pizzashop.services.OrderService;
import com.pizzashop.services.UserRegistrationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
// needed because not within main com.pizzashop package
@ComponentScan(
        basePackages = "com.pizzashop",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                MenuItemIngredientDAO.class,
                MenuItemDAO.class,
                IngredientDAO.class,
                MenuItemService.class,
                OrderService.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                UserRegistrationService.class,
                RegistrationController.class,
                SecConfig.class,
                ManagementController.class,
                OrderController.class,
                OrderNotificationController.class,
                AppRunner.class
        })
)

public class MenuTests {

    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private MenuItemIngredientDAO menuItemIngredientDAO;
    @Autowired
    private MenuItemDAO menuItemDAO;
    @Autowired
    private IngredientDAO ingredientDAO;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EntityManager entityManager;


    private static final Map<String, Integer> sodaSizes = new HashMap<>();
    static {
        sodaSizes.put("Lg", 32);
        sodaSizes.put("Md", 24);
        sodaSizes.put("Sm", 16);
    }

    @BeforeEach
    void setUp() {
        // Creating dishes and quantities are done once upon dish creation outside of testing.

        //create breadsticks dish and it's ingredients

        Ingredient dough = new Ingredient("Dough", 500, 100, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(dough);
        MenuItemDTO breadSticks = new MenuItemDTO("Bread sticks", "sticks of bread", MenuCategoryEnum.APP);
        // set ingredient quantities and save menuItem
        List<int[]> breadSticksIngredientsQuantities = new ArrayList<>();
        breadSticksIngredientsQuantities.add(new int[]{dough.getId(), 1});
        breadSticks.setIngredientIdAmounts(breadSticksIngredientsQuantities);
        breadSticks.setIsAvailable(true);
        System.out.println("inserting test breadsticks...");
        menuItemService.saveMenuItem(breadSticks);

        //create soda and it's ingredients

        Ingredient soda = new Ingredient("Soda", 1280, 3, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(soda);
        MenuItemDTO lgSoda = new MenuItemDTO("Lg Soda", "Large Soda", MenuCategoryEnum.DRINK);
        // set ingredient quantities
        List<int[]> sodaIngredientsQuantities = new ArrayList<>();
        sodaIngredientsQuantities.add(new int[]{soda.getId(), sodaSizes.get("Lg")});
        lgSoda.setIngredientIdAmounts(sodaIngredientsQuantities);
        lgSoda.setIsAvailable(true);

        System.out.println("inserting test soda...");
        menuItemService.saveMenuItem(lgSoda);

        //create spaghetti dish and it's ingredients
        Ingredient tomatoSauce = new Ingredient("Tomato sauce", 2000, 10, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(tomatoSauce);
        Ingredient pasta = new Ingredient("Pasta", 100, 20, 3);
        ingredientDAO.save(pasta);
        Ingredient groundBeef = new Ingredient("Ground Beef", 100,30, 3);
        groundBeef.setIsPizzaTopping(true);
        ingredientDAO.save(groundBeef);
        MenuItemDTO spaghetti = new MenuItemDTO("Spaghetti Bolognese", "Pasta with a meat and tomato sauce", MenuCategoryEnum.PASTA);
        // set ingredient quantities
        List<int[]> spaghettiIngredientsQuantities = new ArrayList<>();
        spaghettiIngredientsQuantities.add(new int[]{tomatoSauce.getId(), 3});
        spaghettiIngredientsQuantities.add(new int[]{pasta.getId(), 3});
        spaghettiIngredientsQuantities.add(new int[]{groundBeef.getId(), 4});
        spaghetti.setIngredientIdAmounts(spaghettiIngredientsQuantities);
        spaghetti.setIsAvailable(true);

        System.out.println("inserting test spaghetti...");
        menuItemService.saveMenuItem(spaghetti);


        Ingredient cheese = new Ingredient("Cheese", 500, 10, 3);
        cheese.setIsPizzaTopping(true);
        ingredientDAO.save(cheese);
        MenuItemDTO smPizza = new MenuItemDTO("Small Cheese Pizza", "8in cheese pizza", MenuCategoryEnum.PIZZA);
        List<int[]> smPizzaIngredientsQuantities = new ArrayList<>();
        int smAmount = PizzaSizeEnum.SMALL.getIngredientAmount();
        smPizzaIngredientsQuantities.add(new int[]{cheese.getId(), smAmount});
        smPizzaIngredientsQuantities.add(new int[]{dough.getId(), smAmount});
        smPizzaIngredientsQuantities.add(new int[]{tomatoSauce.getId(), smAmount});
        smPizza.setIngredientIdAmounts(smPizzaIngredientsQuantities);
        smPizza.setIsAvailable(true);

        System.out.println("inserting test sm pizza...");
        menuItemService.saveMenuItem(smPizza);

        List<int[]> mdPizzaIngredientsQuantities = new ArrayList<>();
        int mdAmount = PizzaSizeEnum.MEDIUM.getIngredientAmount();
        MenuItemDTO mdPizza = new MenuItemDTO("Medium Cheese Pizza", "12in cheese pizza", MenuCategoryEnum.PIZZA);
        mdPizzaIngredientsQuantities.add(new int[]{cheese.getId(), mdAmount});
        mdPizzaIngredientsQuantities.add(new int[]{dough.getId(), mdAmount});
        mdPizzaIngredientsQuantities.add(new int[]{tomatoSauce.getId(), mdAmount});
        mdPizza.setIngredientIdAmounts(mdPizzaIngredientsQuantities);
        mdPizza.setIsAvailable(true);

        System.out.println("inserting test md pizza...");
        menuItemService.saveMenuItem(mdPizza);

        List<int[]> lgPizzaIngredientsQuantities = new ArrayList<>();
        int lgAmount = PizzaSizeEnum.LARGE.getIngredientAmount();
        MenuItemDTO lgPizza = new MenuItemDTO("Large Cheese Pizza", "16in cheese pizza", MenuCategoryEnum.PIZZA);
        lgPizzaIngredientsQuantities.add(new int[]{cheese.getId(), lgAmount});
        lgPizzaIngredientsQuantities.add(new int[]{dough.getId(), lgAmount});
        lgPizzaIngredientsQuantities.add(new int[]{tomatoSauce.getId(), lgAmount});
        lgPizza.setIngredientIdAmounts(lgPizzaIngredientsQuantities);
        lgPizza.setIsAvailable(true);

        System.out.println("inserting test lg pizza...");
        menuItemService.saveMenuItem(lgPizza);

        //create other pizza toppings

        Ingredient pepperoni = new Ingredient("Pepperoni", 100, 15, 3);
        pepperoni.setIsPizzaTopping(true);
        ingredientDAO.save(pepperoni);
    }

    @Test
    public void openMenuTest() {
        entityManager.flush();
        entityManager.clear();

        System.out.println("finding test menu...");

        System.out.println("*** find all menuItemIngredients *** ");
        List<MenuItemIngredient> ingredients = menuItemIngredientDAO.findAllJoinFetchMenuIngredients();
        for (MenuItemIngredient menuItemIngredient : ingredients) {
            System.out.println("menuItemIngredient: " + menuItemIngredient);
        }

        assertEquals(14, ingredients.size());
    }

    @Test
    public void testValidateOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();

        entityManager.flush();
        entityManager.clear();

        MenuItem spaghetti = menuItemDAO.findByName("Spaghetti Bolognese");
        OrderMenuItemDTO orderMenuItemDTO = new OrderMenuItemDTO(
                spaghetti.getId(), spaghetti.getDishName(), 1, spaghetti.getAmountAvailable(), spaghetti.getPriceCents()
        );

        MenuItem breadSticks = menuItemDAO.findByName("Bread sticks");
        OrderMenuItemDTO orderMenuItemDTO2 = new OrderMenuItemDTO(
                breadSticks.getId(), breadSticks.getDishName(), 1, breadSticks.getAmountAvailable(), breadSticks.getPriceCents()
        );

        MenuItem lgSoda = menuItemDAO.findByName("Lg Soda");
        OrderMenuItemDTO orderMenuItemDTO3 = new OrderMenuItemDTO(
                lgSoda.getId(), lgSoda.getDishName(), 4, lgSoda.getAmountAvailable(), lgSoda.getPriceCents()
        );

        Ingredient pepperoni = ingredientDAO.findByName("Pepperoni");
        ToppingDTO toppingDTO1 = new ToppingDTO(pepperoni.getIngredientName(), pepperoni.getId());
        Ingredient groundBeef = ingredientDAO.findByName("Ground Beef");
        ToppingDTO toppingDTO2 = new ToppingDTO(groundBeef.getIngredientName(), groundBeef.getId());

        MenuItem basePizza = menuItemDAO.findByName(PizzaSizeEnum.LARGE.getPizzaName());
        SizeDTO sizeDTO = new SizeDTO(PizzaSizeEnum.LARGE, basePizza.getPriceCents());

        CustomPizzaDTO customPizzaDTO = new CustomPizzaDTO(
                "Pizza 1", List.of(toppingDTO1, toppingDTO2), null, sizeDTO, 2
        );

        customPizzaDTO.setPricePerPizza(2250);
        customPizzaDTO.setTotalPizzaPrice(4500);

        orderDTO.setMenuItemList(List.of(orderMenuItemDTO, orderMenuItemDTO2, orderMenuItemDTO3));
        orderDTO.setCustomPizzaList(List.of(customPizzaDTO));

        orderDTO.setTotalPrice(5888);

        System.out.println("Order before ->" + orderDTO);

        Map<String, List<String>> orderResponse = orderService.submitOrderForValidation(orderDTO);
        System.out.println(orderResponse);

        System.out.println("Order after ->" + orderDTO);

        List<Ingredient> ingredientsBefore = new ArrayList<>(ingredientDAO.findAll());
        System.out.println("*** ingredients before submit *** ");
        for (Ingredient ingredient : ingredientsBefore) {
            System.out.println("ingredient: " + ingredient);
        }

        Order submissionResponse = orderService.submitOrder(orderDTO, buildTestUser().getUsername());

        System.out.println("*** submission response: " + submissionResponse);

        List<Ingredient> ingredientsAfter = ingredientDAO.findAll();
        System.out.println("*** ingredients after submit *** ");
        for (Ingredient ingredient : ingredientsAfter) {
            System.out.println("ingredient: " + ingredient);
        }

        assertEquals(3, orderDTO.getMenuItemList().size());
        assertEquals(1, orderDTO.getCustomPizzaList().size());
        assertEquals(2, orderResponse.size());
    }

    public User buildTestUser() {
        User user;
        UserDetail userDetail;
        Role role;
        user = new User();
        user.setUsername("TestName");
        user.setPassword("TestPassword");
        user.setActive(true);

        userDetail = new UserDetail();
        userDetail.setFirstName("TestFirstName");
        userDetail.setLastName("TestLastName");
        userDetail.setEmail("TestEmail");
        userDetail.setPhone("TestPhone");
        userDetail.setAddress("TestAddress");
        userDetail.setCity("TestCity");
        userDetail.setState("TestState");
        user.setUserDetail(userDetail);

        role = new Role(RoleEnum.ROLE_CUSTOMER);
        user.addRole(role);
        userDAO.save(user);
        return user;
    }

}
