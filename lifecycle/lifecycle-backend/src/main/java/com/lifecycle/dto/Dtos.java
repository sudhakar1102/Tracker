package com.lifecycle.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class Dtos {
    private Dtos() {}
    public record RegisterReq(@NotBlank String name, @Email @NotBlank String email,
                              @Size(min = 6, message = "must be at least 6 characters") String password, String phone) {}
    public record LoginReq(@NotBlank String email, @NotBlank String password) {}
    public record AuthRes(String token, String name) {}
    public record WarrantyReq(@NotNull Long assetId, String warrantyType, LocalDate startDate,
                              @NotNull @Min(1) Integer months, String provider) {}
    public record MaintenanceReq(@NotNull Long assetId, @NotBlank String title, LocalDate lastServiceDate,
                                 @NotNull @Min(1) Integer frequencyDays, String source) {}
    public record ExtractReq(String text) {}
    public record Valuation(BigDecimal purchasePrice, BigDecimal currentValue, BigDecimal depreciation,
                            double ageYears, double annualRate) {}
    public record Recommendation(String verdict, String explanation, BigDecimal repairCost, BigDecimal currentValue,
                                 BigDecimal replacementCost, BigDecimal previousRepairs, double remainingLifeYears,
                                 String disclaimer) {}
    public record CostSummary(BigDecimal purchase, BigDecimal repairs, BigDecimal otherExpenses, BigDecimal total) {}
}
