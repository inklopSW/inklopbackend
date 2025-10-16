package com.inklop.inklop.entities;
import com.inklop.inklop.entities.valueObject.Status;
import com.inklop.inklop.entities.valueObject.user.CreatorCategory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "creator_categories")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CreatorCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_creator", referencedColumnName = "id", nullable = false)
    private Creator creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CreatorCategory category;
    
    private Status status;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = Status.ACTIVE;
        }
    }
}
