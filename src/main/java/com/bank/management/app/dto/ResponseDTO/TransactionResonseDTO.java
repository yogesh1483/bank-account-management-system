package com.bank.management.app.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResonseDTO {
    private Long id;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;
}
