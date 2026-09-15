# Digital Certificate Verification System 🛡️

A production-ready, full-stack **Digital Certificate Generation & Verification System** built with **Java 17**, **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, **OpenPDF**, and **ZXing QR Engine**.

---

## 🌟 Key Features

- 📜 **Certificate Generation**: Issues unique cryptographically verifiable certificates with UUID v4 primary keys.
- 📱 **Embedded ZXing QR Codes**: Renders high-density QR codes embedded directly into PDF credentials pointing to the online verification portal.
- 🔍 **Public Verification Portal**: Instant lookup page where anyone can enter a Certificate ID or scan a QR code to verify authenticity and inspect status (`VALID`, `REVOKED`, `EXPIRED`).
- ⚡ **Real-Time Tamper & Expiry Check**: Automatically updates expired credentials based on issue/expiration dates and logs every public verification attempt for auditing.
- 📊 **Admin Dashboard & Analytics**: Protected control center displaying key metrics (Total Issued, Total Valid, Total Revoked, Total Expired, Verification Hits) with search, status filtering, and revocation controls.
- 📄 **PDF & CSV Export**: Server-side vector PDF generation with decorative borders and bulk CSV metadata export for administrators.

---

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Backend Framework** | Java 17+, Spring Boot 3.2.3, Spring Security 6, Spring Data JPA |
| **Frontend UI** | Thymeleaf, Bootstrap 5, Custom Glassmorphism CSS, Bootstrap Icons |
| **PDF Generation** | OpenPDF (`com.github.librepdf:openpdf`) |
| **QR Code Engine** | Google ZXing (`com.google.zxing:core`, `javase`) |
| **Database** | H2 (In-Memory Dev Profile) / PostgreSQL 15 (Docker Production Profile) |
| **Build & Container** | Apache Maven 3.9+, Docker, Docker Compose |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, Spring Security Test |

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17 JDK** or higher
- **Apache Maven 3.8+** (or Docker)

### Option 1: Run Locally with Maven & In-Memory H2 DB

1. Clone the repository and navigate into the project directory:
   ```bash
   git clone https://github.com/example/digital-certificate-verification-system.git
   cd "Digital Certificate Verification System"
   ```

2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

3. Access the application in your browser:
   - **Public Verification Portal**: [http://localhost:8080](http://localhost:8080)
   - **Admin Login**: [http://localhost:8080/login](http://localhost:8080/login)
   - **Admin Dashboard**: [http://localhost:8080/admin/dashboard](http://localhost:8080/admin/dashboard)
   - **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:certdb`, User: `sa`, Password: empty)

---

### Option 2: Run with Docker Compose (App + PostgreSQL)

Build and launch both the Spring Boot web application and PostgreSQL database in isolated containers:

```bash
docker compose up --build
```

To stop containers:
```bash
docker compose down
```

---

## 🔐 Default Admin Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123password` |

---

## 🔌 REST API Endpoints

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/certificates/{id}` | Public | Get detailed certificate metadata and status |
| `GET` | `/api/certificates/{id}/pdf` | Public | Download official PDF certificate |
| `GET` | `/api/certificates/search` | Public | Search certificates with pagination and query filters |
| `POST` | `/api/certificates` | Admin | Issue a new certificate |
| `POST` | `/api/certificates/{id}/revoke` | Admin | Revoke an existing certificate |
| `GET` | `/api/admin/analytics` | Admin | Fetch analytics summary JSON |
| `GET` | `/api/admin/export/csv` | Admin | Export all certificate records as CSV file |

---

## 🧪 Running Unit & Integration Tests

Execute the full suite of JUnit 5 and Mockito tests:

```bash
mvn test
```

---

## 📄 License

Distributed under the [MIT License](LICENSE).
