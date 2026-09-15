package com.example.certificates.service;

import com.example.certificates.dto.AnalyticsSummary;
import com.example.certificates.dto.CertificateRequest;
import com.example.certificates.model.Certificate;
import com.example.certificates.model.CertificateStatus;
import com.example.certificates.model.VerificationLog;
import com.example.certificates.repository.CertificateRepository;
import com.example.certificates.repository.VerificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private VerificationLogRepository verificationLogRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Value("${app.base-url:http://localhost:8080}")
    private String configuredBaseUrl;

    @Transactional
    public Certificate createCertificate(CertificateRequest request, String creatorUsername, String currentBaseUrl) {
        String id = UUID.randomUUID().toString();
        String baseUrl = (currentBaseUrl != null && !currentBaseUrl.isBlank()) ? currentBaseUrl : configuredBaseUrl;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String verificationUrl = baseUrl + "/verify/" + id;

        CertificateStatus initialStatus = CertificateStatus.VALID;
        if (request.getExpiryDate() != null && request.getExpiryDate().isBefore(LocalDate.now())) {
            initialStatus = CertificateStatus.EXPIRED;
        }

        Certificate cert = new Certificate(
                id,
                request.getRecipientName().trim(),
                request.getRecipientEmail() != null ? request.getRecipientEmail().trim() : null,
                request.getCourseName().trim(),
                request.getIssuedBy().trim(),
                request.getIssueDate(),
                request.getExpiryDate(),
                initialStatus,
                verificationUrl,
                creatorUsername != null ? creatorUsername : "ADMIN"
        );

        Certificate saved = certificateRepository.save(cert);
        log.info("Successfully created certificate with ID: {} for recipient: {}", saved.getId(), saved.getRecipientName());
        return saved;
    }

    @Transactional
    public Optional<Certificate> verifyCertificate(String id, String ipAddress, String userAgent) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String cleanId = id.trim();
        Optional<Certificate> optionalCert = certificateRepository.findById(cleanId);

        if (optionalCert.isEmpty()) {
            List<Certificate> matches = certificateRepository.searchAllByQuery(cleanId);
            if (!matches.isEmpty()) {
                optionalCert = Optional.of(matches.get(0));
            }
        }

        if (optionalCert.isPresent()) {
            Certificate cert = optionalCert.get();

            // Auto-check expiry
            if (cert.getStatus() == CertificateStatus.VALID && 
                cert.getExpiryDate() != null && 
                cert.getExpiryDate().isBefore(LocalDate.now())) {
                cert.setStatus(CertificateStatus.EXPIRED);
                cert = certificateRepository.save(cert);
            }

            // Log verification attempt
            VerificationLog verificationLog = new VerificationLog(
                    cert.getId(),
                    LocalDateTime.now(),
                    ipAddress != null ? ipAddress : "UNKNOWN",
                    userAgent != null ? userAgent : "UNKNOWN",
                    cert.getStatus()
            );
            verificationLogRepository.save(verificationLog);

            return Optional.of(cert);
        }

        return Optional.empty();
    }

    public Optional<Certificate> getCertificateById(String id) {
        return certificateRepository.findById(id);
    }

    public Page<Certificate> searchCertificates(String query, CertificateStatus status, Pageable pageable) {
        return certificateRepository.searchCertificates(query, status, pageable);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @Transactional
    public Certificate revokeCertificate(String id, String reason) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Certificate with ID " + id + " not found"));

        cert.setStatus(CertificateStatus.REVOKED);
        cert.setRevocationReason(reason != null && !reason.isBlank() ? reason : "Revoked by Administrator");
        Certificate updated = certificateRepository.save(cert);
        log.info("Certificate ID {} has been revoked. Reason: {}", id, reason);
        return updated;
    }

    public AnalyticsSummary getAnalyticsSummary() {
        long totalIssued = certificateRepository.count();
        long totalValid = certificateRepository.countByStatus(CertificateStatus.VALID);
        long totalRevoked = certificateRepository.countByStatus(CertificateStatus.REVOKED);
        long totalExpired = certificateRepository.countByStatus(CertificateStatus.EXPIRED);
        long totalVerifications = verificationLogRepository.count();

        return new AnalyticsSummary(totalIssued, totalValid, totalRevoked, totalExpired, totalVerifications);
    }

    public byte[] generatePdfForCertificate(String id) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Certificate with ID " + id + " not found"));
        return pdfGeneratorService.generateCertificatePdf(cert);
    }

    public byte[] exportCertificatesCsv() {
        List<Certificate> certificates = certificateRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            // Write CSV Header
            writer.println("ID,Recipient Name,Recipient Email,Course Name,Issued By,Issue Date,Expiry Date,Status,Verification URL,Created At");

            // Write CSV Data
            for (Certificate c : certificates) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        c.getId(),
                        c.getRecipientName().replace("\"", "\"\""),
                        c.getRecipientEmail() != null ? c.getRecipientEmail().replace("\"", "\"\"") : "",
                        c.getCourseName().replace("\"", "\"\""),
                        c.getIssuedBy().replace("\"", "\"\""),
                        c.getIssueDate() != null ? c.getIssueDate().toString() : "",
                        c.getExpiryDate() != null ? c.getExpiryDate().toString() : "",
                        c.getStatus().name(),
                        c.getVerificationUrl(),
                        c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
                );
            }
            writer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export certificates to CSV", e);
        }
    }
}
