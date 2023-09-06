package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.ChatRoomRepository;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(String userEmail, String friendName) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByNickname(friendName);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("Invalid user or friend");
        }

        // 두 사용자 모두 참여한 체팅룸 검색
        List<ChatRoom> commonRooms = user.getChatRooms().stream()
                .filter(room -> room.getUsers().contains(friend))
                .collect(Collectors.toList());

        if (!commonRooms.isEmpty()) {
            return commonRooms.get(0);  // 이미 있는 체팅룸 반환
        }

        // 두 사용자를 포함하는 새로운 채팅방 생성
        ChatRoom newChatroom = new ChatRoom();

        // 새로운 채팅방에 사용자,친구 연관관계 설정
        newChatroom.getUsers().add(user);
        newChatroom.getUsers().add(friend);

        chatRoomRepository.save(newChatroom);


        // 양쪽 사용자에게도 연관관계 설정
        user.getChatRooms().add(newChatroom);
        friend.getChatRooms().add(newChatroom);


        // 저장 및 반환
        userRepository.save(user);  // 변경된 상태를 저장하기 위해 userRepository.save() 호출
        userRepository.save(friend);  // 추가: friend 엔트리의 변경사항도 반영

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


    public Long findChatRoomId(User user, String friendNickname) throws NotFoundException {
        User friend = userRepository.findByNickname(friendNickname);

        if (friend == null) {
            throw new NotFoundException();
        }

        Optional<ChatRoom> optionalChatroom = chatRoomRepository.findChatRoomByUsers(user, friend);

        if (!optionalChatroom.isPresent()) {
            throw new NotFoundException();
        }

        return optionalChatroom.get().getId();
    }
}
