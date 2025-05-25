package com.pizzashop.dto;

public class EmployeeInfoDTO {
    private Integer employeeID;
    private String employeeUsername;

    public EmployeeInfoDTO() {}
    public EmployeeInfoDTO(Integer employeeID, String employeeUsername) {
        this.employeeID = employeeID;
        this.employeeUsername = employeeUsername;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    @Override
    public String toString() {
        return "EmployeeInfoDTO{" +
                "employeeID=" + employeeID +
                ", employeeUsername='" + employeeUsername + '\'' +
                '}';
    }
}
