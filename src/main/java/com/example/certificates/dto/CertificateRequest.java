package com.example.certificates.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CertificateRequest {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    private String recipientEmail;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Issuer name is required")
    private String issuedBy;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    private LocalDate expiryDate;

    public CertificateRequest() {
    }

    public CertificateRequest(String recipientName, String recipientEmail, String courseName, String issuedBy, LocalDate issueDate, LocalDate expiryDate) {
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.courseName = courseName;
        this.issuedBy = issuedBy;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
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
}
