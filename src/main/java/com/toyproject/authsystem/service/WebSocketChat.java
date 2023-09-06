package com.toyproject.authsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.authsystem.CustomConfigurator;
import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.Message;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.ChatRoomRepository;
import com.toyproject.authsystem.repository.MessageRepository;
import com.toyproject.authsystem.repository.UserRepository;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ServerEndpoint(value = "/socket/chat/{chatRoomId}")
@Slf4j
public class WebSocketChat {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // Save the sessions for each chat room.
    private static Map<Long, List<Session>> chatRooms = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("chatRoomId") Long chatRoomId) throws IOException {
        log.info("open session : {}", session.toString());
        this.chatRoomRepository = CustomConfigurator.getChatroomRepositroy();
        // Retrieve the chat room by its id.
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);

        if (!optionalChatRoom.isPresent()) {
            log.error("Chatroom is not found");
            return;
        }

        ChatRoom chatroom = optionalChatRoom.get();

        // Store the chat room in the user properties of the session.
        session.getUserProperties().put("chatroom", chatroom);
        log.info("session open : {}", session);

        this.messageRepository=CustomConfigurator.getMessageRepositroy();
        List<Message> previousMessages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);

        ObjectMapper mapper = new ObjectMapper();

        for (Message message : previousMessages) {
            Map<String, String> data = new HashMap<>();
            data.put("nickname", message.getSender().getNickname());
            data.put("message", message.getContent());

            String jsonStr = mapper.writeValueAsString(data);

            session.getBasicRemote().sendText(jsonStr);
        }

        // Add the session to the list of sessions for this chat room.
        List<Session> sessions = chatRooms.getOrDefault(chatRoomId, new ArrayList<>());
        sessions.add(session);
        chatRooms.put(chatRoomId, sessions);

    }

    @OnMessage
    public void onMessage(String jsonStr, Session session) throws IOException {

        // Parse the received JSON string.
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data;

        try {
            data = mapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            session.getBasicRemote().sendText("Error parsing JSON: " + e.getMessage());
            return;
        }

        String nickname = data.get("nickname");
        String messageContent = data.get("message");

        this.userRepository = CustomConfigurator.getUserRepository();
        User sender = userRepository.findByNickname(nickname);
        if(sender == null){
            log.error("User is not found");
            return;
        }

        Message message = new Message();

        this.chatRoomRepository = CustomConfigurator.getChatroomRepositroy();
        ChatRoom chatRoom = (ChatRoom)session.getUserProperties().get("chatroom");

        if(chatRoom == null){
            log.error("Chatroom is not found");
            return;
        }
        log.info("receive message from {}: {}", nickname, messageContent);

        message.setSender(sender);
        message.setChatRoom(chatRoom);
        message.setContent(messageContent);

        this.messageRepository = CustomConfigurator.getMessageRepositroy();
        Message savedMessage = messageRepository.save(message);

        // Create a new JSON string with updated information.
        Map<String,String> responseMap=new HashMap<>();
        responseMap.put("nickname",savedMessage.getSender().getNickname());
        responseMap.put("message",savedMessage.getContent());

        String jsonResponse=mapper.writeValueAsString(responseMap);

        // Send updated information to all clients in this chat room in real-time.
        List<Session> sessions = chatRooms.get(chatRoom.getId());

        if(sessions != null) {
            Iterator<Session> iterator=sessions.iterator();

            while(iterator.hasNext()){
                Session clientSession=iterator.next();

                if(clientSession.isOpen()){
                    clientSession.getBasicRemote().sendText(jsonResponse);
                }

                else{
                    iterator.remove();
                }
            }
        }

    }

    @OnClose
    public void onClose(Session session, @PathParam("chatRoomId") Long chatRoomId) {
        log.info("session close : {}", session);

        List<Session> sessions = chatRooms.get(chatRoomId);

        if(sessions != null){
            sessions.remove(session);

            if(sessions.isEmpty()){
                chatRooms.remove(chatRoomId);
            }

            else{
                chatRooms.put(chatRoomId,sessions);
            }

        }
    }
}
