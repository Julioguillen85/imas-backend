package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.LeadResponseDTO;
import com.martec.imas.agencia.service.LeadManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/leads")
@RequiredArgsConstructor
public class AdminLeadController {

    private final LeadManagementService leadManagementService;

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> getAllLeads() {
        return ResponseEntity.ok(leadManagementService.getAllLeads());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LeadResponseDTO> updateLeadStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "EN_PROCESO");
        return ResponseEntity.ok(leadManagementService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLead(@PathVariable Long id) {
        leadManagementService.deleteLead(id);
        return ResponseEntity.ok(Map.of("message", "Prospecto eliminado correctamente"));
    }
}
