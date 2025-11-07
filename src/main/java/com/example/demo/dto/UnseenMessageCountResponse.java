package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnseenMessageCountResponse {
    private String ticketId;
    private long unseenCount;
    private boolean hasUnseenMessages;
}
