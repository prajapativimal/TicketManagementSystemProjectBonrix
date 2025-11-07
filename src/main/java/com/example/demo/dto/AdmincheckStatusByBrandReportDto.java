package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class AdmincheckStatusByBrandReportDto 
{
     private String brandName;
        private long totalCount;
        private Map<String, Long> statusCounts;

}