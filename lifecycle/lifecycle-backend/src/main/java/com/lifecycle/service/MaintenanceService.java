package com.lifecycle.service;

import com.lifecycle.entity.*;
import com.lifecycle.repository.MaintenanceRepository;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaintenanceService {
    private final MaintenanceRepository repo; private final AssetService assets;
    public MaintenanceService(MaintenanceRepository repo, AssetService assets) { this.repo = repo; this.assets = assets; }

    // General (NOT manufacturer-specific) suggestions by category. Always labelled as such.
    private static final Map<String, List<Object[]>> SUGGESTIONS = Map.of(
        "appliance", List.of(new Object[]{"Clean filters / coils", 90}, new Object[]{"Deep clean and inspection", 365}),
        "electronics", List.of(new Object[]{"Internal dust cleaning", 180}, new Object[]{"Battery health check", 180}),
        "vehicle", List.of(new Object[]{"Engine oil change", 180}, new Object[]{"General servicing", 365}),
        "furniture", List.of(new Object[]{"Inspect joints and fittings", 365}, new Object[]{"Polish and treat surfaces", 365}));

    public List<Map<String, Object>> suggestions(String category) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] s : SUGGESTIONS.getOrDefault(category == null ? "" : category.toLowerCase(), List.of()))
            out.add(Map.of("title", s[0], "frequencyDays", s[1], "source", "General recommendation"));
        return out;
    }

    public MaintenanceSchedule create(Long userId, Long assetId, String title, LocalDate last, int freq, String source) {
        Asset a = assets.owned(userId, assetId);
        MaintenanceSchedule m = new MaintenanceSchedule();
        m.assetId = a.id; m.title = title; m.frequencyDays = freq;
        m.lastServiceDate = last != null ? last : (a.purchaseDate != null ? a.purchaseDate : LocalDate.now());
        m.nextServiceDate = m.lastServiceDate.plusDays(freq);
        if (source != null) m.source = source;
        return repo.save(m);
    }

    public MaintenanceSchedule complete(Long userId, Long id) {
        MaintenanceSchedule m = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        assets.owned(userId, m.assetId);
        m.lastServiceDate = LocalDate.now();
        m.nextServiceDate = m.lastServiceDate.plusDays(m.frequencyDays);
        return repo.save(m);
    }
}
