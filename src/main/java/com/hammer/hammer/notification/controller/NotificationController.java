package com.hammer.hammer.notification.controller;

import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notification notifyUser(Notification notification) {
        //클라이언트로 받은 알림 객체 처리
        notificationService.saveNotification(notification);
        return notification; // 클라이언트로 전송
    }

    // 거래포기 처리
    @PostMapping("/transaction/cancel")
    public String cancelTransaction(@RequestParam Long transactionId, @RequestParam Long userId) {
        notificationService.handleTransactionCancel(transactionId, userId);
        return "거래가 성공적으로 취소되었습니다.";
    }

    // 거래완료 처리
    @PostMapping("/transaction/complete")
    public String completeTransaction(@RequestParam Long transactionId, @RequestParam Long userId) {
        notificationService.handleTransactionComplete(transactionId, userId);
        return "거래가 성공적으로 완료되었습니다.";
    }
}
