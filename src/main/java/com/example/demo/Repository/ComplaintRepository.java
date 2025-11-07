package com.example.demo.Repository;


import org.springframework.data.domain.Pageable;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.SupportAgent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByMerchantId(Long merchantId);
    // ✅ ADD THIS METHOD
    // It finds complaints where status is "OPEN" and no agent is assigned,
    // ordered by the creation date to get the oldest ones first.
    List<Complaint> findByStatusAndSupportAgentIsNullOrderByCreatedAtAsc(String status, Pageable pageable);
	List<Complaint> findBySupportAgent(SupportAgent agent);
	List<Complaint> findByStatusAndSupportAgentIsNullOrderByCreatedAtAsc(ComplaintStatus status, Pageable pageable);

    Optional<Complaint> findByTicketId(String ticketId);
    
 // Counts all complaints with a specific status
    long countByStatus(ComplaintStatus status);
    
    // Counts complaints resolved within a specific time frame
    long countByStatusInAndUpdatedAtAfter(List<ComplaintStatus> statuses, LocalDateTime startTime);
    
    // Finds all complaints that have been resolved/closed
    List<Complaint> findByStatusIn(List<ComplaintStatus> statuses);

    // Finds all resolved/closed complaints that have an agent assigned
    List<Complaint> findByStatusInAndSupportAgentIsNotNull(List<ComplaintStatus> statuses);

    
    // ✅ Add this method to find all complaints for a specific brand name subadmin 
    List<Complaint> findByBrandName(String brandName);
	List<Complaint> findBySupportAgentAndStatusNotIn(SupportAgent agent, List<ComplaintStatus> of);
    
    
    //

    
    
   
}

