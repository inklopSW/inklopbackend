package com.inklop.inklop.repositories;

import com.inklop.inklop.entities.Room;
import com.inklop.inklop.entities.valueObject.chat.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query("""
        SELECT r
        FROM Room r
        JOIN r.campaign c
        JOIN Submission s ON s.campaign = c
        WHERE s.socialMedia.user.id = :userId
        AND c.paymentStatus = 'APPROVED'
        AND s.submissionStatus = 'APPROVED'
        ORDER BY r.updatedAt DESC
        """)
    List<Room> findRoomsForAcceptedUser(Long userId);

     @Query("""
        SELECT r
        FROM Room r
        JOIN r.campaign c
        JOIN c.business b
        WHERE b.user.id = :userId
        AND c.paymentStatus = 'APPROVED'
        ORDER BY r.updatedAt DESC
        """)
    List<Room> findRoomsForBusinessUser(Long userId);

    Optional<Room> findByCampaignIdAndType(Long campaignId, RoomType type);
}
