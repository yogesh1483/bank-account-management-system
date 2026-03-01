package com.bank.management.app.controller;

import com.bank.management.app.dto.RequestDTO.AccountRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountResponseDTO;
import com.bank.management.app.dto.ResponseDTO.AccountWithCustomerResponseDTO;
import com.bank.management.app.dto.ResponseDTO.CustomerAccountsSummaryDTO;
import com.bank.management.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO dto) {

        return new ResponseEntity<>(accountService.createAccount(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountWithCustomerResponseDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerAccountsSummaryDTO> getAccountsByCustomer(@PathVariable Long customerId) {

        return ResponseEntity.ok(accountService.getAccountByCustomerId(customerId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO dto) {

        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

}
