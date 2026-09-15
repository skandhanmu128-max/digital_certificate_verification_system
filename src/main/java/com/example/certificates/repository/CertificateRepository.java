package com.example.certificates.repository;

import com.example.certificates.model.Certificate;
import com.example.certificates.model.CertificateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, String> {

    List<Certificate> findByStatus(CertificateStatus status);

    @Query("SELECT c FROM Certificate c WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(c.id) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.recipientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.courseName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.issuedBy) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Certificate> searchCertificates(@Param("query") String query, 
                                        @Param("status") CertificateStatus status, 
                                        Pageable pageable);

    @Query("SELECT c FROM Certificate c WHERE " +
           " LOWER(c.id) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.recipientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.courseName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Certificate> searchAllByQuery(@Param("query") String query);

    long countByStatus(CertificateStatus status);
}
