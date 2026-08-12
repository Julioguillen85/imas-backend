package com.martec.imas.agencia.service;

import com.martec.imas.agencia.entity.Lead;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEmailService {

    public static final String DEFAULT_NOTIFY_EMAIL = "info@imasagenciaaduanal.com";

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:info@imasagenciaaduanal.com}")
    private String fromEmail;

    @Value("${notification.email.to:info@imasagenciaaduanal.com}")
    private String targetRecipientEmail;

    @PostConstruct
    public void init() {
        if (mailSender != null) {
            log.info("📧 [MAIL SERVICE STATUS] JavaMailSender está INICIALIZADO correctamente. Remitente: {}", fromEmail);
        } else {
            log.error("❌ [MAIL SERVICE STATUS] JavaMailSender es NULL. Spring Mail auto-configuration no creó el bean.");
        }
    }

    public void dispatchLeadNotification(Lead lead) {
        CompletableFuture.runAsync(() -> {
            try {
                String recipient = (targetRecipientEmail != null && !targetRecipientEmail.isBlank()) 
                        ? targetRecipientEmail 
                        : DEFAULT_NOTIFY_EMAIL;
                String sourceLabel = "AI_ASSISTANT".equalsIgnoreCase(lead.getSource())
                        ? "🤖 Chatbot Asistente IA"
                        : "🌐 Formulario Web";

                String subject = "🔔 [IMAS LEAD] Nueva Solicitud de Información / Cotización - " 
                        + (lead.getCompany() != null && !lead.getCompany().isBlank() ? lead.getCompany() : lead.getName());
                
                String formattedDate = lead.getCreatedAt() != null 
                        ? lead.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        : "Recién recibido";

                log.info("==================================================================================");
                log.info("📧 [NOTIFICACIÓN DE CORREO DESPACHADA - IMAS AGENCIA ADUANAL]");
                log.info("DESTINATARIO: {}", recipient);
                log.info("ASUNTO:       {}", subject);
                log.info("ORIGEN:       {}", sourceLabel);
                log.info("----------------------------------------------------------------------------------");
                log.info("👤 CLIENTE:     {}", lead.getName());
                log.info("✉️ EMAIL:       {}", lead.getEmail());
                log.info("📞 TELÉFONO:    {}", lead.getPhone() != null ? lead.getPhone() : "No especificado");
                log.info("🏢 EMPRESA:     {}", lead.getCompany() != null ? lead.getCompany() : "Particular");
                log.info("📦 SERVICIO:    {}", lead.getOperationType() != null ? lead.getOperationType() : "Consulta General");
                log.info("📝 DUDA/MENSAJE:{}", lead.getMessage() != null ? lead.getMessage() : "Sin mensaje");
                log.info("🕒 FECHA:       {}", formattedDate);
                log.info("==================================================================================");

                String htmlContent = buildHtmlTemplate(lead, formattedDate, sourceLabel, recipient);

                if (mailSender != null) {
                    log.info("▶️ [SMTP INIT] Creando MimeMessage para enviar correo...");
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    String senderAddr = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : DEFAULT_TEST_EMAIL;
                    helper.setFrom(senderAddr);
                    helper.setTo(recipient);
                    helper.setSubject(subject);
                    helper.setText(htmlContent, true);

                    // Adjuntar el logo oficial como recurso en línea (CID)
                    try {
                        Resource logoResource = new ClassPathResource("static/images/logo1.png");
                        if (logoResource.exists()) {
                            helper.addInline("imasLogo", logoResource);
                        } else {
                            File logoFile = new File("/home/julio-guillen/Documentos/imas-frontend/public/images/logo1.png");
                            if (logoFile.exists()) {
                                helper.addInline("imasLogo", logoFile);
                            }
                        }
                    } catch (Exception imgEx) {
                        log.warn("⚠️ No se pudo adjuntar el logo oficial como inline CID: {}", imgEx.getMessage());
                    }

                    log.info("▶️ [SMTP SENDING] Conectando a Gmail SMTP para entregar mensaje a {}...", recipient);
                    mailSender.send(message);
                    log.info("✅ [SMTP SUCCESS] Correo enviado exitosamente a {}", recipient);
                } else {
                    log.warn("⚠️ JavaMailSender no está inicializado (falta credencial SMTP en application.properties o SPRING_MAIL_PASSWORD)");
                }
            } catch (Throwable t) {
                log.error("❌ Error grave o excepción al enviar correo por SMTP a {}: ", DEFAULT_TEST_EMAIL, t);
            }
        });
    }

    private String buildHtmlTemplate(Lead lead, String formattedDate, String sourceLabel, String recipient) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nueva Solicitud - IMAS Agencia Aduanal</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Helvetica, Arial, sans-serif;
                        background-color: #E52E71;
                        color: #ffffff;
                        margin: 0;
                        padding: 24px 12px;
                    }
                    .container {
                        max-width: 620px;
                        margin: 0 auto;
                        background: #0b1120;
                        border-radius: 24px;
                        overflow: hidden;
                        border: 2px solid rgba(255, 255, 255, 0.3);
                        box-shadow: 0 25px 60px rgba(0, 0, 0, 0.5);
                    }
                    .header {
                        background: linear-gradient(135deg, #E52E71 0%%, #EA3875 50%%, #D82365 100%%);
                        padding: 36px 24px;
                        text-align: center;
                    }
                    .header h1 {
                        color: #ffffff;
                        margin: 0;
                        font-size: 26px;
                        font-weight: 900;
                        letter-spacing: 1.5px;
                        text-transform: uppercase;
                        text-shadow: 0 2px 8px rgba(0,0,0,0.3);
                    }
                    .header p {
                        color: #ffffff;
                        margin: 6px 0 0 0;
                        font-size: 13px;
                        font-weight: 700;
                        letter-spacing: 2px;
                        text-transform: uppercase;
                        opacity: 0.95;
                    }
                    .badge {
                        display: inline-block;
                        background-color: #0b1120;
                        color: #E52E71;
                        padding: 8px 20px;
                        border-radius: 9999px;
                        font-size: 11px;
                        font-weight: 900;
                        margin-top: 16px;
                        border: 1.5px solid #ffffff;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
                    }
                    .content {
                        padding: 28px 24px;
                        background-color: #0b1120;
                    }
                    .section-title {
                        color: #E52E71;
                        font-size: 12px;
                        font-weight: 900;
                        text-transform: uppercase;
                        letter-spacing: 1.5px;
                        margin: 0 0 14px 0;
                        padding-bottom: 8px;
                        border-bottom: 2px solid #1e293b;
                    }
                    .grid {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 12px;
                        margin-bottom: 20px;
                    }
                    .card {
                        background-color: #151d30;
                        border-radius: 16px;
                        padding: 14px 16px;
                        border: 1px solid #232f48;
                    }
                    .field-label {
                        color: #E52E71;
                        font-size: 10px;
                        text-transform: uppercase;
                        font-weight: 800;
                        letter-spacing: 0.8px;
                        margin-bottom: 4px;
                    }
                    .field-value {
                        color: #ffffff;
                        font-size: 14px;
                        font-weight: 800;
                        word-break: break-word;
                    }
                    .service-card {
                        background: linear-gradient(135deg, rgba(229, 46, 113, 0.2), rgba(216, 35, 101, 0.08));
                        border: 1.5px solid #E52E71;
                        border-radius: 16px;
                        padding: 16px;
                        margin-bottom: 22px;
                    }
                    .service-title {
                        color: #ffffff;
                        font-size: 17px;
                        font-weight: 900;
                    }
                    .message-box {
                        background-color: #151d30;
                        border-left: 4px solid #E52E71;
                        padding: 16px;
                        border-radius: 12px;
                        color: #ffffff;
                        font-size: 13px;
                        line-height: 1.6;
                        font-weight: 600;
                        margin-bottom: 20px;
                        border-top: 1px solid #232f48;
                        border-right: 1px solid #232f48;
                        border-bottom: 1px solid #232f48;
                    }
                    .footer {
                        text-align: center;
                        padding: 20px 24px;
                        font-size: 11px;
                        color: #94a3b8;
                        background-color: #070b14;
                        border-top: 1px solid #1e293b;
                    }
                    .footer strong {
                        color: #E52E71;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <img src="%s" alt="IMAS AGENCIA ADUANAL" style="max-height: 70px; width: auto; margin-bottom: 14px; display: inline-block; filter: drop-shadow(0px 4px 10px rgba(0,0,0,0.4));" />
                        <h1>IMAS AGENCIA ADUANAL</h1>
                        <p>¡Transporta tu cadena logística a otro nivel!</p>
                        <span class="badge">NUEVA SOLICITUD RECIÉN REGISTRADA</span>
                    </div>

                    <div class="content">
                        <!-- SERVICIO REQUERIDO -->
                        <div class="section-title">📦 Servicio u Operación Requerida</div>
                        <div class="service-card">
                            <div class="field-label">Tipo de Servicio Solicitado</div>
                            <div class="service-title">%s</div>
                        </div>

                        <!-- DATOS DEL PROSPECTO -->
                        <div class="section-title">👤 Datos del Cliente / Contacto</div>
                        <div class="grid">
                            <div class="card">
                                <div class="field-label">Nombre Completo</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="card">
                                <div class="field-label">Correo Electrónico</div>
                                <div class="field-value"><a href="mailto:%s" style="color: #ffffff; text-decoration: underline; font-weight: 800;">%s</a></div>
                            </div>
                            <div class="card">
                                <div class="field-label">Teléfono / WhatsApp</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="card">
                                <div class="field-label">Empresa / Razón Social</div>
                                <div class="field-value">%s</div>
                            </div>
                        </div>

                        <!-- DUDA / DESCRIPCIÓN DEL CLIENTE -->
                        <div class="section-title">📝 Descripción de la Duda o Carga del Cliente</div>
                        <div class="message-box">%s</div>

                        <!-- INFORMACIÓN DEL REGISTRO -->
                        <div class="grid">
                            <div class="card">
                                <div class="field-label">Origen de la Solicitud</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="card">
                                <div class="field-label">Fecha y Hora de Recepción</div>
                                <div class="field-value">%s</div>
                            </div>
                        </div>
                    </div>

                    <div class="footer">
                        Notificación enviada automáticamente a <strong>%s</strong> desde el sistema de IMAS Agencia Aduanal.<br>
                        Manzanillo, Colima, México.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                getLogoSrc(),
                lead.getOperationType() != null ? lead.getOperationType() : "Consulta General de Comercio Exterior",
                lead.getName(),
                lead.getEmail(), lead.getEmail(),
                buildPhoneLinkHtml(lead.getPhone()),
                lead.getCompany() != null ? lead.getCompany() : "Particular",
                lead.getMessage() != null ? lead.getMessage() : "Sin mensaje o detalles adicionales",
                sourceLabel,
                formattedDate,
                recipient
        );
    }

    private String getLogoSrc() {
        try {
            Resource logoResource = new ClassPathResource("static/images/logo1.png");
            if (logoResource.exists()) {
                byte[] bytes = logoResource.getInputStream().readAllBytes();
                return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(bytes);
            }
            File logoFile = new File("/home/julio-guillen/Documentos/imas-frontend/public/images/logo1.png");
            if (logoFile.exists()) {
                byte[] bytes = java.nio.file.Files.readAllBytes(logoFile.toPath());
                return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            log.warn("⚠️ No se pudo convertir logo a Base64: {}", e.getMessage());
        }
        return "cid:imasLogo";
    }

    private String buildPhoneLinkHtml(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank() || "No especificado".equalsIgnoreCase(rawPhone.trim())) {
            return "<span style=\"color: #94a3b8;\">No especificado</span>";
        }
        String cleanPhone = rawPhone.trim();
        String digits = cleanPhone.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return cleanPhone;
        }

        String waDigits = digits.length() == 10 ? "52" + digits : digits;
        String waUrl = "https://wa.me/" + waDigits;
        String telUrl = "tel:+" + (digits.length() == 10 ? "52" + digits : digits);

        return String.format(
            "<a href=\"%s\" target=\"_blank\" style=\"color: #22c55e; text-decoration: underline; font-weight: 800; margin-right: 10px;\">💬 Abrir WhatsApp</a>" +
            "<a href=\"%s\" style=\"color: #38bdf8; text-decoration: underline; font-weight: 800;\">📞 %s</a>",
            waUrl, telUrl, cleanPhone
        );
    }
}
