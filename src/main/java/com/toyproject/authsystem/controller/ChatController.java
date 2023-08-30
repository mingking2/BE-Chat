package com.toyproject.authsystem.controller;

import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.Message;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired private SimpMessagingTemplate template;
    @Autowired private UserRepository userRepository;

    @MessageMapping("/chat/{chatroomId}/sendMessage")
    public void sendMessage(@DestinationVariable Long chatroomId,
                            @Payload Message message,
                            SimpMessageHeaderAccessor headerAccessor) {
        String email = (String) headerAccessor.getSessionAttributes().get("email");
        User user = userRepository.findByEmail(email);
        ChatRoom chatroom = user.getChatRooms().stream()
                .filter(room -> room.getId().equals(chatroomId))
                .findFirst()
                .orElse(null);

        if (chatroom == null) {
            // Handle the case where the user is not a member of the chatroom.
            return;
        }

        message.setSender(user);
        message.setChatRoom(chatroom);

        template.convertAndSend("/topic/chatrooms/" + chatroomId, message);
    }

    @MessageMapping("/chat/{chatroomId}/addUser")
    public void addUser(@DestinationVariable Long chatroomId,
                        SimpMessageHeaderAccessor headerAccessor) {
        String email = (String) headerAccessor.getSessionAttributes().get("email");
        User user = userRepository.findByEmail(email);

        if (!user.getChatRooms().stream()
                .anyMatch(chatRoom -> chatRoom.getId().equals(chatroomId))) {
            // Add the room to the user's room list and save.
            ChatRoom chatroom = new ChatRoom();
            chatroom.setId(chatroomId);
            user.getChatRooms().add(chatroom);
            userRepository.save(user);
        }

        // Notify all clients of the new user.
        Message joinMsg = new Message();
        joinMsg.setSender(user);
        ChatRoom chatroom = new ChatRoom();
        chatroom.setId(chatroomId);
        joinMsg.setChatRoom(chatroom);
        template.convertAndSend("/topic/chatrooms/" + chatroomId, joinMsg);
    }
}
