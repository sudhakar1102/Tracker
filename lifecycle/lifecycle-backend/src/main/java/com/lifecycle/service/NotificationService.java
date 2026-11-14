package com.lifecycle.service;

import com.lifecycle.entity.*;
import com.lifecycle.repository.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notes; private final AssetRepository assets;
    private final WarrantyRepository warranties; private final MaintenanceRepository maintenance;

    public NotificationService(NotificationRepository n, AssetRepository a, WarrantyRepository w, MaintenanceRepository m) {
        notes = n; assets = a; warranties = w; maintenance = m;
    }

    /** Creates in-app reminders for warranties expiring within 30 days and maintenance due within 7 days (or overdue). */
    public void generateFor(Long userId) {
        LocalDate today = LocalDate.now();
        for (Asset a : assets.findByUserId(userId)) {
            for (Warranty w : warranties.findByAssetId(a.id)) {
                if (w.expiryDate == null) continue;
                long days = ChronoUnit.DAYS.between(today, w.expiryDate);
                if (days >= 0 && days <= 30)
                    add(userId, "WARRANTY", "Warranty for " + a.productName + " expires on " + w.expiryDate + " (" + days + " days left).");
            }
            for (MaintenanceSchedule m : maintenance.findByAssetId(a.id)) {
                if (m.nextServiceDate == null) continue;
                long days = ChronoUnit.DAYS.between(today, m.nextServiceDate);
                if (days < 0)
                    add(userId, "MAINTENANCE", "Overdue: " + m.title + " for " + a.productName + " was due on " + m.nextServiceDate + ".");
                else if (days <= 7)
                    add(userId, "MAINTENANCE", "Upcoming: " + m.title + " for " + a.productName + " is due on " + m.nextServiceDate + ".");
            }
        }
    }

    private void add(Long userId, String type, String message) {
        if (notes.existsByUserIdAndMessage(userId, message)) return;
        Notification n = new Notification(); n.userId = userId; n.type = type; n.message = message;
        notes.save(n);
    }
}
