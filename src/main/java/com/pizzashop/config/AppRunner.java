package com.pizzashop.config;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.dao.RoleDAO;
import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.MenuItemService;
import com.pizzashop.services.UserRegistrationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final MenuItemService menuItemService;
    private final UserRegistrationService userRegistrationService;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${INITIAL_ADMIN_PASS}") private String initialPass;

    @Autowired
    public AppRunner(MenuItemService menuItemService, UserRegistrationService userRegistrationService, UserDAO userDAO,
                     RoleDAO roleDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.menuItemService = menuItemService;
        this.userRegistrationService = userRegistrationService;
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Welcome to Pizza Shop!");
        this.initializeDatabase();
    }

    @Transactional
    protected void initializeDatabase() {
        logger.info("Initializing database...");
        User foundAdmin = userDAO.findByUsername("pizzashop");
        List<Role> roles = roleDAO.findAll();

        logger.info("Roles found: {}", roles);

        if (roles.size() != 3) {
            logger.info("Adding roles to database...");
            roleDAO.deleteAll();

            Role customerRole = new Role();
            customerRole.setRole(RoleEnum.ROLE_CUSTOMER);
            roleDAO.save(customerRole);

            Role employeeRole = new Role();
            employeeRole.setRole(RoleEnum.ROLE_EMPLOYEE);
            roleDAO.save(employeeRole);

            Role adminRole = new Role();
            adminRole.setRole(RoleEnum.ROLE_MANAGER);
            roleDAO.save(adminRole);
        }


        if (foundAdmin == null) {
            logger.info("Admin not found! Inserting admin user pizzashop with default values...");
            UserRegisterDTO initialAdmin = this.setInitialAdmin();
            userRegistrationService.save(initialAdmin, RoleEnum.ROLE_MANAGER.name());
        } else if (!foundAdmin.isActive() || !bCryptPasswordEncoder.matches(initialPass, foundAdmin.getPassword())) {
            logger.info("Admin deactivated or password has changed! Activating admin user pizzashop with default values...");
            UserRegisterDTO initialAdmin = this.setInitialAdmin();
            userRegistrationService.update(initialAdmin, foundAdmin.getId(), RoleEnum.ROLE_MANAGER.name());
            userDAO.activateUser(foundAdmin.getId());
        }

        if (menuItemService.findMenuItemByName(PizzaSizeEnum.SMALL.getPizzaName()) == null) {
            logger.info("Inserting initial pizza ingredients and base pizzas...");
            IngredientDTO dough = new IngredientDTO();
            dough.setIngredientName("Dough");
            dough.setCurrentStock(2000);
            dough.setCentsCostPer(10);
            dough.setMarkupMulti(3);
            dough.setIsPizzaTopping(false);
            menuItemService.saveIngredient(dough, null);

            IngredientDTO tomatoSauce = new IngredientDTO();
            tomatoSauce.setIngredientName("Tomato Sauce");
            tomatoSauce.setCurrentStock(2000);
            tomatoSauce.setCentsCostPer(10);
            tomatoSauce.setMarkupMulti(3);
            tomatoSauce.setIsPizzaTopping(false);
            menuItemService.saveIngredient(tomatoSauce, null);

            IngredientDTO cheese = new IngredientDTO();
            cheese.setIngredientName("Mozzarella");
            cheese.setCurrentStock(2000);
            cheese.setCentsCostPer(35);
            cheese.setMarkupMulti(3);
            cheese.setIsPizzaTopping(true);
            menuItemService.saveIngredient(cheese, null);

            MenuItemDTO smallPizza = new MenuItemDTO(
                    PizzaSizeEnum.SMALL.getPizzaName(),
                    PizzaSizeEnum.SMALL.getDescription(),
                    MenuCategoryEnum.PIZZA
            );
            smallPizza.setMarkupMultiplier(3);
            // ids will be in order they are added here. [id, qty (adds one from base ingredient amt)]
            List<int[]> ingredientIdAmounts = new ArrayList<>();
            ingredientIdAmounts.add(new int[]{1, PizzaSizeEnum.SMALL.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{2, PizzaSizeEnum.SMALL.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{3, PizzaSizeEnum.SMALL.getIngredientAmount() + 1});
            smallPizza.setIngredientIdAmounts(ingredientIdAmounts);
            menuItemService.saveMenuItem(smallPizza);

            MenuItemDTO medPizza = new MenuItemDTO(
                    PizzaSizeEnum.MEDIUM.getPizzaName(),
                    PizzaSizeEnum.MEDIUM.getDescription(),
                    MenuCategoryEnum.PIZZA
            );
            medPizza.setMarkupMultiplier(3);
            // ids will be in order they are added here. [id, qty (adds one from base ingredient amt)]
            ingredientIdAmounts = new ArrayList<>();
            ingredientIdAmounts.add(new int[]{1, PizzaSizeEnum.MEDIUM.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{2, PizzaSizeEnum.MEDIUM.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{3, PizzaSizeEnum.MEDIUM.getIngredientAmount() + 1});
            medPizza.setIngredientIdAmounts(ingredientIdAmounts);
            menuItemService.saveMenuItem(medPizza);

            MenuItemDTO lgPizza = new MenuItemDTO(
                    PizzaSizeEnum.LARGE.getPizzaName(),
                    PizzaSizeEnum.LARGE.getDescription(),
                    MenuCategoryEnum.PIZZA
            );
            lgPizza.setMarkupMultiplier(3);
            // ids will be in order they are added here. [id, qty (adds one from base ingredient amt)]
            ingredientIdAmounts = new ArrayList<>();
            ingredientIdAmounts.add(new int[]{1, PizzaSizeEnum.LARGE.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{2, PizzaSizeEnum.LARGE.getIngredientAmount() + 1});
            ingredientIdAmounts.add(new int[]{3, PizzaSizeEnum.LARGE.getIngredientAmount() + 1});
            lgPizza.setIngredientIdAmounts(ingredientIdAmounts);
            menuItemService.saveMenuItem(lgPizza);
        }

        logger.info("Database initialization complete...");
    }

    private UserRegisterDTO setInitialAdmin() {
        UserRegisterDTO initialAdmin = new UserRegisterDTO(
                "pizzashop", "Shawn", "Osborne",
                "fakeEmail@gmail.com", "(123)456-7890",
                "123 Main St.", "PizzaTown", "ST"
        );
        initialAdmin.setPassword(initialPass);

        return initialAdmin;
    }
}
