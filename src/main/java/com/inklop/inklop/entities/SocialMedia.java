package com.inklop.inklop.entities;

import com.inklop.inklop.entities.valueObject.Status;
import com.inklop.inklop.entities.valueObject.user.Platform;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "social_media")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
public class SocialMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private String ownerId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "link")
    private String link;

    private String name;

    private String avatar;

    private String nickname;

    @OneToMany(mappedBy = "socialMedia")
    private List<Submission> submissions;

    @CreatedDate
    @Column(name = "connected_at")
    private LocalDateTime connectedAt;

    @PrePersist
    public void prePersist() {
        this.connectedAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }


}
