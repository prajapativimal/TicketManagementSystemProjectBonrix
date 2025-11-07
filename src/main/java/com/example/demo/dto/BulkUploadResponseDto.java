package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // Good practice to add
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor // Add default constructor
public class BulkUploadResponseDto
{
	private boolean status;
    private List<String> messages;

}
