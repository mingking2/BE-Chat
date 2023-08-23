package com.toyproject.authsystem.domain.dto;

import com.toyproject.authsystem.service.ChatService;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChatRoom {

    private String roomId; //채팅방 아이디
    private String name; // 채팅방 이름
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void handleAction(WebSocketSession session, ChatDto message, ChatService service) {
        // message에 담긴 타입을 확인한다.
        // 이때 message 에서 getType으로 가져온 내용이
        // chatDto의 열거형인 MessageType 안에 있는 ENTER과 동일한 값이라면
        if(message.getType().equals(ChatDto.MessageType.ENTER)) {
            //sessions에 넘어온 session을 담고,
            sessions.add(session);

            //message 에는 입장하였다는 메세지를 띄워준다.
            message.setMessage(message.getSender() + " 님이 입장하였습니다.");
            sendMessage(message.getMessage(), service);
        } else if (message.getType().equals(ChatDto.MessageType.TALK)) {
            message.setMessage(message.getMessage());
            sendMessage(message.getMessage(),service);
        }
    }
    public void sendMessage(String message, ChatService service){
        TextMessage textMessage = new TextMessage(message);
        sessions.parallelStream().forEach(session -> service.sendMessage(session,textMessage));
    }
}
