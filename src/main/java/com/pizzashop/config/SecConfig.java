package com.pizzashop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select username, password, active from user where username=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select username, role from role where username=?");

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/order/**").hasRole("CUSTOMER")
                                .requestMatchers("/employees/**").hasRole("EMPLOYEE")
                                .requestMatchers("/system/**").hasRole("MANAGER")
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form.loginPage("/loginPage").loginProcessingUrl("/authenticateUser").permitAll()
                )
                .logout(logout -> logout.permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}