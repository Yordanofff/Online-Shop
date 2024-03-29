package com.onlineshop.shop.Controller;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import com.onlineshop.shop.Entity.HttpResponse;
import com.onlineshop.shop.Exception.ApiException;
import com.onlineshop.shop.Service.Implementation.EmployeeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeRestController {
    private final EmployeeServiceImpl employeeService;

    @GetMapping("/get_all")
    public ResponseEntity<?> getAllEmployees() {
        try {
            return HttpResponse.ok(employeeService.getAllEmployees());
        } catch (Exception e) {
            return HttpResponse.internalServerError("Error while running employeeService.getAllEmployees()", e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getEmployeeByID(@PathVariable Long id) {
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.getEmployeeByID(id);
            return HttpResponse.ok(employeeResponseDto);
        } catch (Exception e) {
            return HttpResponse.internalServerError("Error while trying to retrieve data for user with id: " + id, e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editEmployeeByID(@PathVariable Long id,
                                              @RequestBody @Valid EmployeeRequestDto employeeRequestDto,
                                              BindingResult bindingResult) {
        // TODO - instead of binding errors - check every field manually to allow partial data ... if field == null - ignore.
        if (bindingResult.hasErrors()) {
            return HttpResponse.validationError(bindingResult);
        }

        try {
            EmployeeResponseDto employeeResponseDto = employeeService.editEmployeeByID(id, employeeRequestDto);
            return HttpResponse.ok(employeeResponseDto);
        } catch (ApiException e) {
            return HttpResponse.conflict(e.getMessage()); // TODO - add Another Exception - one will be 500, the other one will be HTTPResponse conflict.
        } catch (Exception e) {
            return HttpResponse.internalServerError("", e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return HttpResponse.validationError(bindingResult);
        }
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.create(employeeRequestDto);
            return HttpResponse.created(employeeResponseDto);
        } catch (Exception ex) {
            return HttpResponse.internalServerError("Error while attempting to add new employee", ex.getMessage());
        }
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/employee/get/<employeeId>").toUriString());
    }

}
