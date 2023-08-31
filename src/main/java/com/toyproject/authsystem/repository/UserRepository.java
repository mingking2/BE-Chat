package com.toyproject.authsystem.repository;

import com.toyproject.authsystem.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Modifying
    @Query(value = "ALTER TABLE user AUTO_INCREMENT = 1", nativeQuery = true)
    void resetIdSequence();

    User findByNickname(String nickname);


    @Modifying
    @Query(value = "ALTER TABLE chatroom_user AUTO_INCREMENT = 1", nativeQuery = true)
    void resetChatroomUserIdSequence();

    @Modifying
    @Query(value = "ALTER TABLE chat_room AUTO_INCREMENT = 1", nativeQuery = true)
    void resetChatRoomIdSequence();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chatroom_user;", nativeQuery = true)
    void deleteAllFromChatroomUserTable();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_room;", nativeQuery = true)
    void deleteAllFromChatRoomTable();
}
