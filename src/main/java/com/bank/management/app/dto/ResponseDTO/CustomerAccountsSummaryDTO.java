package com.bank.management.app.dto.ResponseDTO;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountsSummaryDTO {
    private String customerName;
    private List<AccountResponseDTO> accounts;
}
