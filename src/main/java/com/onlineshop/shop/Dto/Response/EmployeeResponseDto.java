package com.onlineshop.shop.Dto.Response;

import com.onlineshop.shop.Entity.Role;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private BigDecimal salary;
    private String phoneNumber;
    private Role role;
    private LocalDateTime createdAt;
    private boolean isEnabled;
}
