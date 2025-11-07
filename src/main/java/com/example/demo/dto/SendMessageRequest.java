package com.example.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data // Use @Data for getters and setters
public class SendMessageRequest {
    
    private String content;
    
    // This will hold the uploaded files. It's optional.
    private MultipartFile[] attachments;
}