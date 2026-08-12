package com.martec.imas.agencia.Config;

import com.martec.imas.agencia.entity.Category;
import com.martec.imas.agencia.entity.HeroVideo;
import com.martec.imas.agencia.entity.Service;
import com.martec.imas.agencia.entity.SiteSetting;
import com.martec.imas.agencia.entity.User;
import com.martec.imas.agencia.repository.CategoryRepository;
import com.martec.imas.agencia.repository.HeroVideoRepository;
import com.martec.imas.agencia.repository.ServiceRepository;
import com.martec.imas.agencia.repository.SiteSettingRepository;
import com.martec.imas.agencia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final HeroVideoRepository heroVideoRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Admin User if not present
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@imasagenciaaduanal.com")
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Usuario administrador inicial creado: admin / admin123");
        }

        // 2. Seed Hero Videos if empty
        if (heroVideoRepository.count() == 0) {
            heroVideoRepository.save(HeroVideo.builder()
                    .title("Maniobras y Logística de Carga en Manzanillo")
                    .subtitle("Despacho aduanal y gestión logística integral en el puerto más importante de México")
                    .videoUrl("/videos/hero1.mp4")
                    .badgeText("Operación Logística")
                    .displayOrder(1)
                    .isActive(true)
                    .build());

            heroVideoRepository.save(HeroVideo.builder()
                    .title("Despacho Aduanal & Comercio Exterior")
                    .subtitle("Agilidad, cumplimiento normativo y asesoría especializada para tus operaciones")
                    .videoUrl("/videos/hero2.mp4")
                    .badgeText("APEGADOS A LA LEGALIDAD")
                    .displayOrder(2)
                    .isActive(true)
                    .build());

            heroVideoRepository.save(HeroVideo.builder()
                    .title("Transporte Terrestre y Multimodal Seguro")
                    .subtitle("Conectividad nacional e internacional con monitoreo GPS 24/7 de tu carga")
                    .videoUrl("/videos/hero3.mp4")
                    .badgeText("Rastreo GPS en Tiempo Real")
                    .displayOrder(3)
                    .isActive(true)
                    .build());

            heroVideoRepository.save(HeroVideo.builder()
                    .title("Almacenaje, Custodia y Sanitización de Contenedores")
                    .subtitle("Infraestructura propia y patios de resguardo estratégicos en Puerto de Manzanillo")
                    .videoUrl("/videos/hero4.mp4")
                    .badgeText("Custodia 24/7 en Manzanillo")
                    .displayOrder(4)
                    .isActive(true)
                    .build());
            System.out.println(">>> Videos del Hero inicializados.");
        }

        // 3. Seed Site Settings (Vision, Mision, Objetivos, Footer) if empty
        if (siteSettingRepository.count() == 0) {
            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("about_p1")
                    .settingValue("Somos una Agencia Aduanal que ofrece un servicio integral para tus proyectos de importación y exportación, transformándolos en operaciones exitosas, apegadas a la legalidad, mediante un proceso ágil con atención personalizada.")
                    .section("about_us")
                    .description("Párrafo 1 Quiénes Somos").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("about_p2")
                    .settingValue("Ofrecemos asesoría legal especializada que respalda cada operación y gestión ante la autoridad.")
                    .section("about_us")
                    .description("Párrafo 2 Quiénes Somos").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("about_p3")
                    .settingValue("Nuestro compromiso es ser un socio comercial estratégico que impulsa el crecimiento de nuestros clientes.")
                    .section("about_us")
                    .description("Párrafo 3 Quiénes Somos").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("mission_title")
                    .settingValue("MISIÓN")
                    .section("fixed_features")
                    .description("Título de la Misión").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("mission_text")
                    .settingValue("Brindar asesoría y soluciones integrales en la cadena logística y gestión aduanal, con atención personalizada, siempre apegados a la legalidad. Optimizando tiempos y costos para potenciar el comercio de nuestros clientes.")
                    .section("fixed_features")
                    .description("Texto de la Misión").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("vision_title")
                    .settingValue("VISIÓN")
                    .section("fixed_features")
                    .description("Título de la Visión").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("vision_text")
                    .settingValue("Consolidarnos como una agencia aduanal líder, destacando por nuestro compromiso con la legalidad y satisfacción de nuestros clientes.")
                    .section("fixed_features")
                    .description("Texto de la Visión").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("objectives_title")
                    .settingValue("VALORES")
                    .section("fixed_features")
                    .description("Título de Valores").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("footer_email")
                    .settingValue("info@imasagenciaaduanal.com")
                    .section("footer")
                    .description("Correo oficial en el footer").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("footer_phone")
                    .settingValue("+52 (314) 105 3428")
                    .section("footer")
                    .description("Teléfono oficial").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("footer_address")
                    .settingValue("Av. Paseo de las gaviotas #190, Col. Valle de las garzas.")
                    .section("footer")
                    .description("Dirección física").build());

            siteSettingRepository.save(SiteSetting.builder()
                    .settingKey("footer_rights")
                    .settingValue("© 2026 IMAS Agencia Aduanal. Todos los derechos reservados.")
                    .section("footer")
                    .description("Derechos de autor").build());

            System.out.println(">>> Configuraciones del sitio (Misión, Visión, Footer) inicializadas.");
        }

        // 4. Seed Categories if empty
        if (categoryRepository.count() == 0) {
            Category puerto = categoryRepository.save(Category.builder()
                    .name("Almacén & Puerto")
                    .icon("Warehouse")
                    .displayOrder(1)
                    .isActive(true)
                    .build());

            Category transporte = categoryRepository.save(Category.builder()
                    .name("Fletes & Transporte")
                    .icon("Anchor")
                    .displayOrder(2)
                    .isActive(true)
                    .build());

            Category tramites = categoryRepository.save(Category.builder()
                    .name("Trámites & Legales")
                    .icon("FileCheck2")
                    .displayOrder(3)
                    .isActive(true)
                    .build());

            // 5. Seed Initial Services
            if (serviceRepository.count() == 0) {
                serviceRepository.save(Service.builder()
                        .title("Resguardo de Mercancía")
                        .shortDescription("Almacenaje y custodia segura de tu carga en todo momento.")
                        .fullDescription("Contamos con infraestructura y patios estratégicamente ubicados en Manzanillo para el resguardo, almacenaje y custodia de carga general, sobredimensionada y de alto valor con vigilancia las 24 horas del día.")
                        .features("[\"Vigilancia 24/7\", \"Patios estratégicos en Manzanillo\", \"Carga general y sobredimensionada\"]")
                        .icon("Warehouse")
                        .category(puerto)
                        .isActive(true)
                        .displayOrder(1)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Flete Marítimo y Terrestre")
                        .shortDescription("Soluciones multimodales para mover tu carga por mar y tierra.")
                        .fullDescription("Coordinación eficiente de transporte terrestre y multimodal. Conectamos el Puerto de Manzanillo con los principales corredores industriales de México y el mundo con monitoreo GPS en tiempo real.")
                        .features("[\"Monitoreo GPS 24/7\", \"Transporte terrestre y multimodal\", \"Cobertura nacional e internacional\"]")
                        .icon("Anchor")
                        .category(transporte)
                        .isActive(true)
                        .displayOrder(2)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Trámites Aduanales")
                        .shortDescription("Despacho ágil y apegado a la normativa y regulaciones vigentes.")
                        .fullDescription("Despacho aduanal de importación y exportación apegado 100% a la legislación aduanera vigente. Clasificación arancelaria, cumplimiento de Normas Oficiales Mexicanas (NOMs) y regulación no arancelaria.")
                        .features("[\"Despacho de Importación / Exportación\", \"Clasificación arancelaria\", \"Cumplimiento NOMs\"]")
                        .icon("FileCheck2")
                        .category(tramites)
                        .isActive(true)
                        .displayOrder(3)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Consolidación y Desconsolidación")
                        .shortDescription("Gestión experta de contenedores y resguardo de mercancía.")
                        .fullDescription("Servicios de vaciado y llenado de contenedores (FCL / LCL), desconsolidación de carga de grupaje, emplayado, etiquetado y clasificación previa para la correcta liberación en aduana.")
                        .features("[\"FCL / LCL\", \"Vaciado y llenado\", \"Etiquetado y emplayado\"]")
                        .icon("Box")
                        .category(puerto)
                        .isActive(true)
                        .displayOrder(4)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Lavado y Sanitización de Contenedores")
                        .shortDescription("Servicio de limpieza especializado de contenedores en puerto.")
                        .fullDescription("Sanitización y acondicionamiento especializado de unidades de transporte y contenedores según exigencias internacionales y requerimientos de salubridad e inspección aduanera.")
                        .features("[\"Sanitización certificada\", \"Normativa internacional\", \"Inspección aduanera\"]")
                        .icon("Truck")
                        .category(puerto)
                        .isActive(true)
                        .displayOrder(5)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Coordinación con Proveedores")
                        .shortDescription("Gestión y comunicación directa con proveedores desde el origen.")
                        .fullDescription("Enlace directo con tus proveedores extranjeros en China, Asia, Europa y América Latina para asegurar la correcta emisión de documentos (BL, Facturas, Certificados de Origen) antes del arribo al puerto.")
                        .features("[\"Revisión documental en origen\", \"Contacto con Asia/Europa/LATAM\", \"Validación de BL y Facturas\"]")
                        .icon("Globe2")
                        .category(transporte)
                        .isActive(true)
                        .displayOrder(6)
                        .build());

                serviceRepository.save(Service.builder()
                        .title("Asesoría Legal Aduanera")
                        .shortDescription("Respaldo jurídico especializado ante autoridades y comercio exterior.")
                        .fullDescription("Defensa legal, consultoría en Tratados de Libre Comercio e impugnaciones ante autoridades fiscales y aduaneras.")
                        .features("[\"Consultoría en TLCs\", \"Defensa jurídica aduanera\", \"Asesoría en regulaciones\"]")
                        .icon("Scale")
                        .category(tramites)
                        .isActive(true)
                        .displayOrder(7)
                        .build());

                System.out.println(">>> Base de datos inicializada con categorías y servicios de IMAS Agencia.");
            }
        }

        // 6. Automated live update & synchronization for existing database records
        try {
            heroVideoRepository.findAll().forEach(hv -> {
                boolean changed = false;
                if (hv.getBadgeText() != null && hv.getBadgeText().contains("SAT")) {
                    hv.setBadgeText("APEGADOS A LA LEGALIDAD");
                    changed = true;
                }
                if (hv.getBadgeText() != null && hv.getBadgeText().contains("en Vivo")) {
                    hv.setBadgeText("Operación Logística");
                    changed = true;
                }
                if (changed) {
                    heroVideoRepository.save(hv);
                }
            });

            serviceRepository.findAll().forEach(srv -> {
                boolean changed = false;
                if (srv.getTitle() != null && srv.getTitle().equalsIgnoreCase("Trámites Aduanales")) {
                    srv.setShortDescription("Despacho ágil y apegado a la normativa y regulaciones vigentes.");
                    srv.setFeatures("[\"Despacho de Importación / Exportación\", \"Clasificación arancelaria\", \"Cumplimiento NOMs\"]");
                    changed = true;
                }
                if (srv.getTitle() != null && srv.getTitle().equalsIgnoreCase("Asesoría Legal Aduanera")) {
                    srv.setFullDescription("Defensa legal, consultoría en Tratados de Libre Comercio e impugnaciones ante autoridades fiscales y aduaneras.");
                    srv.setFeatures("[\"Consultoría en TLCs\", \"Defensa jurídica aduanera\", \"Asesoría en regulaciones\"]");
                    changed = true;
                }
                if (changed) {
                    serviceRepository.save(srv);
                }
            });

            siteSettingRepository.findById("footer_phone").ifPresent(setting -> {
                setting.setSettingValue("+52 (314) 105 3428");
                siteSettingRepository.save(setting);
            });

            siteSettingRepository.findById("footer_address").ifPresent(setting -> {
                if (setting.getSettingValue() != null && (setting.getSettingValue().contains("Puerto de Manzanillo") || setting.getSettingValue().contains("Manzanillo"))) {
                    setting.setSettingValue("Av. Paseo de las gaviotas #190, Col. Valle de las garzas.");
                    siteSettingRepository.save(setting);
                }
            });
        } catch (Exception e) {
            System.err.println("Advertencia al sincronizar datos existentes: " + e.getMessage());
        }
    }
}
