package com.lifecycle.controller;

import com.lifecycle.entity.Notification;
import com.lifecycle.repository.NotificationRepository;
import com.lifecycle.service.NotificationService;
import com.lifecycle.utility.CurrentUser;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repo; private final NotificationService service;
    public NotificationController(NotificationRepository r, NotificationService s) { repo = r; service = s; }

    @GetMapping public List<Notification> list() {
        service.generateFor(CurrentUser.id());   // also refreshed daily by ReminderScheduler
        return repo.findByUserIdOrderByCreatedAtDesc(CurrentUser.id());
    }
    @PostMapping("/{id}/read") public void read(@PathVariable Long id) {
        repo.findById(id).filter(n -> n.userId.equals(CurrentUser.id())).ifPresent(n -> { n.read = true; repo.save(n); });
    }
}
