package com.toyproject.authsystem.controller;

import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.Message;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.ChatService;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired private SimpMessagingTemplate template;
    @Autowired private UserService userService;
    @Autowired private ChatService chatService;


    // Create a new chat room with the friend
    @PostMapping("/{friendName}")
    public ResponseEntity<?> createChatRoom(@PathVariable String friendName, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        try {
            ChatRoom chatRoom = chatService.createChatRoom(user.getEmail(), friendName);
            return ResponseEntity.ok(chatRoom);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<?> getChatRooms(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }
        log.info("whatthefuck");
        try {
            List<ChatRoom> chatRooms = chatService.getChatRooms(user.getEmail());
            log.info(chatRooms.toString());
            return ResponseEntity.ok(chatRooms);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Send a message in the chat room
    @MessageMapping("/chat/{chatroomId}/sendMessage")
    public void sendMessage(@DestinationVariable Long chatroomId,
                            @Payload Message message,
                            SimpMessageHeaderAccessor headerAccessor, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if(session == null){
            throw new IllegalStateException("세션이 없습니다.");
        }

        User user = (User) session.getAttribute("user");

        if(user == null){
            throw new IllegalStateException("로그인 상태가 아닙니다.");
        }

        message.setSender(user);

        template.convertAndSend("/topic/chatrooms/" + chatroomId, message);
    }
}
