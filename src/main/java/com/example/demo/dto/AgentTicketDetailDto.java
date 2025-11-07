package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentTicketDetailDto {
    private String ticketId;
    private String priority;
    private String status;
}