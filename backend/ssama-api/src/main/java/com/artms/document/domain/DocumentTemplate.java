package com.artms.document.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_template")
@Getter
@Setter
@NoArgsConstructor
public class DocumentTemplate extends TenantBaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 64)
    private DocumentType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "template_body", columnDefinition = "text")
    private String templateBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DocumentTemplateStatus status = DocumentTemplateStatus.DRAFT;
}
