package com.reliaquest.api.service;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;

import java.util.List;

public interface IEmployeeService {


     List<Employee> getAllEmployees();
     Employee getEmployeeById(String id);
    List<Employee>getEmployeesByNameSearch(String name);
    Integer getHighestSalaryOfEmployees();
    List<String>getTopTenHighestEarningEmployeeNames();
    Employee createEmployee(EmployeeRequest request);
    String deleteEmployeeById(String id);
}
