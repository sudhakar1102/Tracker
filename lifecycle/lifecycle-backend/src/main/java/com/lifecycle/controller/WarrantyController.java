
package com.lifecycle.controller;

import com.lifecycle.dto.Dtos.WarrantyReq;
import com.lifecycle.entity.*;
import com.lifecycle.repository.WarrantyRepository;
import com.lifecycle.service.AssetService;
import com.lifecycle.utility.CurrentUser;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WarrantyController {

    private final WarrantyRepository repo;
    private final AssetService assets;

    public WarrantyController(WarrantyRepository r, AssetService a) {
        repo = r;
        assets = a;
    }

    @PostMapping("/warranties")
    @ResponseStatus(HttpStatus.CREATED)
    public Warranty add(@Valid @RequestBody WarrantyReq r) {

        Asset a = assets.owned(CurrentUser.id(), r.assetId());

        Warranty w = new Warranty();

        w.assetId = a.id;
        w.warrantyType = r.warrantyType() == null ? "Manufacturer" : r.warrantyType();
        w.provider = r.provider();

        if ("Extended".equalsIgnoreCase(w.warrantyType)) {

            LocalDate existingEndDate = repo.findByAssetId(a.id)
                    .stream()
                    .map(existing -> existing.expiryDate)
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No existing warranty found for extension"
                    ));

            w.startDate = existingEndDate.plusDays(1);

        } else {

            w.startDate = r.startDate() != null
                    ? r.startDate()
                    : (a.purchaseDate != null ? a.purchaseDate : LocalDate.now());
        }

        w.expiryDate = w.startDate.plusMonths(r.months());

        return repo.save(w);
    }

    @GetMapping("/assets/{id}/warranty")
    public List<Warranty> forAsset(@PathVariable Long id) {
        assets.owned(CurrentUser.id(), id);
        return repo.findByAssetId(id);
    }

    @GetMapping("/warranties/expiring")
    public List<Map<String, Object>> expiring(@RequestParam(defaultValue = "30") int days) {
        LocalDate limit = LocalDate.now().plusDays(days);
        List<Map<String, Object>> out = new ArrayList<>();

        for (Asset a : assets.list(CurrentUser.id(), null))
            for (Warranty w : repo.findByAssetId(a.id))
                if (w.expiryDate != null && !w.expiryDate.isBefore(LocalDate.now()) && !w.expiryDate.isAfter(limit))
                    out.add(Map.of("assetId", a.id, "productName", a.productName, "expiryDate", w.expiryDate));

        return out;
    }

    @DeleteMapping("/warranties/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.findById(id).ifPresent(w -> {
            assets.owned(CurrentUser.id(), w.assetId);
            repo.delete(w);
        });
    }
}
