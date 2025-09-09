package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

        User fetchedUserById = userDAO.findById(8);


        System.out.println("Add user test complete, fetched user 1:\n" + fetchedUser + "\nEntered user:\n" + user);
        System.out.println("Add user test complete, fetched user 2:\n" + fetchedUserById);

        assertNull(fetchedUser);
        assertEquals(0, fetchedUsers.size());

        assertNull(fetchedUserById);
    }

    @Test
    public void testUpdateUser() {
        userDAO.save(user);

        User fetchedUser = userDAO.findByUsername(user.getUsername());
        UserDetail fetchedUserDetail = fetchedUser.getUserDetail();
        System.out.println("Fetched user:\n" + fetchedUser);
        System.out.println("Fetched userdetail:\n" + fetchedUserDetail);

        fetchedUserDetail.setFirstName("TestFirstName_changed");
        fetchedUser.setUserDetail(fetchedUserDetail);

        userDAO.save(fetchedUser);

        User changedUser = userDAO.findByUsername(fetchedUser.getUsername());

        System.out.println("Updated user:\n" + changedUser);
        System.out.println("Updated userdetail:\n" + changedUser.getUserDetail());
        // ensure same identifier
        assertEquals(changedUser.getId(), fetchedUser.getId());
        assertEquals(changedUser.getUserDetail().getFirstName(), user.getUserDetail().getFirstName());
    }

    @Test
    public void testFindUserByLastName() {
        System.out.println("Save user =>");
        userDAO.save(user);
        System.out.println("Find user by last name =>");
        List<User> fetchedUsers = userDAO.findAllByLastName("TestLastName");
        System.out.println(fetchedUsers);

        assertNotNull(fetchedUsers);
    }
}
