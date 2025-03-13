package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.dao.RoleDAO;
import com.pizzashop.dao.UserDetailDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.entities.UserDetail;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserDetailDAO userDetailDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(
            UserDAO userDAO, RoleDAO roleDAO, UserDetailDAO userDetailDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userDetailDAO = userDetailDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return Optional.ofNullable(userDAO.findByUsername(userName));
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        User user;
        UserDetail userDetails;
        Role role;

        user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
        user.setActive(true);

        userDetails = new UserDetail(
                userRegisterDTO.getFirstName(),
                userRegisterDTO.getLastName(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getPhone(),
                userRegisterDTO.getAddress(),
                userRegisterDTO.getCity(),
                userRegisterDTO.getState()
        );

        role = new Role(RoleEnum.ROLE_CUSTOMER);
        user.setUserDetail(userDetails);
        user.setRoles(List.of(role));
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
