package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserRepository userRepository;

    public ChatRoom createChatRoom(String userEmail, String friendName) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByNickname(friendName);

        log.info(userEmail);
        log.info(friendName);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("Invalid user or friend");
        }

        // 이미 존재하는 채팅방인지 확인
        for (ChatRoom chatroom : user.getChatRooms()) {
            if (chatroom.getUsers().contains(friend)) {
                return chatroom; // 이미 있는 채팅방이면 해당 채팅방 반환
            }
        }

        // 두 사용자를 포함하는 새로운 채팅방 생성
        ChatRoom newChatroom = new ChatRoom();

        // 양쪽 사용자에게도 연관관계 설정
        user.getChatRooms().add(newChatroom);
        friend.getChatRooms().add(newChatroom);

        // 새로운 채팅방에 사용자,친구 연관관계 설정
        newChatroom.getUsers().add(user);
        newChatroom.getUsers().add(friend);

        // 저장 및 반환
        userRepository.save(user);  // 변경된 상태를 저장하기 위해 userRepository.save() 호출

        return newChatroom;
    }

    public List<ChatRoom> getChatRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user email");
        }

        // Returns all the chat rooms the user participates in.
        return new ArrayList<>(user.getChatRooms());
    }

}
