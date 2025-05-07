package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.config.SecConfig;
import com.pizzashop.controllers.ManagementController;
import com.pizzashop.controllers.RegistrationController;
import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
// needed because not within main com.pizzashop package
@ComponentScan(
        basePackages = "com.pizzashop",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                MenuItemDAO.class,
                IngredientDAO.class,
                MenuItemService.class,
                OrderService.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                UserRegistrationService.class,
                RegistrationController.class,
                SecConfig.class,
                ManagementController.class
        })
)
public class MenuTests {

    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private MenuItemDAO menuItemDAO;
    @Autowired
    private IngredientDAO ingredientDAO;
    @Autowired
    private OrderService orderService;
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
        // Creating dishes and quantities are done once upon dish creation outside of testing.

        //create breadsticks dish and it's ingredients
        MenuItemDTO breadSticks = new MenuItemDTO("Bread sticks", "sticks of bread", MenuCategoryEnum.APP);
        Ingredient dough = new Ingredient("Dough", 500, 100, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(dough);
        // set ingredient quantities and save menuItem
        List<int[]> breadSticksIngredientsQuantities = new ArrayList<>();
        breadSticksIngredientsQuantities.add(new int[]{dough.getId(), 1});
        breadSticks.setIngredientIdAmounts(breadSticksIngredientsQuantities);
        breadSticks.setIsAvailable(true);
        System.out.println("inserting test breadsticks...");
        menuItemService.saveMenuItem(breadSticks);

        //create soda and it's ingredients
        MenuItemDTO lgSoda = new MenuItemDTO("Lg Soda", "Large Soda", MenuCategoryEnum.DRINK);
        Ingredient soda = new Ingredient("Soda", 1280, 3, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(soda);
        // set ingredient quantities
        List<int[]> sodaIngredientsQuantities = new ArrayList<>();
        sodaIngredientsQuantities.add(new int[]{soda.getId(), sodaSizes.get("Lg")});
        lgSoda.setIngredientIdAmounts(sodaIngredientsQuantities);
        lgSoda.setIsAvailable(true);

        System.out.println("inserting test soda...");
        menuItemService.saveMenuItem(lgSoda);

        //create spaghetti dish and it's ingredients
        MenuItemDTO spaghetti = new MenuItemDTO("Spaghetti Bolognese", "Pasta with a meat and tomato sauce", MenuCategoryEnum.PASTA);
        Ingredient tomatoSauce = new Ingredient("Tomato sauce", 2000, 10, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(tomatoSauce);
        Ingredient pasta = new Ingredient("Pasta", 100, 20, 3);
        ingredientDAO.save(pasta);
        Ingredient groundBeef = new Ingredient("Ground Beef", 100,30, 3);
        groundBeef.setIsPizzaTopping(true);
        ingredientDAO.save(groundBeef);
        // set ingredient quantities
        List<int[]> spaghettiIngredientsQuantities = new ArrayList<>();
        spaghettiIngredientsQuantities.add(new int[]{tomatoSauce.getId(), 3});
        spaghettiIngredientsQuantities.add(new int[]{pasta.getId(), 3});
        spaghettiIngredientsQuantities.add(new int[]{groundBeef.getId(), 4});
        spaghetti.setIngredientIdAmounts(spaghettiIngredientsQuantities);
        spaghetti.setIsAvailable(true);

        System.out.println("inserting test spaghetti...");
        menuItemService.saveMenuItem(spaghetti);

        MenuItemDTO smPizza = new MenuItemDTO("Small Cheese Pizza", "8in cheese pizza", MenuCategoryEnum.PIZZA);
        Ingredient cheese = new Ingredient("Cheese", 500, 10, 3);
        cheese.setIsPizzaTopping(true);
        ingredientDAO.save(cheese);
        List<int[]> smPizzaIngredientsQuantities = new ArrayList<>();
        int smAmount = PizzaSizeEnum.SMALL.getIngredientAmount();
        smPizzaIngredientsQuantities.add(new int[]{cheese.getId(), smAmount});
        smPizzaIngredientsQuantities.add(new int[]{dough.getId(), smAmount});
        smPizzaIngredientsQuantities.add(new int[]{tomatoSauce.getId(), smAmount});
        smPizza.setIngredientIdAmounts(smPizzaIngredientsQuantities);
        smPizza.setIsAvailable(true);

        System.out.println("inserting test sm pizza...");
        menuItemService.saveMenuItem(smPizza);

        MenuItemDTO mdPizza = new MenuItemDTO("Medium Cheese Pizza", "12in cheese pizza", MenuCategoryEnum.PIZZA);
        List<int[]> mdPizzaIngredientsQuantities = new ArrayList<>();
        int mdAmount = PizzaSizeEnum.MEDIUM.getIngredientAmount();
        mdPizzaIngredientsQuantities.add(new int[]{cheese.getId(), mdAmount});
        mdPizzaIngredientsQuantities.add(new int[]{dough.getId(), mdAmount});
        mdPizzaIngredientsQuantities.add(new int[]{tomatoSauce.getId(), mdAmount});
        mdPizza.setIngredientIdAmounts(mdPizzaIngredientsQuantities);
        mdPizza.setIsAvailable(true);

        System.out.println("inserting test md pizza...");
        menuItemService.saveMenuItem(mdPizza);

        MenuItemDTO lgPizza = new MenuItemDTO("Large Cheese Pizza", "16in cheese pizza", MenuCategoryEnum.PIZZA);
        List<int[]> lgPizzaIngredientsQuantities = new ArrayList<>();
        int lgAmount = PizzaSizeEnum.LARGE.getIngredientAmount();
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
        System.out.println("finding test menu...");
        List<MenuItem> menuItems = menuItemDAO.findAll();

        // look at menu items and mapped ingredients
        for (MenuItem menuItem : menuItems) {
            System.out.println(menuItem);
            for (MenuItemIngredient menuItemIngredient : menuItem.getMenuItemIngredients()) {
                Ingredient ingredient = menuItemIngredient.getIngredient();
                System.out.println(ingredient.getIngredientName() + " -- quantity: " + menuItemIngredient.getQuantityUsed());
            }
        }

        List<Ingredient> pizzaToppings = ingredientDAO.findAllPizzaToppings();
        for (Ingredient ingredient : pizzaToppings) {
            System.out.println(ingredient + " id: " + ingredient.getId());
        }

        assertEquals(6, menuItems.size());
        assertEquals(3, pizzaToppings.size());
    }

    @Test
    public void testValidateOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();

        //25000 is actual 45000 after 1 removed and price corrected from DB
        OrderMenuItemDTO orderMenuItemDTO = new OrderMenuItemDTO(
                menuItemDAO.findByName("Spaghetti Bolognese").getId(), "Spaghetti Bolognese", 1, 50, 500
        );
        //200
        OrderMenuItemDTO orderMenuItemDTO2 = new OrderMenuItemDTO(
                menuItemDAO.findByName("Bread sticks").getId(), "Bread sticks", 1, 50, 200
        );
        //768
        OrderMenuItemDTO orderMenuItemDTO3 = new OrderMenuItemDTO(
                menuItemDAO.findByName("Lg Soda").getId(), "Lg Soda", 4, 40, 192
        );
        //1000
        OrderMenuItemDTO orderMenuItemDTO4 = new OrderMenuItemDTO(
                55, "Lasagna", 1, 50, 1000
        );

        // 270 for pep
        ToppingDTO toppingDTO1 = new ToppingDTO("Pepperoni", ingredientDAO.findByName("Pepperoni").getId());
        ToppingDTO toppingDTO2 = new ToppingDTO("Ground Beef", ingredientDAO.findByName("Ground Beef").getId());
        // 1125 for large plain cheese
        SizeDTO sizeDTO = new SizeDTO(PizzaSizeEnum.LARGE, 1125);

        CustomPizzaDTO customPizzaDTO = new CustomPizzaDTO(
                "Pizza 1", List.of(toppingDTO1, toppingDTO2), null, sizeDTO, 2
        );

        customPizzaDTO.setPricePerPizza(1395);
        customPizzaDTO.setTotalPizzaPrice(2790);

        orderDTO.setMenuItemList(List.of(orderMenuItemDTO, orderMenuItemDTO2, orderMenuItemDTO3, orderMenuItemDTO4));
        orderDTO.setCustomPizzaList(List.of(customPizzaDTO));

        // 47363 should be price after validation
        orderDTO.setTotalPrice(28363);

        System.out.println("Order before ->" + orderDTO);

        Map<String, List<String>> orderResponse = orderService.submitOrderForValidation(orderDTO);
        System.out.println(orderResponse);

        System.out.println("Order after ->" + orderDTO);

        assertEquals(3, orderDTO.getMenuItemList().size());
        assertEquals(1, orderDTO.getCustomPizzaList().size());
        assertEquals(2, orderResponse.size());
    }

/*    @Test
    public void submitOrderForFulfillmentTest() {
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

        // todo : test submitting orders in OrderService.submitOrderForFulfillment()
    }*/

}
