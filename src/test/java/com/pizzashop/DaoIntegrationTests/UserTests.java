package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // uses H2
@ComponentScan("com.pizzashop.dao") // needed because not within main com.pizzashop package
public class UserTests {
    @Autowired
    private UserDAO userDAO;

    private User user;
    private UserDetail userDetail;
    private Role role;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    public void testAddUser() {
        userDAO.save(user);
        User fetchedUser = userDAO.findByUsername(user.getUsername());

        assertNotNull(fetchedUser);
        assertEquals(fetchedUser.getUsername(), user.getUsername());
        assertEquals(fetchedUser.getPassword(), user.getPassword());
        assertEquals(fetchedUser.getRoles(), user.getRoles());
        assertEquals(fetchedUser.getUserDetail(), userDetail);

        System.out.println("Add user test complete, fetched user:\n" + fetchedUser + "\nEntered user:\n" + user);
    }

    @Test
    public void testAddOrder() {
        List<Order> orders = new ArrayList<>();
        MenuItem menuItem1 = new MenuItem("Bread sticks", "sticks of bread", 350);
        MenuItem menuItem2 = new MenuItem("Spaghetti Bolognese", "Pasta with a meat and tomato sauce", 900);
        MenuItem menuItem3 = new MenuItem("Lg Soda", "Large Soda", 150);

        Order order1 = new Order(user, LocalDateTime.now());
        order1.addMenuItem(menuItem1);
        order1.addMenuItem(menuItem2);
        order1.addMenuItem(menuItem3);
        order1.setFinal_price_cents();

        user.addOrder(order1);
        System.out.println("in test add order: " + user);
        userDAO.save(user);

        User userWithOrders = userDAO.findByUsernameJoinFetchOrders(user.getUsername());

        System.out.println("Add user test complete, fetched user:\n" + userWithOrders + "\nEntered user:\n" + user);
        System.out.println("orders: \n" + userWithOrders.getOrders());

        assertNotNull(userWithOrders);
        assertEquals(userWithOrders.getUsername(), user.getUsername());
        assertEquals(1, userWithOrders.getOrders().size());
        assertEquals(userWithOrders.getOrders().getFirst().getMenuItems().getFirst(), order1.getMenuItems().getFirst());
    }

}
