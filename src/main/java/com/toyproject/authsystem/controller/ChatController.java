package com.toyproject.authsystem.controller;

import com.toyproject.authsystem.domain.dto.CreateChatRoomRequest;
import com.toyproject.authsystem.domain.dto.FriendRequestDto;
import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.Message;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.ChatService;
import com.toyproject.authsystem.service.MessageService;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired private UserService userService;
    @Autowired private ChatService chatService;
    @Autowired private MessageService messageService;


    // Create a new chat room with the friend
    @PostMapping("/add")
    public ResponseEntity<?> createChatRoom(HttpServletRequest request, @RequestBody CreateChatRoomRequest req) {


        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        try {
            ChatRoom chatRoom = chatService.createChatRoom(user.getEmail(), req.getNickname());
            return ResponseEntity.ok(chatRoom);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<?> getChatRoom(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        try {
            List<ChatRoom> chatRooms = chatService.getChatRooms(user.getEmail());

            // Convert ChatRoom list to response objects
            List<Map<String, Object>> responseList = new ArrayList<>();

            for(ChatRoom chatRoom : chatRooms){
                Map<String, Object> responseObject = new HashMap<>();
                responseObject.put("id", chatRoom.getId()); // Add the chat room's ID

                for(User roomUser : chatRoom.getUsers()){
                    // Skip the current user
                    if(roomUser.getEmail().equals(user.getEmail())) continue;

                    responseObject.put("nickname", roomUser.getNickname());

                    Message latestMessage = messageService.getLatestMessage(chatRoom.getId());

                    if (latestMessage != null) {
                        responseObject.put("latestMessage", latestMessage.getContent());
                    }

                    responseList.add(responseObject);
                }
            }

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> goChatRoom(HttpServletRequest request, @RequestBody FriendRequestDto friendName) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        try {
            Long chatroomId = chatService.findChatRoomId(user, friendName.getFriendName());

            Map<String, String> response = new HashMap<>();
            response.put("chatRoomId", String.valueOf(chatroomId));
            response.put("yourName", user.getNickname());

            return ResponseEntity.ok(response);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }



    }




}
