package com.pizzashop.serviceUnitTests;

import com.pizzashop.dao.RoleDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.entities.UserDetail;
import com.pizzashop.services.UserRegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// integrates Mockito with JUnit
@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTest {

    // mock instances of the dependencies.
    @Mock
    private UserDAO userDAO;
    @Mock
    private RoleDAO roleDAO;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // inject mock dependencies into the tested class instance
    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    private UserRegisterDTO userRegisterDTO;
    private User user;
    private Role customerRole;
    private Role employeeRole;
    private Role managerRole;

    // Create common data needed for tests
    @BeforeEach
    void setUp() {
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("testuser");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");
        userRegisterDTO.setEmail("john.doe@example.com");
        userRegisterDTO.setPhone("123-456-7890");
        userRegisterDTO.setAddress("123 Test St");
        userRegisterDTO.setCity("Test City");
        userRegisterDTO.setState("TS");

        UserDetail userDetail = new UserDetail(
                userRegisterDTO.getFirstName(),
                userRegisterDTO.getLastName(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getPhone(),
                userRegisterDTO.getAddress(),
                userRegisterDTO.getCity(),
                userRegisterDTO.getState()
        );

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("encodedPassword123");
        user.setActive(true);
        user.setUserDetail(userDetail);

        customerRole = new Role(RoleEnum.ROLE_CUSTOMER);
        employeeRole = new Role(RoleEnum.ROLE_EMPLOYEE);
        managerRole = new Role(RoleEnum.ROLE_MANAGER);
    }

    @Test
    void testSave_RoleDefault() {
        when(roleDAO.findByRole(RoleEnum.ROLE_CUSTOMER)).thenReturn(customerRole);

        userRegistrationService.save(userRegisterDTO, RoleEnum.ROLE_CUSTOMER.name());

        // Verify that password was encoded
        verify(bCryptPasswordEncoder, times(1)).encode(userRegisterDTO.getPassword());

        // Store this User object passed to userDAO.save for verification since userDAO is mocked.
        var userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        System.out.println("Saved Customer User: " + savedUser);

        // should contain role (CUSTOMER)
        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
        assertSame(savedUser.getRoles().getFirst(), customerRole);
    }

    @Test
    void testSave_RoleEmployee() {
        when(roleDAO.findByRole(RoleEnum.ROLE_CUSTOMER)).thenReturn(customerRole);
        when(roleDAO.findByRole(RoleEnum.ROLE_EMPLOYEE)).thenReturn(employeeRole);

        userRegistrationService.save(userRegisterDTO, RoleEnum.ROLE_EMPLOYEE.name());

        verify(bCryptPasswordEncoder, times(1)).encode(userRegisterDTO.getPassword());

        var userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        System.out.println("Saved Employee User: " + savedUser);

        // should contain roles (CUSTOMER, EMPLOYEE)
        assertNotNull(savedUser.getRoles());
        assertEquals(2, savedUser.getRoles().size());
        assertSame(savedUser.getRoles().getFirst(), customerRole);
        assertSame(savedUser.getRoles().get(1), employeeRole);
    }

    @Test
    void testSave_RoleManager() {
        when(roleDAO.findAll()).thenReturn(Arrays.asList(customerRole, employeeRole, managerRole));

        userRegistrationService.save(userRegisterDTO, RoleEnum.ROLE_MANAGER.name());

        verify(bCryptPasswordEncoder, times(1)).encode(userRegisterDTO.getPassword());

        var userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        System.out.println("Saved Manager User: " + savedUser);
        // should contain all roles (CUSTOMER, EMPLOYEE, MANAGER)
        assertNotNull(savedUser.getRoles());
        assertEquals(3, savedUser.getRoles().size());
        assertSame(savedUser.getRoles().getFirst(), customerRole);
        assertSame(savedUser.getRoles().get(1), employeeRole);
        assertSame(savedUser.getRoles().get(2), managerRole);
    }

    @Test
    void testLoadUserByUsername_UserFoundAndActive() {
        // give set up User a role for security authority
        user.addRole(customerRole);

        // Mock the DAO call to return the user
        when(userDAO.findByUsernameJoinFetchRole("testuser")).thenReturn(user);

        // Load the security user details
        UserDetails userDetails = userRegistrationService.loadUserByUsername("testuser");

        // Verify the loaded user details
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
        verify(userDAO, times(1)).findByUsernameJoinFetchRole("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userDAO.findByUsernameJoinFetchRole("nonexistent")).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                userRegistrationService.loadUserByUsername("nonexistent")
        );

        assertEquals("Invalid username or password!", exception.getMessage());
        verify(userDAO, times(1)).findByUsernameJoinFetchRole("nonexistent");
    }
}
