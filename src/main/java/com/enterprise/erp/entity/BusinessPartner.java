package com.enterprise.erp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="business_partner")
public class BusinessPartner {

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_code", nullable = false, unique = true, length = 20)
    private String partnerCode;

    @Column(name = "partner_type", nullable = false, length = 20)
    private String partnerType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
