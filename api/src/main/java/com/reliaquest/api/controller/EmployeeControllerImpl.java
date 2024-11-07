package com.reliaquest.api.controller;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;
import com.reliaquest.api.service.IEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeControllerImpl implements IEmployeeController<Employee, EmployeeRequest> {
    private final IEmployeeService employeeService;
    private final Logger logger = LoggerFactory.getLogger(IEmployeeController.class);

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("EmployeeController - Entered getAllEmployees()");
        List<Employee> employees = employeeService.getAllEmployees();
        logger.info("EmployeeController - Successfully retrieved all employees. Total employees: {}", employees.size());
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("EmployeeController - Entered getEmployeesByNameSearch() with searchString: {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        logger.info("EmployeeController - Found {} employees matching '{}'", employees.size(), searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("EmployeeController - Entered getEmployeeById() with id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            logger.info("EmployeeController - Retrieved employee with id: {}", id);
        } else {
            logger.warn("EmployeeController - No employee found with id: {}", id);
        }
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("EmployeeController - Entered getHighestSalaryOfEmployees()");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        logger.info("EmployeeController - Highest employee salary is: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("EmployeeController - Entered getTopTenHighestEarningEmployeeNames()");
        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        logger.info("EmployeeController - Retrieved top 10 highest earning employees. Total employees in list: {}", topEarners.size());
        return ResponseEntity.ok(topEarners);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeRequest employeeRequest) {
        logger.info("EmployeeController - Entered createEmployee() with request: {}", employeeRequest);
        Employee createdEmployee = employeeService.createEmployee(employeeRequest);
        logger.info("EmployeeController - Created employee with id: {}", createdEmployee.getId());
        return ResponseEntity.ok(createdEmployee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("EmployeeController - Entered deleteEmployeeById() with id: {}", id);
        String result = employeeService.deleteEmployeeById(id);
        logger.info("EmployeeController - Deleted employee with id: {}", id);
        return ResponseEntity.ok(result);
    }
}