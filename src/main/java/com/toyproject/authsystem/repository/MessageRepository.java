package com.toyproject.authsystem.repository;

import com.toyproject.authsystem.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Modifying
    @Query(value = "ALTER TABLE message AUTO_INCREMENT = 1", nativeQuery = true)
    void resetIdSequence();

    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    Message findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
