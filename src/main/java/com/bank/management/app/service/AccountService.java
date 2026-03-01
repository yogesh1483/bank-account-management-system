package com.bank.management.app.service;

import com.bank.management.app.dto.RequestDTO.AccountRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountResponseDTO;
import com.bank.management.app.dto.ResponseDTO.AccountWithCustomerResponseDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerAccountsSummaryDTO;

import java.util.List;

public interface AccountService {
    AccountResponseDTO createAccount(AccountRequestDTO request);

    AccountWithCustomerResponseDTO getAccountById(Long id);

    CustomerAccountsSummaryDTO getAccountByCustomerId(Long customerId);

    AccountResponseDTO updateAccount(Long id, AccountRequestDTO request);

    void deleteAccount(Long id);

}
