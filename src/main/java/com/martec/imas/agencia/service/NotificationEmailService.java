package com.martec.imas.agencia.service;

import com.martec.imas.agencia.entity.Lead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class NotificationEmailService {

    public static final String DEFAULT_TEST_EMAIL = "julioguillen85@gmail.com";

    public void dispatchLeadNotification(Lead lead) {
        String recipient = DEFAULT_TEST_EMAIL;
        String subject = "🔔 [IMAS LEAD] Nueva Solicitud de Información / Cotización - " + (lead.getCompany() != null && !lead.getCompany().isBlank() ? lead.getCompany() : lead.getName());
        
        String formattedDate = lead.getCreatedAt() != null 
                ? lead.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : "Recién recibido";

        log.info("==================================================================================");
        log.info("📧 [CORREO DE PRUEBA DESPACHADO PARA IMAS AGENCIA ADUANAL]");
        log.info("DESTINATARIO: {}", recipient);
        log.info("ASUNTO:       {}", subject);
        log.info("ORIGEN:       {}", lead.getSource() != null ? lead.getSource() : "LANDING_PAGE");
        log.info("----------------------------------------------------------------------------------");
        log.info("👤 PROSPECTO:   {}", lead.getName());
        log.info("✉️ EMAIL:       {}", lead.getEmail());
        log.info("📞 TELÉFONO:    {}", lead.getPhone() != null ? lead.getPhone() : "No especificado");
        log.info("🏢 EMPRESA:     {}", lead.getCompany() != null ? lead.getCompany() : "Particular / No especificada");
        log.info("📦 OPERACIÓN:   {}", lead.getOperationType() != null ? lead.getOperationType() : "Consulta General");
        log.info("📝 MENSAJE:     {}", lead.getMessage() != null ? lead.getMessage() : "Sin mensaje adicional");
        log.info("🕒 FECHA:       {}", formattedDate);
        log.info("==================================================================================");

        // Generación del contenido HTML listo para proveedores SMTP futuros
        String htmlContent = buildHtmlTemplate(lead, formattedDate);
        log.debug("HTML Template generado con éxito para {}", recipient);
    }

    private String buildHtmlTemplate(Lead lead, String formattedDate) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #1e293b; border-radius: 16px; overflow: hidden; border: 1px solid #334155; }
                    .header { background: linear-gradient(135deg, #E52E71, #991b1b); padding: 30px 20px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 22px; text-transform: uppercase; letter-spacing: 1px; }
                    .badge { display: inline-block; background-color: #020617; color: #E52E71; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: bold; margin-top: 8px; }
                    .content { padding: 24px; }
                    .card { background-color: #0f172a; border-radius: 12px; padding: 16px; margin-bottom: 16px; border: 1px solid #1e293b; }
                    .field-label { color: #94a3b8; font-size: 11px; text-transform: uppercase; font-weight: bold; margin-bottom: 4px; }
                    .field-value { color: #ffffff; font-size: 15px; font-weight: 600; }
                    .message-box { background-color: #1e293b; border-left: 4px solid #E52E71; padding: 12px 16px; border-radius: 4px; color: #e2e8f0; font-size: 14px; line-height: 1.5; }
                    .footer { text-align: center; padding: 16px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>IMAS Agencia Aduanal</h1>
                        <span class="badge">NUEVA SOLICITUD DE PROSPECTO</span>
                    </div>
                    <div class="content">
                        <div class="card">
                            <div class="field-label">Nombre del Cliente / Contacto</div>
                            <div class="field-value">%s</div>
                        </div>
                        <div class="card">
                            <div class="field-label">Correo Electrónico</div>
                            <div class="field-value"><a href="mailto:%s" style="color: #38bdf8; text-decoration: none;">%s</a></div>
                        </div>
                        <div class="card">
                            <div class="field-label">Teléfono / WhatsApp</div>
                            <div class="field-value">%s</div>
                        </div>
                        <div class="card">
                            <div class="field-label">Empresa / Razón Social</div>
                            <div class="field-value">%s</div>
                        </div>
                        <div class="card">
                            <div class="field-label">Tipo de Operación Requerida</div>
                            <div class="field-value" style="color: #ec4899;">%s</div>
                        </div>
                        <div class="field-label" style="margin-top: 16px;">Requerimiento / Mensaje del Cliente</div>
                        <div class="message-box">%s</div>
                    </div>
                    <div class="footer">
                        Notificación generada automáticamente desde %s • Fecha: %s<br>
                        Destino de prueba: julioguillen85@gmail.com
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                lead.getName(),
                lead.getEmail(), lead.getEmail(),
                lead.getPhone() != null ? lead.getPhone() : "No indicado",
                lead.getCompany() != null ? lead.getCompany() : "Particular",
                lead.getOperationType() != null ? lead.getOperationType() : "Asesoría General",
                lead.getMessage() != null ? lead.getMessage() : "Sin mensaje",
                lead.getSource() != null ? lead.getSource() : "LANDING_PAGE",
                formattedDate
        );
    }
}
