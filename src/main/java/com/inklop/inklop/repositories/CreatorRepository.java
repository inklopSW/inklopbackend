package com.inklop.inklop.repositories;

import com.inklop.inklop.entities.Creator;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByUser_Id(Long userId);
}
