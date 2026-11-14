package com.lifecycle.controller;

import com.lifecycle.dto.Dtos.MaintenanceReq;
import com.lifecycle.entity.*;
import com.lifecycle.repository.MaintenanceRepository;
import com.lifecycle.service.AssetService;
import com.lifecycle.service.MaintenanceService;
import com.lifecycle.utility.CurrentUser;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class MaintenanceController {
    private final MaintenanceService service; private final MaintenanceRepository repo; private final AssetService assets;
    public MaintenanceController(MaintenanceService s, MaintenanceRepository r, AssetService a) { service = s; repo = r; assets = a; }

    @PostMapping("/maintenance") @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceSchedule create(@Valid @RequestBody MaintenanceReq r) {
        return service.create(CurrentUser.id(), r.assetId(), r.title(), r.lastServiceDate(), r.frequencyDays(), r.source());
    }
    @PostMapping("/maintenance/{id}/complete")
    public MaintenanceSchedule complete(@PathVariable Long id) { return service.complete(CurrentUser.id(), id); }

    @GetMapping("/assets/{id}/maintenance")
    public List<MaintenanceSchedule> forAsset(@PathVariable Long id) {
        assets.owned(CurrentUser.id(), id); return repo.findByAssetId(id);
    }
    @GetMapping("/assets/{id}/maintenance/suggestions")
    public List<Map<String, Object>> suggestions(@PathVariable Long id) {
        return service.suggestions(assets.owned(CurrentUser.id(), id).category);
    }

    /** All schedules for the user, soonest first, each flagged overdue/upcoming. */
    @GetMapping("/maintenance/upcoming")
    public List<Map<String, Object>> upcoming(@RequestParam(defaultValue = "90") int days) {
        LocalDate limit = LocalDate.now().plusDays(days);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Asset a : assets.list(CurrentUser.id(), null))
            for (MaintenanceSchedule m : repo.findByAssetId(a.id))
                if (m.nextServiceDate != null && !m.nextServiceDate.isAfter(limit))
                    out.add(Map.of("id", m.id, "assetId", a.id, "productName", a.productName, "title", m.title,
                            "nextServiceDate", m.nextServiceDate, "overdue", m.nextServiceDate.isBefore(LocalDate.now()), "source", m.source));
        out.sort(Comparator.comparing(x -> (LocalDate) x.get("nextServiceDate")));
        return out;
    }
}
