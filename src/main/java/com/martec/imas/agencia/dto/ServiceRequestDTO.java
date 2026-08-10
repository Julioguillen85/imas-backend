package com.martec.imas.agencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDTO {
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String features;
    private String icon;
    private String videoUrl;
    private Boolean isMain;
    private Long categoryId;
    private Boolean isActive;
    private Integer displayOrder;
}
