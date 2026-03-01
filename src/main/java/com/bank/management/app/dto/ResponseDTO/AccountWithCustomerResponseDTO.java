package com.bank.management.app.dto.ResponseDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountWithCustomerResponseDTO {
    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String customerName;
}
