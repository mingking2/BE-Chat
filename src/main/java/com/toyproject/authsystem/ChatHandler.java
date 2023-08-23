package com.toyproject.authsystem;


import com.toyproject.authsystem.domain.entity.Chat;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.ChatRepository;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    private Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    @Autowired
    private ChatRepository chatRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = (User) session.getAttributes().get("user");

        if(user == null){
            log.error("User is not logged in.");
            throw new RuntimeException("User is not logged in.");
        }

        String userMessage = String.format("유저 %s: %s", user.getId(), message.getPayload());
        log.info(userMessage);

        chatRepository.save(new Chat(user, message.getPayload(), LocalDateTime.now())); // DB에 채팅내용 저장

        for (WebSocketSession s : sessions) {
            if (!s.equals(session) && s.isOpen()) {
                s.sendMessage(new TextMessage(userMessage));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("새로운 사용자 연결: " + session.getId());
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("사용자 연결 종료: " + session.getId());
    }
}
