package com.inklop.inklop.repositories;

import com.inklop.inklop.entities.Message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findByChatRoomId(Long room);
    
    List<Message> findTop50ByChatRoomIdOrderBySentAtDesc(Long roomId);
    // Cargar mensajes anteriores a una fecha (keyset pagination)
    List<Message> findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(Long roomId, LocalDateTime before, Pageable pageable);
}
