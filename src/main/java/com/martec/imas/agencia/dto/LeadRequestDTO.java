package com.martec.imas.agencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String company;
    private String operationType;
    private String message;
    private String source; // "AI_ASSISTANT" | "LANDING_PAGE"
}
