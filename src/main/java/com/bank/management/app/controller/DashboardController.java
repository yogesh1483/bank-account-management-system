package com.bank.management.app.controller;

import com.bank.management.app.dto.ResponseDTO.DashboardResponseDTO;
import com.bank.management.app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public DashboardResponseDTO getDashboard() {
        return dashboardService.getDashboard();
    }
}
