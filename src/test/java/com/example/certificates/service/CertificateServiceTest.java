package com.example.certificates.service;

import com.example.certificates.dto.AnalyticsSummary;
import com.example.certificates.dto.CertificateRequest;
import com.example.certificates.model.Certificate;
import com.example.certificates.model.CertificateStatus;
import com.example.certificates.model.VerificationLog;
import com.example.certificates.repository.CertificateRepository;
import com.example.certificates.repository.VerificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private VerificationLogRepository verificationLogRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @InjectMocks
    private CertificateService certificateService;

    private Certificate sampleCertificate;

    @BeforeEach
    void setUp() {
        sampleCertificate = new Certificate(
                "test-uuid-1234",
                "Jane Doe",
                "jane@example.com",
                "Spring Boot Masterclass",
                "Tech Academy",
                LocalDate.now(),
                null,
                CertificateStatus.VALID,
                "http://localhost:8080/verify/test-uuid-1234",
                "ADMIN"
        );
    }

    @Test
    @DisplayName("Should successfully create a new certificate with generated UUID")
    void testCreateCertificate() {
        CertificateRequest request = new CertificateRequest(
                "John Smith",
                "john@example.com",
                "Java Fundamentals",
                "Code Institute",
                LocalDate.now(),
                null
        );

        when(certificateRepository.save(any(Certificate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Certificate created = certificateService.createCertificate(request, "admin_user", "http://localhost:8080");

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("John Smith", created.getRecipientName());
        assertEquals("Java Fundamentals", created.getCourseName());
        assertEquals(CertificateStatus.VALID, created.getStatus());
        assertTrue(created.getVerificationUrl().contains(created.getId()));
        verify(certificateRepository, times(1)).save(any(Certificate.class));
    }

    @Test
    @DisplayName("Should verify existing certificate and create a verification log entry")
    void testVerifyCertificate_Found() {
        when(certificateRepository.findById("test-uuid-1234")).thenReturn(Optional.of(sampleCertificate));
        when(verificationLogRepository.save(any(VerificationLog.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Certificate> result = certificateService.verifyCertificate("test-uuid-1234", "127.0.0.1", "Mozilla/5.0");

        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getRecipientName());
        verify(verificationLogRepository, times(1)).save(any(VerificationLog.class));
    }

    @Test
    @DisplayName("Should successfully revoke a valid certificate with reason")
    void testRevokeCertificate() {
        when(certificateRepository.findById("test-uuid-1234")).thenReturn(Optional.of(sampleCertificate));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> i.getArgument(0));

        Certificate revoked = certificateService.revokeCertificate("test-uuid-1234", "Misrepresentation of credentials");

        assertEquals(CertificateStatus.REVOKED, revoked.getStatus());
        assertEquals("Misrepresentation of credentials", revoked.getRevocationReason());
        verify(certificateRepository, times(1)).save(sampleCertificate);
    }

    @Test
    @DisplayName("Should return correct analytics numbers")
    void testGetAnalyticsSummary() {
        when(certificateRepository.count()).thenReturn(10L);
        when(certificateRepository.countByStatus(CertificateStatus.VALID)).thenReturn(7L);
        when(certificateRepository.countByStatus(CertificateStatus.REVOKED)).thenReturn(2L);
        when(certificateRepository.countByStatus(CertificateStatus.EXPIRED)).thenReturn(1L);
        when(verificationLogRepository.count()).thenReturn(45L);

        AnalyticsSummary summary = certificateService.getAnalyticsSummary();

        assertEquals(10L, summary.getTotalIssued());
        assertEquals(7L, summary.getTotalValid());
        assertEquals(2L, summary.getTotalRevoked());
        assertEquals(1L, summary.getTotalExpired());
        assertEquals(45L, summary.getTotalVerifications());
    }
}
