package com.example.certificates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CertificateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public index landing page should be accessible without authentication")
    void testPublicIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Public verification page for seeded valid certificate should display details")
    void testVerifySeededCertificate() throws Exception {
        mockMvc.perform(get("/verify/c8f1e2a0-4b5c-4d3e-8f1a-2b3c4d5e6f7a"))
                .andExpect(status().isOk())
                .andExpect(view().name("verify"))
                .andExpect(model().attribute("found", true))
                .andExpect(model().attributeExists("certificate", "qrBase64"));
    }

    @Test
    @DisplayName("PDF download endpoint should return application/pdf content")
    void testDownloadCertificatePdf() throws Exception {
        mockMvc.perform(get("/api/certificates/c8f1e2a0-4b5c-4d3e-8f1a-2b3c4d5e6f7a/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"certificate-c8f1e2a0-4b5c-4d3e-8f1a-2b3c4d5e6f7a.pdf\""));
    }

    @Test
    @DisplayName("Admin dashboard should redirect unauthenticated requests to login")
    void testAdminDashboardUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Admin dashboard should be accessible with ADMIN role")
    void testAdminDashboardAuthorized() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("analytics", "certificates"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Admin REST API certificate creation flow")
    void testCreateCertificateApi() throws Exception {
        String jsonPayload = """
                {
                    "recipientName": "Integration Test Student",
                    "recipientEmail": "test@student.com",
                    "courseName": "Automated Testing Masterclass",
                    "issuedBy": "QA Testing Authority",
                    "issueDate": "2026-02-23"
                }
                """;

        mockMvc.perform(post("/api/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.recipientName").value("Integration Test Student"))
                .andExpect(jsonPath("$.valid").value(true));
    }
}
