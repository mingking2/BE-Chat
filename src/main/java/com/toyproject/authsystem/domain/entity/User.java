    package com.toyproject.authsystem.domain.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.ArrayList;
    import java.util.List;

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


        // 다대다 관계 설정: 한 명의 사용자가 여러 명의 친구를 가질 수 있음.
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "friendship",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "friend_id")
        )

        // JSON 결과에서 제외하기 위한 설정.
        // 이렇게 하면 해당 필드는 JSON으로 변환할 때 무시됩니다.
        // 이는 순환 참조 문제를 방지하거나 출력 결과를 간결하게 만드는 데 도움이 됩니다.
        @JsonIgnore
        private List<User> friends = new ArrayList<>();


        // 다대다 관계 설정: 한 명의 사용자가 여러 채팅방에 참여할 수 있음.
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "chatroom_user",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "chatroom_id")
        )
        @JsonIgnore
        private List<ChatRoom> chatRooms = new ArrayList<>();

    }
