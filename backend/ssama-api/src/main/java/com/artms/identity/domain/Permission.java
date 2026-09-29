package com.artms.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 128)
    private String code;  // e.g. result:approve

    @Column(name = "description")
    private String description;

    @Column(name = "domain", nullable = false, length = 64)
    private String domain; // e.g. result
}
