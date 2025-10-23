package com.inklop.inklop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long>{
    //  Query to find all submission but with business and then to user id :v
    @Query("""
        SELECT s
        FROM Submission s
        JOIN s.campaign c
        JOIN c.business b
        JOIN b.user u
        WHERE u.id = :userId
        """)
    List<Submission> findAllByBusinessUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Submission s WHERE s.campaign.id = :campaignId")
    List<Submission> findAllByCampaignId(@Param("campaignId") Long campaignId);

    // JPQL with joins to get submissions by social media
    @Query("""
        SELECT s
        FROM Submission s
        JOIN s.socialMedia sm
        JOIN sm.user u
        WHERE u.id = :userId
        """)
    List<Submission> findAllBySocialMediaUserId(@Param("userId") Long userId);

    boolean existsBySavedVideoUrl(String savedVideoUrl);
}
