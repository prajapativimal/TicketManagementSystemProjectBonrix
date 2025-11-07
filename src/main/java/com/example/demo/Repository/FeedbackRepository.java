package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Feedback;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.SupportAgent;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>
{
    List<Feedback> findByMerchant(Merchant merchant);
    
    //
 //   List<Feedback> findByComplaintSupportAgent(SupportAgent agent);

    // ✅ ADD THIS METHOD
    // This efficiently feedback  checks if a record exists without fetching the whole object
    boolean existsByTicketId(String ticketId);
    

	

}