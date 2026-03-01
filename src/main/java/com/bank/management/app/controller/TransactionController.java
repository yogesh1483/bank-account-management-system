package com.bank.management.app.controller;

import com.bank.management.app.dto.RequestDTO.TransactionRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountStatementResponseDTO;
import com.bank.management.app.dto.ResponseDTO.TransactionResonseDTO;
import com.bank.management.app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // -------- Deposit --------
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResonseDTO> deposit(
            @Valid @RequestBody TransactionRequestDTO dto) {

        return ResponseEntity.ok(transactionService.deposite(dto));
    }

    // -------- Withdraw --------
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResonseDTO> withdraw(
            @Valid @RequestBody TransactionRequestDTO dto) {

        return ResponseEntity.ok(transactionService.withdraw(dto));
    }

    // -------- Transaction History --------
    @GetMapping("/statement")
    public ResponseEntity<AccountStatementResponseDTO> getAccountStatement(
            @RequestParam String accountType,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.getAccountStatement(accountType, pageable));
    }
}
