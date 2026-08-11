package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.LeadRequestDTO;
import com.martec.imas.agencia.dto.LeadResponseDTO;
import com.martec.imas.agencia.service.LeadManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/leads")
@RequiredArgsConstructor
public class PublicLeadController {

    private final LeadManagementService leadManagementService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitLead(@RequestBody LeadRequestDTO request) {
        LeadResponseDTO response = leadManagementService.registerLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Solicitud recibida correctamente. Correo de prueba despachado a julioguillen85@gmail.com.",
                "data", response
        ));
    }
}
