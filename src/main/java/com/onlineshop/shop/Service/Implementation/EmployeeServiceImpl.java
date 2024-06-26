package com.onlineshop.shop.Service.Implementation;

import com.onlineshop.shop.Dto.Request.EmployeeRequestDto;
import com.onlineshop.shop.Dto.Response.EmployeeResponseDto;
import com.onlineshop.shop.Entity.Employee;
import com.onlineshop.shop.Entity.Role;
import com.onlineshop.shop.Exception.ApiException;
import com.onlineshop.shop.Mapper.EmployeeMapper;
import com.onlineshop.shop.Repository.EmployeeRepository;
import com.onlineshop.shop.Repository.RoleRepository;
import com.onlineshop.shop.Service.EmployeeService;
import com.onlineshop.shop.Static.RoleType;
import com.onlineshop.shop.Utility.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;


    @Override
    public EmployeeResponseDto getEmployeeByID(Long id) {
        Employee employee = validateEmployeeExistsById(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDto create(EmployeeRequestDto employeeRequestDto) {
        if (isEmailInDB(employeeRequestDto.getEmail())) {
            throw new ApiException("Email already in use. Please use a different email");
        }
        if (employeeRequestDto.getPhoneNumber() != null && isPhoneNumberInDB(employeeRequestDto.getPhoneNumber())) {
            throw new ApiException("Phone number already in use. Please use a different phone number or leave blank");
        }

        validatePasswordsAreMatching(employeeRequestDto);

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (optionalRole.isEmpty()) {
            throw new ApiException("Employee role not found");
        }

        try {
            Employee employee = employeeMapper.toEntity(employeeRequestDto);
            employee.setRole(optionalRole.get());
            employee.setPassword(encoder.passwordEncoder().encode(employeeRequestDto.getPassword()));
            employeeRepository.save(employee);
            return employeeMapper.toDto(employee);
        } catch (Exception exception) {
            throw new ApiException("An internal error occurred. Please try again. " + exception.getCause());
        }
    }

    @Override
    public EmployeeResponseDto editEmployeeByID(Long id, EmployeeRequestDto employeeRequestDto) {

        Employee employee = validateEmployeeExistsById(id);
        validateEmailIsNotUsedByAnotherEmployee(id, employeeRequestDto);
        validatePhoneNumberIsNotUsedByAnotherEmployee(id, employeeRequestDto);
        validatePasswordsAreMatching(employeeRequestDto);  // TODO: create another DTO for edit - without password + another one just for password (old + new x 2)

        try {
            Employee newDataForEmployee = employeeMapper.toEntity(employeeRequestDto);
            newDataForEmployee.setId(employee.getId());
            newDataForEmployee.setRole(employee.getRole());  // keep same role  // TODO: new method to update the role for user
            newDataForEmployee.setPassword(encoder.passwordEncoder().encode(employeeRequestDto.getPassword())); // TODO: delete this once there's another DTO for password

            employeeRepository.save(newDataForEmployee);
            return employeeMapper.toDto(newDataForEmployee);
        } catch (Exception exception) {
            throw new ApiException("An internal error occurred. Please try again. " + exception.getCause());
        }
    }

    private void validatePasswordsAreMatching(EmployeeRequestDto employeeRequestDto) {
        if (!employeeRequestDto.getPassword().equals(employeeRequestDto.getRepeatedPassword())) {
            throw new ApiException("Passwords don't match");
        }
    }

    private void validatePasswordsAreMatching(String pw1, String pw2) {
        if (!pw1.equals(pw2)) {
            throw new ApiException("Passwords don't match");
        }
    }

    private void validatePhoneNumberIsNotUsedByAnotherEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee e = getEmployeeByPhoneNumber(employeeRequestDto.getPhoneNumber());
        if (e != null) {
            // Employee with this email exist
            if (!e.getId().equals(id)) {
                throw new ApiException("Another employee is registered with this Phone number: " + employeeRequestDto.getPhoneNumber());
            }
        }

    }

    private void validateEmailIsNotUsedByAnotherEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee e = getEmployeeByEmail(employeeRequestDto.getEmail());
        if (e != null) {
            // Employee with this email exist
            if (!e.getId().equals(id)) {
                throw new ApiException("Another employee is registered with this Email: " + employeeRequestDto.getEmail());
            }
        }

    }

    private Employee validateEmployeeExistsById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new ApiException("Employee with ID: " + id + " not found!");
        }
        return optionalEmployee.get();
    }

    private boolean isEmailInDB(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private Employee getEmployeeByEmail(String email) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        return optionalEmployee.orElse(null);
    }

    private Employee getEmployeeByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        Optional<Employee> optionalEmployee = employeeRepository.findByPhoneNumber(phoneNumber);
        return optionalEmployee.orElse(null);
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
