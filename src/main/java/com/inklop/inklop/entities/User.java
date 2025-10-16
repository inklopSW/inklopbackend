package com.inklop.inklop.entities;

import com.inklop.inklop.entities.valueObject.Status;
import com.inklop.inklop.entities.valueObject.user.AuthProvider;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import com.inklop.inklop.entities.valueObject.user.TypeDocument;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;


    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document")
    private TypeDocument typeDocument;

    @Column(name = "document", unique = true)
    private String document;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "email_verify")
    private Boolean emailVerify;


    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //relations

    @OneToOne(mappedBy = "user")
    private Business business;

    @OneToOne(mappedBy = "user")
    private Creator creator;

    @OneToMany(mappedBy = "user")
    private List<SocialMedia> socialMedias;

    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccounts;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.authProvider= AuthProvider.LOCAL;
            this.emailVerify = true;
            this.createdAt = LocalDateTime.now();
            this.status= Status.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
