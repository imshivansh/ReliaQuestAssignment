package com.reliaquest.api.service;

import com.reliaquest.api.Exception.EmployeeException;
import com.reliaquest.api.constants.ApplicationConstants;
import com.reliaquest.api.dao.EmployeeDao;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeDao employeeDao;
    private final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("EmployeeService - Fetching all employees");
        return employeeDao.getListOfAllEmployees();
    }

    @Override
    public Employee getEmployeeById(String id) {
        logger.info("EmployeeService - Fetching employee with id: {}", id);
        // Directly return the employee retrieved from DAO. DAO will handle the exception if not found.
        return employeeDao.getEmployeeById(id);
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String name) {
        logger.info("EmployeeService - Searching employees by name containing: '{}'", name);
        // No need to check for an empty list here; let DAO handle it if necessary
        List<Employee> employeeList = employeeDao.getListOfAllEmployees().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        if (employeeList.isEmpty()) {
            logger.warn("EmployeeService - No employees found with name containing: '{}'", name);
            throw new EmployeeException(String.format("No employees found with name: %s", name), ApplicationConstants.NAME);
        }

        logger.info("EmployeeService - Found {} employees with name containing: '{}'", employeeList.size(), name);
        return employeeList;
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        logger.info("EmployeeService - Retrieving highest salary among employees");
        return employeeDao.getListOfAllEmployees().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElseThrow(() -> {
                    logger.warn("EmployeeService - No employees found or no salaries available");
                    return new EmployeeException("No employees found or no salaries available", ApplicationConstants.NA);
                });
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("EmployeeService - Retrieving top 10 highest earning employee names");
        return employeeDao.getListOfAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(ApplicationConstants.DIGIT_TEN)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Employee createEmployee(EmployeeRequest request) {
        logger.info("EmployeeService - Creating employee with request: {}", request);
        return employeeDao.addEmployee(request);
    }

    @Override
    public String deleteEmployeeById(String id) {
        logger.info("EmployeeService - Deleting employee with id: {}", id);
        return employeeDao.deleteEmployeeByName(id);
    }
}
