package com.martec.imas.agencia.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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