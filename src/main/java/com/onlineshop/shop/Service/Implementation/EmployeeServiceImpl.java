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
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()){
            throw new ApiException("User with ID: " + id + " not found!");
        }
        return employeeMapper.toDto(optionalEmployee.get());
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDto create(EmployeeRequestDto employeeRequestDtoRequest) {
        if (isEmailInDB(employeeRequestDtoRequest.getEmail())) {
            throw new ApiException("Email already in use. Please use a different email");
        }
        if (employeeRequestDtoRequest.getPhoneNumber() != null && isPhoneNumberInDB(employeeRequestDtoRequest.getPhoneNumber())) {
            throw new ApiException("Phone number already in use. Please use a different phone number or leave blank");
        }
        if (!employeeRequestDtoRequest.getPassword().equals(employeeRequestDtoRequest.getRepeatedPassword())) {
            throw new ApiException("Passwords don't match");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (optionalRole.isEmpty()) {
            throw new ApiException("Employee role not found");
        }

        try {
            Employee employee = employeeMapper.toEntity(employeeRequestDtoRequest);
            employee.setRole(optionalRole.get());
            employee.setPassword(encoder.passwordEncoder().encode(employeeRequestDtoRequest.getPassword()));
            employeeRepository.save(employee);
            return employeeMapper.toDto(employee);
        } catch (Exception exception){
            throw new ApiException("An internal error occurred. Please try again. " + exception.getCause());
        }
    }

    private boolean isEmailInDB(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
