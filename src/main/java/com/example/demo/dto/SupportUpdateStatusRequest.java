package com.example.demo.dto;

import com.example.demo.entity.ComplaintStatus;

import lombok.Data;

@Data
public class SupportUpdateStatusRequest
{
    private ComplaintStatus status;


}
