package com.example.certificates.service;

import com.example.certificates.model.Certificate;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    @Autowired
    private QrCodeService qrCodeService;

    public byte[] generateCertificatePdf(Certificate cert) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Landscape A4 document
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            document.open();

            // Draw Decorative Border
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(3f);
            cb.setColorStroke(new Color(37, 99, 235)); // Royal Blue #2563eb
            cb.rectangle(20, 20, PageSize.A4.getHeight() - 40, PageSize.A4.getWidth() - 40);
            cb.stroke();

            cb.setLineWidth(1f);
            cb.setColorStroke(new Color(217, 119, 6)); // Amber Gold #d97706
            cb.rectangle(26, 26, PageSize.A4.getHeight() - 52, PageSize.A4.getWidth() - 52);
            cb.stroke();

            // Header - Certificate Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, new Color(30, 41, 59));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, new Color(100, 116, 139));
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, new Color(37, 99, 235));
            Font courseFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(15, 23, 42));
            Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(71, 85, 105));
            Font idFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 10, new Color(100, 116, 139));

            Paragraph header = new Paragraph("CERTIFICATE OF ACHIEVEMENT", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingBefore(15);
            header.setSpacingAfter(10);
            document.add(header);

            Paragraph subHeader = new Paragraph("THIS IS PROUDLY PRESENTED TO", subTitleFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(15);
            document.add(subHeader);

            // Recipient Name
            Paragraph name = new Paragraph(cert.getRecipientName(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(15);
            document.add(name);

            // Completion text
            Paragraph text1 = new Paragraph("for successfully completing the course / qualification program", subTitleFont);
            text1.setAlignment(Element.ALIGN_CENTER);
            text1.setSpacingAfter(15);
            document.add(text1);

            // Course Name
            Paragraph course = new Paragraph(cert.getCourseName(), courseFont);
            course.setAlignment(Element.ALIGN_CENTER);
            course.setSpacingAfter(25);
            document.add(course);

            // Footer Table: Issued By, Dates, QR Code
            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(90);
            footerTable.setWidths(new float[]{3.5f, 3.5f, 3.0f});

            // Column 1: Issuer Info
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.addElement(new Paragraph("ISSUED BY", subTitleFont));
            cell1.addElement(new Paragraph(cert.getIssuedBy(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(30, 41, 59))));
            String issueStr = cert.getIssueDate() != null ? cert.getIssueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A";
            cell1.addElement(new Paragraph("Issue Date: " + issueStr, detailFont));
            if (cert.getExpiryDate() != null) {
                cell1.addElement(new Paragraph("Expiry Date: " + cert.getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), detailFont));
            } else {
                cell1.addElement(new Paragraph("Expiry Date: Never (Lifetime)", detailFont));
            }
            footerTable.addCell(cell1);

            // Column 2: Status & Verification ID
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.addElement(new Paragraph("STATUS & VERIFICATION", subTitleFont));
            Font statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, 
                cert.getStatus() == com.example.certificates.model.CertificateStatus.VALID ? new Color(16, 185, 129) : new Color(239, 68, 68));
            cell2.addElement(new Paragraph("Status: " + cert.getStatus().name(), statusFont));
            cell2.addElement(new Paragraph("Certificate ID:", detailFont));
            cell2.addElement(new Paragraph(cert.getId(), idFont));
            footerTable.addCell(cell2);

            // Column 3: QR Code
            PdfPCell cell3 = new PdfPCell();
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

            byte[] qrPng = qrCodeService.generateQrCodePng(cert.getVerificationUrl(), 120, 120);
            Image qrImage = Image.getInstance(qrPng);
            qrImage.scaleToFit(90, 90);
            qrImage.setAlignment(Element.ALIGN_RIGHT);

            cell3.addElement(qrImage);
            Paragraph qrCaption = new Paragraph("Scan to verify online", FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            qrCaption.setAlignment(Element.ALIGN_RIGHT);
            cell3.addElement(qrCaption);

            footerTable.addCell(cell3);

            document.add(footerTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF certificate for ID: " + cert.getId(), e);
        }
    }
}
