package com.onlineshop.shop.Controller;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import com.onlineshop.shop.Entity.HttpResponse;
import com.onlineshop.shop.Service.Implementation.EmployeeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeRestController {
    private final EmployeeServiceImpl employeeService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .message("Validation error")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .data(Map.of("errors", errors))
                            .build()
            );
        }
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.create(employeeRequestDto);
//            return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponseDto);
            return ResponseEntity.created(getUri()).body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("employeeResponseDto", employeeResponseDto))
                            .message("Employee created")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build()
            );
        } catch (Exception ex) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(HttpResponse.builder()
                            .timeStamp(now().toString())
                            .message(ex.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
                    );
        }
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/employee/get/<employeeId>").toUriString());
    }

}
