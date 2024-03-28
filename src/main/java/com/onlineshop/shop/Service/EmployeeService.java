package com.onlineshop.shop.Service;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {
    EmployeeResponseDto getEmployeeByID(Long id);
    List<EmployeeResponseDto> getAllEmployees();
    EmployeeResponseDto create(EmployeeRequestDto employeeRequestDtoRequest);
}
