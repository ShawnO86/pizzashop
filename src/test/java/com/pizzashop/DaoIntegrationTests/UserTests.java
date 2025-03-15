package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.entities.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // uses H2
@ComponentScan("com.pizzashop.dao") // needed because not within com.pizzashop package
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
    public void userAddOrder() {

    }

}
