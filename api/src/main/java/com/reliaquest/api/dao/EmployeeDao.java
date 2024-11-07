package com.reliaquest.api.dao;

import com.reliaquest.api.Exception.ApiRateLimitException;
import com.reliaquest.api.Exception.EmployeeException;
import com.reliaquest.api.Exception.ExternalApiException;
import com.reliaquest.api.constants.ApplicationConstants;
import com.reliaquest.api.models.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeDao {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDao.class);

    public Employee addEmployee(EmployeeRequest request) {
        String uri = Strings.EMPTY;
        logger.info("EmployeeDao - Adding new employee with request: {}", request);
        try {
            ApiResponse<Employee> apiResponse = webClient.post()
                    .uri(uri)
                    .body(Mono.just(request), EmployeeRequest.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Employee>>() {})
                    .block();

            if (apiResponse == null || apiResponse.getData() == null) {
                throw new ExternalApiException("Failed to add employee. Response is null.");
            }

            logger.info("EmployeeDao - Successfully added employee with id: {}", apiResponse.getData().getId());
            return apiResponse.getData();

        } catch (Exception e) {
            handleException(e, ApplicationConstants.NA);
            return null; // Unreachable, but required by the compiler.
        }
    }

    public List<Employee> getListOfAllEmployees() {
        String uri = Strings.EMPTY;
        logger.info("EmployeeDao - Retrieving list of all employees");
        try {
            ApiResponse<List<Employee>> apiResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {})
                    .block();

            if (apiResponse == null || apiResponse.getData() == null) {
                throw new ExternalApiException("Failed to retrieve employees. Response is null.");
            }

            logger.info("EmployeeDao - Successfully retrieved {} employees", apiResponse.getData().size());
            return apiResponse.getData();

        } catch (Exception e) {
            handleException(e, ApplicationConstants.NA);
            return null;
        }
    }

    public Employee getEmployeeById(String id) {
        String uri = ApplicationConstants.FORWARD_SLASH + id;
        logger.info("EmployeeDao - Retrieving employee with id: {}", id);
        try {
            ApiResponse<Employee> apiResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Employee>>() {})
                    .block();
            if (apiResponse == null || apiResponse.getData() == null) {
                throw new EmployeeException("Employee not Found", ApplicationConstants.ID);
            }
            logger.info("EmployeeDao - Successfully retrieved employee with id: {}", id);
            return apiResponse.getData();

        } catch (Exception e) {
            handleException(e, ApplicationConstants.ID);
            return null;
        }
    }
    public String deleteEmployeeByName(String id) {
        logger.info("EmployeeDao - Deleting employee with id: {}", id);
        Employee employee = getEmployeeById(id); // Retrieve the employee details first
        DeleteMockEmployeeInput deleteInput = new DeleteMockEmployeeInput(employee.getName());
        String uri = Strings.EMPTY;
        try {
            ApiResponse<Boolean> apiResponse = webClient.method(HttpMethod.DELETE)
                    .uri(uri)
                    .body(Mono.just(deleteInput), DeleteMockEmployeeInput.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Boolean>>() {})
                    .block();

            if (apiResponse == null || apiResponse.getData() == null || !apiResponse.getData()) {
                throw new ExternalApiException("Failed to delete employee. Response is null or deletion unsuccessful.");
            }

            logger.info("EmployeeDao - Successfully deleted employee with name: {}", employee.getName());
            return employee.getName();

        } catch (Exception e) {
            handleException(e, ApplicationConstants.NAME);
            return null;
        }
    }

    private void handleException(Exception e, String requestIdentificationParam) {
        if (e instanceof WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                logger.warn("EmployeeDao - Rate limit exceeded for request: {}", requestIdentificationParam);
                throw new ApiRateLimitException("Unusual traffic has been detected, please try again later", requestIdentificationParam);
            } else if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("EmployeeDao - Employee not found for request: {}", requestIdentificationParam);
                throw new EmployeeException("Employee not Found", requestIdentificationParam);
            } else {
                logger.error("EmployeeDao - Error occurred with status code {} for request: {}", ex.getStatusCode(), requestIdentificationParam);
                throw new ExternalApiException("Error occurred while processing employee details. Check your request or try again later.");
            }
        } else {
            logger.error("EmployeeDao - Unexpected error occurred: {}", e.getMessage());
            throw new ExternalApiException("An unexpected error occurred while processing the request.");
        }
    }
}
