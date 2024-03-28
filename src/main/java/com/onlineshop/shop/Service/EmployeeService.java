package com.onlineshop.shop.Service;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import com.onlineshop.shop.Entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {
    List<EmployeeResponseDto> getAllEmployees();
    Employee create(EmployeeRequestDto employeeRequestDtoRequest);
}
