package com.toyproject.authsystem;

import com.toyproject.authsystem.repository.ChatRoomRepository;
import com.toyproject.authsystem.repository.MessageRepository;
import com.toyproject.authsystem.repository.UserRepository;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomConfigurator extends ServerEndpointConfig.Configurator {

    private static UserRepository userRepository;
    private static MessageRepository messageRepository;
    private static ChatRoomRepository chatRoomRepository;

    public CustomConfigurator() {
        // Add this default constructor
    }

    @Autowired
    public CustomConfigurator(UserRepository userRepository, MessageRepository messageRepository,
                              ChatRoomRepository chatRoomRepository) {
        CustomConfigurator.userRepository = userRepository;
        CustomConfigurator.messageRepository = messageRepository;
        CustomConfigurator.chatRoomRepository = chatRoomRepository;
    }



    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static MessageRepository getMessageRepositroy() {
        return messageRepository;
    }

    public static ChatRoomRepository getChatroomRepositroy() {
        return chatRoomRepository;
    }
}
