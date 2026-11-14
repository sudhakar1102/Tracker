package com.lifecycle.controller;

import com.lifecycle.entity.*;
import com.lifecycle.repository.*;
import com.lifecycle.service.AssetService;
import com.lifecycle.utility.CurrentUser;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class ServiceExpenseController {
    private final ServiceRecordRepository services; private final ExpenseRepository expenses; private final AssetService assets;
    public ServiceExpenseController(ServiceRecordRepository s, ExpenseRepository e, AssetService a) { services = s; expenses = e; assets = a; }

    @PostMapping("/services") @ResponseStatus(HttpStatus.CREATED)
    public ServiceRecord addService(@RequestBody ServiceRecord r) {
        assets.owned(CurrentUser.id(), r.assetId); r.id = null; return services.save(r);
    }
    @GetMapping("/assets/{id}/services")
    public List<ServiceRecord> serviceHistory(@PathVariable Long id) {
        assets.owned(CurrentUser.id(), id); return services.findByAssetIdOrderByServiceDateDesc(id);
    }
    @PostMapping("/expenses") @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@RequestBody Expense e) {
        assets.owned(CurrentUser.id(), e.assetId); e.id = null; return expenses.save(e);
    }
    @GetMapping("/assets/{id}/expenses")
    public List<Expense> expenseHistory(@PathVariable Long id) {
        assets.owned(CurrentUser.id(), id); return expenses.findByAssetIdOrderByExpenseDateDesc(id);
    }
}
