package com.inklop.inklop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    private String title;

    private String photo;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RoomType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChatStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Message> messages;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = ChatStatus.NORMAL;
        this.description= campaign.getName() + "- default description";
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
