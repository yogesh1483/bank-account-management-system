package com.bank.management.app.service;

import com.bank.management.app.dto.RequestDTO.TransactionRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountStatementResponseDTO;
import com.bank.management.app.dto.ResponseDTO.TransactionResonseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResonseDTO deposite(TransactionRequestDTO depositeMoney);

    TransactionResonseDTO withdraw(TransactionRequestDTO withdrawMoney);

    public AccountStatementResponseDTO getAccountStatement(
            String accountTyoe,
            Pageable pageable);
}
