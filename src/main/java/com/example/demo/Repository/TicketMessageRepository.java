package com.example.demo.Repository;


import com.example.demo.entity.TicketMessage;
import com.example.demo.entity.SenderRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(String ticketId);
    
    // Count unseen messages for specific ticket and roles
    @Query("SELECT COUNT(tm) FROM TicketMessage tm WHERE tm.ticketId = :ticketId AND tm.isSeen = false AND tm.senderRole IN :senderRoles")
    long countUnseenMessagesBySenderRoles(@Param("ticketId") String ticketId, @Param("senderRoles") List<SenderRole> senderRoles);
    
    // Count unseen messages for specific ticket excluding current user's role
    @Query("SELECT COUNT(tm) FROM TicketMessage tm WHERE tm.ticketId = :ticketId AND tm.isSeen = false AND tm.senderRole != :excludeRole")
    long countUnseenMessagesExcludingRole(@Param("ticketId") String ticketId, @Param("excludeRole") SenderRole excludeRole);
    
    // Mark messages as seen for specific ticket and roles
    @Modifying
    @Transactional
    @Query("UPDATE TicketMessage tm SET tm.isSeen = true WHERE tm.ticketId = :ticketId AND tm.senderRole IN :senderRoles")
    void markMessagesAsSeenBySenderRoles(@Param("ticketId") String ticketId, @Param("senderRoles") List<SenderRole> senderRoles);
    
    // Get all unique ticket IDs that have unseen messages for specific roles
    @Query("SELECT DISTINCT tm.ticketId FROM TicketMessage tm WHERE tm.isSeen = false AND tm.senderRole IN :senderRoles")
    List<String> findTicketIdsWithUnseenMessagesBySenderRoles(@Param("senderRoles") List<SenderRole> senderRoles);
    
    // Count total unseen messages across all tickets for specific roles
    @Query("SELECT COUNT(tm) FROM TicketMessage tm WHERE tm.isSeen = false AND tm.senderRole IN :senderRoles")
    long countTotalUnseenMessagesBySenderRoles(@Param("senderRoles") List<SenderRole> senderRoles);
}