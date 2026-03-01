package com.bank.management.app.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWithAccountsResponseDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private List<CustomerAccountDTO> accounts;
}
