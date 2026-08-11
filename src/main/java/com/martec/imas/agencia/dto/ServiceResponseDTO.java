package com.martec.imas.agencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private Object features; // Se deserializa limpio como array/objeto JSON
    private String icon;
    private String videoUrl;
    private Boolean isMain;
    private Integer displayOrder;
    private Long categoryId;
    private String categoryName;
}