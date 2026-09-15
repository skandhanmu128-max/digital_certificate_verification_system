-- Initial Sample Data for Digital Certificate Verification System
INSERT INTO certificates (id, recipient_name, recipient_email, course_name, issued_by, issue_date, expiry_date, status, verification_url, created_at, updated_at, created_by)
VALUES 
('c8f1e2a0-4b5c-4d3e-8f1a-2b3c4d5e6f7a', 'Alice Johnson', 'alice@example.com', 'Advanced Java & Spring Boot Masterclass', 'Global Tech Institute', '2026-01-15', NULL, 'VALID', 'http://localhost:8080/verify/c8f1e2a0-4b5c-4d3e-8f1a-2b3c4d5e6f7a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),

('e9d8c7b6-5a4f-3e2d-1c0b-9a8b7c6d5e4f', 'Bob Smith', 'bob@example.com', 'Full-Stack Web Development Bootcamp', 'Digital Academy', '2025-06-20', NULL, 'REVOKED', 'http://localhost:8080/verify/e9d8c7b6-5a4f-3e2d-1c0b-9a8b7c6d5e4f', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),

('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'Carol Danvers', 'carol@example.com', 'Certified Cloud Solutions Architect', 'Cloud Standard Alliance', '2024-03-10', '2025-03-10', 'EXPIRED', 'http://localhost:8080/verify/a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),

('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'David Miller', 'david@example.com', 'Cybersecurity Fundamentals & Defense', 'Tech Defense Lab', '2026-02-01', NULL, 'VALID', 'http://localhost:8080/verify/f47ac10b-58cc-4372-a567-0e02b2c3d479', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM');
