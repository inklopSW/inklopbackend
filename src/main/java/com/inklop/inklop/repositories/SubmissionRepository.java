package com.inklop.inklop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long>{
    @Query("SELECT s FROM Submission s WHERE s.campaign.business.user.id = :userId")
    List<Submission> findByCampaignBusinessUserId(@Param("userId") Long userId);
    List<Submission> findByCampaignBusinessId(Long businessId);
    List<Submission> findByCampaignId(Long campaignId);
    List<Submission> findBySocialMediaUserId(Long userId);
}
