package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
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
    public void notFoundUser() {
        User fetchedUser = userDAO.findByUsername(user.getUsername());
        List<User> fetchedUsers = userDAO.findAll();

        assertNull(fetchedUser);
        assertEquals(0, fetchedUsers.size());

        System.out.println("Add user test complete, fetched user:\n" + fetchedUser + "\nEntered user:\n" + user);
    }

    @Test
    public void testUpdateUser() {
        userDAO.save(user);

        User fetchedUser = userDAO.findByUsername(user.getUsername());
        UserDetail fetchedUserDetail = fetchedUser.getUserDetail();
        System.out.println("Fetched user:\n" + fetchedUser);

        fetchedUserDetail.setFirstName("TestFirstName_changed");
        fetchedUser.setUserDetail(fetchedUserDetail);

        User changedUser = userDAO.findByUsername(user.getUsername());

        System.out.println("Updated user:\n" + changedUser);
        // ensure same identifier
        assertEquals(changedUser.getId(), fetchedUser.getId());
        assertEquals(changedUser.getUserDetail().getFirstName(), user.getUserDetail().getFirstName());
    }


}
