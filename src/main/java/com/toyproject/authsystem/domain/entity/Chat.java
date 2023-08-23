package com.toyproject.authsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String message;

    private LocalDateTime timestamp;


    public Chat(User user, String payload, LocalDateTime now) {
        this.user = user;
        this.message = payload;
        this.timestamp = now;
    }
}
