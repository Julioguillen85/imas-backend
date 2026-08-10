package com.martec.imas.agencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String operationType;
    private String message;
    private String source;
    private String testEmailTarget;
    private String status;
    private LocalDateTime createdAt;
}
