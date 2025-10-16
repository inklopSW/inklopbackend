package com.inklop.inklop.entities;

import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "submission_payments")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal payment;

    @Column(name = "payment_received", nullable = false, precision = 18, scale = 4)
    private BigDecimal paymentReceived;

    @Column(name = "engagement", precision = 5, scale = 2)
    private BigDecimal engagement;

    // (likes+comments)/views >2% = 

    @Column(nullable = false)
    private Long views;

    @Column(nullable = false)
    private Long likes;

    @Column(nullable = false)
    private Long comments;

    @Column(name="share_count")
    private Long shareCount;

    @Column(name="bookmarks")
    private Long bookmarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    //error
}
