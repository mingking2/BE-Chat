package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.Message;
import com.toyproject.authsystem.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message getLatestMessage(Long chatRoomId) {
        return messageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    }
}
