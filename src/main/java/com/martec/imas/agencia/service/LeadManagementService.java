package com.martec.imas.agencia.service;

import com.martec.imas.agencia.dto.LeadRequestDTO;
import com.martec.imas.agencia.dto.LeadResponseDTO;
import com.martec.imas.agencia.entity.Lead;
import com.martec.imas.agencia.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadManagementService {

    private final LeadRepository leadRepository;
    private final NotificationEmailService notificationEmailService;

    @Transactional
    public LeadResponseDTO registerLead(LeadRequestDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del contacto es obligatorio.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }

        String contactName = request.getName().trim();
        Lead lead = Lead.builder()
                .name(contactName)
                .fullName(contactName)
                .email(request.getEmail().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : "")
                .company(request.getCompany() != null ? request.getCompany().trim() : "")
                .operationType(request.getOperationType() != null && !request.getOperationType().trim().isEmpty() 
                        ? request.getOperationType().trim() 
                        : "Consulta General / Cotización")
                .message(request.getMessage() != null ? request.getMessage().trim() : "")
                .source(request.getSource() != null && !request.getSource().trim().isEmpty() 
                        ? request.getSource().trim() 
                        : "LANDING_PAGE")
                .testEmailTarget(NotificationEmailService.DEFAULT_TEST_EMAIL)
                .status("NUEVO")
                .build();

        Lead savedLead = leadRepository.save(lead);

        // Despacha la notificación por correo electrónico de prueba
        notificationEmailService.dispatchLeadNotification(savedLead);

        return mapToResponse(savedLead);
    }

    public List<LeadResponseDTO> getAllLeads() {
        return leadRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LeadResponseDTO> getLeadsBySource(String source) {
        return leadRepository.findBySourceOrderByCreatedAtDesc(source)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }

    @Transactional
    public LeadResponseDTO updateStatus(Long id, String status) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prospecto no encontrado con id: " + id));
        lead.setStatus(status);
        return mapToResponse(leadRepository.save(lead));
    }

    private LeadResponseDTO mapToResponse(Lead lead) {
        return LeadResponseDTO.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .operationType(lead.getOperationType())
                .message(lead.getMessage())
                .source(lead.getSource())
                .testEmailTarget(lead.getTestEmailTarget())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}
