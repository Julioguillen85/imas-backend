package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.ServiceRequestDTO;
import com.martec.imas.agencia.dto.ServiceResponseDTO;

import com.martec.imas.agencia.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final ServiceManagementService serviceManagementService;

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody ServiceRequestDTO requestDTO) {
        return new ResponseEntity<>(serviceManagementService.createService(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(@PathVariable Long id,
            @RequestBody ServiceRequestDTO requestDTO) {
        return ResponseEntity.ok(serviceManagementService.updateService(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}