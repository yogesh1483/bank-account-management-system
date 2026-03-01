package com.bank.management.app.dto.ResponseDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountStatementResponseDTO {
    private String customerName;
    private String accountNumber;

    private List<TransactionResonseDTO> transactions;
}
