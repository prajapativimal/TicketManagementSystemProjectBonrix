package com.example.demo.dto;

import java.util.List;

public record AssignByNameRequest(
    String agentName,
    List<Long> complaintIds
) {}
