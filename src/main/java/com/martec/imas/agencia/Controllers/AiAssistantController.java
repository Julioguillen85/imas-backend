package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.dto.LeadRequestDTO;
import com.martec.imas.agencia.dto.LeadResponseDTO;
import com.martec.imas.agencia.service.ImasAiService;
import com.martec.imas.agencia.service.LeadManagementService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiAssistantController {

    private final ImasAiService imasAiService;
    private final LeadManagementService leadManagementService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chatWithAgent(@RequestBody ChatRequestDTO request) {
        try {
            String aiResponse = imasAiService.processUserQueryWithHistory(
                    request != null ? request.getMessage() : null,
                    request != null ? request.getHistory() : null
            );
            return ResponseEntity.ok(new ChatResponseDTO(aiResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ChatResponseDTO(
                    "En **IMAS Agencia Aduanal** te apoyamos con gusto. ¿Cuál es tu nombre completo para enviarte la propuesta a tu correo?"
            ));
        }
    }

    @PostMapping("/lead")
    public ResponseEntity<?> registerAiLead(@RequestBody LeadRequestDTO request) {
        try {
            System.out.println(">>> Recibiendo lead en /api/v1/ai/lead: " + request);
            LeadResponseDTO response = leadManagementService.registerLead(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Solicitud recibida correctamente. Correo de prueba despachado a julioguillen85@gmail.com.",
                    "data", response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequestDTO {
        private String message;
        private List<ChatMessageDTO> history;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDTO {
        private String role; // "user" or "assistant"
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponseDTO {
        private String response;
    }
}