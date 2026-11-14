package com.lifecycle.service;

import com.lifecycle.dto.Dtos.*;
import com.lifecycle.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/** Depreciation + repair-vs-replace decision support. Rules are configurable in application.properties. */
@Service
public class ValuationService {
    private final Environment env; private final AssetService assets;
    public ValuationService(Environment env, AssetService assets) { this.env = env; this.assets = assets; }

    private double rateFor(String category) {
        double def = env.getProperty("app.depreciation.default", Double.class, 0.12);
        if (category == null) return def;
        return env.getProperty("app.depreciation." + category.toLowerCase(), Double.class, def);
    }
    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
    private BigDecimal money(double v) { return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP); }

    private double ageYears(Asset a) {
        if (a.purchaseDate == null) return 0;
        return Math.max(0, ChronoUnit.DAYS.between(a.purchaseDate, LocalDate.now()) / 365.25);
    }

    public Valuation estimate(Asset a) {
        BigDecimal price = a.purchasePrice == null ? BigDecimal.ZERO : a.purchasePrice;
        double age = ageYears(a), rate = rateFor(a.category);
        double factor = switch (a.condition == null ? "good" : a.condition.toLowerCase()) {
            case "excellent" -> 1.0; case "fair" -> 0.7; case "poor" -> 0.5; default -> 0.9; };
        double v = Math.max(price.doubleValue() * Math.pow(1 - rate, age) * factor, price.doubleValue() * 0.05);
        BigDecimal cur = money(v);
        return new Valuation(price, cur, price.subtract(cur), round(age), rate);
    }

    public Recommendation recommend(Asset a, BigDecimal repairCost) {
        Valuation val = estimate(a);
        BigDecimal replacement = a.replacementCost != null ? a.replacementCost
                : (a.purchasePrice == null ? BigDecimal.ZERO : a.purchasePrice);
        BigDecimal prior = assets.repairsTotal(a.id);
        double life = a.usefulLifeYears == null ? 8 : a.usefulLifeYears;
        double remaining = Math.max(0, life - val.ageYears());
        double repair = repairCost.doubleValue(), repl = replacement.doubleValue();
        double ratio = repl > 0 ? repair / repl : 0;
        double cumulative = repl > 0 ? (prior.doubleValue() + repair) / repl : 0;

        String verdict, why;
        if (repl <= 0) {
            verdict = "Not enough data";
            why = "Add a replacement cost to this product to enable the comparison.";
        } else if (ratio >= 0.5) {
            verdict = "Consider replacing";
            why = "This repair costs " + pct(ratio) + " of a new replacement, which is above the usual 50% threshold.";
        } else if (cumulative >= 0.6) {
            verdict = "Consider replacing";
            why = "Previous repairs plus this one total " + pct(cumulative) + " of the replacement cost, so ongoing repair spend is adding up.";
        } else if (remaining <= 1 && repair > val.currentValue().doubleValue()) {
            verdict = "Consider replacing";
            why = "The product is near the end of its estimated useful life and the repair costs more than its estimated current value.";
        } else {
            verdict = "Consider repairing";
            why = "This repair costs " + pct(ratio) + " of a new replacement and the product has an estimated " + round(remaining) + " years of useful life left.";
        }
        return new Recommendation(verdict, why, repairCost, val.currentValue(), replacement, prior, round(remaining),
                "This is a financial decision-support estimate based on your records and configurable rules, not a guarantee of product reliability.");
    }
    private String pct(double r) { return Math.round(r * 100) + "%"; }
}
