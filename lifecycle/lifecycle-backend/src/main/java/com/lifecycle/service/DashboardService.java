package com.lifecycle.service;

import com.lifecycle.dto.Dtos.CostSummary;
import com.lifecycle.entity.*;
import com.lifecycle.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final AssetService assetService; private final WarrantyRepository warranties; private final MaintenanceRepository maintenance;

    public DashboardService(AssetService as, WarrantyRepository w, MaintenanceRepository m) {
        assetService = as; warranties = w; maintenance = m;
    }

    public Map<String, Object> summary(Long userId) {
        LocalDate today = LocalDate.now();
        int active = 0, expiring = 0, upcoming = 0, overdue = 0;
        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new TreeMap<>();
        List<Map<String, Object>> byProduct = new ArrayList<>();
        List<Asset> list = assetService.list(userId, null);

        for (Asset a : list) {
            CostSummary c = assetService.costs(a);
            total = total.add(c.total());
            byCategory.merge(a.category == null ? "Other" : a.category, c.total(), BigDecimal::add);
            byProduct.add(Map.of("name", a.productName, "purchase", c.purchase(),
                    "running", c.repairs().add(c.otherExpenses()), "total", c.total()));
            for (Warranty w : warranties.findByAssetId(a.id)) {
                if (w.expiryDate == null || w.expiryDate.isBefore(today)) continue;
                active++;
                if (!w.expiryDate.isAfter(today.plusDays(30))) expiring++;
            }
            for (MaintenanceSchedule m : maintenance.findByAssetId(a.id)) {
                if (m.nextServiceDate == null) continue;
                if (m.nextServiceDate.isBefore(today)) overdue++;
                else if (!m.nextServiceDate.isAfter(today.plusDays(30))) upcoming++;
            }
        }
        byProduct.sort((x, y) -> ((BigDecimal) y.get("total")).compareTo((BigDecimal) x.get("total")));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalProducts", list.size());
        out.put("activeWarranties", active);
        out.put("expiringWarranties", expiring);
        out.put("upcomingMaintenance", upcoming);
        out.put("overdueMaintenance", overdue);
        out.put("totalOwnershipCost", total);
        out.put("costByCategory", byCategory);
        out.put("costByProduct", byProduct.stream().limit(8).toList());
        return out;
    }
}
