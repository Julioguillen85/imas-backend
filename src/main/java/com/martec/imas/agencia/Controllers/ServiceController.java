package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.ServiceResponseDTO;
import com.martec.imas.agencia.service.ServiceManagementService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getPublicServices() {
        return ResponseEntity.ok(serviceManagementService.getAllActiveServices());
    }
}