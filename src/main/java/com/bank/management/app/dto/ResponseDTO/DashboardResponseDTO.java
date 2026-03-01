package com.bank.management.app.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {
    private String fullName;
    private String username;
    private String email;
    private int totalAccounts;
    private BigDecimal totalBalance;
}
