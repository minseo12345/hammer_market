package com.hammer.hammer.notification.controller;

import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notification notifyUser(Notification notification) {
        return notification; // 클라이언트로 전송
    }

    // 알림 목록 가져오기
    @PostMapping("/notifications/list")
    @ResponseBody
    public List<Notification> getNotificationsAndSend(@RequestBody HashMap<String, Object> params, Model model) {
        Long userId = (params.get("userId") instanceof Number)
                ? ((Number) params.get("userId")).longValue()
                : Long.valueOf(params.get("userId").toString());

        // userId를 이용해 알림 목록 가져오기
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

        model.addAttribute("notifications", notifications);
        notificationService.sendNotificationsToClient(userId);
        return notifications;
    }

    @PostMapping("/notifications/update-read-status")
    public ResponseEntity<Void> updateReadStatus(
            @RequestParam Long notificationId,
            @RequestParam boolean isRead) {
        notificationService.updateReadStatus(notificationId, isRead);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transaction/complete")
    public String completeTransaction(@RequestParam Long transactionId, @RequestParam Long userId, Model model) {
        notificationService.handleTransactionComplete(transactionId, userId);
        // 거래 완료 알림 데이터 추가
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);

        return "notifications :: #notification-list";
    }

    @PostMapping("/transaction/cancel")
    public String cancelTransaction(@RequestParam Long transactionId, @RequestParam Long userId, Model model) {
        notificationService.handleTransactionCancel(transactionId, userId);

        // 거래 포기 알림 데이터 추가
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
