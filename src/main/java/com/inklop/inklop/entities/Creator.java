package com.inklop.inklop.entities;

import java.util.List;

import com.inklop.inklop.entities.valueObject.user.CreatorType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "creator")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String username;

    @Column(name="description", length = 1000)
    private String description;

    private String avatar;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "creator_type", nullable = false)
    private CreatorType creatorType;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreatorCategories> creatorCategories;

}
