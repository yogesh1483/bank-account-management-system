package com.bank.management.app.dto.RequestDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    @NotNull
    private String accountType;   // SAVINGS or CURRENT
    @NotNull
    private BigDecimal amount;
}
