package com.inklop.inklop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inklop.inklop.entities.Appeal;
import com.inklop.inklop.entities.valueObject.submission.AppealStatus;

import java.util.List;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, Long> {
     List<Appeal> findByAppealStatusOrderByUpdatedAtAsc(AppealStatus appealStatus);

} 
