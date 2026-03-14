package com.example.managewallet.controller;

import com.example.managewallet.dto.DashboardSummaryResponse;
import com.example.managewallet.dto.HeatmapPointResponse;
import com.example.managewallet.dto.TopCategoryResponse;
import com.example.managewallet.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api", "/api/dashboard"})
@Tag(name = "Dashboard", description = "Dashboard analytics APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Income vs expense totals and net balance for a month")
    public DashboardSummaryResponse summary(@RequestParam String month) {
        return dashboardService.getSummary(month);
    }

    @GetMapping("/heatmap")
    @Operation(summary = "Daily expense totals for heatmap")
    public List<HeatmapPointResponse> heatmap(@RequestParam String month) {
        return dashboardService.getHeatmap(month);
    }

    @GetMapping({"/expenses/top", "/top-expense-categories"})
    @Operation(summary = "Top 5 expense categories for a month")
    public List<TopCategoryResponse> topExpenseCategories(@RequestParam String month) {
        return dashboardService.getTopExpenseCategories(month);
    }
}

