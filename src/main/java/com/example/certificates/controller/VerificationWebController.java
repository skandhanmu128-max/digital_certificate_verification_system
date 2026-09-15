package com.example.certificates.controller;

import com.example.certificates.model.Certificate;
import com.example.certificates.service.CertificateService;
import com.example.certificates.service.QrCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class VerificationWebController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("recentCount", certificateService.getAnalyticsSummary().getTotalIssued());
        return "index";
    }

    @GetMapping("/verify")
    public String searchVerification(@RequestParam(name = "id", required = false) String id) {
        if (id != null && !id.isBlank()) {
            return "redirect:/verify/" + id.trim();
        }
        return "redirect:/";
    }

    @GetMapping("/verify/{id}")
    public String verifyCertificateDetails(@PathVariable String id, Model model, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Optional<Certificate> optionalCertificate = certificateService.verifyCertificate(id, clientIp, userAgent);

        if (optionalCertificate.isPresent()) {
            Certificate cert = optionalCertificate.get();
            model.addAttribute("certificate", cert);
            
            // Generate QR code base64 for direct HTML rendering
            String qrBase64 = qrCodeService.generateQrCodeBase64(cert.getVerificationUrl(), 160, 160);
            model.addAttribute("qrBase64", qrBase64);
            model.addAttribute("found", true);
        } else {
            model.addAttribute("found", false);
            model.addAttribute("searchedId", id);
        }

        return "verify";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid Admin credentials. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "You have been logged out successfully.");
        }
        return "login";
    }
}
