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
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> getNotificationsAndSend(@RequestBody Map<String, Object> params) {
        // userId 변환
        Long userId = Long.valueOf(params.get("userId").toString());
        System.out.println("Request Params: " + params);
        // userId를 이용해 알림 목록 가져오기
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

        // 알림 데이터를 응답 형태로 변환
        List<Map<String, Object>> response = notifications.stream()
                .map(notification -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("notificationId", notification.getNotificationId());
                    map.put("userId", notification.getUserId());
                    map.put("itemId", notification.getItemId());
                    map.put("itemStatus", notification.getItemStatus()); // 동적 상태 대신 고정된 상태 반환
                    map.put("message", notification.getMessage());
                    map.put("isRead", notification.isRead());
                    map.put("createdAt", notification.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        // WebSocket으로 알림 전송
        notificationService.sendNotificationsToClient(userId);

        // 디버깅용 로그 출력
        System.out.println("Response Data: " + response);

        return response;
    }



    @PostMapping("/notifications/update-read-status")
    public ResponseEntity<Void> updateReadStatus(
            @RequestParam Long notificationId,
            @RequestParam boolean isRead) {
        notificationService.updateReadStatus(notificationId, isRead);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transaction/complete")
    public String completeTransaction(@RequestParam Long itemId, @RequestParam Long userId, Model model) {
        try {
            notificationService.handleTransactionComplete(itemId, userId);
            System.out.println("handleTransactionComplete 실행 성공");
        } catch (Exception e) {
            System.err.println("handleTransactionComplete 실행 중 예외 발생:");
            e.printStackTrace();
            return "error";
        }
        // 거래 완료 알림 데이터 추가
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);

        return "notifications :: #notification-list";
    }

    @PostMapping("/transaction/cancel")
    public String cancelTransaction(@RequestParam Long itemId, @RequestParam Long userId, Model model) {
        notificationService.handleTransactionCancel(itemId, userId);

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
