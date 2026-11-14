package com.lifecycle.controller;

import com.lifecycle.service.DashboardService;
import com.lifecycle.utility.CurrentUser;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboard;
    public DashboardController(DashboardService d) { dashboard = d; }
    @GetMapping("/dashboard/summary") public Map<String, Object> summary() { return dashboard.summary(CurrentUser.id()); }
}
