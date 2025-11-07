package com.example.demo.dto;

import lombok.Data;

@Data
public class SubAdminUpdateStatusRequest 
{
	  private String ticketId;
      private String newStatus;

}
