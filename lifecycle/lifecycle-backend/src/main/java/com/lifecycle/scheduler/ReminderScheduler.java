package com.lifecycle.scheduler;

import com.lifecycle.entity.User;
import com.lifecycle.repository.UserRepository;
import com.lifecycle.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {
    private final UserRepository users; private final NotificationService notifications;
    public ReminderScheduler(UserRepository u, NotificationService n) { users = u; notifications = n; }

    @Scheduled(cron = "0 0 8 * * *")   // every day at 08:00
    public void dailyReminders() { for (User u : users.findAll()) notifications.generateFor(u.id); }
}
