package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Finds all messages for a complaint, ordered by oldest first
    List<Message> findByComplaintOrderByCreatedAtAsc(Complaint complaint);
    // ✅ Add this method to find ALL messages, newest first
    List<Message> findAllByOrderByCreatedAtDesc();
}