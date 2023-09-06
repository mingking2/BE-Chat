package com.toyproject.authsystem.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 다대다 관계 설정: 한 채팅방에 여러 명의 사용자가 참여할 수 있음.
    @ManyToMany(mappedBy = "chatRooms")
    private List<User> users = new ArrayList<>();


    // 일대다 관계 설정: 한 채팅방에 여러 개의 메시지가 속할 수 있음.
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                '}';
    }


}
