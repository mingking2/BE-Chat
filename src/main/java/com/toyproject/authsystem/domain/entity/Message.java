package com.toyproject.authsystem.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 일대다 관계 설정: 한 명의 사용자가 여러 개의 메시지를 보낼 수 있음.
    @ManyToOne
    private User sender;

    // 일대다 관계 설정: 한 채팅방에 여러 개의 메시지가 속할 수 있음.
    @ManyToOne
    private ChatRoom chatRoom;

    private String content;


    public void setSender(User user) {
        this.sender = user;
    }

    public User getSender() {
        return this.sender;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoom getChatRoom() {
        return this.chatRoom;
    }
}
