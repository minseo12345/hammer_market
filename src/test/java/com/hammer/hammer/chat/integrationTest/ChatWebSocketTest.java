//package com.hammer.hammer.chat.integrationTest;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//
//import java.lang.reflect.Type;
//import java.net.URI;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//import static org.aspectj.bridge.MessageUtil.fail;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class ChatWebSocketTest {
//    @LocalServerPort
//    private int port;
//
//    private static final String WEBSOCKET_PATH = "/ws";  // 서버 URL
//
//    @Test
//    public void testSendMessage() throws InterruptedException, ExecutionException, TimeoutException {
//        String WEBSOCKET_URL = "ws://localhost:" + port + WEBSOCKET_PATH;
//        WebSocketClient client = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//
//        URI url = URI.create(WEBSOCKET_URL); // URI 객체로 생성
//
//        // 연결 완료를 비동기적으로 기다리기 위한 CompletableFuture 사용
//        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();
//
//        // 새로운 WebSocket 연결 설정
//        stompClient.connectAsync(url.toString(), new StompSessionHandlerAdapter() {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                // 연결 후 실행되는 콜백
//                sessionFuture.complete(session);
//            }
//
//            @Override
//            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
//                sessionFuture.completeExceptionally(exception);
//            }
//
//            @Override
//            public void handleTransportError(StompSession session, Throwable exception) {
//                sessionFuture.completeExceptionally(exception);
//            }
//        });
//
//        // 연결 대기
//        StompSession stompSession = sessionFuture.get(5, TimeUnit.SECONDS);
//
//        // 메시지를 보내는 CompletableFuture
//        String messageToSend = "Hello, world!";
//
//        // 메시지 보내기
//        try {
//            stompSession.send("/app/chat", messageToSend.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Failed to send message");
//        }
//
//        // 수신할 메시지 구독 설정
//        CompletableFuture<String> receivedMessageFuture = new CompletableFuture<>();
//        stompSession.subscribe("/topic/messages", new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders stompHeaders) {
//                return String.class; // 수신 메시지 타입 지정
//            }
//
//            @Override
//            public void handleFrame(StompHeaders stompHeaders, Object payload) {
//                String receivedMessage = (String) payload;
//                System.out.println("Received message: " + receivedMessage);
//                receivedMessageFuture.complete(receivedMessage); // 메시지 수신 후 완료
//            }
//        });
//
//        // 메시지를 수신할 때까지 기다림
//        String receivedMessage = receivedMessageFuture.get(5, TimeUnit.SECONDS);
//
//        // 보내고 받은 메시지 비교
//        assert messageToSend.equals(receivedMessage);
//
//        // 테스트 완료 후 WebSocket 연결 종료
//        stompSession.disconnect();
//    }
//}
//
