package com.toyproject.authsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String imageUrl;
    private String status;

}
