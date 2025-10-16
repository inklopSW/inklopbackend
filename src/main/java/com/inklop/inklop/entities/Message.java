package com.inklop.inklop.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.MessageType;
@Entity
@Table(name = "chat_message")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_room")
    private Room chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private ChatStatus status;

    private MessageType type;

    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
        this.status = ChatStatus.NORMAL;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
