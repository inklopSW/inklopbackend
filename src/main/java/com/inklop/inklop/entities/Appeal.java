package com.inklop.inklop.entities;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.submission.AppealStatus;
import com.inklop.inklop.entities.valueObject.submission.TypeAppeal;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "appeal")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Appeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    private Submission submission;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_appeal")
    private TypeAppeal typeAppeal;

    @Column(name = "to_creator")
    private Boolean toCreator;

    private String reason;

    @Column(name = "admin_comment")
    private String adminComment;

    @Enumerated(EnumType.STRING)
    private AppealStatus appealStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.appealStatus = AppealStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
