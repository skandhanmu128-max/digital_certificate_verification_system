package com.example.certificates.controller;

import com.example.certificates.dto.AnalyticsSummary;
import com.example.certificates.dto.CertificateRequest;
import com.example.certificates.dto.CertificateResponse;
import com.example.certificates.model.Certificate;
import com.example.certificates.model.CertificateStatus;
import com.example.certificates.service.CertificateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CertificateRestController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping("/certificates")
    public ResponseEntity<CertificateResponse> createCertificate(
            @Valid @RequestBody CertificateRequest request,
            Authentication authentication,
            HttpServletRequest httpServletRequest) {
        
        String scheme = httpServletRequest.getScheme();
        String serverName = httpServletRequest.getServerName();
        int serverPort = httpServletRequest.getServerPort();
        String baseUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        String username = authentication != null ? authentication.getName() : "ADMIN";
        Certificate cert = certificateService.createCertificate(request, username, baseUrl);
        return ResponseEntity.created(URI.create("/api/certificates/" + cert.getId()))
                .body(new CertificateResponse(cert));
    }

    @GetMapping("/certificates/{id}")
    public ResponseEntity<?> getCertificateDetails(@PathVariable String id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        Optional<Certificate> certOpt = certificateService.verifyCertificate(id, ip, userAgent);

        if (certOpt.isPresent()) {
            return ResponseEntity.ok(new CertificateResponse(certOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Certificate Not Found", "id", id));
        }
    }

    @GetMapping("/certificates/{id}/pdf")
    public ResponseEntity<byte[]> downloadCertificatePdf(@PathVariable String id) {
        byte[] pdfBytes = certificateService.generatePdfForCertificate(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/certificates/search")
    public ResponseEntity<Page<CertificateResponse>> searchCertificates(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) CertificateStatus status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Certificate> certs = certificateService.searchCertificates(q, status, pageable);
        Page<CertificateResponse> dtoPage = certs.map(CertificateResponse::new);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/certificates/{id}/revoke")
    public ResponseEntity<CertificateResponse> revokeCertificate(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> payload) {
        
        String reason = payload != null ? payload.getOrDefault("reason", "Revoked via API") : "Revoked via API";
        Certificate revoked = certificateService.revokeCertificate(id, reason);
        return ResponseEntity.ok(new CertificateResponse(revoked));
    }

    @GetMapping("/admin/analytics")
    public ResponseEntity<AnalyticsSummary> getAnalytics() {
        return ResponseEntity.ok(certificateService.getAnalyticsSummary());
    }

    @GetMapping("/admin/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] csvData = certificateService.exportCertificatesCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "certificates-export.csv");
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}
