package com.inklop.inklop.entities;

import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name="submission")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false, referencedColumnName="id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "social_media_id", nullable = false, referencedColumnName="id")
    private SocialMedia socialMedia;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "saved_video_url", unique = true)
    private String savedVideoUrl;

    private String description;

    @Column(name="percentage")
    private Integer percentage;

    @CreatedDate
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", nullable = false)
    private SubmissionStatus submissionStatus;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubmissionPayment submissionPayment;

    @PrePersist
    void prePersist() {
        this.submittedAt = LocalDateTime.now();
        if (uuid == null) uuid = UUID.randomUUID();
        
    }

    public Boolean isApproved(){
        return this.submissionStatus == SubmissionStatus.APPROVED || this.submissionStatus == SubmissionStatus.FINAL_APPROVED;
    }

    public Boolean isRejected(){
        return this.submissionStatus == SubmissionStatus.REJECTED || this.submissionStatus == SubmissionStatus.FINAL_REJECTED;
    }

    public Boolean isFinalStatus(){
        return this.submissionStatus == SubmissionStatus.FINAL_APPROVED || this.submissionStatus == SubmissionStatus.FINAL_REJECTED;
    }
}
