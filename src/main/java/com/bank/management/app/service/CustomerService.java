package com.bank.management.app.service;

import com.bank.management.app.dto.RequestDTO.CustomerRequestDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerResponseDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerWithAccountsResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO request);

    CustomerWithAccountsResponseDTO getCustomer(Long id);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request);

    void deleteCustomer(Long id);
}
