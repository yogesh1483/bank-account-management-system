package com.bank.management.app.serviceImpl;

import com.bank.management.app.dto.RequestDTO.CustomerRequestDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerAccountDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerWithAccountsResponseDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerResponseDTO;
import com.bank.management.app.entity.Customer;
import com.bank.management.app.exception.ResourceNotFoundException;
import com.bank.management.app.repository.CustomerRepository;
import com.bank.management.app.security.SecurityUtils;
import com.bank.management.app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {

        if(!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException(
                    "Only ADMIN can create customers");
        }

        if(dto.getRole() == null) {
            dto.setRole("CUSTOMER");
        }

        Customer customer = new Customer();

        customer.setUsername(dto.getUsername());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(
                passwordEncoder.encode(dto.getPassword()));
        customer.setRole(dto.getRole());

        customerRepository.save(customer);

        return modelMapper.map(customer,
                CustomerResponseDTO.class);
    }

    @Override
    public CustomerWithAccountsResponseDTO getCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer Not Found"));

        String loggedUsername = SecurityUtils.getCurrentUsername();

        if(SecurityUtils.isCustomer() &&
                !customer.getUsername().equals(loggedUsername)) {

            throw new AccessDeniedException(
                    "You can only view your own account");
        }

        CustomerWithAccountsResponseDTO response =
                modelMapper.map(customer,
                        CustomerWithAccountsResponseDTO.class);

        List<CustomerAccountDTO> accounts =
                customer.getAccounts().stream().map(acc -> {

                    CustomerAccountDTO dto =
                            new CustomerAccountDTO();

                    dto.setAccountNumber(acc.getAccountNumber());
                    dto.setBalance(acc.getBalance());
                    dto.setAccountType(acc.getAccountType());

                    return dto;

                }).toList();

        response.setAccounts(accounts);

        return response;
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {

        String username = SecurityUtils.getCurrentUsername();

        // ADMIN → see all
        if(SecurityUtils.isAdmin()) {

            return customerRepository.findAll()
                    .stream()
                    .map(c -> modelMapper.map(
                            c,
                            CustomerResponseDTO.class))
                    .toList();
        }

        // CUSTOMER → only self

        Customer customer =
                customerRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer Not Found"));

        return List.of(
                modelMapper.map(customer,
                        CustomerResponseDTO.class));
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id,
                                              CustomerRequestDTO request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String role = SecurityUtils.getCurrentUserRole();
        String username = SecurityUtils.getCurrentUsername();

        // CUSTOMER → only self
        if(role.equals("ROLE_CUSTOMER") &&
                !customer.getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You cannot update another customer");
        }

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());

        return modelMapper.map(
                customerRepository.save(customer),
                CustomerResponseDTO.class);
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String role = SecurityUtils.getCurrentUserRole();
        String username = SecurityUtils.getCurrentUsername();

        // CUSTOMER → only self
        if(role.equals("ROLE_CUSTOMER") &&
                !customer.getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You cannot delete another customer");
        }

        customerRepository.delete(customer);
    }
}
