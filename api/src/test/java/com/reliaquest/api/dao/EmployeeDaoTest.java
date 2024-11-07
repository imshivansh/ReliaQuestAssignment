package com.reliaquest.api.dao;

import com.reliaquest.api.Exception.ApiRateLimitException;
import com.reliaquest.api.Exception.EmployeeException;
import com.reliaquest.api.Exception.ExternalApiException;
import com.reliaquest.api.constants.ApplicationConstants;
import com.reliaquest.api.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeDaoTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeDao employeeDao;

    private EmployeeRequest employeeRequest;
    private Employee employee;
    private ApiResponse<Employee> apiEmployeeResponse;
    private ApiResponse<List<Employee>> apiEmployeeListResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize data
        employeeRequest = new EmployeeRequest("Shivansh Singh", 50000, "Software Engineer", 25);
        employee = new Employee(UUID.randomUUID(), "Shivansh Singh", 50000, 25, "Software Engineer", "shivansh.singh@example.com");
        apiEmployeeResponse = new ApiResponse<>("success", employee);
        apiEmployeeListResponse = new ApiResponse<>("success", List.of(employee));
    }

    @Test
    void testAddEmployee_Success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(EmployeeRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.just(apiEmployeeResponse));

        Employee result = employeeDao.addEmployee(employeeRequest);

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getName(), result.getName());
    }

    @Test
    void testAddEmployee_ThrowsExternalApiException_WhenResponseIsNull() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(EmployeeRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.empty());

        assertThrows(ExternalApiException.class, () -> employeeDao.addEmployee(employeeRequest));
    }

    @Test
    void testGetListOfAllEmployees_Success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.just(apiEmployeeListResponse));

        List<Employee> result = employeeDao.getListOfAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employee.getName(), result.get(0).getName());
    }

    @Test
    void testGetListOfAllEmployees_ThrowsExternalApiException_WhenResponseIsNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.empty());

        assertThrows(ExternalApiException.class, employeeDao::getListOfAllEmployees);
    }

    @Test
    void testGetEmployeeById_Success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.just(apiEmployeeResponse));

        Employee result = employeeDao.getEmployeeById(employee.getId().toString());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getName(), result.getName());
    }

    @Test
    void testGetEmployeeById_ThrowsEmployeeException_WhenNotFound() {
        WebClientResponseException notFoundException = WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenThrow(notFoundException);

        assertThrows(EmployeeException.class, () -> employeeDao.getEmployeeById(employee.getId().toString()));
    }

    @Test
    void testDeleteEmployeeByName_Success() {
        // Mock the `getEmployeeById` call to return an Employee
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<ApiResponse<Employee>>() {}))
                .thenReturn(Mono.just(apiEmployeeResponse)); // This should return Employee data, not Boolean

        // Mock the delete operation to return a Boolean wrapped in ApiResponse
        ApiResponse<Boolean> deleteResponse = new ApiResponse<>("success", true);
        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(DeleteMockEmployeeInput.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<ApiResponse<Boolean>>() {}))
                .thenReturn(Mono.just(deleteResponse)); // Correctly returning Boolean for delete response

        /* Now execute the delete method and assert*/

        String result = employeeDao.deleteEmployeeByName(employee.getId().toString());

        assertNotNull(result);
        assertEquals(employee.getName(), result);
    }


    @Test
    void testDeleteEmployeeByName_ThrowsExternalApiException_WhenDeletionFails() {
        DeleteMockEmployeeInput deleteInput = new DeleteMockEmployeeInput(employee.getName());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.just(apiEmployeeResponse));

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(DeleteMockEmployeeInput.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenReturn(Mono.empty());

        assertThrows(ExternalApiException.class, () -> employeeDao.deleteEmployeeByName(employee.getId().toString()));
    }


    @Test
    void testHandleException_ThrowsApiRateLimitException_WhenTooManyRequests() {
        WebClientResponseException tooManyRequestsException = WebClientResponseException.create(
                HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", null, null, null);
        String requestParam = "testParam";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenThrow(tooManyRequestsException);

        ApiRateLimitException exception = assertThrows(ApiRateLimitException.class, () -> {
            employeeDao.getEmployeeById(requestParam);
        });

        assertEquals("Unusual traffic has been detected, please try again later", exception.getMessage());
    }


    @Test
    void testAddEmployee_ThrowsExternalApiException_OnInternalServerError() {
        WebClientResponseException serverErrorException = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null, null, null);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(EmployeeRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenThrow(serverErrorException);

        assertThrows(ExternalApiException.class, () -> employeeDao.addEmployee(employeeRequest));
    }

    @Test
    void testGetEmployeeById_ThrowsExternalApiException_OnServiceUnavailable() {
        WebClientResponseException serviceUnavailableException = WebClientResponseException.create(
                HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable", null, null, null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(anyParameterizedTypeReference())).thenThrow(serviceUnavailableException);

        assertThrows(ExternalApiException.class, () -> employeeDao.getEmployeeById("12345"));
    }

    // Helper method to handle ParameterizedTypeReference generics
    private <T> ParameterizedTypeReference<T> anyParameterizedTypeReference() {
        return any();
    }
}
