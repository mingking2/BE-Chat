    package com.toyproject.authsystem.domain.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

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

        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "friendship",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "friend_id")
        )

        @JsonIgnore
        private List<User> friends = new ArrayList<>();


        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "chatroom_user",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "chatroom_id")
        )
        private List<ChatRoom> chatRooms = new ArrayList<>();

    }
