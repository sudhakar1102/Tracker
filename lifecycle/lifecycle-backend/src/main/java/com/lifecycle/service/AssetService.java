package com.lifecycle.service;

import com.lifecycle.dto.Dtos.CostSummary;
import com.lifecycle.entity.*;
import com.lifecycle.repository.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssetService {
    private final AssetRepository assets; private final WarrantyRepository warranties;
    private final MaintenanceRepository maintenance; private final ServiceRecordRepository services;
    private final ExpenseRepository expenses;

    public AssetService(AssetRepository a, WarrantyRepository w, MaintenanceRepository m,
                        ServiceRecordRepository s, ExpenseRepository e) {
        assets = a; warranties = w; maintenance = m; services = s; expenses = e;
    }

    /** Central ownership check: every child-record endpoint goes through here. */
    public Asset owned(Long userId, Long assetId) {
        Asset a = assets.findById(assetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!a.userId.equals(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        return a;
    }

    public List<Asset> list(Long userId, String q) {
        List<Asset> all = assets.findByUserId(userId);
        if (q == null || q.isBlank()) return all;
        String s = q.toLowerCase(Locale.ROOT);
        return all.stream().filter(a -> contains(a.productName, s) || contains(a.brand, s) || contains(a.category, s)).toList();
    }
    private boolean contains(String v, String s) { return v != null && v.toLowerCase(Locale.ROOT).contains(s); }

    public Asset create(Long userId, Asset a) {
        if (a.productName == null || a.productName.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is required");
        a.id = null; a.userId = userId;
        return assets.save(a);
    }

    public Asset update(Long userId, Long id, Asset in) {
        Asset a = owned(userId, id);
        in.id = a.id; in.userId = userId;
        return assets.save(in);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Asset a = owned(userId, id);
        warranties.deleteByAssetId(id); maintenance.deleteByAssetId(id);
        services.deleteByAssetId(id); expenses.deleteByAssetId(id);
        assets.delete(a);
    }

    public BigDecimal repairsTotal(Long assetId) {
        return services.findByAssetIdOrderByServiceDateDesc(assetId).stream()
                .map(r -> r.repairCost == null ? BigDecimal.ZERO : r.repairCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CostSummary costs(Asset a) {
        BigDecimal purchase = a.purchasePrice == null ? BigDecimal.ZERO : a.purchasePrice;
        BigDecimal repairs = repairsTotal(a.id);
        BigDecimal other = expenses.findByAssetIdOrderByExpenseDateDesc(a.id).stream()
                .map(e -> e.amount == null ? BigDecimal.ZERO : e.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CostSummary(purchase, repairs, other, purchase.add(repairs).add(other));
    }
}
