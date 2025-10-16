package com.inklop.inklop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.CreatorCategories;

@Repository
public interface CreatorCategoriesRepository extends JpaRepository<CreatorCategories, Long> {
}
