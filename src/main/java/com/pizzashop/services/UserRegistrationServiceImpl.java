package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.dao.RoleDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.UserDetail;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserRegistrationServiceImpl(
            UserDAO userDAO, RoleDAO roleDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        User user = userDAO.findByUsername(userName);
        return Optional.ofNullable(user);
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO, String role) {
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
        user.setActive(true);

        UserDetail userDetails = new UserDetail(
                userRegisterDTO.getFirstName(),
                userRegisterDTO.getLastName(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getPhone(),
                userRegisterDTO.getAddress(),
                userRegisterDTO.getCity(),
                userRegisterDTO.getState()
        );

        user.setUserDetail(userDetails);

        this.setUserRoles(user, role);

        userDAO.save(user);
    }

    @Override
    public void update(UserRegisterDTO userRegisterDTO, int userId, String role) {
        User user = userDAO.findByIdJoinFetchUserDetailsRoles(userId);
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));

        user.setRoles(null);
        this.setUserRoles(user, role);

        UserDetail userDetails = user.getUserDetail();
        userDetails.setFirstName(userRegisterDTO.getFirstName());
        userDetails.setLastName(userRegisterDTO.getLastName());
        userDetails.setEmail(userRegisterDTO.getEmail());
        userDetails.setPhone(userRegisterDTO.getPhone());
        userDetails.setAddress(userRegisterDTO.getAddress());
        userDetails.setCity(userRegisterDTO.getCity());
        userDetails.setState(userRegisterDTO.getState());

        user.setUserDetail(userDetails);

        userDAO.updateUser(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsernameJoinFetchRole(username);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password!");
        } else if (!user.isActive()) {
            throw new UsernameNotFoundException(username + " is not active!");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getRole().name());
            authorities.add(tempAuthority);
        }

        return authorities;
    }

    private void setUserRoles(User user, String role) {
        Role customer;
        switch(role) {
            case "ROLE_EMPLOYEE":
                customer = roleDAO.findByRole(RoleEnum.ROLE_CUSTOMER);
                Role employee = roleDAO.findByRole(RoleEnum.ROLE_EMPLOYEE);
                user.addRole(customer);
                user.addRole(employee);
                break;
            case "ROLE_MANAGER":
                List<Role> roles = roleDAO.findAll();
                user.setRoles(roles);
                break;
            case "ROLE_CUSTOMER":
                customer = roleDAO.findByRole(RoleEnum.ROLE_CUSTOMER);
                user.addRole(customer);
                break;
        }
    }

}
