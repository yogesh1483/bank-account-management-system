package com.bank.management.app.serviceImpl;

import com.bank.management.app.dto.ResponseDTO.DashboardResponseDTO;
import com.bank.management.app.entity.Customer;
import com.bank.management.app.repository.CustomerRepository;
import com.bank.management.app.security.SecurityUtils;
import com.bank.management.app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public DashboardResponseDTO getDashboard() {
        // Get logged-in username
        String username = SecurityUtils.getCurrentUsername();
        // Fetch customer
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        DashboardResponseDTO dto = new DashboardResponseDTO();
        dto.setFullName(customer.getFullName());
        dto.setUsername(customer.getUsername());
        dto.setEmail(customer.getEmail());
        int totalAccounts = customer.getAccounts() != null ? customer.getAccounts().size() : 0;
        BigDecimal totalBalance = customer.getAccounts() != null ?
                customer.getAccounts().stream()
                        .map(account -> account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        dto.setTotalAccounts(totalAccounts);
        dto.setTotalBalance(totalBalance);
        return dto;
    }
}
