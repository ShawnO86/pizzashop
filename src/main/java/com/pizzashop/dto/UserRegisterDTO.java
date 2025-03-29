package com.pizzashop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
    @Pattern(regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", message = "invalid format")
    private String email;
    @NotNull(message = "required")
    @Size(min = 5, message = "minimum of 10 characters required")
    @Pattern(regexp = "^(\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]\\d{4})$", message = "invalid format. please use format: (123)456-7890 or 123-456-7890")
    private String phone;
    @NotNull(message = "required")
    @Size(min = 2, message = "minimum of 2 characters required")
    private String address;
    @NotNull(message = "required")
    @Size(min = 2, message = "minimum of 2 characters required")
    private String city;
    @NotNull(message = "required")
    @Size(min = 2, max = 2, message = "2 characters for state abbreviation required")
    private String state;

    public UserRegisterDTO() {}

    public UserRegisterDTO(String username, String firstName, String lastName, String email,
                           String phone, String address, String city, String state) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
    }

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
