package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardUnseenCountResponse {
    private long totalUnseenMessages;
    private List<TicketUnseenCount> ticketUnseenCounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketUnseenCount {
        private String ticketId;
        private long unseenCount;
        private boolean hasUnseenMessages;
    }
}
