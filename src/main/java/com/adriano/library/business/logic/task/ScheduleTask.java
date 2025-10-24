package com.adriano.library.business.logic.task;

import com.adriano.library.business.logic.service.EmailService;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    private final UserService userService;
    private final EmailService emailService;
    private final String zone = "America/Argentina/Buenos_Aires";

    public ScheduleTask(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0 15 5 1/1 ?", zone = zone)
    public void schedulePersonSaleEmailTask() {
        userService.findAll().forEach(user -> {
            try {
                emailService.sendSale(
                        user.getEmail(),
                        "Sale!",
                        user.getName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
