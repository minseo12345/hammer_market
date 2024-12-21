//package com.hammer.hammer.notification.controller;
//
//import com.hammer.hammer.notification.entity.Notification;
//import com.hammer.hammer.notification.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//    @MessageMapping("/notify")
//    @SendTo("/topic/notifications")
//    public Notification notifyUser(Notification notification) {
//        // 클라이언트로 받은 알림 객체 처리
////        notificationService.saveNotification(notification);
//        return notification; // 클라이언트로 전송
//    }
//}
