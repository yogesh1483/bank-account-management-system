package com.bank.management.app.serviceImpl;

import com.bank.management.app.dto.RequestDTO.AccountRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountResponseDTO;
import com.bank.management.app.dto.ResponseDTO.AccountWithCustomerResponseDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerAccountsSummaryDTO;
import com.bank.management.app.entity.Account;
import com.bank.management.app.entity.Customer;
import com.bank.management.app.exception.ResourceNotFoundException;
import com.bank.management.app.repository.AccountRepository;
import com.bank.management.app.repository.CustomerRepository;
import com.bank.management.app.security.SecurityUtils;
import com.bank.management.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO request) {

        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));

        // 🔐 Ownership check
        String loggedInUsername = SecurityUtils.getCurrentUsername();
        if (!customer.getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You cannot create an account for another customer");
        }

        String type = request.getAccountType().toUpperCase();

        // 🏦 Allow only SAVINGS or CURRENT
        if (!type.equals("SAVINGS") && !type.equals("CURRENT")) {
            throw new IllegalArgumentException("Only SAVINGS and CURRENT accounts are allowed");
        }

        // ❌ No duplicate account types
        boolean alreadyExists = customer.getAccounts().stream().anyMatch(acc -> acc.getAccountType().equalsIgnoreCase(type));
        if (alreadyExists) {
            throw new IllegalArgumentException("You already have a " + type + " account");
        }

        // ❌ Max 2 accounts
        if (customer.getAccounts().size() >= 2) {
            throw new IllegalArgumentException("You can only have SAVINGS and CURRENT accounts");
        }

        // ✅ Create account
        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setAccountType(type);
        account.setBalance(request.getInitialBalance());
        account.setCustomer(customer);

        Account saved = accountRepository.save(account);
        return modelMapper.map(saved, AccountResponseDTO.class);
    }


    @Override
    public AccountWithCustomerResponseDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account Not Found"));

        // 🔐 Ownership check
        String loggedInUsername = SecurityUtils.getCurrentUsername();
        if (!account.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You are not allowed to access this account");
        }

        AccountWithCustomerResponseDTO response = modelMapper.map(account, AccountWithCustomerResponseDTO.class);
        response.setCustomerName(account.getCustomer().getFullName());
        return response;
    }

    @Override
    public CustomerAccountsSummaryDTO getAccountByCustomerId(Long customerId) {

        String loggedInUsername = SecurityUtils.getCurrentUsername();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));

        if (!customer.getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You cannot access another customer's accounts");
        }

        List<AccountResponseDTO> accounts = accountRepository.findByCustomerId(customerId).stream().map(acc -> modelMapper.map(acc, AccountResponseDTO.class)).toList();

        CustomerAccountsSummaryDTO response = new CustomerAccountsSummaryDTO();
        response.setCustomerName(customer.getFullName());
        response.setAccounts(accounts);

        return response;
    }


    @Override
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO request) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // 🔐 Ownership check
        String loggedInUsername = SecurityUtils.getCurrentUsername();
        if (!account.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You cannot update another customer's account");
        }

        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());

        return modelMapper.map(accountRepository.save(account), AccountResponseDTO.class);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // 🔐 Ownership check
        String loggedInUsername = SecurityUtils.getCurrentUsername();
        if (!account.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You cannot delete another customer's account");
        }

        accountRepository.delete(account);
    }
}
