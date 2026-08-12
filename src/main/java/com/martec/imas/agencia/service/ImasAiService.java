package com.martec.imas.agencia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.martec.imas.agencia.Controllers.AiAssistantController;
import com.martec.imas.agencia.dto.LeadRequestDTO;
import com.martec.imas.agencia.dto.ServiceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImasAiService {

    private final ServiceManagementService serviceManagementService;
    private final LeadManagementService leadManagementService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.api.model:llama-3.3-70b-versatile}")
    private String groqModel;

    private static final String DEFAULT_GREETING = "¡Hola! 👋 Estoy para ayudarte, ¿qué tipo de información necesitas?";

    public String processUserQuery(String userQuery) {
        return processUserQueryWithHistory(userQuery, null);
    }

    public String processUserQueryWithHistory(String userQuery, List<AiAssistantController.ChatMessageDTO> history) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return DEFAULT_GREETING;
        }

        String query = userQuery.toLowerCase(Locale.ROOT).trim();

        // Si es solo un saludo inicial y no hay historial previo, responder con la frase exacta
        if ((history == null || history.isEmpty()) && query.matches("^(hola|buenos dias|buenas tardes|buenas noches|hey|que tal|saludos)[.! ]*$")) {
            return DEFAULT_GREETING;
        }

        // Si tenemos API Key de Groq configurada, llamamos a Groq con parámetros anti-alucinación y System Prompt estricto
        if (groqApiKey != null && !groqApiKey.trim().isEmpty() && !groqApiKey.contains("tu_groq_api_key")) {
            try {
                String groqResponse = callGroqAiWithHistory(userQuery, history);
                if (groqResponse != null && !groqResponse.trim().isEmpty()) {
                    checkAndAutoRegisterLead(userQuery, history);
                    return groqResponse;
                }
            } catch (Exception e) {
                log.error("Error al comunicarse con Groq AI, usando motor de contingencia: {}", e.getMessage());
            }
        }

        // Motor de contingencia local
        return processFallbackQuery(userQuery, history);
    }

    /**
     * Comunicación con Groq Cloud API con temperatura muy baja (0.1) para evitar alucinaciones
     */
    private String callGroqAiWithHistory(String userMessage, List<AiAssistantController.ChatMessageDTO> history) throws Exception {
        String systemPrompt = buildSystemPrompt();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("model", groqModel);
        rootNode.put("temperature", 0.1); // Temperatura baja para máxima fidelidad y cero alucinaciones
        rootNode.put("max_tokens", 900);

        ArrayNode messagesArray = rootNode.putArray("messages");

        // 1. System Prompt estricto
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messagesArray.add(systemMessage);

        // 2. Historial de conversación
        if (history != null && !history.isEmpty()) {
            for (AiAssistantController.ChatMessageDTO h : history) {
                if (h.getContent() != null && !h.getContent().isBlank()) {
                    ObjectNode hMsg = objectMapper.createObjectNode();
                    hMsg.put("role", "assistant".equalsIgnoreCase(h.getRole()) ? "assistant" : "user");
                    hMsg.put("content", h.getContent());
                    messagesArray.add(hMsg);
                }
            }
        }

        // 3. Mensaje actual del usuario
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messagesArray.add(userMsg);

        String requestBody = objectMapper.writeValueAsString(rootNode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(groqApiUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode responseJson = objectMapper.readTree(response.body());
            JsonNode choices = responseJson.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
        } else {
            log.error("Groq API retornó código HTTP {}: {}", response.statusCode(), response.body());
        }

        return null;
    }

    /**
     * Construcción de System Prompt con guardrails anti-alucinación y delimitación estricta
     */
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres el Asistente Virtual Inteligente de **IMAS Agencia Aduanal** en el Puerto de Manzanillo, Colima, México.\n\n");

        sb.append("=== PROTOCOLO ANTI-ALUCINACIÓN Y PRECISIÓN ESTRICTA ===\n");
        sb.append("1. **NUNCA INVENTES TARIFAS O COSTOS FIJOS EN DÓLARES O PESOS**: Los costos logísticos y aduanales son dinámicos y dependen de la clasificación arancelaria, peso, volumen, ruta y tipo de contenedor. En lugar de inventar números o rangos no confirmados, explica de forma profesional que las tarifas se cotizan a la medida y por ello le solicitas sus datos para que un agente aduanal le envíe la propuesta oficial exacta.\n");
        sb.append("2. **VERACIDAD TOTAL**: Responde ÚNICAMENTE con datos reales y verificables. Si desconoces un detalle muy específico o no tienes certeza de un dato, indícalo con honestidad e invita al usuario a dejar sus datos para que el área técnica de IMAS lo revise.\n");
        sb.append("3. **ALCANCE EXCLUSIVO**: Habla ÚNICAMENTE de los servicios que ofrece IMAS. Si preguntan sobre temas ajenos (recetas, programación, deportes, otros negocios, etc.), responde amablemente:\n");
        sb.append("   \"Como asistente virtual de **IMAS Agencia Aduanal**, estoy especializado únicamente en orientarte sobre nuestros servicios aduanales, logísticos y de transporte en Manzanillo. ¿En cuál de nuestras soluciones de comercio exterior te puedo ayudar?\"\n\n");

        sb.append("=== REGLA DE SALUDO INICIAL ===\n");
        sb.append("Si el usuario solo saluda (ej: 'hola', 'buenos días', 'hey'), responde exactamente:\n");
        sb.append("\"¡Hola! 👋 Estoy para ayudarte, ¿qué tipo de información necesitas?\"\n\n");

        sb.append("=== CATÁLOGO OFICIAL DE SERVICIOS IMAS (INFORMACIÓN VERIFICADA) ===\n");
        List<ServiceResponseDTO> activeServices = serviceManagementService.getAllActiveServices();
        if (activeServices != null && !activeServices.isEmpty()) {
            for (ServiceResponseDTO s : activeServices) {
                sb.append("• **").append(s.getTitle()).append("**\n");
                sb.append("  ").append(s.getShortDescription()).append("\n");
                if (s.getFeatures() != null) {
                    sb.append("  *(Incluye: ").append(s.getFeatures().toString()).append(")*\n");
                }
                sb.append("\n");
            }
        } else {
            sb.append("• 🚢 **Despacho y Trámites Aduanales:** Gestión, manejo y liberación de mercancías con apego 100% a la legalidad.\n\n");
            sb.append("• 📑 **Alta y Reactivación en Padrón de Importadores:** Asesoría y regularización de padrones.\n\n");
            sb.append("• 🔒 **Consolidación, Desconsolidación y Resguardo:** Patios, bodegas seguras y almacenaje en Manzanillo.\n\n");
            sb.append("• 🛠️ **Servicios Operativos Portuarios:** Etiquetado NOM, acondicionamiento y lavado/sanitizado de contenedores.\n\n");
            sb.append("• 🚛 **Flete Marítimo y Terrestre:** Transporte nacional e internacional con monitoreo GPS 24/7.\n\n");
            sb.append("• 🌐 **Coordinación de Proveedores desde Origen:** Seguimiento de carga desde fábrica hasta destino.\n\n");
        }

        sb.append("=== REGLA DE RECOLECCIÓN DE DATOS: DATO POR DATO (PASO A PASO) ===\n");
        sb.append("Cuando el cliente pregunte por un servicio, requiera asesoría o pida cotización:\n");
        sb.append("1. Explica brevemente y con claridad el servicio de IMAS.\n");
        sb.append("2. Menciona que para enviarle la información completa y una cotización personalizada a su correo, le irás pidiendo unos breves datos.\n");
        sb.append("3. **SOLICITA ESTRICTAMENTE UN SOLO DATO POR MENSAJE**:\n");
        sb.append("   - Paso 1: Pregunta su **Nombre completo**.\n");
        sb.append("   - Paso 2: Salúdalo amablemente por su nombre y pide su **Correo electrónico**.\n");
        sb.append("   - Paso 3: Pide su número de **Teléfono o WhatsApp**.\n");
        sb.append("   - Paso 4: Pide el **Nombre de su empresa o tipo de mercancía**.\n");
        sb.append("   - Paso 5: Confirma los datos, agradece e infórmale que se envió la notificación a su correo y a nuestro equipo de operaciones para contactarlo a la brevedad.\n");

        return sb.toString();
    }

    /**
     * Detección y registro automático del lead en la base de datos
     */
    private void checkAndAutoRegisterLead(String currentMessage, List<AiAssistantController.ChatMessageDTO> history) {
        try {
            StringBuilder fullConversation = new StringBuilder();
            if (history != null) {
                for (AiAssistantController.ChatMessageDTO h : history) {
                    fullConversation.append(" ").append(h.getContent());
                }
            }
            fullConversation.append(" ").append(currentMessage);
            String text = fullConversation.toString();

            Pattern emailPattern = Pattern.compile("(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\\b");
            Matcher emailMatcher = emailPattern.matcher(text);
            String detectedEmail = null;
            if (emailMatcher.find()) {
                detectedEmail = emailMatcher.group();
            }

            Pattern phonePattern = Pattern.compile("(?i)(\\+?52\\s?)?(\\d{10}|\\d{3}[-\\s]\\d{3}[-\\s]\\d{4})");
            Matcher phoneMatcher = phonePattern.matcher(text);
            String detectedPhone = null;
            if (phoneMatcher.find()) {
                detectedPhone = phoneMatcher.group();
            }

            if (detectedEmail != null && !detectedEmail.isBlank()) {
                LeadRequestDTO lead = new LeadRequestDTO();
                lead.setEmail(detectedEmail.trim());
                lead.setPhone(detectedPhone != null ? detectedPhone.trim() : "Por confirmar");
                lead.setName("Prospecto Chat IMAS");
                lead.setCompany("Empresa / Importador");
                lead.setOperationType("Consulta de Servicios IA");
                lead.setMessage("Conversación asistida por IA: " + (currentMessage.length() > 200 ? currentMessage.substring(0, 200) : currentMessage));
                lead.setSource("AI_ASSISTANT");

                leadManagementService.registerLead(lead);
                log.info("Lead registrado automáticamente desde chat de IA: {}", detectedEmail);
            }
        } catch (Exception e) {
            log.debug("Auto-lead check info: {}", e.getMessage());
        }
    }

    /**
     * Fallback de contingencia local
     */
    private String processFallbackQuery(String userQuery, List<AiAssistantController.ChatMessageDTO> history) {
        String query = userQuery.toLowerCase(Locale.ROOT).trim();

        if (query.matches("^(hola|buenos dias|buenas tardes|buenas noches|hey|que tal|saludos)[.! ]*$")) {
            return DEFAULT_GREETING;
        }

        if (query.contains("servicios") || query.contains("ofrecen") || query.contains("opciones") || query.contains("que hacen")) {
            return "En **IMAS Agencia Aduanal** te ofrecemos soluciones integrales para tus operaciones de comercio exterior en Manzanillo:\n\n"
                    + "🚢 **Despacho y Trámites Aduanales:** Liberación ágil y revisión documental con apego 100% a la legalidad.\n\n"
                    + "📑 **Alta y Reactivación en Padrón de Importadores:** Diagnóstico y regularización de padrones.\n\n"
                    + "🔒 **Consolidación, Desconsolidación y Resguardo:** Patios y bodegas seguras para tu mercancía.\n\n"
                    + "🛠️ **Servicios Operativos Portuarios:** Etiquetado NOM, acondicionamiento y lavado de contenedores.\n\n"
                    + "🚛 **Flete Marítimo y Terrestre:** Cobertura nacional e internacional con monitoreo GPS 24/7.\n\n"
                    + "🌐 **Coordinación de Proveedores desde Origen:** Seguimiento de tu carga desde fábrica hasta destino.\n\n"
                    + "¿Sobre cuál de estos servicios te gustaría recibir más información o cotización?";
        }

        if (query.contains("@")) {
            return "¡Excelente! Tomé nota de tu correo. 📱 ¿A qué número de **teléfono o WhatsApp** te podemos contactar?";
        }

        if (query.matches(".*\\d{7,}.*")) {
            return "¡Perfecto! Hemos recibido tus datos con éxito. 📋 Nuestro equipo de operaciones te enviará la información completa y cotización formal a tu correo. ¡Estamos a tus órdenes!";
        }

        return "En **IMAS Agencia Aduanal** te apoyamos con gusto en todos nuestros servicios aduanales y logísticos.\n\n"
                + "👉 Para enviarte la información completa y una cotización a tu correo, ¿cuál es tu **nombre completo**?";
    }
}