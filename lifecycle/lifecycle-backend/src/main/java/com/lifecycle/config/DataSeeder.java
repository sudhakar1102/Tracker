package com.lifecycle.config;

import com.lifecycle.entity.*;
import com.lifecycle.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates a SAMPLE demo account (demo@lifecycle.app / demo1234). Product names are prefixed "Sample" so they are never mistaken for real data. */
@Component
@ConditionalOnProperty(name = "app.seed", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users; private final AssetRepository assets; private final WarrantyRepository warranties;
    private final MaintenanceRepository maintenance; private final ServiceRecordRepository services; private final PasswordEncoder enc;

    public DataSeeder(UserRepository u, AssetRepository a, WarrantyRepository w, MaintenanceRepository m, ServiceRecordRepository s, PasswordEncoder e) {
        users = u; assets = a; warranties = w; maintenance = m; services = s; enc = e;
    }

    @Override public void run(String... args) {
        if (users.findByEmail("demo@lifecycle.app").isPresent()) return;
        User u = new User(); u.name = "Demo User"; u.email = "demo@lifecycle.app"; u.password = enc.encode("demo1234"); users.save(u);
        LocalDate today = LocalDate.now();

        Asset fridge = asset(u, "Sample Refrigerator", "Appliance", "Samsung", "RT28", today.minusYears(3), 28000, 32000, "Good", 12);
        Warranty w = new Warranty(); w.assetId = fridge.id; w.warrantyType = "Manufacturer"; w.startDate = fridge.purchaseDate;
        w.expiryDate = today.plusDays(20); w.provider = "Samsung"; warranties.save(w);
        ServiceRecord s = new ServiceRecord(); s.assetId = fridge.id; s.serviceDate = today.minusMonths(8); s.serviceType = "Gas refilling";
        s.serviceProvider = "Local service centre"; s.repairCost = new BigDecimal("2000"); services.save(s);
        MaintenanceSchedule m = new MaintenanceSchedule(); m.assetId = fridge.id; m.title = "Clean condenser coils"; m.frequencyDays = 180;
        m.lastServiceDate = today.minusDays(175); m.nextServiceDate = today.plusDays(5); m.source = "General recommendation"; maintenance.save(m);

        Asset laptop = asset(u, "Sample Laptop", "Electronics", "Dell", "Inspiron 15", today.minusYears(1), 55000, 58000, "Excellent", 5);
        Warranty w2 = new Warranty(); w2.assetId = laptop.id; w2.warrantyType = "Manufacturer"; w2.startDate = laptop.purchaseDate;
        w2.expiryDate = laptop.purchaseDate.plusYears(2); w2.provider = "Dell"; warranties.save(w2);
    }

    private Asset asset(User u, String name, String cat, String brand, String model, LocalDate date, int price, int repl, String cond, int life) {
        Asset a = new Asset(); a.userId = u.id; a.productName = name; a.category = cat; a.brand = brand; a.model = model;
        a.purchaseDate = date; a.purchasePrice = BigDecimal.valueOf(price); a.replacementCost = BigDecimal.valueOf(repl);
        a.condition = cond; a.usefulLifeYears = life; return assets.save(a);
    }
}
