package com.hammer.hammer.notification.controller;

import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notification notifyUser(Notification notification) {

        notificationService.saveNotification(notification);
        return notification; // 클라이언트로 전송
    }

    // 알림 목록 가져오기
    @PostMapping("/notifications/list")
    public String getNotificationList(@RequestParam Long userId, Model model) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);
        return "notifications :: #notification-list";
    }

    // 거래포기 처리
    @PostMapping("/transaction/cancel")
    public String cancelTransaction(@RequestParam Long transactionId, @RequestParam Long userId, Model model) {
        notificationService.handleTransactionCancel(transactionId, userId);

        // 거래포기 알림 데이터 추가
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);

        return "notifications :: #notification-list";
    }

    // 거래완료 처리
    @PostMapping("/transaction/complete")
    public String completeTransaction(@RequestParam Long transactionId, @RequestParam Long userId, Model model) {
        notificationService.handleTransactionComplete(transactionId, userId);

        // 거래완료 알림 데이터 추가
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);

        return "notifications :: #notification-list";
    }

    // 알림 화면 렌더링
    @GetMapping("/notifications")
    public String renderNotificationsPage(Model model) {
        return "notifications";
    }
}
