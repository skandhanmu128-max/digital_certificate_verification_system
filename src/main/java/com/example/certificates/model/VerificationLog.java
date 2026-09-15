package com.example.certificates.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_logs")
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private String certificateId;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_at_verification")
    private CertificateStatus statusAtVerification;

    public VerificationLog() {
    }

    public VerificationLog(String certificateId, LocalDateTime verifiedAt, String ipAddress, String userAgent, CertificateStatus statusAtVerification) {
        this.certificateId = certificateId;
        this.verifiedAt = verifiedAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.statusAtVerification = statusAtVerification;
    }

    @PrePersist
    protected void onCreate() {
        if (this.verifiedAt == null) {
            this.verifiedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public CertificateStatus getStatusAtVerification() {
        return statusAtVerification;
    }

    public void setStatusAtVerification(CertificateStatus statusAtVerification) {
        this.statusAtVerification = statusAtVerification;
    }
}
