package com.example.certificates.dto;

import com.example.certificates.model.Certificate;
import com.example.certificates.model.CertificateStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CertificateResponse {

    private String id;
    private String recipientName;
    private String recipientEmail;
    private String courseName;
    private String issuedBy;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private CertificateStatus status;
    private String verificationUrl;
    private LocalDateTime createdAt;
    private String revocationReason;
    private boolean valid;

    public CertificateResponse() {
    }

    public CertificateResponse(Certificate cert) {
        this.id = cert.getId();
        this.recipientName = cert.getRecipientName();
        this.recipientEmail = cert.getRecipientEmail();
        this.courseName = cert.getCourseName();
        this.issuedBy = cert.getIssuedBy();
        this.issueDate = cert.getIssueDate();
        this.expiryDate = cert.getExpiryDate();
        this.status = cert.getStatus();
        this.verificationUrl = cert.getVerificationUrl();
        this.createdAt = cert.getCreatedAt();
        this.revocationReason = cert.getRevocationReason();
        this.valid = cert.getStatus() == CertificateStatus.VALID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
