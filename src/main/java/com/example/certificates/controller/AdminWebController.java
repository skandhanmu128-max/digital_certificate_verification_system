package com.example.certificates.controller;

import com.example.certificates.dto.CertificateRequest;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) CertificateStatus status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Certificate> certificatesPage = certificateService.searchCertificates(q, status, pageable);

        model.addAttribute("analytics", certificateService.getAnalyticsSummary());
        model.addAttribute("certificates", certificatesPage.getContent());
        model.addAttribute("page", certificatesPage);
        model.addAttribute("query", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", CertificateStatus.values());

        return "admin/dashboard";
    }

    @GetMapping("/certificates/new")
    public String showCreateForm(Model model) {
        CertificateRequest form = new CertificateRequest();
        form.setIssueDate(LocalDate.now());
        form.setIssuedBy("Global Tech Certification Authority");
        model.addAttribute("certificateForm", form);
        return "admin/create-certificate";
    }

    @PostMapping("/certificates/new")
    public String processCreateForm(
            @Valid @ModelAttribute("certificateForm") CertificateRequest form,
            BindingResult bindingResult,
            Authentication authentication,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/create-certificate";
        }

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String baseUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        String username = authentication != null ? authentication.getName() : "ADMIN";
        Certificate created = certificateService.createCertificate(form, username, baseUrl);

        redirectAttributes.addFlashAttribute("successMessage", 
            "Certificate generated successfully! ID: " + created.getId());

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/certificates/{id}/revoke")
    public String revokeCertificate(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason,
            RedirectAttributes redirectAttributes) {

        try {
            certificateService.revokeCertificate(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Certificate " + id + " has been revoked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error revoking certificate: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/certificates/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        byte[] pdfBytes = certificateService.generatePdfForCertificate(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate-" + id + ".pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
