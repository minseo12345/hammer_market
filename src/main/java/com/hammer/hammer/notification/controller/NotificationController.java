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
import java.util.Map;

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
    public List<Notification> getNotifications(@RequestBody HashMap<String, Object> params, Model model) {
        Long userId = (params.get("userId") instanceof Number)
                ? ((Number) params.get("userId")).longValue()
                : Long.valueOf(params.get("userId").toString());

        // userId를 이용해 알림 목록 가져오기
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        System.out.println(notifications);
        model.addAttribute("notifications", notifications);
        return notifications;
    }

    /*@PostMapping("/notifications/list")
    @ResponseBody
    public List<Notification> getNotifications(@RequestBody HashMap<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        return notificationService.getNotificationsByUserId(userId);
    }*/

    @PostMapping("/notifications/unread")
    @ResponseBody
    public List<Notification> getUnreadNotifications(@RequestParam Long userId) {
        return notificationService.getUnreadNotificationsByUserId(userId);
    }

    @PostMapping("/notifications/mark-read")
    @ResponseBody
    public ResponseEntity<String> markAllNotificationsAsRead(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        notificationService.markNotificationsAsRead(userId);
        // WebSocket을 통해 클라이언트에 알림
        messagingTemplate.convertAndSend("/topic/notifications", "updated");
        return ResponseEntity.ok("success");
    }

    /*@PostMapping("/notifications/mark-read")
    @ResponseBody
    public ResponseEntity<String> markAllNotificationsAsRead(@RequestParam Long userId) {
        notificationService.markNotificationsAsRead(userId);
        messagingTemplate.convertAndSend("/topic/notifications", "updated");
        return ResponseEntity.ok("success");
    }*/

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
