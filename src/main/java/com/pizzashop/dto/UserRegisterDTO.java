package com.pizzashop.dto;

import com.pizzashop.entities.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserRegisterDTO {
    @NotNull(message = "required")
    @Size(min = 3, message = "minimum of 3 characters required")
    private String username;
    @NotNull(message = "required")
    @Size(min = 4, message = "minimum of 4 characters required")
    private String password;
    @NotNull(message = "required")
    @Size(min = 1, message = "minimum of 1 character required")
    private String firstName;
    @NotNull(message = "required")
    @Size(min = 1, message = "minimum of 1 character required")
    private String lastName;
    @NotNull(message = "required")
    @Size(min = 5, message = "minimum of 5 characters required")
    @Pattern(regexp="^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", message = "invalid format")
    private String email;
    @NotNull(message = "required")
    @Size(min = 5, message = "minimum of 10 characters required")
    @Pattern(regexp="^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", message = "invalid format")
    private String phone;
    @NotNull(message = "required")
    @Size(min = 1, message = "minimum of 1 character required")
    private String address;
    @NotNull(message = "required")
    @Size(min = 2, message = "minimum of 2 characters required")
    private String city;
    @NotNull(message = "required")
    @Size(min = 2, message = "minimum of 2 characters required")
    private String state;

    private List<Role> roles;

    public UserRegisterDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Role> getRoles() {
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserRegisterDTO{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
