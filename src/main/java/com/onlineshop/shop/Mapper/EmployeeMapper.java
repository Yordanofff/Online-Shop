package com.onlineshop.shop.Mapper;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import com.onlineshop.shop.Entity.Employee;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequestDto employeeRequestDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeRequestDto, employee);
        return employee;
    }

    public static EmployeeResponseDto toDto(Employee employee) {
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
        BeanUtils.copyProperties(employee, employeeResponseDto);
        return employeeResponseDto;
    }
}
