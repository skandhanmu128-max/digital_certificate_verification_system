package com.example.certificates.dto;

public class AnalyticsSummary {

    private long totalIssued;
    private long totalValid;
    private long totalRevoked;
    private long totalExpired;
    private long totalVerifications;

    public AnalyticsSummary() {
    }

    public AnalyticsSummary(long totalIssued, long totalValid, long totalRevoked, long totalExpired, long totalVerifications) {
        this.totalIssued = totalIssued;
        this.totalValid = totalValid;
        this.totalRevoked = totalRevoked;
        this.totalExpired = totalExpired;
        this.totalVerifications = totalVerifications;
    }

    public long getTotalIssued() {
        return totalIssued;
    }

    public void setTotalIssued(long totalIssued) {
        this.totalIssued = totalIssued;
    }

    public long getTotalValid() {
        return totalValid;
    }

    public void setTotalValid(long totalValid) {
        this.totalValid = totalValid;
    }

    public long getTotalRevoked() {
        return totalRevoked;
    }

    public void setTotalRevoked(long totalRevoked) {
        this.totalRevoked = totalRevoked;
    }

    public long getTotalExpired() {
        return totalExpired;
    }

    public void setTotalExpired(long totalExpired) {
        this.totalExpired = totalExpired;
    }

    public long getTotalVerifications() {
        return totalVerifications;
    }

    public void setTotalVerifications(long totalVerifications) {
        this.totalVerifications = totalVerifications;
    }
}
