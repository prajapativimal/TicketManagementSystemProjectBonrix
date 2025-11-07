package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketVolumeByGroupDto {
    private String groupName;
    private long count;
}