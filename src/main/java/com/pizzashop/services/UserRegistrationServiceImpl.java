package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.dao.RoleDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.UserDetail;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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
        User user;
        try {
           user = userDAO.findByUsername(userName);
           return Optional.of(user);
        } catch (EmptyResultDataAccessException e_2) {
            System.out.println("User not found" + e_2.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        System.out.println("Saving user: " + userRegisterDTO);

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

        // set default role of customer
        // ToDo: allow adding other roles in management view
        Role role = roleDAO.findByRole(RoleEnum.ROLE_CUSTOMER);
        user.addRole(role);

        userDAO.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
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
}
