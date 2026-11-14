package com.lifecycle.controller;

import com.lifecycle.dto.Dtos.*;
import com.lifecycle.entity.Asset;
import com.lifecycle.service.AssetService;
import com.lifecycle.service.ValuationService;
import com.lifecycle.utility.CurrentUser;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/assets")
public class AssetController {
    private final AssetService assets; private final ValuationService valuation;
    public AssetController(AssetService a, ValuationService v) { assets = a; valuation = v; }

    @GetMapping public List<Asset> list(@RequestParam(required = false) String q) { return assets.list(CurrentUser.id(), q); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Asset create(@RequestBody Asset a) { return assets.create(CurrentUser.id(), a); }
    @GetMapping("/{id}") public Asset get(@PathVariable Long id) { return assets.owned(CurrentUser.id(), id); }
    @PutMapping("/{id}") public Asset update(@PathVariable Long id, @RequestBody Asset a) { return assets.update(CurrentUser.id(), id, a); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { assets.delete(CurrentUser.id(), id); }

    @GetMapping("/{id}/valuation") public Valuation valuation(@PathVariable Long id) {
        return valuation.estimate(assets.owned(CurrentUser.id(), id));
    }
    @GetMapping("/{id}/costs") public CostSummary costs(@PathVariable Long id) {
        return assets.costs(assets.owned(CurrentUser.id(), id));
    }
    @GetMapping("/{id}/recommendation") public Recommendation recommendation(@PathVariable Long id, @RequestParam BigDecimal repairCost) {
        return valuation.recommend(assets.owned(CurrentUser.id(), id), repairCost);
    }
}
