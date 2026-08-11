package com.martec.imas.agencia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martec.imas.agencia.dto.ServiceRequestDTO;
import com.martec.imas.agencia.dto.ServiceResponseDTO;
import com.martec.imas.agencia.entity.Category;
import com.martec.imas.agencia.entity.Service;
import com.martec.imas.agencia.repository.CategoryRepository;
import com.martec.imas.agencia.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getAllActiveServices() {
        return serviceRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ServiceResponseDTO createService(ServiceRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        Service service = Service.builder()
                .title(dto.getTitle())
                .shortDescription(dto.getShortDescription())
                .fullDescription(dto.getFullDescription())
                .features(dto.getFeatures())
                .icon(dto.getIcon())
                .videoUrl(dto.getVideoUrl())
                .isMain(dto.getIsMain() != null ? dto.getIsMain() : false)
                .category(category)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .displayOrder(dto.getDisplayOrder())
                .build();

        Service saved = serviceRepository.save(service);
        return mapToDTO(saved);
    }

    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        if (dto.getTitle() != null) service.setTitle(dto.getTitle());
        if (dto.getShortDescription() != null) service.setShortDescription(dto.getShortDescription());
        if (dto.getFullDescription() != null) service.setFullDescription(dto.getFullDescription());
        if (dto.getFeatures() != null) service.setFeatures(dto.getFeatures());
        if (dto.getIcon() != null) service.setIcon(dto.getIcon());
        if (dto.getVideoUrl() != null) service.setVideoUrl(dto.getVideoUrl());
        if (dto.getIsMain() != null) service.setIsMain(dto.getIsMain());
        if (dto.getIsActive() != null) service.setIsActive(dto.getIsActive());
        if (dto.getDisplayOrder() != null) service.setDisplayOrder(dto.getDisplayOrder());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            service.setCategory(category);
        }

        Service saved = serviceRepository.save(service);
        return mapToDTO(saved);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    private ServiceResponseDTO mapToDTO(Service service) {
        Object parsedFeatures = null;
        try {
            if (service.getFeatures() != null) {
                parsedFeatures = objectMapper.readValue(service.getFeatures(), Object.class);
            }
        } catch (Exception e) {
            parsedFeatures = service.getFeatures();
        }

        return ServiceResponseDTO.builder()
                .id(service.getId())
                .title(service.getTitle())
                .shortDescription(service.getShortDescription())
                .fullDescription(service.getFullDescription())
                .features(parsedFeatures)
                .icon(service.getIcon())
                .videoUrl(service.getVideoUrl())
                .isMain(service.getIsMain())
                .displayOrder(service.getDisplayOrder())
                .categoryId(service.getCategory() != null ? service.getCategory().getId() : null)
                .categoryName(service.getCategory() != null ? service.getCategory().getName() : null)
                .build();
    }
}