package com.example.demo.dto;

import com.example.demo.entity.SenderRole;
import java.time.LocalDateTime;
import java.util.List;

public record ConversationViewResponse(
    Long messageId,
    String content,
    String senderName,
    SenderRole senderRole,
    LocalDateTime createdAt,
    List<String> attachmentUrls // ✅ Add this field

    
) {
	
}