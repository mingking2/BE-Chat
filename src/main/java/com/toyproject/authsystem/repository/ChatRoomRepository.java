package com.toyproject.authsystem.repository;

import com.toyproject.authsystem.domain.entity.ChatRoom;
import com.toyproject.authsystem.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Query(value = "ALTER TABLE message AUTO_INCREMENT = 1", nativeQuery = true)
    void resetIdSequence();

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.users u WHERE u IN (:user1, :user2) GROUP BY cr HAVING COUNT(DISTINCT u) = 2")
    Optional<ChatRoom> findChatRoomByUsers(@Param("user1") User user1, @Param("user2") User user2);

}
