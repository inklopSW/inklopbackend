package com.inklop.inklop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inklop.inklop.entities.SubmissionPayment;

@Repository
public interface SubmissionPaymentRepository extends JpaRepository<SubmissionPayment,Long>{
    
}
