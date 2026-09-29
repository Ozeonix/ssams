package com.artms.document.application;

import com.artms.audit.application.AuditService;
import com.artms.document.domain.*;
import com.artms.exam.domain.Exam;
import com.artms.exam.domain.ExamRepository;
import com.artms.result.domain.ResultSnapshot;
import com.artms.result.domain.ResultSnapshotRepository;
import com.artms.result.domain.ResultStatus;
import com.artms.result.domain.ResultSubjectSnapshot;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.exception.ValidationException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentTemplateRepository templateRepository;
    private final GeneratedDocumentRepository generatedDocumentRepository;
    private final StudentRepository studentRepository;
    private final TenantRepository tenantRepository;
    private final ExamRepository examRepository;
    private final ResultSnapshotRepository resultSnapshotRepository;
    private final AuditService auditService;

    // --- Template Management ---

    @Transactional
    public DocumentTemplateResponse createTemplate(CreateDocumentTemplateRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        List<DocumentTemplate> existing = templateRepository.findByTenantIdAndType(tenantId, req.type());
        int version = existing.stream().mapToInt(DocumentTemplate::getVersion).max().orElse(0) + 1;

        DocumentTemplate template = new DocumentTemplate();
        template.setTenantId(tenantId);
        template.setType(req.type());
        template.setName(req.name());
        template.setVersion(version);
        template.setTemplateBody(req.templateBody());
        template.setStatus(DocumentTemplateStatus.ACTIVE);

        DocumentTemplate saved = templateRepository.save(template);

        auditService.record(tenantId, userId, "document_template:create", "DocumentTemplate", saved.getId().toString(),
                Map.of("name", saved.getName(), "type", saved.getType(), "version", version));

        return DocumentTemplateResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public DocumentTemplateResponse getTemplate(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        DocumentTemplate template = templateRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("DocumentTemplate", id));
        return DocumentTemplateResponse.from(template);
    }

    @Transactional(readOnly = true)
    public Page<DocumentTemplateResponse> listTemplates(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return templateRepository.findByTenantId(tenantId, pageable).map(DocumentTemplateResponse::from);
    }

    @Transactional(readOnly = true)
    public List<DocumentTemplateResponse> listTemplatesByType(DocumentType type) {
        UUID tenantId = TenantContext.getTenantId();
        return templateRepository.findByTenantIdAndType(tenantId, type).stream()
                .map(DocumentTemplateResponse::from)
                .toList();
    }

    // --- Document Generation ---

    @Transactional
    public GeneratedDocumentResponse generateDocument(GenerateDocumentRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Student student = studentRepository.findByTenantIdAndId(tenantId, req.studentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Student", req.studentId()));

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> ResourceNotFoundException.of("Tenant", tenantId));

        DocumentType type = req.documentType();
        String renderedContent;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("documentType", type.name());
        requestData.put("studentId", student.getId().toString());

        UUID docId = UUID.randomUUID();

        switch (type) {
            case GRADE_SHEET -> {
                if (req.examId() == null) {
                    throw new ValidationException("Exam ID is required for grade sheet generation");
                }
                requestData.put("examId", req.examId().toString());
                Exam exam = examRepository.findByTenantIdAndId(tenantId, req.examId())
                        .orElseThrow(() -> ResourceNotFoundException.of("Exam", req.examId()));

                ResultSnapshot snapshot = resultSnapshotRepository
                        .findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(tenantId, req.examId(), student.getId())
                        .orElseThrow(() -> new BusinessRuleException("NO_PUBLISHED_RESULT",
                                "No published result found for student in exam: " + exam.getName()));

                if (snapshot.getPublishedAt() == null) {
                    throw new BusinessRuleException("RESULT_NOT_PUBLISHED", "Result is not yet published");
                }

                // Load with subjects
                snapshot = resultSnapshotRepository.findWithSubjectsByTenantIdAndId(tenantId, snapshot.getId())
                        .orElse(snapshot);

                requestData.put("resultSnapshotId", snapshot.getId().toString());
                requestData.put("resultVersion", snapshot.getResultVersion());

                renderedContent = renderGradeSheet(tenant, student, exam, snapshot, docId);
            }
            case TRANSCRIPT -> {
                List<ResultSnapshot> snapshots = resultSnapshotRepository
                        .findByTenantIdAndStudentIdAndPublishedAtIsNotNull(tenantId, student.getId());

                if (snapshots.isEmpty()) {
                    throw new BusinessRuleException("NO_PUBLISHED_RESULTS",
                            "Student has no published results for cumulative transcript");
                }

                List<ResultSnapshot> loaded = new ArrayList<>();
                for (ResultSnapshot s : snapshots) {
                    loaded.add(resultSnapshotRepository.findWithSubjectsByTenantIdAndId(tenantId, s.getId()).orElse(s));
                }

                renderedContent = renderTranscript(tenant, student, loaded, docId);
            }
            case CERTIFICATE -> {
                if (req.examId() == null) {
                    throw new ValidationException("Exam ID is required for certificate generation");
                }
                requestData.put("examId", req.examId().toString());
                Exam exam = examRepository.findByTenantIdAndId(tenantId, req.examId())
                        .orElseThrow(() -> ResourceNotFoundException.of("Exam", req.examId()));

                ResultSnapshot snapshot = resultSnapshotRepository
                        .findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(tenantId, req.examId(), student.getId())
                        .orElseThrow(() -> new BusinessRuleException("NO_PUBLISHED_RESULT",
                                "No published result found for student in exam: " + exam.getName()));

                if (snapshot.getResultStatus() != ResultStatus.PASS && snapshot.getResultStatus() != ResultStatus.CONDITIONAL_PASS) {
                    throw new BusinessRuleException("CANNOT_ISSUE_CERTIFICATE",
                            "Cannot issue certificate for result status: " + snapshot.getResultStatus());
                }

                requestData.put("resultSnapshotId", snapshot.getId().toString());
                renderedContent = renderCertificate(tenant, student, exam, snapshot, docId);
            }
            case PROGRESS_REPORT -> {
                if (req.examId() == null) {
                    throw new ValidationException("Exam ID is required for progress report generation");
                }
                requestData.put("examId", req.examId().toString());
                Exam exam = examRepository.findByTenantIdAndId(tenantId, req.examId())
                        .orElseThrow(() -> ResourceNotFoundException.of("Exam", req.examId()));

                ResultSnapshot snapshot = resultSnapshotRepository
                        .findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(tenantId, req.examId(), student.getId())
                        .orElseThrow(() -> new BusinessRuleException("NO_PUBLISHED_RESULT",
                                "No published result found for student in exam: " + exam.getName()));

                snapshot = resultSnapshotRepository.findWithSubjectsByTenantIdAndId(tenantId, snapshot.getId())
                        .orElse(snapshot);

                renderedContent = renderGradeSheet(tenant, student, exam, snapshot, docId);
            }
            default -> throw new ValidationException("Unsupported document type: " + type);
        }

        String checksum = computeChecksum(renderedContent);
        String objectKey = "documents/" + tenantId + "/" + type.name().toLowerCase() + "/" + docId + ".html";

        GeneratedDocument doc = new GeneratedDocument();
        doc.setId(docId);
        doc.setTenantId(tenantId);
        doc.setStudentId(student.getId());
        doc.setDocumentType(type);
        doc.setObjectKey(objectKey);
        doc.setChecksum(checksum);
        doc.setStatus(GeneratedDocumentStatus.READY);
        doc.setGeneratedAt(OffsetDateTime.now());
        doc.setRequestData(requestData);

        GeneratedDocument saved = generatedDocumentRepository.save(doc);

        auditService.record(tenantId, userId, "document:generate", "GeneratedDocument", saved.getId().toString(),
                Map.of("studentId", student.getId(), "documentType", type, "checksum", checksum));

        return GeneratedDocumentResponse.from(saved, renderedContent);
    }

    @Transactional(readOnly = true)
    public GeneratedDocumentResponse getDocument(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        GeneratedDocument doc = generatedDocumentRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("GeneratedDocument", id));
        return GeneratedDocumentResponse.from(doc, null);
    }

    @Transactional(readOnly = true)
    public Page<GeneratedDocumentResponse> listDocuments(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return generatedDocumentRepository.findByTenantId(tenantId, pageable)
                .map(d -> GeneratedDocumentResponse.from(d, null));
    }

    @Transactional(readOnly = true)
    public Page<GeneratedDocumentResponse> listStudentDocuments(UUID studentId, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return generatedDocumentRepository.findByTenantIdAndStudentId(tenantId, studentId, pageable)
                .map(d -> GeneratedDocumentResponse.from(d, null));
    }

    @Transactional
    public GeneratedDocumentResponse revokeDocument(UUID id, String reason) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        GeneratedDocument doc = generatedDocumentRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("GeneratedDocument", id));

        if (doc.getStatus() == GeneratedDocumentStatus.REVOKED) {
            throw new BusinessRuleException("ALREADY_REVOKED", "Document is already revoked");
        }

        doc.setStatus(GeneratedDocumentStatus.REVOKED);
        GeneratedDocument saved = generatedDocumentRepository.save(doc);

        auditService.record(tenantId, userId, "document:revoke", "GeneratedDocument", id.toString(),
                Map.of("reason", reason != null ? reason : ""));

        return GeneratedDocumentResponse.from(saved, null);
    }

    // --- Public Document Verification ---

    @Transactional(readOnly = true)
    public DocumentVerificationResponse verifyDocument(UUID id) {
        Optional<GeneratedDocument> docOpt = generatedDocumentRepository.findById(id);
        if (docOpt.isEmpty()) {
            return new DocumentVerificationResponse(
                    false, id, null, null, null, null, null, null, null,
                    "Document not found or invalid verification identifier");
        }

        GeneratedDocument doc = docOpt.get();
        Tenant tenant = tenantRepository.findById(doc.getTenantId()).orElse(null);
        String tenantName = tenant != null ? tenant.getName() : "Unknown Institution";

        Student student = null;
        if (doc.getStudentId() != null) {
            student = studentRepository.findById(doc.getStudentId()).orElse(null);
        }
        String studentName = student != null ? student.getFullName() : "N/A";
        String admissionNo = student != null ? student.getAdmissionNo() : "N/A";

        if (doc.getStatus() == GeneratedDocumentStatus.REVOKED) {
            return new DocumentVerificationResponse(
                    false, id, doc.getDocumentType(), doc.getStatus(),
                    studentName, admissionNo, tenantName, doc.getGeneratedAt(), doc.getChecksum(),
                    "Document has been REVOKED by the issuing institution");
        }

        return new DocumentVerificationResponse(
                true, id, doc.getDocumentType(), doc.getStatus(),
                studentName, admissionNo, tenantName, doc.getGeneratedAt(), doc.getChecksum(),
                "Document is authentic and valid");
    }

    // --- HTML Rendering Engine ---

    private String renderGradeSheet(Tenant tenant, Student student, Exam exam, ResultSnapshot snapshot, UUID docId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<title>Official Grade Sheet - ").append(escapeHtml(student.getFullName())).append("</title>");
        sb.append("<style>");
        sb.append("body { font-family: 'Helvetica Neue', Arial, sans-serif; color: #1e293b; margin: 40px; }");
        sb.append(".header { text-align: center; border-bottom: 2px solid #0f172a; padding-bottom: 20px; }");
        sb.append(".institution-name { font-size: 24px; font-weight: bold; text-transform: uppercase; color: #0f172a; }");
        sb.append(".doc-title { font-size: 18px; margin-top: 8px; color: #475569; font-weight: 600; }");
        sb.append(".meta-grid { display: grid; grid-template-columns: 1fr 1fr; margin: 24px 0; gap: 12px; font-size: 14px; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        sb.append("th, td { border: 1px solid #cbd5e1; padding: 10px; text-align: left; font-size: 13px; }");
        sb.append("th { background-color: #f1f5f9; font-weight: 600; }");
        sb.append(".summary-box { margin-top: 24px; display: flex; justify-content: space-between; background: #f8fafc; padding: 16px; border: 1px solid #e2e8f0; border-radius: 6px; }");
        sb.append(".footer { margin-top: 40px; display: flex; justify-content: space-between; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; padding-top: 16px; }");
        sb.append("</style></head><body>");

        sb.append("<div class='header'>");
        sb.append("<div class='institution-name'>").append(escapeHtml(tenant.getName())).append("</div>");
        sb.append("<div class='doc-title'>OFFICIAL GRADE SHEET</div>");
        sb.append("<div style='font-size: 14px; color: #64748b;'>Examination: ").append(escapeHtml(exam.getName())).append("</div>");
        sb.append("</div>");

        sb.append("<div class='meta-grid'>");
        sb.append("<div><strong>Student Name:</strong> ").append(escapeHtml(student.getFullName())).append("</div>");
        sb.append("<div><strong>Admission No:</strong> ").append(escapeHtml(student.getAdmissionNo())).append("</div>");
        if (student.getRegistrationNo() != null) {
            sb.append("<div><strong>Registration No:</strong> ").append(escapeHtml(student.getRegistrationNo())).append("</div>");
        }
        sb.append("<div><strong>Result Version:</strong> ").append(snapshot.getResultVersion()).append("</div>");
        sb.append("</div>");

        sb.append("<table>");
        sb.append("<thead><tr>");
        sb.append("<th>Subject</th><th>Credit Hours</th><th>Full Marks</th><th>Obtained</th><th>Percentage</th><th>Grade</th><th>Grade Point</th>");
        sb.append("</tr></thead><tbody>");

        for (ResultSubjectSnapshot s : snapshot.getSubjectSnapshots()) {
            sb.append("<tr>");
            sb.append("<td>").append(escapeHtml(s.getSubjectNameSnapshot())).append("</td>");
            sb.append("<td>").append(s.getCreditHours()).append("</td>");
            sb.append("<td>").append(s.getTotalFullMarks()).append("</td>");
            sb.append("<td>").append(s.getTotalObtained()).append("</td>");
            sb.append("<td>").append(s.getPercentage() != null ? s.getPercentage() + "%" : "-").append("</td>");
            sb.append("<td><strong>").append(s.getLetterGrade() != null ? s.getLetterGrade() : "-").append("</strong></td>");
            sb.append("<td>").append(s.getGradePoint() != null ? s.getGradePoint() : "-").append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        sb.append("<div class='summary-box'>");
        sb.append("<div><strong>Total Credits:</strong> ").append(snapshot.getTotalCredits()).append("</div>");
        sb.append("<div><strong>GPA:</strong> <span style='font-size: 18px; font-weight: bold; color: #0284c7;'>").append(snapshot.getGpa() != null ? snapshot.getGpa() : "N/A").append("</span></div>");
        sb.append("<div><strong>Result Status:</strong> <span style='font-weight: bold;'>").append(snapshot.getResultStatus()).append("</span></div>");
        sb.append("</div>");

        sb.append("<div class='footer'>");
        sb.append("<div>Document ID: ").append(docId).append("</div>");
        sb.append("<div>Issued: ").append(OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("</div>");
        sb.append("<div>Verified Hash: ").append(snapshot.getImmutableHash() != null ? snapshot.getImmutableHash().substring(0, 16) + "..." : "N/A").append("</div>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String renderTranscript(Tenant tenant, Student student, List<ResultSnapshot> snapshots, UUID docId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<title>Official Academic Transcript - ").append(escapeHtml(student.getFullName())).append("</title>");
        sb.append("<style>");
        sb.append("body { font-family: 'Helvetica Neue', Arial, sans-serif; color: #1e293b; margin: 40px; }");
        sb.append(".header { text-align: center; border-bottom: 2px solid #0f172a; padding-bottom: 20px; }");
        sb.append(".institution-name { font-size: 24px; font-weight: bold; text-transform: uppercase; color: #0f172a; }");
        sb.append(".doc-title { font-size: 18px; margin-top: 8px; color: #475569; font-weight: 600; }");
        sb.append(".meta-grid { display: grid; grid-template-columns: 1fr 1fr; margin: 24px 0; gap: 12px; font-size: 14px; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 14px; margin-bottom: 24px; }");
        sb.append("th, td { border: 1px solid #cbd5e1; padding: 8px 10px; text-align: left; font-size: 12px; }");
        sb.append("th { background-color: #f1f5f9; font-weight: 600; }");
        sb.append(".exam-header { font-size: 15px; font-weight: bold; margin-top: 20px; color: #0f172a; background: #e2e8f0; padding: 6px 10px; border-radius: 4px; }");
        sb.append(".footer { margin-top: 40px; display: flex; justify-content: space-between; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; padding-top: 16px; }");
        sb.append("</style></head><body>");

        sb.append("<div class='header'>");
        sb.append("<div class='institution-name'>").append(escapeHtml(tenant.getName())).append("</div>");
        sb.append("<div class='doc-title'>OFFICIAL ACADEMIC TRANSCRIPT</div>");
        sb.append("</div>");

        sb.append("<div class='meta-grid'>");
        sb.append("<div><strong>Student Name:</strong> ").append(escapeHtml(student.getFullName())).append("</div>");
        sb.append("<div><strong>Admission No:</strong> ").append(escapeHtml(student.getAdmissionNo())).append("</div>");
        if (student.getRegistrationNo() != null) {
            sb.append("<div><strong>Registration No:</strong> ").append(escapeHtml(student.getRegistrationNo())).append("</div>");
        }
        sb.append("<div><strong>Total Exams Completed:</strong> ").append(snapshots.size()).append("</div>");
        sb.append("</div>");

        for (ResultSnapshot s : snapshots) {
            Exam ex = examRepository.findById(s.getExamId()).orElse(null);
            String examName = ex != null ? ex.getName() : "Examination";

            sb.append("<div class='exam-header'>").append(escapeHtml(examName)).append(" — GPA: ").append(s.getGpa() != null ? s.getGpa() : "N/A").append(" (").append(s.getResultStatus()).append(")</div>");
            sb.append("<table>");
            sb.append("<thead><tr><th>Subject</th><th>Credit Hours</th><th>Total Marks</th><th>Obtained</th><th>Grade</th><th>Grade Point</th></tr></thead><tbody>");
            for (ResultSubjectSnapshot sub : s.getSubjectSnapshots()) {
                sb.append("<tr>");
                sb.append("<td>").append(escapeHtml(sub.getSubjectNameSnapshot())).append("</td>");
                sb.append("<td>").append(sub.getCreditHours()).append("</td>");
                sb.append("<td>").append(sub.getTotalFullMarks()).append("</td>");
                sb.append("<td>").append(sub.getTotalObtained()).append("</td>");
                sb.append("<td><strong>").append(sub.getLetterGrade() != null ? sub.getLetterGrade() : "-").append("</strong></td>");
                sb.append("<td>").append(sub.getGradePoint() != null ? sub.getGradePoint() : "-").append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        sb.append("<div class='footer'>");
        sb.append("<div>Document ID: ").append(docId).append("</div>");
        sb.append("<div>Issued: ").append(OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("</div>");
        sb.append("<div>Official Record</div>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String renderCertificate(Tenant tenant, Student student, Exam exam, ResultSnapshot snapshot, UUID docId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<title>Certificate of Achievement - ").append(escapeHtml(student.getFullName())).append("</title>");
        sb.append("<style>");
        sb.append("body { font-family: 'Georgia', serif; text-align: center; color: #0f172a; margin: 40px; padding: 40px; border: 10px double #cbd5e1; }");
        sb.append(".institution { font-size: 28px; font-weight: bold; text-transform: uppercase; letter-spacing: 2px; }");
        sb.append(".cert-title { font-size: 22px; font-style: italic; margin-top: 30px; color: #d97706; }");
        sb.append(".cert-body { font-size: 16px; margin: 30px auto; max-width: 600px; line-height: 1.8; }");
        sb.append(".student-name { font-size: 26px; font-weight: bold; text-decoration: underline; margin: 15px 0; }");
        sb.append(".meta { margin-top: 40px; display: flex; justify-content: space-around; font-size: 14px; font-family: sans-serif; }");
        sb.append(".seal { border-top: 1px solid #0f172a; padding-top: 8px; width: 180px; }");
        sb.append("</style></head><body>");

        sb.append("<div class='institution'>").append(escapeHtml(tenant.getName())).append("</div>");
        sb.append("<div class='cert-title'>Certificate of Academic Achievement</div>");
        sb.append("<div class='cert-body'>");
        sb.append("This is to certify that");
        sb.append("<div class='student-name'>").append(escapeHtml(student.getFullName())).append("</div>");
        sb.append("bearing Admission Number <strong>").append(escapeHtml(student.getAdmissionNo())).append("</strong> ");
        sb.append("has successfully completed the <strong>").append(escapeHtml(exam.getName())).append("</strong> ");
        sb.append("securing a Grade Point Average (GPA) of <strong>").append(snapshot.getGpa() != null ? snapshot.getGpa() : "N/A").append("</strong> ");
        sb.append("with result status <strong>").append(snapshot.getResultStatus()).append("</strong>.");
        sb.append("</div>");

        sb.append("<div class='meta'>");
        sb.append("<div class='seal'>Authorized Signatory</div>");
        sb.append("<div class='seal'>Principal / Head</div>");
        sb.append("</div>");

        sb.append("<div style='margin-top: 40px; font-size: 11px; color: #94a3b8; font-family: sans-serif;'>");
        sb.append("Document ID: ").append(docId).append(" | Issued: ").append(OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String computeChecksum(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
