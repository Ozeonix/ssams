.
├── admin
│   └── ssams-admin
│       ├── analysis_options.yaml
│       ├── build
│       │   ├── native_assets
│       │   │   ├── flutter-tester
│       │   │   └── linux
│       │   │       └── native_assets.json
│       │   ├── test_cache
│       │   │   └── build
│       │   │       └── 89a6598c8854ed031dfc25d83c80860e.cache.dill.track.dill
│       │   └── unit_test_assets
│       │       ├── AssetManifest.bin
│       │       ├── FontManifest.json
│       │       ├── fonts
│       │       │   └── MaterialIcons-Regular.otf
│       │       ├── NativeAssetsManifest.json
│       │       ├── NOTICES.Z
│       │       ├── packages
│       │       │   └── cupertino_icons
│       │       │       └── assets
│       │       │           └── CupertinoIcons.ttf
│       │       └── shaders
│       │           ├── ink_sparkle.frag
│       │           └── stretch_effect.frag
│       ├── Dockerfile
│       ├── lib
│       │   ├── core
│       │   │   ├── auth
│       │   │   │   └── auth_provider.dart
│       │   │   ├── network
│       │   │   │   └── api_client.dart
│       │   │   ├── router
│       │   │   │   └── admin_router.dart
│       │   │   └── theme
│       │   │       └── admin_theme.dart
│       │   ├── features
│       │   │   ├── academic
│       │   │   │   └── presentation
│       │   │   │       └── academic_screen.dart
│       │   │   ├── attendance
│       │   │   │   └── presentation
│       │   │   │       └── attendance_screen.dart
│       │   │   ├── auth
│       │   │   │   └── presentation
│       │   │   │       └── login_screen.dart
│       │   │   ├── dashboard
│       │   │   │   └── presentation
│       │   │   │       └── dashboard_screen.dart
│       │   │   ├── exams
│       │   │   │   └── presentation
│       │   │   │       └── exams_screen.dart
│       │   │   ├── settings
│       │   │   │   └── presentation
│       │   │   │       └── settings_screen.dart
│       │   │   ├── shell
│       │   │   │   └── presentation
│       │   │   │       └── admin_shell.dart
│       │   │   └── students
│       │   │       └── presentation
│       │   │           └── students_screen.dart
│       │   └── main.dart
│       ├── nginx.conf
│       ├── pubspec.lock
│       ├── pubspec.yaml
│       ├── README.md
│       ├── ssams_admin.iml
│       ├── test
│       │   └── widget_test.dart
│       └── web
│           ├── favicon.png
│           ├── icons
│           │   ├── Icon-192.png
│           │   ├── Icon-512.png
│           │   ├── Icon-maskable-192.png
│           │   └── Icon-maskable-512.png
│           ├── index.html
│           └── manifest.json
├── AGENT_BOOTSTRAP_PROMPT.md
├── AGENT_RULES.md
├── backend
│   └── ssama-api
│       ├── Dockerfile
│       ├── pom.xml
│       ├── src
│       │   ├── main
│       │   │   ├── java
│       │   │   │   └── com
│       │   │   │       └── artms
│       │   │   │           ├── academic
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AcademicYearService.java
│       │   │   │           │   │   ├── AddCurriculumSubjectRequest.java
│       │   │   │           │   │   ├── ClassGroupService.java
│       │   │   │           │   │   ├── CreateAcademicYearRequest.java
│       │   │   │           │   │   ├── CreateClassGroupRequest.java
│       │   │   │           │   │   ├── CreateCurriculumRequest.java
│       │   │   │           │   │   ├── CreateDepartmentRequest.java
│       │   │   │           │   │   ├── CreateProgramRequest.java
│       │   │   │           │   │   ├── CreateSubjectRequest.java
│       │   │   │           │   │   ├── CurriculumService.java
│       │   │   │           │   │   ├── DepartmentService.java
│       │   │   │           │   │   ├── ProgramService.java
│       │   │   │           │   │   └── SubjectService.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── AcademicYear.java
│       │   │   │           │   │   ├── AcademicYearRepository.java
│       │   │   │           │   │   ├── AcademicYearStatus.java
│       │   │   │           │   │   ├── AssessmentType.java
│       │   │   │           │   │   ├── ClassGroup.java
│       │   │   │           │   │   ├── ClassGroupRepository.java
│       │   │   │           │   │   ├── Curriculum.java
│       │   │   │           │   │   ├── CurriculumRepository.java
│       │   │   │           │   │   ├── CurriculumStatus.java
│       │   │   │           │   │   ├── CurriculumSubject.java
│       │   │   │           │   │   ├── CurriculumSubjectRepository.java
│       │   │   │           │   │   ├── Department.java
│       │   │   │           │   │   ├── DepartmentRepository.java
│       │   │   │           │   │   ├── Program.java
│       │   │   │           │   │   ├── ProgramRepository.java
│       │   │   │           │   │   ├── SubjectComponent.java
│       │   │   │           │   │   ├── SubjectComponentRepository.java
│       │   │   │           │   │   ├── Subject.java
│       │   │   │           │   │   ├── SubjectRepository.java
│       │   │   │           │   │   ├── SubjectType.java
│       │   │   │           │   │   ├── Term.java
│       │   │   │           │   │   └── TermRepository.java
│       │   │   │           │   └── web
│       │   │   │           │       ├── AcademicYearController.java
│       │   │   │           │       ├── ClassGroupController.java
│       │   │   │           │       ├── CurriculumController.java
│       │   │   │           │       ├── DepartmentController.java
│       │   │   │           │       ├── ProgramController.java
│       │   │   │           │       └── SubjectController.java
│       │   │   │           ├── ArtmsApplication.java
│       │   │   │           ├── attendance
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AttendanceEntryDto.java
│       │   │   │           │   │   ├── AttendanceRecordResponse.java
│       │   │   │           │   │   ├── AttendanceService.java
│       │   │   │           │   │   ├── AttendanceSessionResponse.java
│       │   │   │           │   │   ├── CorrectAttendanceRecordRequest.java
│       │   │   │           │   │   ├── StudentAttendanceSummaryDto.java
│       │   │   │           │   │   ├── SubjectAttendanceStatDto.java
│       │   │   │           │   │   └── SubmitAttendanceRequest.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── AttendanceRecord.java
│       │   │   │           │   │   ├── AttendanceRecordRepository.java
│       │   │   │           │   │   ├── AttendanceSession.java
│       │   │   │           │   │   ├── AttendanceSessionRepository.java
│       │   │   │           │   │   ├── AttendanceSessionStatus.java
│       │   │   │           │   │   └── AttendanceStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       └── AttendanceController.java
│       │   │   │           ├── audit
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AuditEvent.java
│       │   │   │           │   │   └── AuditService.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── AuditLog.java
│       │   │   │           │   │   └── AuditLogRepository.java
│       │   │   │           │   └── web
│       │   │   │           │       └── AuditLogController.java
│       │   │   │           ├── document
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── CreateDocumentTemplateRequest.java
│       │   │   │           │   │   ├── DocumentService.java
│       │   │   │           │   │   ├── DocumentTemplateResponse.java
│       │   │   │           │   │   ├── DocumentVerificationResponse.java
│       │   │   │           │   │   ├── GeneratedDocumentResponse.java
│       │   │   │           │   │   └── GenerateDocumentRequest.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── DocumentTemplate.java
│       │   │   │           │   │   ├── DocumentTemplateRepository.java
│       │   │   │           │   │   ├── DocumentTemplateStatus.java
│       │   │   │           │   │   ├── DocumentType.java
│       │   │   │           │   │   ├── GeneratedDocument.java
│       │   │   │           │   │   ├── GeneratedDocumentRepository.java
│       │   │   │           │   │   └── GeneratedDocumentStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       ├── DocumentController.java
│       │   │   │           │       └── DocumentTemplateController.java
│       │   │   │           ├── enrollment
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── BatchEnrollClassRequest.java
│       │   │   │           │   │   ├── EnrollmentResponse.java
│       │   │   │           │   │   ├── EnrollmentService.java
│       │   │   │           │   │   ├── EnrollStudentRequest.java
│       │   │   │           │   │   ├── PromoteStudentsRequest.java
│       │   │   │           │   │   ├── StudentEnrollmentDto.java
│       │   │   │           │   │   └── TransferStudentRequest.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── Enrollment.java
│       │   │   │           │   │   ├── EnrollmentRepository.java
│       │   │   │           │   │   ├── EnrollmentStatus.java
│       │   │   │           │   │   ├── SubjectEnrollment.java
│       │   │   │           │   │   ├── SubjectEnrollmentRepository.java
│       │   │   │           │   │   └── SubjectEnrollmentStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       └── EnrollmentController.java
│       │   │   │           ├── exam
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AddExamSubjectRequest.java
│       │   │   │           │   │   ├── BatchMarkEntryRequest.java
│       │   │   │           │   │   ├── CreateExamRequest.java
│       │   │   │           │   │   ├── ExamMarksGridDto.java
│       │   │   │           │   │   ├── ExamResponse.java
│       │   │   │           │   │   ├── ExamService.java
│       │   │   │           │   │   ├── ExamSubjectResponse.java
│       │   │   │           │   │   ├── ExamWorkflowRequest.java
│       │   │   │           │   │   ├── MarkEntryDto.java
│       │   │   │           │   │   └── MarkEntryResponse.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── Exam.java
│       │   │   │           │   │   ├── ExamRepository.java
│       │   │   │           │   │   ├── ExamStatus.java
│       │   │   │           │   │   ├── ExamSubject.java
│       │   │   │           │   │   ├── ExamSubjectRepository.java
│       │   │   │           │   │   ├── ExamSubjectStatus.java
│       │   │   │           │   │   ├── ExamType.java
│       │   │   │           │   │   ├── MarkEntry.java
│       │   │   │           │   │   ├── MarkEntryRepository.java
│       │   │   │           │   │   └── MarkStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       └── ExamController.java
│       │   │   │           ├── grading
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── CreateGradingSchemeRequest.java
│       │   │   │           │   │   └── GradingSchemeService.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── GradeBand.java
│       │   │   │           │   │   ├── GradingScheme.java
│       │   │   │           │   │   ├── GradingSchemeRepository.java
│       │   │   │           │   │   ├── GradingSchemeStatus.java
│       │   │   │           │   │   ├── GradingSchemeValidator.java
│       │   │   │           │   │   └── RoundingMode.java
│       │   │   │           │   ├── engine
│       │   │   │           │   │   ├── ComponentMark.java
│       │   │   │           │   │   ├── ComponentResult.java
│       │   │   │           │   │   ├── GradingEngine.java
│       │   │   │           │   │   ├── MarkStatus.java
│       │   │   │           │   │   ├── OverallResultStatus.java
│       │   │   │           │   │   ├── StudentResult.java
│       │   │   │           │   │   ├── SubjectResult.java
│       │   │   │           │   │   └── SubjectResultStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       └── GradingSchemeController.java
│       │   │   │           ├── identity
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AuthService.java
│       │   │   │           │   │   ├── CreateRoleRequest.java
│       │   │   │           │   │   ├── ForgotPasswordRequest.java
│       │   │   │           │   │   ├── LoginRequest.java
│       │   │   │           │   │   ├── LoginResponse.java
│       │   │   │           │   │   ├── RefreshResponse.java
│       │   │   │           │   │   ├── ResetPasswordRequest.java
│       │   │   │           │   │   ├── RoleResponse.java
│       │   │   │           │   │   ├── RoleService.java
│       │   │   │           │   │   ├── SessionResponse.java
│       │   │   │           │   │   └── UserSummary.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── MembershipStatus.java
│       │   │   │           │   │   ├── MfaProvider.java
│       │   │   │           │   │   ├── PasswordResetToken.java
│       │   │   │           │   │   ├── PasswordResetTokenRepository.java
│       │   │   │           │   │   ├── Permission.java
│       │   │   │           │   │   ├── PermissionRepository.java
│       │   │   │           │   │   ├── RefreshToken.java
│       │   │   │           │   │   ├── RefreshTokenRepository.java
│       │   │   │           │   │   ├── Role.java
│       │   │   │           │   │   ├── RoleRepository.java
│       │   │   │           │   │   ├── UserAccount.java
│       │   │   │           │   │   ├── UserAccountRepository.java
│       │   │   │           │   │   ├── UserStatus.java
│       │   │   │           │   │   ├── UserTenantMembership.java
│       │   │   │           │   │   └── UserTenantMembershipRepository.java
│       │   │   │           │   ├── infrastructure
│       │   │   │           │   │   ├── ArtmsPrincipal.java
│       │   │   │           │   │   ├── DefaultMfaProvider.java
│       │   │   │           │   │   ├── JwtAuthenticationFilter.java
│       │   │   │           │   │   └── JwtService.java
│       │   │   │           │   └── web
│       │   │   │           │       ├── AuthController.java
│       │   │   │           │       ├── RefreshTokenRequest.java
│       │   │   │           │       └── RoleController.java
│       │   │   │           ├── notification
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── NotificationResponse.java
│       │   │   │           │   │   ├── NotificationService.java
│       │   │   │           │   │   ├── NotificationSyncResponse.java
│       │   │   │           │   │   └── OutboxProcessor.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── Notification.java
│       │   │   │           │   │   ├── NotificationRepository.java
│       │   │   │           │   │   ├── OutboxEvent.java
│       │   │   │           │   │   └── OutboxEventRepository.java
│       │   │   │           │   ├── infrastructure
│       │   │   │           │   │   ├── PushAdapter.java
│       │   │   │           │   │   ├── RealtimeEventPublisher.java
│       │   │   │           │   │   └── WebSocketConfig.java
│       │   │   │           │   └── web
│       │   │   │           │       └── NotificationController.java
│       │   │   │           ├── result
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── ResultCorrectionRequest.java
│       │   │   │           │   │   ├── ResultService.java
│       │   │   │           │   │   ├── ResultSnapshotResponse.java
│       │   │   │           │   │   └── ResultSubjectDto.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── ResultSnapshot.java
│       │   │   │           │   │   ├── ResultSnapshotRepository.java
│       │   │   │           │   │   ├── ResultStatus.java
│       │   │   │           │   │   └── ResultSubjectSnapshot.java
│       │   │   │           │   └── web
│       │   │   │           │       └── ResultController.java
│       │   │   │           ├── shared
│       │   │   │           │   ├── config
│       │   │   │           │   │   └── SecurityConfig.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   └── TenantBaseEntity.java
│       │   │   │           │   ├── exception
│       │   │   │           │   │   ├── BusinessRuleException.java
│       │   │   │           │   │   ├── OptimisticLockConflictException.java
│       │   │   │           │   │   ├── ResourceNotFoundException.java
│       │   │   │           │   │   ├── TenantAccessException.java
│       │   │   │           │   │   └── ValidationException.java
│       │   │   │           │   ├── tenant
│       │   │   │           │   │   └── TenantContext.java
│       │   │   │           │   └── web
│       │   │   │           │       ├── ApiError.java
│       │   │   │           │       ├── ApiResponse.java
│       │   │   │           │       └── GlobalExceptionHandler.java
│       │   │   │           ├── staff
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AssignTeacherSubjectRequest.java
│       │   │   │           │   │   ├── CreateTeacherRequest.java
│       │   │   │           │   │   └── TeacherService.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── Teacher.java
│       │   │   │           │   │   ├── TeacherRepository.java
│       │   │   │           │   │   ├── TeacherStatus.java
│       │   │   │           │   │   ├── TeacherSubject.java
│       │   │   │           │   │   └── TeacherSubjectRepository.java
│       │   │   │           │   └── web
│       │   │   │           │       └── TeacherController.java
│       │   │   │           ├── student
│       │   │   │           │   ├── application
│       │   │   │           │   │   ├── AddGuardianRequest.java
│       │   │   │           │   │   ├── BatchImportResult.java
│       │   │   │           │   │   ├── CreateStudentRequest.java
│       │   │   │           │   │   ├── StudentService.java
│       │   │   │           │   │   └── UpdateStudentRequest.java
│       │   │   │           │   ├── domain
│       │   │   │           │   │   ├── Gender.java
│       │   │   │           │   │   ├── StudentGuardian.java
│       │   │   │           │   │   ├── StudentGuardianRepository.java
│       │   │   │           │   │   ├── Student.java
│       │   │   │           │   │   ├── StudentRepository.java
│       │   │   │           │   │   └── StudentStatus.java
│       │   │   │           │   └── web
│       │   │   │           │       └── StudentController.java
│       │   │   │           └── tenant
│       │   │   │               ├── application
│       │   │   │               │   ├── CreateTenantRequest.java
│       │   │   │               │   ├── TenantService.java
│       │   │   │               │   └── UpdateTenantRequest.java
│       │   │   │               ├── domain
│       │   │   │               │   ├── Tenant.java
│       │   │   │               │   ├── TenantRepository.java
│       │   │   │               │   └── TenantStatus.java
│       │   │   │               └── web
│       │   │   │                   └── InstitutionController.java
│       │   │   └── resources
│       │   │       ├── application-prod.yml
│       │   │       ├── application-staging.yml
│       │   │       ├── application.yml
│       │   │       └── db
│       │   │           └── migration
│       │   │               ├── V1__tenant_identity_foundation.sql
│       │   │               ├── V2__academic_core.sql
│       │   │               ├── V3__student_enrollment.sql
│       │   │               ├── V4__exam_marks_results.sql
│       │   │               ├── V5__attendance_timetable_notices_documents.sql
│       │   │               ├── V6__seed_platform_roles_permissions.sql
│       │   │               └── V7__password_reset_token.sql
│       │   └── test
│       │       ├── java
│       │       │   └── com
│       │       │       └── artms
│       │       │           ├── academic
│       │       │           │   └── CurriculumServiceTest.java
│       │       │           ├── attendance
│       │       │           │   └── AttendanceServiceTest.java
│       │       │           ├── document
│       │       │           │   └── DocumentServiceTest.java
│       │       │           ├── e2e
│       │       │           │   └── AcademicWorkflowE2ETest.java
│       │       │           ├── enrollment
│       │       │           │   └── EnrollmentServiceTest.java
│       │       │           ├── exam
│       │       │           │   └── ExamServiceTest.java
│       │       │           ├── grading
│       │       │           │   └── GradingEngineTest.java
│       │       │           ├── identity
│       │       │           │   ├── AuthIntegrationTest.java
│       │       │           │   └── AuthServiceTest.java
│       │       │           └── IntegrationTestBase.java
│       │       └── resources
│       │           └── application.yml
│       └── target
│           ├── classes
│           │   ├── application-prod.yml
│           │   ├── application-staging.yml
│           │   ├── application.yml
│           │   ├── com
│           │   │   └── artms
│           │   │       ├── academic
│           │   │       │   ├── application
│           │   │       │   │   ├── AcademicYearService.class
│           │   │       │   │   ├── AddCurriculumSubjectRequest$ComponentDto.class
│           │   │       │   │   ├── AddCurriculumSubjectRequest.class
│           │   │       │   │   ├── ClassGroupService.class
│           │   │       │   │   ├── CreateAcademicYearRequest.class
│           │   │       │   │   ├── CreateClassGroupRequest.class
│           │   │       │   │   ├── CreateCurriculumRequest.class
│           │   │       │   │   ├── CreateDepartmentRequest.class
│           │   │       │   │   ├── CreateProgramRequest.class
│           │   │       │   │   ├── CreateSubjectRequest.class
│           │   │       │   │   ├── CurriculumService.class
│           │   │       │   │   ├── DepartmentService.class
│           │   │       │   │   ├── ProgramService.class
│           │   │       │   │   └── SubjectService.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── AcademicYear.class
│           │   │       │   │   ├── AcademicYearRepository.class
│           │   │       │   │   ├── AcademicYearStatus.class
│           │   │       │   │   ├── AssessmentType.class
│           │   │       │   │   ├── ClassGroup.class
│           │   │       │   │   ├── ClassGroupRepository.class
│           │   │       │   │   ├── Curriculum.class
│           │   │       │   │   ├── CurriculumRepository.class
│           │   │       │   │   ├── CurriculumStatus.class
│           │   │       │   │   ├── CurriculumSubject.class
│           │   │       │   │   ├── CurriculumSubjectRepository.class
│           │   │       │   │   ├── Department.class
│           │   │       │   │   ├── DepartmentRepository.class
│           │   │       │   │   ├── Program.class
│           │   │       │   │   ├── ProgramRepository.class
│           │   │       │   │   ├── Subject.class
│           │   │       │   │   ├── SubjectComponent.class
│           │   │       │   │   ├── SubjectComponentRepository.class
│           │   │       │   │   ├── SubjectRepository.class
│           │   │       │   │   ├── SubjectType.class
│           │   │       │   │   ├── Term.class
│           │   │       │   │   └── TermRepository.class
│           │   │       │   └── web
│           │   │       │       ├── AcademicYearController.class
│           │   │       │       ├── ClassGroupController.class
│           │   │       │       ├── CurriculumController.class
│           │   │       │       ├── DepartmentController.class
│           │   │       │       ├── ProgramController.class
│           │   │       │       └── SubjectController.class
│           │   │       ├── ArtmsApplication.class
│           │   │       ├── attendance
│           │   │       │   ├── application
│           │   │       │   │   ├── AttendanceEntryDto.class
│           │   │       │   │   ├── AttendanceRecordResponse.class
│           │   │       │   │   ├── AttendanceService.class
│           │   │       │   │   ├── AttendanceSessionResponse.class
│           │   │       │   │   ├── CorrectAttendanceRecordRequest.class
│           │   │       │   │   ├── StudentAttendanceSummaryDto.class
│           │   │       │   │   ├── SubjectAttendanceStatDto.class
│           │   │       │   │   └── SubmitAttendanceRequest.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── AttendanceRecord.class
│           │   │       │   │   ├── AttendanceRecordRepository.class
│           │   │       │   │   ├── AttendanceSession.class
│           │   │       │   │   ├── AttendanceSessionRepository.class
│           │   │       │   │   ├── AttendanceSessionStatus.class
│           │   │       │   │   └── AttendanceStatus.class
│           │   │       │   └── web
│           │   │       │       └── AttendanceController.class
│           │   │       ├── audit
│           │   │       │   ├── application
│           │   │       │   │   ├── AuditEvent.class
│           │   │       │   │   └── AuditService.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── AuditLog$AuditLogBuilder.class
│           │   │       │   │   ├── AuditLog.class
│           │   │       │   │   └── AuditLogRepository.class
│           │   │       │   └── web
│           │   │       │       └── AuditLogController.class
│           │   │       ├── document
│           │   │       │   ├── application
│           │   │       │   │   ├── CreateDocumentTemplateRequest.class
│           │   │       │   │   ├── DocumentService.class
│           │   │       │   │   ├── DocumentTemplateResponse.class
│           │   │       │   │   ├── DocumentVerificationResponse.class
│           │   │       │   │   ├── GeneratedDocumentResponse.class
│           │   │       │   │   └── GenerateDocumentRequest.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── DocumentTemplate.class
│           │   │       │   │   ├── DocumentTemplateRepository.class
│           │   │       │   │   ├── DocumentTemplateStatus.class
│           │   │       │   │   ├── DocumentType.class
│           │   │       │   │   ├── GeneratedDocument.class
│           │   │       │   │   ├── GeneratedDocumentRepository.class
│           │   │       │   │   └── GeneratedDocumentStatus.class
│           │   │       │   └── web
│           │   │       │       ├── DocumentController.class
│           │   │       │       └── DocumentTemplateController.class
│           │   │       ├── enrollment
│           │   │       │   ├── application
│           │   │       │   │   ├── BatchEnrollClassRequest.class
│           │   │       │   │   ├── EnrollmentResponse$SubjectEnrollmentResponse.class
│           │   │       │   │   ├── EnrollmentResponse.class
│           │   │       │   │   ├── EnrollmentService.class
│           │   │       │   │   ├── EnrollStudentRequest.class
│           │   │       │   │   ├── PromoteStudentsRequest.class
│           │   │       │   │   ├── StudentEnrollmentDto.class
│           │   │       │   │   └── TransferStudentRequest.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── Enrollment.class
│           │   │       │   │   ├── EnrollmentRepository.class
│           │   │       │   │   ├── EnrollmentStatus.class
│           │   │       │   │   ├── SubjectEnrollment.class
│           │   │       │   │   ├── SubjectEnrollmentRepository.class
│           │   │       │   │   └── SubjectEnrollmentStatus.class
│           │   │       │   └── web
│           │   │       │       └── EnrollmentController.class
│           │   │       ├── exam
│           │   │       │   ├── application
│           │   │       │   │   ├── AddExamSubjectRequest.class
│           │   │       │   │   ├── BatchMarkEntryRequest.class
│           │   │       │   │   ├── CreateExamRequest.class
│           │   │       │   │   ├── ExamMarksGridDto$ComponentHeaderDto.class
│           │   │       │   │   ├── ExamMarksGridDto$StudentMarksRowDto.class
│           │   │       │   │   ├── ExamMarksGridDto.class
│           │   │       │   │   ├── ExamResponse.class
│           │   │       │   │   ├── ExamService.class
│           │   │       │   │   ├── ExamSubjectResponse.class
│           │   │       │   │   ├── ExamWorkflowRequest.class
│           │   │       │   │   ├── MarkEntryDto.class
│           │   │       │   │   └── MarkEntryResponse.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── Exam.class
│           │   │       │   │   ├── ExamRepository.class
│           │   │       │   │   ├── ExamStatus.class
│           │   │       │   │   ├── ExamSubject.class
│           │   │       │   │   ├── ExamSubjectRepository.class
│           │   │       │   │   ├── ExamSubjectStatus.class
│           │   │       │   │   ├── ExamType.class
│           │   │       │   │   ├── MarkEntry.class
│           │   │       │   │   ├── MarkEntryRepository.class
│           │   │       │   │   └── MarkStatus.class
│           │   │       │   └── web
│           │   │       │       └── ExamController.class
│           │   │       ├── grading
│           │   │       │   ├── application
│           │   │       │   │   ├── CreateGradingSchemeRequest$GradeBandDto.class
│           │   │       │   │   ├── CreateGradingSchemeRequest.class
│           │   │       │   │   └── GradingSchemeService.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── GradeBand.class
│           │   │       │   │   ├── GradingScheme.class
│           │   │       │   │   ├── GradingSchemeRepository.class
│           │   │       │   │   ├── GradingSchemeStatus.class
│           │   │       │   │   ├── GradingSchemeValidator.class
│           │   │       │   │   └── RoundingMode.class
│           │   │       │   ├── engine
│           │   │       │   │   ├── ComponentMark.class
│           │   │       │   │   ├── ComponentResult.class
│           │   │       │   │   ├── GradingEngine.class
│           │   │       │   │   ├── MarkStatus.class
│           │   │       │   │   ├── OverallResultStatus.class
│           │   │       │   │   ├── StudentResult.class
│           │   │       │   │   ├── SubjectResult.class
│           │   │       │   │   └── SubjectResultStatus.class
│           │   │       │   └── web
│           │   │       │       └── GradingSchemeController.class
│           │   │       ├── identity
│           │   │       │   ├── application
│           │   │       │   │   ├── AuthService.class
│           │   │       │   │   ├── CreateRoleRequest.class
│           │   │       │   │   ├── ForgotPasswordRequest.class
│           │   │       │   │   ├── LoginRequest.class
│           │   │       │   │   ├── LoginResponse.class
│           │   │       │   │   ├── RefreshResponse.class
│           │   │       │   │   ├── ResetPasswordRequest.class
│           │   │       │   │   ├── RoleResponse.class
│           │   │       │   │   ├── RoleService.class
│           │   │       │   │   ├── SessionResponse.class
│           │   │       │   │   └── UserSummary.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── MembershipStatus.class
│           │   │       │   │   ├── MfaProvider.class
│           │   │       │   │   ├── PasswordResetToken.class
│           │   │       │   │   ├── PasswordResetTokenRepository.class
│           │   │       │   │   ├── Permission.class
│           │   │       │   │   ├── PermissionRepository.class
│           │   │       │   │   ├── RefreshToken.class
│           │   │       │   │   ├── RefreshTokenRepository.class
│           │   │       │   │   ├── Role.class
│           │   │       │   │   ├── RoleRepository.class
│           │   │       │   │   ├── UserAccount.class
│           │   │       │   │   ├── UserAccountRepository.class
│           │   │       │   │   ├── UserStatus.class
│           │   │       │   │   ├── UserTenantMembership.class
│           │   │       │   │   └── UserTenantMembershipRepository.class
│           │   │       │   ├── infrastructure
│           │   │       │   │   ├── ArtmsPrincipal.class
│           │   │       │   │   ├── DefaultMfaProvider.class
│           │   │       │   │   ├── JwtAuthenticationFilter.class
│           │   │       │   │   └── JwtService.class
│           │   │       │   └── web
│           │   │       │       ├── AuthController.class
│           │   │       │       ├── RefreshTokenRequest.class
│           │   │       │       └── RoleController.class
│           │   │       ├── notification
│           │   │       │   ├── application
│           │   │       │   │   ├── NotificationResponse.class
│           │   │       │   │   ├── NotificationService.class
│           │   │       │   │   ├── NotificationSyncResponse.class
│           │   │       │   │   └── OutboxProcessor.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── Notification.class
│           │   │       │   │   ├── NotificationRepository.class
│           │   │       │   │   ├── OutboxEvent.class
│           │   │       │   │   └── OutboxEventRepository.class
│           │   │       │   ├── infrastructure
│           │   │       │   │   ├── PushAdapter$DefaultPushAdapter.class
│           │   │       │   │   ├── PushAdapter.class
│           │   │       │   │   ├── RealtimeEventPublisher.class
│           │   │       │   │   └── WebSocketConfig.class
│           │   │       │   └── web
│           │   │       │       └── NotificationController.class
│           │   │       ├── result
│           │   │       │   ├── application
│           │   │       │   │   ├── ResultCorrectionRequest.class
│           │   │       │   │   ├── ResultService$CalculationBundle.class
│           │   │       │   │   ├── ResultService$SubjectInfo.class
│           │   │       │   │   ├── ResultService.class
│           │   │       │   │   ├── ResultSnapshotResponse.class
│           │   │       │   │   └── ResultSubjectDto.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── ResultSnapshot.class
│           │   │       │   │   ├── ResultSnapshotRepository.class
│           │   │       │   │   ├── ResultStatus.class
│           │   │       │   │   └── ResultSubjectSnapshot.class
│           │   │       │   └── web
│           │   │       │       └── ResultController.class
│           │   │       ├── shared
│           │   │       │   ├── config
│           │   │       │   │   └── SecurityConfig.class
│           │   │       │   ├── domain
│           │   │       │   │   └── TenantBaseEntity.class
│           │   │       │   ├── exception
│           │   │       │   │   ├── BusinessRuleException.class
│           │   │       │   │   ├── OptimisticLockConflictException.class
│           │   │       │   │   ├── ResourceNotFoundException.class
│           │   │       │   │   ├── TenantAccessException.class
│           │   │       │   │   └── ValidationException.class
│           │   │       │   ├── tenant
│           │   │       │   │   └── TenantContext.class
│           │   │       │   └── web
│           │   │       │       ├── ApiError$ApiErrorBuilder.class
│           │   │       │       ├── ApiError$FieldError$FieldErrorBuilder.class
│           │   │       │       ├── ApiError$FieldError.class
│           │   │       │       ├── ApiError.class
│           │   │       │       ├── ApiResponse$ApiResponseBuilder.class
│           │   │       │       ├── ApiResponse.class
│           │   │       │       └── GlobalExceptionHandler.class
│           │   │       ├── staff
│           │   │       │   ├── application
│           │   │       │   │   ├── AssignTeacherSubjectRequest.class
│           │   │       │   │   ├── CreateTeacherRequest.class
│           │   │       │   │   └── TeacherService.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── Teacher.class
│           │   │       │   │   ├── TeacherRepository.class
│           │   │       │   │   ├── TeacherStatus.class
│           │   │       │   │   ├── TeacherSubject.class
│           │   │       │   │   └── TeacherSubjectRepository.class
│           │   │       │   └── web
│           │   │       │       └── TeacherController.class
│           │   │       ├── student
│           │   │       │   ├── application
│           │   │       │   │   ├── AddGuardianRequest.class
│           │   │       │   │   ├── BatchImportResult.class
│           │   │       │   │   ├── CreateStudentRequest.class
│           │   │       │   │   ├── StudentService.class
│           │   │       │   │   └── UpdateStudentRequest.class
│           │   │       │   ├── domain
│           │   │       │   │   ├── Gender.class
│           │   │       │   │   ├── Student.class
│           │   │       │   │   ├── StudentGuardian.class
│           │   │       │   │   ├── StudentGuardianRepository.class
│           │   │       │   │   ├── StudentRepository.class
│           │   │       │   │   └── StudentStatus.class
│           │   │       │   └── web
│           │   │       │       └── StudentController.class
│           │   │       └── tenant
│           │   │           ├── application
│           │   │           │   ├── CreateTenantRequest.class
│           │   │           │   ├── TenantService.class
│           │   │           │   └── UpdateTenantRequest.class
│           │   │           ├── domain
│           │   │           │   ├── Tenant.class
│           │   │           │   ├── TenantRepository.class
│           │   │           │   └── TenantStatus.class
│           │   │           └── web
│           │   │               └── InstitutionController.class
│           │   └── db
│           │       └── migration
│           │           ├── V1__tenant_identity_foundation.sql
│           │           ├── V2__academic_core.sql
│           │           ├── V3__student_enrollment.sql
│           │           ├── V4__exam_marks_results.sql
│           │           ├── V5__attendance_timetable_notices_documents.sql
│           │           ├── V6__seed_platform_roles_permissions.sql
│           │           └── V7__password_reset_token.sql
│           ├── generated-sources
│           │   └── annotations
│           ├── generated-test-sources
│           │   └── test-annotations
│           ├── maven-status
│           │   └── maven-compiler-plugin
│           │       ├── compile
│           │       │   └── default-compile
│           │       │       ├── createdFiles.lst
│           │       │       └── inputFiles.lst
│           │       └── testCompile
│           │           └── default-testCompile
│           │               ├── createdFiles.lst
│           │               └── inputFiles.lst
│           └── test-classes
│               ├── application.yml
│               └── com
│                   └── artms
│                       ├── academic
│                       │   └── CurriculumServiceTest.class
│                       ├── attendance
│                       │   └── AttendanceServiceTest.class
│                       ├── document
│                       │   └── DocumentServiceTest.class
│                       ├── e2e
│                       │   └── AcademicWorkflowE2ETest.class
│                       ├── enrollment
│                       │   └── EnrollmentServiceTest.class
│                       ├── exam
│                       │   └── ExamServiceTest.class
│                       ├── grading
│                       │   └── GradingEngineTest.class
│                       ├── identity
│                       │   ├── AuthIntegrationTest.class
│                       │   └── AuthServiceTest.class
│                       └── IntegrationTestBase.class
├── currect-tree-dir-structure.md
├── database
│   ├── data
│   │   └── sample_migration_students.csv
│   ├── migrations
│   ├── scripts
│   │   ├── backup_restore_test.sh
│   │   └── validate_migration.py
│   └── seeds
│       └── pilot_institution_seed.sql
├── docker-compose.staging.yml
├── docker-compose.yml
├── docs
│   ├── API_EXAMPLES.md
│   ├── API_SPEC.md
│   ├── ARCHITECTURE.md
│   ├── CHANGELOG.md
│   ├── CODING_STANDARDS.md
│   ├── DATABASE_SCHEMA.md
│   ├── DATA_MIGRATION.md
│   ├── DEPLOYMENT.md
│   ├── DOCS_MANIFEST.json
│   ├── DOCUMENT_TEMPLATES.md
│   ├── ENVIRONMENT.md
│   ├── ERROR_HANDLING.md
│   ├── FEATURES.md
│   ├── GRADING_ENGINE.md
│   ├── IMPLEMENTATION_PLAN.md
│   ├── PILOT_RUNBOOK.md
│   ├── PRD.md
│   ├── PRODUCTION_LAUNCH_CHECKLIST.md
│   ├── PROJECT_STRUCTURE.md
│   ├── README.md
│   ├── REALTIME_ARCHITECTURE.md
│   ├── REFERENCE_MARKSHEET_ANALYSIS.md
│   ├── ROLES_PERMISSIONS.md
│   ├── SECURITY.md
│   ├── SRS.md
│   ├── STAFF_TRAINING_GUIDE.md
│   ├── SYSTEM_DESIGN.md
│   ├── TASKS.md
│   ├── TECH_STACK.md
│   ├── TEST_CASES.md
│   ├── TESTING_STRATEGY.md
│   ├── UI_UX_SPEC.md
│   └── USER_FLOWS.md
├── first_prompt_for_you.md
├── infrastructure
│   ├── deployment
│   ├── docker
│   └── nginx
├── mobile
│   └── ssams-student
│       ├── analysis_options.yaml
│       ├── android
│       │   ├── app
│       │   │   ├── build.gradle.kts
│       │   │   └── src
│       │   │       ├── debug
│       │   │       │   └── AndroidManifest.xml
│       │   │       ├── main
│       │   │       │   ├── AndroidManifest.xml
│       │   │       │   ├── java
│       │   │       │   │   └── io
│       │   │       │   │       └── flutter
│       │   │       │   │           └── plugins
│       │   │       │   │               └── GeneratedPluginRegistrant.java
│       │   │       │   ├── kotlin
│       │   │       │   │   └── com
│       │   │       │   │       └── artms
│       │   │       │   │           └── ssams_student
│       │   │       │   │               └── MainActivity.kt
│       │   │       │   └── res
│       │   │       │       ├── drawable
│       │   │       │       │   └── launch_background.xml
│       │   │       │       ├── drawable-v21
│       │   │       │       │   └── launch_background.xml
│       │   │       │       ├── mipmap-hdpi
│       │   │       │       │   └── ic_launcher.png
│       │   │       │       ├── mipmap-mdpi
│       │   │       │       │   └── ic_launcher.png
│       │   │       │       ├── mipmap-xhdpi
│       │   │       │       │   └── ic_launcher.png
│       │   │       │       ├── mipmap-xxhdpi
│       │   │       │       │   └── ic_launcher.png
│       │   │       │       ├── mipmap-xxxhdpi
│       │   │       │       │   └── ic_launcher.png
│       │   │       │       ├── values
│       │   │       │       │   └── styles.xml
│       │   │       │       └── values-night
│       │   │       │           └── styles.xml
│       │   │       └── profile
│       │   │           └── AndroidManifest.xml
│       │   ├── build.gradle.kts
│       │   ├── gradle
│       │   │   └── wrapper
│       │   │       ├── gradle-wrapper.jar
│       │   │       └── gradle-wrapper.properties
│       │   ├── gradle.properties
│       │   ├── gradlew
│       │   ├── gradlew.bat
│       │   ├── local.properties
│       │   ├── settings.gradle.kts
│       │   └── ssams_student_android.iml
│       ├── build
│       │   ├── 5b5e3d720c12d8a69bfa7eadb6226d33.cache.dill.track.dill
│       │   ├── app
│       │   │   ├── deeplink.json
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aar_metadata_check
│       │   │   │   │   └── debug
│       │   │   │   │       └── checkDebugAarMetadata
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── apk_ide_redirect_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── createDebugApkListingFileRedirect
│       │   │   │   │           └── redirect.txt
│       │   │   │   ├── app_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAppMetadata
│       │   │   │   │           └── app-metadata.properties
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   │           └── flutter_assets
│       │   │   │   │               ├── AssetManifest.bin
│       │   │   │   │               ├── FontManifest.json
│       │   │   │   │               ├── fonts
│       │   │   │   │               │   └── MaterialIcons-Regular.otf
│       │   │   │   │               ├── isolate_snapshot_data
│       │   │   │   │               ├── kernel_blob.bin
│       │   │   │   │               ├── NativeAssetsManifest.json
│       │   │   │   │               ├── NOTICES.Z
│       │   │   │   │               ├── shaders
│       │   │   │   │               │   ├── ink_sparkle.frag
│       │   │   │   │               │   └── stretch_effect.frag
│       │   │   │   │               └── vm_snapshot_data
│       │   │   │   ├── compatible_screen_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── createDebugCompatibleScreenManifests
│       │   │   │   │           └── output-metadata.json
│       │   │   │   ├── compile_and_runtime_not_namespaced_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugResources
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compressed_assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── compressDebugAssets
│       │   │   │   │           └── out
│       │   │   │   │               └── assets
│       │   │   │   │                   └── flutter_assets
│       │   │   │   │                       ├── AssetManifest.bin.jar
│       │   │   │   │                       ├── FontManifest.json.jar
│       │   │   │   │                       ├── fonts
│       │   │   │   │                       │   └── MaterialIcons-Regular.otf.jar
│       │   │   │   │                       ├── isolate_snapshot_data.jar
│       │   │   │   │                       ├── kernel_blob.bin.jar
│       │   │   │   │                       ├── NativeAssetsManifest.json.jar
│       │   │   │   │                       ├── NOTICES.Z.jar
│       │   │   │   │                       ├── shaders
│       │   │   │   │                       │   ├── ink_sparkle.frag.jar
│       │   │   │   │                       │   └── stretch_effect.frag.jar
│       │   │   │   │                       └── vm_snapshot_data.jar
│       │   │   │   ├── cxx
│       │   │   │   │   └── debug
│       │   │   │   │       └── 2p6edq54
│       │   │   │   │           ├── logs
│       │   │   │   │           │   ├── arm64-v8a
│       │   │   │   │           │   │   ├── build_model.json
│       │   │   │   │           │   │   ├── configure_command
│       │   │   │   │           │   │   ├── configure_stderr.txt
│       │   │   │   │           │   │   ├── configure_stdout.txt
│       │   │   │   │           │   │   ├── generate_cxx_metadata_142_timing.txt
│       │   │   │   │           │   │   └── metadata_generation_record.json
│       │   │   │   │           │   ├── armeabi-v7a
│       │   │   │   │           │   │   ├── build_model.json
│       │   │   │   │           │   │   ├── configure_command
│       │   │   │   │           │   │   ├── configure_stderr.txt
│       │   │   │   │           │   │   ├── configure_stdout.txt
│       │   │   │   │           │   │   ├── generate_cxx_metadata_138_timing.txt
│       │   │   │   │           │   │   └── metadata_generation_record.json
│       │   │   │   │           │   └── x86_64
│       │   │   │   │           │       ├── build_model.json
│       │   │   │   │           │       ├── configure_command
│       │   │   │   │           │       ├── configure_stderr.txt
│       │   │   │   │           │       ├── configure_stdout.txt
│       │   │   │   │           │       ├── generate_cxx_metadata_140_timing.txt
│       │   │   │   │           │       └── metadata_generation_record.json
│       │   │   │   │           └── obj
│       │   │   │   │               ├── arm64-v8a
│       │   │   │   │               ├── armeabi-v7a
│       │   │   │   │               └── x86_64
│       │   │   │   ├── data_binding_layout_info_type_merge
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── desugar_graph
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   │               ├── currentProject
│       │   │   │   │               │   ├── dirs_bucket_0
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_1
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_2
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_3
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_4
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_5
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_6
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_7
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_8
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── dirs_bucket_9
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_0
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_1
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_2
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_3
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_4
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_5
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_6
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_7
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   ├── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_8
│       │   │   │   │               │   │   └── graph.bin
│       │   │   │   │               │   └── jar_5d4c47529d911a70eca2a28551dd13a7cd6b8c34bfca73e9d433aad369b53955_bucket_9
│       │   │   │   │               │       └── graph.bin
│       │   │   │   │               ├── externalLibs
│       │   │   │   │               ├── mixedScopes
│       │   │   │   │               └── otherProjects
│       │   │   │   ├── dex
│       │   │   │   │   └── debug
│       │   │   │   │       ├── mergeExtDexDebug
│       │   │   │   │       │   ├── classes2.dex
│       │   │   │   │       │   └── classes.dex
│       │   │   │   │       ├── mergeLibDexDebug
│       │   │   │   │       │   ├── 0
│       │   │   │   │       │   ├── 1
│       │   │   │   │       │   ├── 10
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 11
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 12
│       │   │   │   │       │   ├── 13
│       │   │   │   │       │   ├── 14
│       │   │   │   │       │   ├── 15
│       │   │   │   │       │   ├── 2
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 3
│       │   │   │   │       │   ├── 4
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 5
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 6
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 7
│       │   │   │   │       │   │   └── classes.dex
│       │   │   │   │       │   ├── 8
│       │   │   │   │       │   └── 9
│       │   │   │   │       │       └── classes.dex
│       │   │   │   │       └── mergeProjectDexDebug
│       │   │   │   │           ├── 0
│       │   │   │   │           │   └── classes.dex
│       │   │   │   │           ├── 1
│       │   │   │   │           │   └── classes.dex
│       │   │   │   │           ├── 10
│       │   │   │   │           ├── 11
│       │   │   │   │           ├── 12
│       │   │   │   │           ├── 13
│       │   │   │   │           ├── 14
│       │   │   │   │           ├── 15
│       │   │   │   │           ├── 2
│       │   │   │   │           ├── 3
│       │   │   │   │           ├── 4
│       │   │   │   │           ├── 5
│       │   │   │   │           ├── 6
│       │   │   │   │           ├── 7
│       │   │   │   │           │   └── classes.dex
│       │   │   │   │           ├── 8
│       │   │   │   │           └── 9
│       │   │   │   ├── dex_archive_input_jar_hashes
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── dex_number_of_buckets_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── duplicate_classes_check
│       │   │   │   │   └── debug
│       │   │   │   │       └── checkDebugDuplicateClasses
│       │   │   │   ├── external_file_lib_dex_archives
│       │   │   │   │   └── debug
│       │   │   │   │       └── desugarDebugFileDependencies
│       │   │   │   ├── external_libs_dex_archive
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── external_libs_dex_archive_with_artifact_transforms
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── flutter
│       │   │   │   │   └── debug
│       │   │   │   │       ├── flutter_assets
│       │   │   │   │       │   ├── AssetManifest.bin
│       │   │   │   │       │   ├── FontManifest.json
│       │   │   │   │       │   ├── fonts
│       │   │   │   │       │   │   └── MaterialIcons-Regular.otf
│       │   │   │   │       │   ├── isolate_snapshot_data
│       │   │   │   │       │   ├── kernel_blob.bin
│       │   │   │   │       │   ├── NativeAssetsManifest.json
│       │   │   │   │       │   ├── NOTICES.Z
│       │   │   │   │       │   ├── shaders
│       │   │   │   │       │   │   ├── ink_sparkle.frag
│       │   │   │   │       │   │   └── stretch_effect.frag
│       │   │   │   │       │   └── vm_snapshot_data
│       │   │   │   │       ├── flutter_build.d
│       │   │   │   │       └── libs.jar
│       │   │   │   ├── global_synthetics_dex
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGlobalSynthetics
│       │   │   │   ├── global_synthetics_external_lib
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── global_synthetics_external_libs_artifact_transform
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── global_synthetics_file_lib
│       │   │   │   │   └── debug
│       │   │   │   │       └── desugarDebugFileDependencies
│       │   │   │   ├── global_synthetics_mixed_scope
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── global_synthetics_project
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── global_synthetics_subproject
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   ├── mergeDebugResources
│       │   │   │   │   │   │   ├── compile-file-map.properties
│       │   │   │   │   │   │   ├── merged.dir
│       │   │   │   │   │   │   │   ├── values
│       │   │   │   │   │   │   │   │   └── values.xml
│       │   │   │   │   │   │   │   ├── values-af
│       │   │   │   │   │   │   │   │   └── values-af.xml
│       │   │   │   │   │   │   │   ├── values-am
│       │   │   │   │   │   │   │   │   └── values-am.xml
│       │   │   │   │   │   │   │   ├── values-ar
│       │   │   │   │   │   │   │   │   └── values-ar.xml
│       │   │   │   │   │   │   │   ├── values-as
│       │   │   │   │   │   │   │   │   └── values-as.xml
│       │   │   │   │   │   │   │   ├── values-az
│       │   │   │   │   │   │   │   │   └── values-az.xml
│       │   │   │   │   │   │   │   ├── values-be
│       │   │   │   │   │   │   │   │   └── values-be.xml
│       │   │   │   │   │   │   │   ├── values-bg
│       │   │   │   │   │   │   │   │   └── values-bg.xml
│       │   │   │   │   │   │   │   ├── values-bn
│       │   │   │   │   │   │   │   │   └── values-bn.xml
│       │   │   │   │   │   │   │   ├── values-bs
│       │   │   │   │   │   │   │   │   └── values-bs.xml
│       │   │   │   │   │   │   │   ├── values-b+sr+Latn
│       │   │   │   │   │   │   │   │   └── values-b+sr+Latn.xml
│       │   │   │   │   │   │   │   ├── values-ca
│       │   │   │   │   │   │   │   │   └── values-ca.xml
│       │   │   │   │   │   │   │   ├── values-cs
│       │   │   │   │   │   │   │   │   └── values-cs.xml
│       │   │   │   │   │   │   │   ├── values-da
│       │   │   │   │   │   │   │   │   └── values-da.xml
│       │   │   │   │   │   │   │   ├── values-de
│       │   │   │   │   │   │   │   │   └── values-de.xml
│       │   │   │   │   │   │   │   ├── values-el
│       │   │   │   │   │   │   │   │   └── values-el.xml
│       │   │   │   │   │   │   │   ├── values-en-rAU
│       │   │   │   │   │   │   │   │   └── values-en-rAU.xml
│       │   │   │   │   │   │   │   ├── values-en-rCA
│       │   │   │   │   │   │   │   │   └── values-en-rCA.xml
│       │   │   │   │   │   │   │   ├── values-en-rGB
│       │   │   │   │   │   │   │   │   └── values-en-rGB.xml
│       │   │   │   │   │   │   │   ├── values-en-rIN
│       │   │   │   │   │   │   │   │   └── values-en-rIN.xml
│       │   │   │   │   │   │   │   ├── values-en-rXC
│       │   │   │   │   │   │   │   │   └── values-en-rXC.xml
│       │   │   │   │   │   │   │   ├── values-es
│       │   │   │   │   │   │   │   │   └── values-es.xml
│       │   │   │   │   │   │   │   ├── values-es-rUS
│       │   │   │   │   │   │   │   │   └── values-es-rUS.xml
│       │   │   │   │   │   │   │   ├── values-et
│       │   │   │   │   │   │   │   │   └── values-et.xml
│       │   │   │   │   │   │   │   ├── values-eu
│       │   │   │   │   │   │   │   │   └── values-eu.xml
│       │   │   │   │   │   │   │   ├── values-fa
│       │   │   │   │   │   │   │   │   └── values-fa.xml
│       │   │   │   │   │   │   │   ├── values-fi
│       │   │   │   │   │   │   │   │   └── values-fi.xml
│       │   │   │   │   │   │   │   ├── values-fr
│       │   │   │   │   │   │   │   │   └── values-fr.xml
│       │   │   │   │   │   │   │   ├── values-fr-rCA
│       │   │   │   │   │   │   │   │   └── values-fr-rCA.xml
│       │   │   │   │   │   │   │   ├── values-gl
│       │   │   │   │   │   │   │   │   └── values-gl.xml
│       │   │   │   │   │   │   │   ├── values-gu
│       │   │   │   │   │   │   │   │   └── values-gu.xml
│       │   │   │   │   │   │   │   ├── values-h720dp-v13
│       │   │   │   │   │   │   │   │   └── values-h720dp-v13.xml
│       │   │   │   │   │   │   │   ├── values-hdpi-v4
│       │   │   │   │   │   │   │   │   └── values-hdpi-v4.xml
│       │   │   │   │   │   │   │   ├── values-hi
│       │   │   │   │   │   │   │   │   └── values-hi.xml
│       │   │   │   │   │   │   │   ├── values-hr
│       │   │   │   │   │   │   │   │   └── values-hr.xml
│       │   │   │   │   │   │   │   ├── values-hu
│       │   │   │   │   │   │   │   │   └── values-hu.xml
│       │   │   │   │   │   │   │   ├── values-hy
│       │   │   │   │   │   │   │   │   └── values-hy.xml
│       │   │   │   │   │   │   │   ├── values-in
│       │   │   │   │   │   │   │   │   └── values-in.xml
│       │   │   │   │   │   │   │   ├── values-is
│       │   │   │   │   │   │   │   │   └── values-is.xml
│       │   │   │   │   │   │   │   ├── values-it
│       │   │   │   │   │   │   │   │   └── values-it.xml
│       │   │   │   │   │   │   │   ├── values-iw
│       │   │   │   │   │   │   │   │   └── values-iw.xml
│       │   │   │   │   │   │   │   ├── values-ja
│       │   │   │   │   │   │   │   │   └── values-ja.xml
│       │   │   │   │   │   │   │   ├── values-ka
│       │   │   │   │   │   │   │   │   └── values-ka.xml
│       │   │   │   │   │   │   │   ├── values-kk
│       │   │   │   │   │   │   │   │   └── values-kk.xml
│       │   │   │   │   │   │   │   ├── values-km
│       │   │   │   │   │   │   │   │   └── values-km.xml
│       │   │   │   │   │   │   │   ├── values-kn
│       │   │   │   │   │   │   │   │   └── values-kn.xml
│       │   │   │   │   │   │   │   ├── values-ko
│       │   │   │   │   │   │   │   │   └── values-ko.xml
│       │   │   │   │   │   │   │   ├── values-ky
│       │   │   │   │   │   │   │   │   └── values-ky.xml
│       │   │   │   │   │   │   │   ├── values-land
│       │   │   │   │   │   │   │   │   └── values-land.xml
│       │   │   │   │   │   │   │   ├── values-large-v4
│       │   │   │   │   │   │   │   │   └── values-large-v4.xml
│       │   │   │   │   │   │   │   ├── values-ldltr-v21
│       │   │   │   │   │   │   │   │   └── values-ldltr-v21.xml
│       │   │   │   │   │   │   │   ├── values-lo
│       │   │   │   │   │   │   │   │   └── values-lo.xml
│       │   │   │   │   │   │   │   ├── values-lt
│       │   │   │   │   │   │   │   │   └── values-lt.xml
│       │   │   │   │   │   │   │   ├── values-lv
│       │   │   │   │   │   │   │   │   └── values-lv.xml
│       │   │   │   │   │   │   │   ├── values-mk
│       │   │   │   │   │   │   │   │   └── values-mk.xml
│       │   │   │   │   │   │   │   ├── values-ml
│       │   │   │   │   │   │   │   │   └── values-ml.xml
│       │   │   │   │   │   │   │   ├── values-mn
│       │   │   │   │   │   │   │   │   └── values-mn.xml
│       │   │   │   │   │   │   │   ├── values-mr
│       │   │   │   │   │   │   │   │   └── values-mr.xml
│       │   │   │   │   │   │   │   ├── values-ms
│       │   │   │   │   │   │   │   │   └── values-ms.xml
│       │   │   │   │   │   │   │   ├── values-my
│       │   │   │   │   │   │   │   │   └── values-my.xml
│       │   │   │   │   │   │   │   ├── values-nb
│       │   │   │   │   │   │   │   │   └── values-nb.xml
│       │   │   │   │   │   │   │   ├── values-ne
│       │   │   │   │   │   │   │   │   └── values-ne.xml
│       │   │   │   │   │   │   │   ├── values-night-v8
│       │   │   │   │   │   │   │   │   └── values-night-v8.xml
│       │   │   │   │   │   │   │   ├── values-nl
│       │   │   │   │   │   │   │   │   └── values-nl.xml
│       │   │   │   │   │   │   │   ├── values-or
│       │   │   │   │   │   │   │   │   └── values-or.xml
│       │   │   │   │   │   │   │   ├── values-pa
│       │   │   │   │   │   │   │   │   └── values-pa.xml
│       │   │   │   │   │   │   │   ├── values-pl
│       │   │   │   │   │   │   │   │   └── values-pl.xml
│       │   │   │   │   │   │   │   ├── values-port
│       │   │   │   │   │   │   │   │   └── values-port.xml
│       │   │   │   │   │   │   │   ├── values-pt
│       │   │   │   │   │   │   │   │   └── values-pt.xml
│       │   │   │   │   │   │   │   ├── values-pt-rBR
│       │   │   │   │   │   │   │   │   └── values-pt-rBR.xml
│       │   │   │   │   │   │   │   ├── values-pt-rPT
│       │   │   │   │   │   │   │   │   └── values-pt-rPT.xml
│       │   │   │   │   │   │   │   ├── values-ro
│       │   │   │   │   │   │   │   │   └── values-ro.xml
│       │   │   │   │   │   │   │   ├── values-ru
│       │   │   │   │   │   │   │   │   └── values-ru.xml
│       │   │   │   │   │   │   │   ├── values-si
│       │   │   │   │   │   │   │   │   └── values-si.xml
│       │   │   │   │   │   │   │   ├── values-sk
│       │   │   │   │   │   │   │   │   └── values-sk.xml
│       │   │   │   │   │   │   │   ├── values-sl
│       │   │   │   │   │   │   │   │   └── values-sl.xml
│       │   │   │   │   │   │   │   ├── values-sq
│       │   │   │   │   │   │   │   │   └── values-sq.xml
│       │   │   │   │   │   │   │   ├── values-sr
│       │   │   │   │   │   │   │   │   └── values-sr.xml
│       │   │   │   │   │   │   │   ├── values-sv
│       │   │   │   │   │   │   │   │   └── values-sv.xml
│       │   │   │   │   │   │   │   ├── values-sw
│       │   │   │   │   │   │   │   │   └── values-sw.xml
│       │   │   │   │   │   │   │   ├── values-sw360dp-v13
│       │   │   │   │   │   │   │   │   └── values-sw360dp-v13.xml
│       │   │   │   │   │   │   │   ├── values-sw600dp-v13
│       │   │   │   │   │   │   │   │   └── values-sw600dp-v13.xml
│       │   │   │   │   │   │   │   ├── values-ta
│       │   │   │   │   │   │   │   │   └── values-ta.xml
│       │   │   │   │   │   │   │   ├── values-te
│       │   │   │   │   │   │   │   │   └── values-te.xml
│       │   │   │   │   │   │   │   ├── values-th
│       │   │   │   │   │   │   │   │   └── values-th.xml
│       │   │   │   │   │   │   │   ├── values-tl
│       │   │   │   │   │   │   │   │   └── values-tl.xml
│       │   │   │   │   │   │   │   ├── values-tr
│       │   │   │   │   │   │   │   │   └── values-tr.xml
│       │   │   │   │   │   │   │   ├── values-uk
│       │   │   │   │   │   │   │   │   └── values-uk.xml
│       │   │   │   │   │   │   │   ├── values-ur
│       │   │   │   │   │   │   │   │   └── values-ur.xml
│       │   │   │   │   │   │   │   ├── values-uz
│       │   │   │   │   │   │   │   │   └── values-uz.xml
│       │   │   │   │   │   │   │   ├── values-v16
│       │   │   │   │   │   │   │   │   └── values-v16.xml
│       │   │   │   │   │   │   │   ├── values-v17
│       │   │   │   │   │   │   │   │   └── values-v17.xml
│       │   │   │   │   │   │   │   ├── values-v18
│       │   │   │   │   │   │   │   │   └── values-v18.xml
│       │   │   │   │   │   │   │   ├── values-v21
│       │   │   │   │   │   │   │   │   └── values-v21.xml
│       │   │   │   │   │   │   │   ├── values-v22
│       │   │   │   │   │   │   │   │   └── values-v22.xml
│       │   │   │   │   │   │   │   ├── values-v23
│       │   │   │   │   │   │   │   │   └── values-v23.xml
│       │   │   │   │   │   │   │   ├── values-v24
│       │   │   │   │   │   │   │   │   └── values-v24.xml
│       │   │   │   │   │   │   │   ├── values-v25
│       │   │   │   │   │   │   │   │   └── values-v25.xml
│       │   │   │   │   │   │   │   ├── values-v26
│       │   │   │   │   │   │   │   │   └── values-v26.xml
│       │   │   │   │   │   │   │   ├── values-v28
│       │   │   │   │   │   │   │   │   └── values-v28.xml
│       │   │   │   │   │   │   │   ├── values-vi
│       │   │   │   │   │   │   │   │   └── values-vi.xml
│       │   │   │   │   │   │   │   ├── values-watch-v20
│       │   │   │   │   │   │   │   │   └── values-watch-v20.xml
│       │   │   │   │   │   │   │   ├── values-watch-v21
│       │   │   │   │   │   │   │   │   └── values-watch-v21.xml
│       │   │   │   │   │   │   │   ├── values-xlarge-v4
│       │   │   │   │   │   │   │   │   └── values-xlarge-v4.xml
│       │   │   │   │   │   │   │   ├── values-zh-rCN
│       │   │   │   │   │   │   │   │   └── values-zh-rCN.xml
│       │   │   │   │   │   │   │   ├── values-zh-rHK
│       │   │   │   │   │   │   │   │   └── values-zh-rHK.xml
│       │   │   │   │   │   │   │   ├── values-zh-rTW
│       │   │   │   │   │   │   │   │   └── values-zh-rTW.xml
│       │   │   │   │   │   │   │   └── values-zu
│       │   │   │   │   │   │   │       └── values-zu.xml
│       │   │   │   │   │   │   ├── merger.xml
│       │   │   │   │   │   │   └── stripped.dir
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       │   ├── values
│       │   │   │   │   │       │   │   └── values.xml
│       │   │   │   │   │       │   └── values-night-v8
│       │   │   │   │   │       │       └── values-night-v8.xml
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   │       ├── 1mFW0G5pBet0HEMVDTSo+OjmE08=
│       │   │   │   │   │       ├── 1NahcuYsL6zH2d19FwdFYpPbBXU=
│       │   │   │   │   │       ├── 2kuVY_D3RfgsS4K+FrAJ4qZ0yPU=
│       │   │   │   │   │       ├── 3byjcCTX08GBsNohR+m27lZJWEc=
│       │   │   │   │   │       ├── 3M4VZw2u9rXBFhHx1cCUiLlIttw=
│       │   │   │   │   │       ├── 3mwsDKUSkXcZI_SwU0Xj5DQsQLA=
│       │   │   │   │   │       ├── 3XNbp37xSdxl6HPHUvwsJn2cNjg=
│       │   │   │   │   │       ├── 4D96QyVvQqii1fZbdrTJWFgF0SI=
│       │   │   │   │   │       ├── 4JhJWsU+4yasrq4bQNlENKBCKbU=
│       │   │   │   │   │       ├── 4S41dmyuNb+Sh6CvgMSJYmB0MJk=
│       │   │   │   │   │       ├── 5AYIFGRi5+b3eJ5WYHQQ+PJFgpo=
│       │   │   │   │   │       ├── 5MEyHy5ETyupt5MLz4AdGDh9CUE=
│       │   │   │   │   │       ├── 7mpc0aTdy7hYRD9xF_tQyDoWAeU=
│       │   │   │   │   │       ├── 86ERvlwsPhqtm7cIcMDb4qiUWss=
│       │   │   │   │   │       ├── 8CgKNrTkARTKaiX9ayllyWwEflw=
│       │   │   │   │   │       ├── 8e7yI2aiZxpIhTry6sxNcuaU0NI=
│       │   │   │   │   │       ├── 96WipMkkGYgvOHTOtMkANMau7dE=
│       │   │   │   │   │       ├── 9AdYC0l9n2H6+5Gk00ezM8bBWVA=
│       │   │   │   │   │       ├── 9psXnSjpjh7dD2UdI+kDg5dZjKA=
│       │   │   │   │   │       ├── 9Q8WTmf+EGagXoGCCXxmcDXoRYk=
│       │   │   │   │   │       ├── B0CKMPD3ktzZkZFZbCy+r31eiV0=
│       │   │   │   │   │       ├── b1xIy8F9SyzC2fxIcYFGG0qFpfI=
│       │   │   │   │   │       ├── beI_99cIqSBox3qi8xZS8c9Sak4=
│       │   │   │   │   │       ├── bo3fc5eTZkR54fHVJQkVxZHFasU=
│       │   │   │   │   │       ├── Ce4JZzp4RFFgzlz9vQwor0_yE4Q=
│       │   │   │   │   │       ├── Ce7E6cOIOshn+q_lCkm16K8A+XQ=
│       │   │   │   │   │       ├── cv4TN5PEXWpUVpPgyZNvG_B8Q9I=
│       │   │   │   │   │       ├── +dityx8SmcEPkqpcoNs5dzC6Qww=
│       │   │   │   │   │       ├── DLhkY82DHuhuMidsaU2+2+8aFak=
│       │   │   │   │   │       ├── DVBBh6LE_1FKXdvBlZSxWcZfaMI=
│       │   │   │   │   │       ├── ebFzupNKWE8uuedfFAElup81iag=
│       │   │   │   │   │       ├── Eif16TTqZntEi_zXZD7aY2HPVHY=
│       │   │   │   │   │       ├── EmLi76Z1GJsBnxlvGWHARJ6PL3M=
│       │   │   │   │   │       ├── eS9uG8XPSwI3hl3pZ8ZRv7TrWZU=
│       │   │   │   │   │       ├── EsvasfWZ9mssX9OfWT8rlBAOmL4=
│       │   │   │   │   │       ├── ETGV5cJgKjOKjp2S3ES9AEEQiAU=
│       │   │   │   │   │       ├── eyY+u2bwc9ycAbAd72_H0GyMQF4=
│       │   │   │   │   │       ├── g_EGxZqra5WV7ddaQzqhKuJAJWU=
│       │   │   │   │   │       ├── GJDtPbNZm3tgb_1jsdLKmPOYYcE=
│       │   │   │   │   │       ├── HFpEB+T_Iax+g6kJEzlyveYvaD4=
│       │   │   │   │   │       ├── hKqBP28+DjRnohU7XVdR_34z45o=
│       │   │   │   │   │       ├── hOP8bLfsHydTOWpPPLkwVG3ym1M=
│       │   │   │   │   │       ├── hwyfCEIbr83bGKePR592VXS0scc=
│       │   │   │   │   │       ├── hX2_LxL9zqM9crMiEcuYaE0z5ds=
│       │   │   │   │   │       ├── _Hy5q5Sae5NQfvWpQQdxPm89vDg=
│       │   │   │   │   │       ├── i7ks+zneUMqM+XsQwLhb17Rpzv4=
│       │   │   │   │   │       ├── IAYM1kAv4lhrLyK1GBfaHW9xWnY=
│       │   │   │   │   │       ├── ijonB5nTGWZ3rrPIzfPIxhNM5P0=
│       │   │   │   │   │       ├── imx4n26YIPp3RMoLSAefUiG8TYI=
│       │   │   │   │   │       ├── +j5ibRBWeNRRYjeVgA1kl3V_RCI=
│       │   │   │   │   │       ├── j8MPftVP+4ubCGYEv9+TKc5pQu4=
│       │   │   │   │   │       ├── JhpW5a3DOKCyG6CoOiCMP0hN5h0=
│       │   │   │   │   │       ├── JisV8sEMs9oGOrYbvA0DgF4w7EI=
│       │   │   │   │   │       ├── JMSAYvXBhODLv2NmAZ5UOB4L0u8=
│       │   │   │   │   │       ├── JX8Kq1uCctFvD21yl3_K_PPYShk=
│       │   │   │   │   │       ├── kiWhJh6Y_cq9aXxEJhy3jR_CY6Y=
│       │   │   │   │   │       ├── kQAAa4WtKP0AXLZfUZ+T90gUoEk=
│       │   │   │   │   │       ├── LccPj11HCFyFIYNvvGQgp1jEyv0=
│       │   │   │   │   │       ├── Lflhuz_HIc5IaSyOv+ApVD5z7_4=
│       │   │   │   │   │       ├── MdqW28pz+anHami_5DxfST7eito=
│       │   │   │   │   │       ├── Mf0N4dj33xO83JoZGN6LbAMVP0M=
│       │   │   │   │   │       ├── mjx1lH2M11tB53z2YpeD5ph_6Wc=
│       │   │   │   │   │       ├── mpYMwIPnoZGGeL9XOSq5YKCac8s=
│       │   │   │   │   │       ├── _mtoKR3cKE4ct+0b0M7NaDBFneo=
│       │   │   │   │   │       ├── nAb8LQs5pv30Oxwx2CYbtILYfhI=
│       │   │   │   │   │       ├── NJR3Pc3Xkwl68yI21Kw7xSU7hdU=
│       │   │   │   │   │       ├── NLfCpwbQrFjjdYfE3lHoEzRwPl8=
│       │   │   │   │   │       ├── +NQd_JOG8LuhGZsIUwl484k_F0c=
│       │   │   │   │   │       ├── oJbm6TrnhqL_th+mXtfhcExd_Ik=
│       │   │   │   │   │       ├── ooHVBuAhS9IExPkNF78ReBu7e7c=
│       │   │   │   │   │       ├── Or_o0gM9CPlRb302IJ_5eZxtuGw=
│       │   │   │   │   │       ├── o+XgrJjJLqtWaAAlubjI9TNfDRg=
│       │   │   │   │   │       ├── OzZ9SM91kejp6IZKgkdMn9A8Thg=
│       │   │   │   │   │       ├── p4x1SJnvsTRUbnRGWCr0li9uDF8=
│       │   │   │   │   │       ├── pwRt5pqN0z_30x2fjokV6EN70Qo=
│       │   │   │   │   │       ├── Pyh4zqFyEzSb78iuJdnf3xsfdD4=
│       │   │   │   │   │       ├── qMAr+Xv1px3sqqy_FIl9Z7q+2+E=
│       │   │   │   │   │       ├── qMgTLRw6TlCxjfAUm8+42Qm8Bwc=
│       │   │   │   │   │       ├── QNLW4gPuBCyvsMdAVfiMS0O4ob8=
│       │   │   │   │   │       ├── rbQ7ITxoZ5iH6vaHosHjl4JbyUc=
│       │   │   │   │   │       ├── r_oCsdRz+SycGJ+oM3HiUpKQiIs=
│       │   │   │   │   │       ├── rXjzxGX6FGiqUgJxXlJVFqYSo0I=
│       │   │   │   │   │       ├── S17trbX4BseDbu+eiKud9p2HbJU=
│       │   │   │   │   │       ├── sdMhDa0nQcn86jVoUE3xkLfyvSY=
│       │   │   │   │   │       ├── sT1b9gOSEciOKAs3LShb+UHYeSw=
│       │   │   │   │   │       ├── sU7SnjOwEs49RZv+paKQ3+P8aqM=
│       │   │   │   │   │       ├── Sxt+7zLR+l2QVwnjqBJMqtetkDE=
│       │   │   │   │   │       ├── SZSw4oQehePv0X29H1Oy_5PNzNU=
│       │   │   │   │   │       ├── tG7updNSDIFa31TJuRNvaKuqU4E=
│       │   │   │   │   │       ├── tMBMtpV+MYMkFTqduoJcQxB7hq8=
│       │   │   │   │   │       ├── toGJ0quzMvAXY8n7ffcehg3LOww=
│       │   │   │   │   │       ├── UDK4sOyYF3eDJHrnU_D+Uy13ozE=
│       │   │   │   │   │       ├── +Uk1ev35PmtOle4dRnmj0w9I36k=
│       │   │   │   │   │       ├── V9IsvhYVOX+_cJ3XIsXcI0_6W0w=
│       │   │   │   │   │       ├── VNb2A9iVMVo7bApKa1YIzVWrD3U=
│       │   │   │   │   │       ├── +W75v3RHv6fWk2xg2HfSz1Hh5Mg=
│       │   │   │   │   │       ├── WdmVXqmZxxe3vXwp2xfWKO0Rp9Y=
│       │   │   │   │   │       ├── WhmL8oWWFDzT9fMImyuC3fEa8UQ=
│       │   │   │   │   │       ├── WIH8n202bKtMCg7o2RmSns9XD_I=
│       │   │   │   │   │       ├── wNMQIdrqm3NK7PNQGtF8WTXGDPE=
│       │   │   │   │   │       ├── wQzbcvy3qAgp27pzGp4EzJQfJLs=
│       │   │   │   │   │       ├── ytlkNtZkddSjaBYLgSdiOZJuJGA=
│       │   │   │   │   │       ├── YuXuB1juqtN8+nuz_y0rLR5Z_8I=
│       │   │   │   │   │       ├── ZlT8jslCJwvdV8to3p_n_6DL5pQ=
│       │   │   │   │   │       ├── ZqRN4yL5KkAjrDm1JAFMATZpk+k=
│       │   │   │   │   │       └── Zx9efzoSxFZ9Mbds4QZWzsRar+Q=
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugShaders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── packageDebug
│       │   │   │   │       └── tmp
│       │   │   │   │           └── debug
│       │   │   │   │               ├── dex-renamer-state.txt
│       │   │   │   │               └── zip-cache
│       │   │   │   │                   ├── androidResources
│       │   │   │   │                   └── javaResources0
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── io
│       │   │   │   │                   └── flutter
│       │   │   │   │                       └── plugins
│       │   │   │   │                           └── GeneratedPluginRegistrant.class
│       │   │   │   ├── java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugJavaRes
│       │   │   │   │           └── out
│       │   │   │   │               ├── com
│       │   │   │   │               │   └── artms
│       │   │   │   │               │       └── ssams_student
│       │   │   │   │               └── META-INF
│       │   │   │   │                   └── app_debug.kotlin_module
│       │   │   │   ├── linked_resources_binary_format
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugResources
│       │   │   │   │           ├── linked-resources-binary-format-debug.ap_
│       │   │   │   │           └── output-metadata.json
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugMainManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── base.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       ├── outputDebugAppLinkSettings
│       │   │   │   │       │   └── AndroidManifest.xml
│       │   │   │   │       └── processDebugMainManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           ├── AndroidManifest.xml
│       │   │   │   │           └── output-metadata.json
│       │   │   │   ├── merged_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugNativeLibs
│       │   │   │   │           └── out
│       │   │   │   │               └── lib
│       │   │   │   │                   ├── arm64-v8a
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   ├── libdatastore_shared_counter.so
│       │   │   │   │                   │   ├── libflutter.so
│       │   │   │   │                   │   └── libVkLayer_khronos_validation.so
│       │   │   │   │                   ├── armeabi-v7a
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   └── libdatastore_shared_counter.so
│       │   │   │   │                   ├── x86
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   └── libdatastore_shared_counter.so
│       │   │   │   │                   └── x86_64
│       │   │   │   │                       ├── libdartjni.so
│       │   │   │   │                       └── libdatastore_shared_counter.so
│       │   │   │   ├── merged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugResources
│       │   │   │   │           ├── drawable-v21_launch_background.xml.flat
│       │   │   │   │           ├── mipmap-hdpi_ic_launcher.png.flat
│       │   │   │   │           ├── mipmap-mdpi_ic_launcher.png.flat
│       │   │   │   │           ├── mipmap-xhdpi_ic_launcher.png.flat
│       │   │   │   │           ├── mipmap-xxhdpi_ic_launcher.png.flat
│       │   │   │   │           ├── mipmap-xxxhdpi_ic_launcher.png.flat
│       │   │   │   │           ├── values-af_values-af.arsc.flat
│       │   │   │   │           ├── values-am_values-am.arsc.flat
│       │   │   │   │           ├── values-ar_values-ar.arsc.flat
│       │   │   │   │           ├── values-as_values-as.arsc.flat
│       │   │   │   │           ├── values-az_values-az.arsc.flat
│       │   │   │   │           ├── values-be_values-be.arsc.flat
│       │   │   │   │           ├── values-bg_values-bg.arsc.flat
│       │   │   │   │           ├── values-bn_values-bn.arsc.flat
│       │   │   │   │           ├── values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│       │   │   │   │           ├── values-bs_values-bs.arsc.flat
│       │   │   │   │           ├── values-ca_values-ca.arsc.flat
│       │   │   │   │           ├── values-cs_values-cs.arsc.flat
│       │   │   │   │           ├── values-da_values-da.arsc.flat
│       │   │   │   │           ├── values-de_values-de.arsc.flat
│       │   │   │   │           ├── values-el_values-el.arsc.flat
│       │   │   │   │           ├── values-en-rAU_values-en-rAU.arsc.flat
│       │   │   │   │           ├── values-en-rCA_values-en-rCA.arsc.flat
│       │   │   │   │           ├── values-en-rGB_values-en-rGB.arsc.flat
│       │   │   │   │           ├── values-en-rIN_values-en-rIN.arsc.flat
│       │   │   │   │           ├── values-en-rXC_values-en-rXC.arsc.flat
│       │   │   │   │           ├── values-es-rUS_values-es-rUS.arsc.flat
│       │   │   │   │           ├── values-es_values-es.arsc.flat
│       │   │   │   │           ├── values-et_values-et.arsc.flat
│       │   │   │   │           ├── values-eu_values-eu.arsc.flat
│       │   │   │   │           ├── values-fa_values-fa.arsc.flat
│       │   │   │   │           ├── values-fi_values-fi.arsc.flat
│       │   │   │   │           ├── values-fr-rCA_values-fr-rCA.arsc.flat
│       │   │   │   │           ├── values-fr_values-fr.arsc.flat
│       │   │   │   │           ├── values-gl_values-gl.arsc.flat
│       │   │   │   │           ├── values-gu_values-gu.arsc.flat
│       │   │   │   │           ├── values-h720dp-v13_values-h720dp-v13.arsc.flat
│       │   │   │   │           ├── values-hdpi-v4_values-hdpi-v4.arsc.flat
│       │   │   │   │           ├── values-hi_values-hi.arsc.flat
│       │   │   │   │           ├── values-hr_values-hr.arsc.flat
│       │   │   │   │           ├── values-hu_values-hu.arsc.flat
│       │   │   │   │           ├── values-hy_values-hy.arsc.flat
│       │   │   │   │           ├── values-in_values-in.arsc.flat
│       │   │   │   │           ├── values-is_values-is.arsc.flat
│       │   │   │   │           ├── values-it_values-it.arsc.flat
│       │   │   │   │           ├── values-iw_values-iw.arsc.flat
│       │   │   │   │           ├── values-ja_values-ja.arsc.flat
│       │   │   │   │           ├── values-ka_values-ka.arsc.flat
│       │   │   │   │           ├── values-kk_values-kk.arsc.flat
│       │   │   │   │           ├── values-km_values-km.arsc.flat
│       │   │   │   │           ├── values-kn_values-kn.arsc.flat
│       │   │   │   │           ├── values-ko_values-ko.arsc.flat
│       │   │   │   │           ├── values-ky_values-ky.arsc.flat
│       │   │   │   │           ├── values-land_values-land.arsc.flat
│       │   │   │   │           ├── values-large-v4_values-large-v4.arsc.flat
│       │   │   │   │           ├── values-ldltr-v21_values-ldltr-v21.arsc.flat
│       │   │   │   │           ├── values-lo_values-lo.arsc.flat
│       │   │   │   │           ├── values-lt_values-lt.arsc.flat
│       │   │   │   │           ├── values-lv_values-lv.arsc.flat
│       │   │   │   │           ├── values-mk_values-mk.arsc.flat
│       │   │   │   │           ├── values-ml_values-ml.arsc.flat
│       │   │   │   │           ├── values-mn_values-mn.arsc.flat
│       │   │   │   │           ├── values-mr_values-mr.arsc.flat
│       │   │   │   │           ├── values-ms_values-ms.arsc.flat
│       │   │   │   │           ├── values-my_values-my.arsc.flat
│       │   │   │   │           ├── values-nb_values-nb.arsc.flat
│       │   │   │   │           ├── values-ne_values-ne.arsc.flat
│       │   │   │   │           ├── values-night-v8_values-night-v8.arsc.flat
│       │   │   │   │           ├── values-nl_values-nl.arsc.flat
│       │   │   │   │           ├── values-or_values-or.arsc.flat
│       │   │   │   │           ├── values-pa_values-pa.arsc.flat
│       │   │   │   │           ├── values-pl_values-pl.arsc.flat
│       │   │   │   │           ├── values-port_values-port.arsc.flat
│       │   │   │   │           ├── values-pt-rBR_values-pt-rBR.arsc.flat
│       │   │   │   │           ├── values-pt-rPT_values-pt-rPT.arsc.flat
│       │   │   │   │           ├── values-pt_values-pt.arsc.flat
│       │   │   │   │           ├── values-ro_values-ro.arsc.flat
│       │   │   │   │           ├── values-ru_values-ru.arsc.flat
│       │   │   │   │           ├── values-si_values-si.arsc.flat
│       │   │   │   │           ├── values-sk_values-sk.arsc.flat
│       │   │   │   │           ├── values-sl_values-sl.arsc.flat
│       │   │   │   │           ├── values-sq_values-sq.arsc.flat
│       │   │   │   │           ├── values-sr_values-sr.arsc.flat
│       │   │   │   │           ├── values-sv_values-sv.arsc.flat
│       │   │   │   │           ├── values-sw360dp-v13_values-sw360dp-v13.arsc.flat
│       │   │   │   │           ├── values-sw600dp-v13_values-sw600dp-v13.arsc.flat
│       │   │   │   │           ├── values-sw_values-sw.arsc.flat
│       │   │   │   │           ├── values-ta_values-ta.arsc.flat
│       │   │   │   │           ├── values-te_values-te.arsc.flat
│       │   │   │   │           ├── values-th_values-th.arsc.flat
│       │   │   │   │           ├── values-tl_values-tl.arsc.flat
│       │   │   │   │           ├── values-tr_values-tr.arsc.flat
│       │   │   │   │           ├── values-uk_values-uk.arsc.flat
│       │   │   │   │           ├── values-ur_values-ur.arsc.flat
│       │   │   │   │           ├── values-uz_values-uz.arsc.flat
│       │   │   │   │           ├── values-v16_values-v16.arsc.flat
│       │   │   │   │           ├── values-v17_values-v17.arsc.flat
│       │   │   │   │           ├── values-v18_values-v18.arsc.flat
│       │   │   │   │           ├── values-v21_values-v21.arsc.flat
│       │   │   │   │           ├── values-v22_values-v22.arsc.flat
│       │   │   │   │           ├── values-v23_values-v23.arsc.flat
│       │   │   │   │           ├── values-v24_values-v24.arsc.flat
│       │   │   │   │           ├── values-v25_values-v25.arsc.flat
│       │   │   │   │           ├── values-v26_values-v26.arsc.flat
│       │   │   │   │           ├── values-v28_values-v28.arsc.flat
│       │   │   │   │           ├── values_values.arsc.flat
│       │   │   │   │           ├── values-vi_values-vi.arsc.flat
│       │   │   │   │           ├── values-watch-v20_values-watch-v20.arsc.flat
│       │   │   │   │           ├── values-watch-v21_values-watch-v21.arsc.flat
│       │   │   │   │           ├── values-xlarge-v4_values-xlarge-v4.arsc.flat
│       │   │   │   │           ├── values-zh-rCN_values-zh-rCN.arsc.flat
│       │   │   │   │           ├── values-zh-rHK_values-zh-rHK.arsc.flat
│       │   │   │   │           ├── values-zh-rTW_values-zh-rTW.arsc.flat
│       │   │   │   │           └── values-zu_values-zu.arsc.flat
│       │   │   │   ├── merged_res_blame_folder
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugResources
│       │   │   │   │           └── out
│       │   │   │   │               ├── multi-v2
│       │   │   │   │               │   ├── mergeDebugResources.json
│       │   │   │   │               │   ├── values-af.json
│       │   │   │   │               │   ├── values-am.json
│       │   │   │   │               │   ├── values-ar.json
│       │   │   │   │               │   ├── values-as.json
│       │   │   │   │               │   ├── values-az.json
│       │   │   │   │               │   ├── values-be.json
│       │   │   │   │               │   ├── values-bg.json
│       │   │   │   │               │   ├── values-bn.json
│       │   │   │   │               │   ├── values-bs.json
│       │   │   │   │               │   ├── values-b+sr+Latn.json
│       │   │   │   │               │   ├── values-ca.json
│       │   │   │   │               │   ├── values-cs.json
│       │   │   │   │               │   ├── values-da.json
│       │   │   │   │               │   ├── values-de.json
│       │   │   │   │               │   ├── values-el.json
│       │   │   │   │               │   ├── values-en-rAU.json
│       │   │   │   │               │   ├── values-en-rCA.json
│       │   │   │   │               │   ├── values-en-rGB.json
│       │   │   │   │               │   ├── values-en-rIN.json
│       │   │   │   │               │   ├── values-en-rXC.json
│       │   │   │   │               │   ├── values-es.json
│       │   │   │   │               │   ├── values-es-rUS.json
│       │   │   │   │               │   ├── values-et.json
│       │   │   │   │               │   ├── values-eu.json
│       │   │   │   │               │   ├── values-fa.json
│       │   │   │   │               │   ├── values-fi.json
│       │   │   │   │               │   ├── values-fr.json
│       │   │   │   │               │   ├── values-fr-rCA.json
│       │   │   │   │               │   ├── values-gl.json
│       │   │   │   │               │   ├── values-gu.json
│       │   │   │   │               │   ├── values-h720dp-v13.json
│       │   │   │   │               │   ├── values-hdpi-v4.json
│       │   │   │   │               │   ├── values-hi.json
│       │   │   │   │               │   ├── values-hr.json
│       │   │   │   │               │   ├── values-hu.json
│       │   │   │   │               │   ├── values-hy.json
│       │   │   │   │               │   ├── values-in.json
│       │   │   │   │               │   ├── values-is.json
│       │   │   │   │               │   ├── values-it.json
│       │   │   │   │               │   ├── values-iw.json
│       │   │   │   │               │   ├── values-ja.json
│       │   │   │   │               │   ├── values.json
│       │   │   │   │               │   ├── values-ka.json
│       │   │   │   │               │   ├── values-kk.json
│       │   │   │   │               │   ├── values-km.json
│       │   │   │   │               │   ├── values-kn.json
│       │   │   │   │               │   ├── values-ko.json
│       │   │   │   │               │   ├── values-ky.json
│       │   │   │   │               │   ├── values-land.json
│       │   │   │   │               │   ├── values-large-v4.json
│       │   │   │   │               │   ├── values-ldltr-v21.json
│       │   │   │   │               │   ├── values-lo.json
│       │   │   │   │               │   ├── values-lt.json
│       │   │   │   │               │   ├── values-lv.json
│       │   │   │   │               │   ├── values-mk.json
│       │   │   │   │               │   ├── values-ml.json
│       │   │   │   │               │   ├── values-mn.json
│       │   │   │   │               │   ├── values-mr.json
│       │   │   │   │               │   ├── values-ms.json
│       │   │   │   │               │   ├── values-my.json
│       │   │   │   │               │   ├── values-nb.json
│       │   │   │   │               │   ├── values-ne.json
│       │   │   │   │               │   ├── values-night-v8.json
│       │   │   │   │               │   ├── values-nl.json
│       │   │   │   │               │   ├── values-or.json
│       │   │   │   │               │   ├── values-pa.json
│       │   │   │   │               │   ├── values-pl.json
│       │   │   │   │               │   ├── values-port.json
│       │   │   │   │               │   ├── values-pt.json
│       │   │   │   │               │   ├── values-pt-rBR.json
│       │   │   │   │               │   ├── values-pt-rPT.json
│       │   │   │   │               │   ├── values-ro.json
│       │   │   │   │               │   ├── values-ru.json
│       │   │   │   │               │   ├── values-si.json
│       │   │   │   │               │   ├── values-sk.json
│       │   │   │   │               │   ├── values-sl.json
│       │   │   │   │               │   ├── values-sq.json
│       │   │   │   │               │   ├── values-sr.json
│       │   │   │   │               │   ├── values-sv.json
│       │   │   │   │               │   ├── values-sw360dp-v13.json
│       │   │   │   │               │   ├── values-sw600dp-v13.json
│       │   │   │   │               │   ├── values-sw.json
│       │   │   │   │               │   ├── values-ta.json
│       │   │   │   │               │   ├── values-te.json
│       │   │   │   │               │   ├── values-th.json
│       │   │   │   │               │   ├── values-tl.json
│       │   │   │   │               │   ├── values-tr.json
│       │   │   │   │               │   ├── values-uk.json
│       │   │   │   │               │   ├── values-ur.json
│       │   │   │   │               │   ├── values-uz.json
│       │   │   │   │               │   ├── values-v16.json
│       │   │   │   │               │   ├── values-v17.json
│       │   │   │   │               │   ├── values-v18.json
│       │   │   │   │               │   ├── values-v21.json
│       │   │   │   │               │   ├── values-v22.json
│       │   │   │   │               │   ├── values-v23.json
│       │   │   │   │               │   ├── values-v24.json
│       │   │   │   │               │   ├── values-v25.json
│       │   │   │   │               │   ├── values-v26.json
│       │   │   │   │               │   ├── values-v28.json
│       │   │   │   │               │   ├── values-vi.json
│       │   │   │   │               │   ├── values-watch-v20.json
│       │   │   │   │               │   ├── values-watch-v21.json
│       │   │   │   │               │   ├── values-xlarge-v4.json
│       │   │   │   │               │   ├── values-zh-rCN.json
│       │   │   │   │               │   ├── values-zh-rHK.json
│       │   │   │   │               │   ├── values-zh-rTW.json
│       │   │   │   │               │   └── values-zu.json
│       │   │   │   │               └── single
│       │   │   │   │                   └── mergeDebugResources.json
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_test_only_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugNativeLibs
│       │   │   │   │           └── out
│       │   │   │   ├── mixed_scope_dex_archive
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifestForPackage
│       │   │   │   │           ├── AndroidManifest.xml
│       │   │   │   │           └── output-metadata.json
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           ├── drawable-v21
│       │   │   │   │           │   └── launch_background.xml
│       │   │   │   │           ├── mipmap-hdpi-v4
│       │   │   │   │           │   └── ic_launcher.png
│       │   │   │   │           ├── mipmap-mdpi-v4
│       │   │   │   │           │   └── ic_launcher.png
│       │   │   │   │           ├── mipmap-xhdpi-v4
│       │   │   │   │           │   └── ic_launcher.png
│       │   │   │   │           ├── mipmap-xxhdpi-v4
│       │   │   │   │           │   └── ic_launcher.png
│       │   │   │   │           ├── mipmap-xxxhdpi-v4
│       │   │   │   │           │   └── ic_launcher.png
│       │   │   │   │           ├── values
│       │   │   │   │           │   └── values.xml
│       │   │   │   │           └── values-night-v8
│       │   │   │   │               └── values-night-v8.xml
│       │   │   │   ├── project_dex_archive
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   │               ├── com
│       │   │   │   │               │   └── artms
│       │   │   │   │               │       └── ssams_student
│       │   │   │   │               │           └── MainActivity.dex
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_0.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_1.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_2.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_3.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_4.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_5.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_6.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_7.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_8.jar
│       │   │   │   │               ├── e835c3c8f52058864b58fbc6fe2f0bf0a549bf1e31ce229c281721a0490e7f71_9.jar
│       │   │   │   │               └── io
│       │   │   │   │                   └── flutter
│       │   │   │   │                       └── plugins
│       │   │   │   │                           └── GeneratedPluginRegistrant.dex
│       │   │   │   ├── runtime_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugResources
│       │   │   │   │           └── R.txt
│       │   │   │   ├── signing_config_versions
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugSigningConfigVersions
│       │   │   │   │           └── signing-config-versions.json
│       │   │   │   ├── source_set_path_map
│       │   │   │   │   └── debug
│       │   │   │   │       └── mapDebugSourceSetPaths
│       │   │   │   │           └── file-map.txt
│       │   │   │   ├── stable_resource_ids_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugResources
│       │   │   │   │           └── stableIds.txt
│       │   │   │   ├── stripped_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── stripDebugDebugSymbols
│       │   │   │   │           └── out
│       │   │   │   │               └── lib
│       │   │   │   │                   ├── arm64-v8a
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   ├── libdatastore_shared_counter.so
│       │   │   │   │                   │   ├── libflutter.so
│       │   │   │   │                   │   └── libVkLayer_khronos_validation.so
│       │   │   │   │                   ├── armeabi-v7a
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   └── libdatastore_shared_counter.so
│       │   │   │   │                   ├── x86
│       │   │   │   │                   │   ├── libdartjni.so
│       │   │   │   │                   │   └── libdatastore_shared_counter.so
│       │   │   │   │                   └── x86_64
│       │   │   │   │                       ├── libdartjni.so
│       │   │   │   │                       └── libdatastore_shared_counter.so
│       │   │   │   ├── sub_project_dex_archive
│       │   │   │   │   └── debug
│       │   │   │   │       └── dexBuilderDebug
│       │   │   │   │           └── out
│       │   │   │   ├── symbol_list_with_package_name
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugResources
│       │   │   │   │           └── package-aware-r.txt
│       │   │   │   └── validate_signing_config
│       │   │   │       └── debug
│       │   │   │           └── validateSigningDebug
│       │   │   ├── kotlin
│       │   │   │   └── compileDebugKotlin
│       │   │   │       ├── cacheable
│       │   │   │       │   ├── caches-jvm
│       │   │   │       │   │   ├── inputs
│       │   │   │       │   │   │   ├── source-to-output.tab
│       │   │   │       │   │   │   ├── source-to-output.tab_i
│       │   │   │       │   │   │   ├── source-to-output.tab_i.len
│       │   │   │       │   │   │   ├── source-to-output.tab.keystream
│       │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│       │   │   │       │   │   │   ├── source-to-output.tab.len
│       │   │   │       │   │   │   └── source-to-output.tab.values.at
│       │   │   │       │   │   ├── jvm
│       │   │   │       │   │   │   └── kotlin
│       │   │   │       │   │   │       ├── class-attributes.tab
│       │   │   │       │   │   │       ├── class-attributes.tab_i
│       │   │   │       │   │   │       ├── class-attributes.tab_i.len
│       │   │   │       │   │   │       ├── class-attributes.tab.keystream
│       │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│       │   │   │       │   │   │       ├── class-attributes.tab.len
│       │   │   │       │   │   │       ├── class-attributes.tab.values.at
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│       │   │   │       │   │   │       ├── internal-name-to-source.tab
│       │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│       │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│       │   │   │       │   │   │       ├── proto.tab
│       │   │   │       │   │   │       ├── proto.tab_i
│       │   │   │       │   │   │       ├── proto.tab_i.len
│       │   │   │       │   │   │       ├── proto.tab.keystream
│       │   │   │       │   │   │       ├── proto.tab.keystream.len
│       │   │   │       │   │   │       ├── proto.tab.len
│       │   │   │       │   │   │       ├── proto.tab.values.at
│       │   │   │       │   │   │       ├── source-to-classes.tab
│       │   │   │       │   │   │       ├── source-to-classes.tab_i
│       │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│       │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│       │   │   │       │   │   │       ├── subtypes.tab
│       │   │   │       │   │   │       ├── subtypes.tab_i
│       │   │   │       │   │   │       ├── subtypes.tab_i.len
│       │   │   │       │   │   │       ├── subtypes.tab.keystream
│       │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│       │   │   │       │   │   │       ├── subtypes.tab.len
│       │   │   │       │   │   │       ├── subtypes.tab.values.at
│       │   │   │       │   │   │       ├── supertypes.tab
│       │   │   │       │   │   │       ├── supertypes.tab_i
│       │   │   │       │   │   │       ├── supertypes.tab_i.len
│       │   │   │       │   │   │       ├── supertypes.tab.keystream
│       │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│       │   │   │       │   │   │       ├── supertypes.tab.len
│       │   │   │       │   │   │       └── supertypes.tab.values.at
│       │   │   │       │   │   └── lookups
│       │   │   │       │   │       ├── counters.tab
│       │   │   │       │   │       ├── file-to-id.tab
│       │   │   │       │   │       ├── file-to-id.tab_i
│       │   │   │       │   │       ├── file-to-id.tab_i.len
│       │   │   │       │   │       ├── file-to-id.tab.keystream
│       │   │   │       │   │       ├── file-to-id.tab.keystream.len
│       │   │   │       │   │       ├── file-to-id.tab.len
│       │   │   │       │   │       ├── file-to-id.tab.values.at
│       │   │   │       │   │       ├── id-to-file.tab
│       │   │   │       │   │       ├── id-to-file.tab_i.len
│       │   │   │       │   │       ├── id-to-file.tab.keystream
│       │   │   │       │   │       ├── id-to-file.tab.keystream.len
│       │   │   │       │   │       ├── id-to-file.tab.len
│       │   │   │       │   │       ├── id-to-file.tab.values.at
│       │   │   │       │   │       ├── lookups.tab
│       │   │   │       │   │       ├── lookups.tab_i
│       │   │   │       │   │       ├── lookups.tab_i.len
│       │   │   │       │   │       ├── lookups.tab.keystream
│       │   │   │       │   │       ├── lookups.tab.keystream.len
│       │   │   │       │   │       ├── lookups.tab.len
│       │   │   │       │   │       └── lookups.tab.values.at
│       │   │   │       │   └── last-build.bin
│       │   │   │       ├── classpath-snapshot
│       │   │   │       │   └── shrunk-classpath-snapshot.bin
│       │   │   │       └── local-state
│       │   │   ├── outputs
│       │   │   │   ├── apk
│       │   │   │   │   └── debug
│       │   │   │   │       ├── app-debug.apk
│       │   │   │   │       └── output-metadata.json
│       │   │   │   ├── flutter-apk
│       │   │   │   │   ├── app-debug.apk
│       │   │   │   │   └── app-debug.apk.sha1
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       ├── compileDebugJavaWithJavac
│       │   │       │   └── previous-compilation-data.bin
│       │   │       ├── kotlin-classes
│       │   │       │   └── debug
│       │   │       │       ├── com
│       │   │       │       │   └── artms
│       │   │       │       │       └── ssams_student
│       │   │       │       │           └── MainActivity.class
│       │   │       │       └── META-INF
│       │   │       │           └── app_debug.kotlin_module
│       │   │       └── packJniLibsflutterBuildDebug
│       │   │           └── MANIFEST.MF
│       │   ├── c1b3b754c9f611f51c5c5ee3d492955b
│       │   │   ├── _composite.stamp
│       │   │   ├── dart_build.d
│       │   │   ├── dart_build_result.json
│       │   │   ├── dart_build.stamp
│       │   │   ├── gen_dart_plugin_registrant.stamp
│       │   │   ├── gen_localizations.stamp
│       │   │   └── outputs.json
│       │   ├── connectivity_plus
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── dev
│       │   │   │   │                   └── fluttercommunity
│       │   │   │   │                       └── plus
│       │   │   │   │                           └── connectivity
│       │   │   │   │                               ├── ConnectivityBroadcastReceiver$1.class
│       │   │   │   │                               ├── ConnectivityBroadcastReceiver.class
│       │   │   │   │                               ├── Connectivity.class
│       │   │   │   │                               ├── ConnectivityMethodChannelHandler.class
│       │   │   │   │                               └── ConnectivityPlugin.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-connectivity_plus.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── dev
│       │   │   │   │               └── fluttercommunity
│       │   │   │   │                   └── plus
│       │   │   │   │                       └── connectivity
│       │   │   │   │                           ├── ConnectivityBroadcastReceiver$1.class
│       │   │   │   │                           ├── ConnectivityBroadcastReceiver.class
│       │   │   │   │                           ├── Connectivity.class
│       │   │   │   │                           ├── ConnectivityMethodChannelHandler.class
│       │   │   │   │                           └── ConnectivityPlugin.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── connectivity_plus-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── firebase_core
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   ├── res
│       │   │   │   │   ├── pngs
│       │   │   │   │   │   └── debug
│       │   │   │   │   └── resValues
│       │   │   │   │       └── debug
│       │   │   │   └── source
│       │   │   │       └── buildConfig
│       │   │   │           └── debug
│       │   │   │               └── io
│       │   │   │                   └── flutter
│       │   │   │                       └── plugins
│       │   │   │                           └── firebase
│       │   │   │                               └── core
│       │   │   │                                   └── BuildConfig.java
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── io
│       │   │   │   │                   └── flutter
│       │   │   │   │                       └── plugins
│       │   │   │   │                           └── firebase
│       │   │   │   │                               └── core
│       │   │   │   │                                   ├── BuildConfig.class
│       │   │   │   │                                   ├── FlutterFirebaseCorePlugin.class
│       │   │   │   │                                   ├── FlutterFirebaseCoreRegistrar.class
│       │   │   │   │                                   ├── FlutterFirebasePlugin.class
│       │   │   │   │                                   ├── FlutterFirebasePluginRegistry.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$CanIgnoreReturnValue.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$CoreFirebaseOptions$Builder.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$CoreFirebaseOptions.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$CoreInitializeResponse$Builder.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$CoreInitializeResponse.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$1.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$2.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$3.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$1.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$2.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$3.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$FlutterError.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$NullableResult.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$PigeonCodec.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$Result.class
│       │   │   │   │                                   ├── GeneratedAndroidFirebaseCore$VoidResult.class
│       │   │   │   │                                   └── GeneratedAndroidFirebaseCore.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-firebase_core.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── io
│       │   │   │   │               └── flutter
│       │   │   │   │                   └── plugins
│       │   │   │   │                       └── firebase
│       │   │   │   │                           └── core
│       │   │   │   │                               ├── BuildConfig.class
│       │   │   │   │                               ├── FlutterFirebaseCorePlugin.class
│       │   │   │   │                               ├── FlutterFirebaseCoreRegistrar.class
│       │   │   │   │                               ├── FlutterFirebasePlugin.class
│       │   │   │   │                               ├── FlutterFirebasePluginRegistry.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$CanIgnoreReturnValue.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$CoreFirebaseOptions$Builder.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$CoreFirebaseOptions.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$CoreInitializeResponse$Builder.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$CoreInitializeResponse.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$1.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$2.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi$3.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseAppHostApi.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$1.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$2.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi$3.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FirebaseCoreHostApi.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$FlutterError.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$NullableResult.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$PigeonCodec.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$Result.class
│       │   │   │   │                               ├── GeneratedAndroidFirebaseCore$VoidResult.class
│       │   │   │   │                               └── GeneratedAndroidFirebaseCore.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── firebase_core-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── firebase_messaging
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   ├── res
│       │   │   │   │   ├── pngs
│       │   │   │   │   │   └── debug
│       │   │   │   │   └── resValues
│       │   │   │   │       └── debug
│       │   │   │   └── source
│       │   │   │       └── buildConfig
│       │   │   │           └── debug
│       │   │   │               └── io
│       │   │   │                   └── flutter
│       │   │   │                       └── plugins
│       │   │   │                           └── firebase
│       │   │   │                               └── messaging
│       │   │   │                                   └── BuildConfig.java
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── io
│       │   │   │   │                   └── flutter
│       │   │   │   │                       └── plugins
│       │   │   │   │                           └── firebase
│       │   │   │   │                               └── messaging
│       │   │   │   │                                   ├── BuildConfig.class
│       │   │   │   │                                   ├── ContextHolder.class
│       │   │   │   │                                   ├── ErrorCallback.class
│       │   │   │   │                                   ├── FlutterFirebaseAppRegistrar.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingBackgroundExecutor$1.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingBackgroundExecutor$2.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingBackgroundExecutor.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingBackgroundService.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingInitProvider.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingPlugin$1.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingPlugin$2.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingPlugin.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingReceiver.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingService.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingStore.class
│       │   │   │   │                                   ├── FlutterFirebaseMessagingUtils.class
│       │   │   │   │                                   ├── FlutterFirebasePermissionManager$RequestPermissionsSuccessCallback.class
│       │   │   │   │                                   ├── FlutterFirebasePermissionManager.class
│       │   │   │   │                                   ├── FlutterFirebaseRemoteMessageLiveData.class
│       │   │   │   │                                   ├── FlutterFirebaseTokenLiveData.class
│       │   │   │   │                                   ├── JobIntentService$CommandProcessor$1$1.class
│       │   │   │   │                                   ├── JobIntentService$CommandProcessor$1.class
│       │   │   │   │                                   ├── JobIntentService$CommandProcessor.class
│       │   │   │   │                                   ├── JobIntentService$CompatJobEngine.class
│       │   │   │   │                                   ├── JobIntentService$CompatWorkEnqueuer.class
│       │   │   │   │                                   ├── JobIntentService$CompatWorkItem.class
│       │   │   │   │                                   ├── JobIntentService$ComponentNameWithWakeful.class
│       │   │   │   │                                   ├── JobIntentService$GenericWorkItem.class
│       │   │   │   │                                   ├── JobIntentService$JobServiceEngineImpl$WrapperWorkItem.class
│       │   │   │   │                                   ├── JobIntentService$JobServiceEngineImpl.class
│       │   │   │   │                                   ├── JobIntentService$JobWorkEnqueuer.class
│       │   │   │   │                                   ├── JobIntentService$WorkEnqueuer.class
│       │   │   │   │                                   ├── JobIntentService.class
│       │   │   │   │                                   └── PluginRegistrantException.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-firebase_messaging.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── io
│       │   │   │   │               └── flutter
│       │   │   │   │                   └── plugins
│       │   │   │   │                       └── firebase
│       │   │   │   │                           └── messaging
│       │   │   │   │                               ├── BuildConfig.class
│       │   │   │   │                               ├── ContextHolder.class
│       │   │   │   │                               ├── ErrorCallback.class
│       │   │   │   │                               ├── FlutterFirebaseAppRegistrar.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingBackgroundExecutor$1.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingBackgroundExecutor$2.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingBackgroundExecutor.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingBackgroundService.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingInitProvider.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingPlugin$1.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingPlugin$2.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingPlugin.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingReceiver.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingService.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingStore.class
│       │   │   │   │                               ├── FlutterFirebaseMessagingUtils.class
│       │   │   │   │                               ├── FlutterFirebasePermissionManager$RequestPermissionsSuccessCallback.class
│       │   │   │   │                               ├── FlutterFirebasePermissionManager.class
│       │   │   │   │                               ├── FlutterFirebaseRemoteMessageLiveData.class
│       │   │   │   │                               ├── FlutterFirebaseTokenLiveData.class
│       │   │   │   │                               ├── JobIntentService$CommandProcessor$1$1.class
│       │   │   │   │                               ├── JobIntentService$CommandProcessor$1.class
│       │   │   │   │                               ├── JobIntentService$CommandProcessor.class
│       │   │   │   │                               ├── JobIntentService$CompatJobEngine.class
│       │   │   │   │                               ├── JobIntentService$CompatWorkEnqueuer.class
│       │   │   │   │                               ├── JobIntentService$CompatWorkItem.class
│       │   │   │   │                               ├── JobIntentService$ComponentNameWithWakeful.class
│       │   │   │   │                               ├── JobIntentService$GenericWorkItem.class
│       │   │   │   │                               ├── JobIntentService$JobServiceEngineImpl$WrapperWorkItem.class
│       │   │   │   │                               ├── JobIntentService$JobServiceEngineImpl.class
│       │   │   │   │                               ├── JobIntentService$JobWorkEnqueuer.class
│       │   │   │   │                               ├── JobIntentService$WorkEnqueuer.class
│       │   │   │   │                               ├── JobIntentService.class
│       │   │   │   │                               └── PluginRegistrantException.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── firebase_messaging-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── flutter_secure_storage
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   ├── res
│       │   │   │   │   ├── pngs
│       │   │   │   │   │   └── debug
│       │   │   │   │   └── resValues
│       │   │   │   │       └── debug
│       │   │   │   └── source
│       │   │   │       └── buildConfig
│       │   │   │           └── debug
│       │   │   │               └── com
│       │   │   │                   └── it_nomads
│       │   │   │                       └── fluttersecurestorage
│       │   │   │                           └── BuildConfig.java
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── com
│       │   │   │   │                   └── it_nomads
│       │   │   │   │                       └── fluttersecurestorage
│       │   │   │   │                           ├── BuildConfig.class
│       │   │   │   │                           ├── ciphers
│       │   │   │   │                           │   ├── KeyCipherAlgorithm.class
│       │   │   │   │                           │   ├── KeyCipher.class
│       │   │   │   │                           │   ├── KeyCipherFunction.class
│       │   │   │   │                           │   ├── RSACipher18Implementation.class
│       │   │   │   │                           │   ├── RSACipherOAEPImplementation.class
│       │   │   │   │                           │   ├── StorageCipher18Implementation.class
│       │   │   │   │                           │   ├── StorageCipherAlgorithm.class
│       │   │   │   │                           │   ├── StorageCipher.class
│       │   │   │   │                           │   ├── StorageCipherFactory.class
│       │   │   │   │                           │   ├── StorageCipherFunction.class
│       │   │   │   │                           │   └── StorageCipherGCMImplementation.class
│       │   │   │   │                           ├── FlutterSecureStorage.class
│       │   │   │   │                           ├── FlutterSecureStoragePlugin$MethodResultWrapper.class
│       │   │   │   │                           ├── FlutterSecureStoragePlugin$MethodRunner.class
│       │   │   │   │                           └── FlutterSecureStoragePlugin.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-flutter_secure_storage.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── com
│       │   │   │   │               └── it_nomads
│       │   │   │   │                   └── fluttersecurestorage
│       │   │   │   │                       ├── BuildConfig.class
│       │   │   │   │                       ├── ciphers
│       │   │   │   │                       │   ├── KeyCipherAlgorithm.class
│       │   │   │   │                       │   ├── KeyCipher.class
│       │   │   │   │                       │   ├── KeyCipherFunction.class
│       │   │   │   │                       │   ├── RSACipher18Implementation.class
│       │   │   │   │                       │   ├── RSACipherOAEPImplementation.class
│       │   │   │   │                       │   ├── StorageCipher18Implementation.class
│       │   │   │   │                       │   ├── StorageCipherAlgorithm.class
│       │   │   │   │                       │   ├── StorageCipher.class
│       │   │   │   │                       │   ├── StorageCipherFactory.class
│       │   │   │   │                       │   ├── StorageCipherFunction.class
│       │   │   │   │                       │   └── StorageCipherGCMImplementation.class
│       │   │   │   │                       ├── FlutterSecureStorage.class
│       │   │   │   │                       ├── FlutterSecureStoragePlugin$MethodResultWrapper.class
│       │   │   │   │                       ├── FlutterSecureStoragePlugin$MethodRunner.class
│       │   │   │   │                       └── FlutterSecureStoragePlugin.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── flutter_secure_storage-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── jni
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── cxx
│       │   │   │   │   └── Debug
│       │   │   │   │       └── 4k0606v3
│       │   │   │   │           ├── logs
│       │   │   │   │           │   ├── arm64-v8a
│       │   │   │   │           │   │   ├── build_command_jni
│       │   │   │   │           │   │   ├── build_model.json
│       │   │   │   │           │   │   ├── build_stderr_jni.txt
│       │   │   │   │           │   │   ├── build_stdout_jni.txt
│       │   │   │   │           │   │   ├── configure_command
│       │   │   │   │           │   │   ├── configure_stderr.txt
│       │   │   │   │           │   │   ├── configure_stdout.txt
│       │   │   │   │           │   │   ├── generate_cxx_metadata_129_timing.txt
│       │   │   │   │           │   │   └── metadata_generation_record.json
│       │   │   │   │           │   ├── armeabi-v7a
│       │   │   │   │           │   │   ├── build_command_jni
│       │   │   │   │           │   │   ├── build_model.json
│       │   │   │   │           │   │   ├── build_stderr_jni.txt
│       │   │   │   │           │   │   ├── build_stdout_jni.txt
│       │   │   │   │           │   │   ├── configure_command
│       │   │   │   │           │   │   ├── configure_stderr.txt
│       │   │   │   │           │   │   ├── configure_stdout.txt
│       │   │   │   │           │   │   ├── generate_cxx_metadata_129_timing.txt
│       │   │   │   │           │   │   └── metadata_generation_record.json
│       │   │   │   │           │   ├── x86
│       │   │   │   │           │   │   ├── build_command_jni
│       │   │   │   │           │   │   ├── build_model.json
│       │   │   │   │           │   │   ├── build_stderr_jni.txt
│       │   │   │   │           │   │   ├── build_stdout_jni.txt
│       │   │   │   │           │   │   ├── configure_command
│       │   │   │   │           │   │   ├── configure_stderr.txt
│       │   │   │   │           │   │   ├── configure_stdout.txt
│       │   │   │   │           │   │   ├── generate_cxx_metadata_129_timing.txt
│       │   │   │   │           │   │   └── metadata_generation_record.json
│       │   │   │   │           │   └── x86_64
│       │   │   │   │           │       ├── build_command_jni
│       │   │   │   │           │       ├── build_model.json
│       │   │   │   │           │       ├── build_stderr_jni.txt
│       │   │   │   │           │       ├── build_stdout_jni.txt
│       │   │   │   │           │       ├── configure_command
│       │   │   │   │           │       ├── configure_stderr.txt
│       │   │   │   │           │       ├── configure_stdout.txt
│       │   │   │   │           │       ├── generate_cxx_metadata_129_timing.txt
│       │   │   │   │           │       └── metadata_generation_record.json
│       │   │   │   │           └── obj
│       │   │   │   │               ├── arm64-v8a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── armeabi-v7a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── x86
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               └── x86_64
│       │   │   │   │                   └── libdartjni.so
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── com
│       │   │   │   │                   └── github
│       │   │   │   │                       └── dart_lang
│       │   │   │   │                           └── jni
│       │   │   │   │                               ├── JniPlugin.class
│       │   │   │   │                               ├── JniUtils.class
│       │   │   │   │                               ├── PortCleaner$PortPhantom.class
│       │   │   │   │                               ├── PortCleaner.class
│       │   │   │   │                               ├── PortContinuation.class
│       │   │   │   │                               ├── PortProxyBuilder$DartException.class
│       │   │   │   │                               ├── PortProxyBuilder$DartImplementation.class
│       │   │   │   │                               └── PortProxyBuilder.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   │               ├── arm64-v8a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── armeabi-v7a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── x86
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               └── x86_64
│       │   │   │   │                   └── libdartjni.so
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   │               ├── arm64-v8a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── armeabi-v7a
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               ├── x86
│       │   │   │   │               │   └── libdartjni.so
│       │   │   │   │               └── x86_64
│       │   │   │   │                   └── libdartjni.so
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   │           └── proguard.txt
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-jni.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugNativeLibs
│       │   │   │   │           └── out
│       │   │   │   │               └── lib
│       │   │   │   │                   ├── arm64-v8a
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   ├── armeabi-v7a
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   ├── x86
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   └── x86_64
│       │   │   │   │                       └── libdartjni.so
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_test_only_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugNativeLibs
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── com
│       │   │   │   │               └── github
│       │   │   │   │                   └── dart_lang
│       │   │   │   │                       └── jni
│       │   │   │   │                           ├── JniPlugin.class
│       │   │   │   │                           ├── JniUtils.class
│       │   │   │   │                           ├── PortCleaner$PortPhantom.class
│       │   │   │   │                           ├── PortCleaner.class
│       │   │   │   │                           ├── PortContinuation.class
│       │   │   │   │                           ├── PortProxyBuilder$DartException.class
│       │   │   │   │                           ├── PortProxyBuilder$DartImplementation.class
│       │   │   │   │                           └── PortProxyBuilder.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── stripped_native_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── stripDebugDebugSymbols
│       │   │   │   │           └── out
│       │   │   │   │               └── lib
│       │   │   │   │                   ├── arm64-v8a
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   ├── armeabi-v7a
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   ├── x86
│       │   │   │   │                   │   └── libdartjni.so
│       │   │   │   │                   └── x86_64
│       │   │   │   │                       └── libdartjni.so
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── jni-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── jni_flutter
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── com
│       │   │   │   │                   └── github
│       │   │   │   │                       └── dart_lang
│       │   │   │   │                           └── jni_flutter
│       │   │   │   │                               └── JniFlutterPlugin.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   │           └── proguard.txt
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-jni_flutter.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── com
│       │   │   │   │               └── github
│       │   │   │   │                   └── dart_lang
│       │   │   │   │                       └── jni_flutter
│       │   │   │   │                           └── JniFlutterPlugin.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── jni_flutter-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── native_assets
│       │   │   ├── android
│       │   │   ├── flutter-tester
│       │   │   └── linux
│       │   │       └── native_assets.json
│       │   ├── native_hooks
│       │   ├── reports
│       │   │   └── problems
│       │   │       └── problems-report.html
│       │   ├── shared_preferences_android
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── io
│       │   │   │   │                   └── flutter
│       │   │   │   │                       └── plugins
│       │   │   │   │                           └── sharedpreferences
│       │   │   │   │                               ├── LegacySharedPreferencesPlugin$ListEncoder.class
│       │   │   │   │                               ├── LegacySharedPreferencesPlugin.class
│       │   │   │   │                               ├── Messages$FlutterError.class
│       │   │   │   │                               ├── Messages$PigeonCodec.class
│       │   │   │   │                               ├── Messages$SharedPreferencesApi.class
│       │   │   │   │                               ├── Messages.class
│       │   │   │   │                               └── SharedPreferencesListEncoder.class
│       │   │   │   ├── java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugJavaRes
│       │   │   │   │           └── out
│       │   │   │   │               ├── io
│       │   │   │   │               │   └── flutter
│       │   │   │   │               │       └── plugins
│       │   │   │   │               │           └── sharedpreferences
│       │   │   │   │               └── META-INF
│       │   │   │   │                   └── shared_preferences_android_debug.kotlin_module
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-shared_preferences_android.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           ├── io
│       │   │   │   │           │   └── flutter
│       │   │   │   │           │       └── plugins
│       │   │   │   │           │           └── sharedpreferences
│       │   │   │   │           │               ├── LegacySharedPreferencesPlugin$ListEncoder.class
│       │   │   │   │           │               ├── LegacySharedPreferencesPlugin.class
│       │   │   │   │           │               ├── ListEncoder.class
│       │   │   │   │           │               ├── Messages$FlutterError.class
│       │   │   │   │           │               ├── Messages$PigeonCodec.class
│       │   │   │   │           │               ├── Messages$SharedPreferencesApi.class
│       │   │   │   │           │               ├── MessagesAsyncPigeonCodec.class
│       │   │   │   │           │               ├── MessagesAsyncPigeonUtils.class
│       │   │   │   │           │               ├── Messages.class
│       │   │   │   │           │               ├── SharedPreferencesAsyncApi$Companion.class
│       │   │   │   │           │               ├── SharedPreferencesAsyncApi.class
│       │   │   │   │           │               ├── SharedPreferencesBackend.class
│       │   │   │   │           │               ├── SharedPreferencesError.class
│       │   │   │   │           │               ├── SharedPreferencesListEncoder.class
│       │   │   │   │           │               ├── SharedPreferencesPigeonOptions$Companion.class
│       │   │   │   │           │               ├── SharedPreferencesPigeonOptions.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$clear$1$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$clear$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$dataStoreSetString$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getAll$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getBool$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getDouble$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getInt$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getKeys$prefs$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getPrefs$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getString$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1$2$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1$2.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setBool$1$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setBool$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setDeprecatedStringList$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setDouble$1$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setDouble$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setEncodedStringList$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setInt$1$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setInt$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin$setString$1.class
│       │   │   │   │           │               ├── SharedPreferencesPlugin.class
│       │   │   │   │           │               ├── SharedPreferencesPluginKt.class
│       │   │   │   │           │               ├── StringListLookupResultType$Companion.class
│       │   │   │   │           │               ├── StringListLookupResultType.class
│       │   │   │   │           │               ├── StringListObjectInputStream.class
│       │   │   │   │           │               ├── StringListResult$Companion.class
│       │   │   │   │           │               └── StringListResult.class
│       │   │   │   │           └── META-INF
│       │   │   │   │               └── shared_preferences_android_debug.kotlin_module
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── kotlin
│       │   │   │   └── compileDebugKotlin
│       │   │   │       ├── cacheable
│       │   │   │       │   ├── caches-jvm
│       │   │   │       │   │   ├── inputs
│       │   │   │       │   │   │   ├── source-to-output.tab
│       │   │   │       │   │   │   ├── source-to-output.tab_i
│       │   │   │       │   │   │   ├── source-to-output.tab_i.len
│       │   │   │       │   │   │   ├── source-to-output.tab.keystream
│       │   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│       │   │   │       │   │   │   ├── source-to-output.tab.len
│       │   │   │       │   │   │   └── source-to-output.tab.values.at
│       │   │   │       │   │   ├── jvm
│       │   │   │       │   │   │   └── kotlin
│       │   │   │       │   │   │       ├── class-attributes.tab
│       │   │   │       │   │   │       ├── class-attributes.tab_i
│       │   │   │       │   │   │       ├── class-attributes.tab_i.len
│       │   │   │       │   │   │       ├── class-attributes.tab.keystream
│       │   │   │       │   │   │       ├── class-attributes.tab.keystream.len
│       │   │   │       │   │   │       ├── class-attributes.tab.len
│       │   │   │       │   │   │       ├── class-attributes.tab.values.at
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab_i.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.keystream.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.len
│       │   │   │       │   │   │       ├── class-fq-name-to-source.tab.values.at
│       │   │   │       │   │   │       ├── constants.tab
│       │   │   │       │   │   │       ├── constants.tab_i
│       │   │   │       │   │   │       ├── constants.tab_i.len
│       │   │   │       │   │   │       ├── constants.tab.keystream
│       │   │   │       │   │   │       ├── constants.tab.keystream.len
│       │   │   │       │   │   │       ├── constants.tab.len
│       │   │   │       │   │   │       ├── constants.tab.values.at
│       │   │   │       │   │   │       ├── internal-name-to-source.tab
│       │   │   │       │   │   │       ├── internal-name-to-source.tab_i
│       │   │   │       │   │   │       ├── internal-name-to-source.tab_i.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.keystream.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.len
│       │   │   │       │   │   │       ├── internal-name-to-source.tab.values.at
│       │   │   │       │   │   │       ├── package-parts.tab
│       │   │   │       │   │   │       ├── package-parts.tab_i
│       │   │   │       │   │   │       ├── package-parts.tab_i.len
│       │   │   │       │   │   │       ├── package-parts.tab.keystream
│       │   │   │       │   │   │       ├── package-parts.tab.keystream.len
│       │   │   │       │   │   │       ├── package-parts.tab.len
│       │   │   │       │   │   │       ├── package-parts.tab.values.at
│       │   │   │       │   │   │       ├── proto.tab
│       │   │   │       │   │   │       ├── proto.tab_i
│       │   │   │       │   │   │       ├── proto.tab_i.len
│       │   │   │       │   │   │       ├── proto.tab.keystream
│       │   │   │       │   │   │       ├── proto.tab.keystream.len
│       │   │   │       │   │   │       ├── proto.tab.len
│       │   │   │       │   │   │       ├── proto.tab.values.at
│       │   │   │       │   │   │       ├── source-to-classes.tab
│       │   │   │       │   │   │       ├── source-to-classes.tab_i
│       │   │   │       │   │   │       ├── source-to-classes.tab_i.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.keystream
│       │   │   │       │   │   │       ├── source-to-classes.tab.keystream.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.len
│       │   │   │       │   │   │       ├── source-to-classes.tab.values.at
│       │   │   │       │   │   │       ├── subtypes.tab
│       │   │   │       │   │   │       ├── subtypes.tab_i
│       │   │   │       │   │   │       ├── subtypes.tab_i.len
│       │   │   │       │   │   │       ├── subtypes.tab.keystream
│       │   │   │       │   │   │       ├── subtypes.tab.keystream.len
│       │   │   │       │   │   │       ├── subtypes.tab.len
│       │   │   │       │   │   │       ├── subtypes.tab.values.at
│       │   │   │       │   │   │       ├── supertypes.tab
│       │   │   │       │   │   │       ├── supertypes.tab_i
│       │   │   │       │   │   │       ├── supertypes.tab_i.len
│       │   │   │       │   │   │       ├── supertypes.tab.keystream
│       │   │   │       │   │   │       ├── supertypes.tab.keystream.len
│       │   │   │       │   │   │       ├── supertypes.tab.len
│       │   │   │       │   │   │       └── supertypes.tab.values.at
│       │   │   │       │   │   └── lookups
│       │   │   │       │   │       ├── counters.tab
│       │   │   │       │   │       ├── file-to-id.tab
│       │   │   │       │   │       ├── file-to-id.tab_i
│       │   │   │       │   │       ├── file-to-id.tab_i.len
│       │   │   │       │   │       ├── file-to-id.tab.keystream
│       │   │   │       │   │       ├── file-to-id.tab.keystream.len
│       │   │   │       │   │       ├── file-to-id.tab.len
│       │   │   │       │   │       ├── file-to-id.tab.values.at
│       │   │   │       │   │       ├── id-to-file.tab
│       │   │   │       │   │       ├── id-to-file.tab_i
│       │   │   │       │   │       ├── id-to-file.tab_i.len
│       │   │   │       │   │       ├── id-to-file.tab.keystream
│       │   │   │       │   │       ├── id-to-file.tab.keystream.len
│       │   │   │       │   │       ├── id-to-file.tab.len
│       │   │   │       │   │       ├── id-to-file.tab.values.at
│       │   │   │       │   │       ├── lookups.tab
│       │   │   │       │   │       ├── lookups.tab_i
│       │   │   │       │   │       ├── lookups.tab_i.len
│       │   │   │       │   │       ├── lookups.tab.keystream
│       │   │   │       │   │       ├── lookups.tab.keystream.len
│       │   │   │       │   │       ├── lookups.tab.len
│       │   │   │       │   │       └── lookups.tab.values.at
│       │   │   │       │   └── last-build.bin
│       │   │   │       ├── classpath-snapshot
│       │   │   │       │   └── shrunk-classpath-snapshot.bin
│       │   │   │       └── local-state
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── shared_preferences_android-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       ├── compileDebugJavaWithJavac
│       │   │       │   └── previous-compilation-data.bin
│       │   │       └── kotlin-classes
│       │   │           └── debug
│       │   │               ├── io
│       │   │               │   └── flutter
│       │   │               │       └── plugins
│       │   │               │           └── sharedpreferences
│       │   │               │               ├── ListEncoder.class
│       │   │               │               ├── MessagesAsyncPigeonCodec.class
│       │   │               │               ├── MessagesAsyncPigeonUtils.class
│       │   │               │               ├── SharedPreferencesAsyncApi$Companion.class
│       │   │               │               ├── SharedPreferencesAsyncApi.class
│       │   │               │               ├── SharedPreferencesBackend.class
│       │   │               │               ├── SharedPreferencesError.class
│       │   │               │               ├── SharedPreferencesPigeonOptions$Companion.class
│       │   │               │               ├── SharedPreferencesPigeonOptions.class
│       │   │               │               ├── SharedPreferencesPlugin$clear$1$1.class
│       │   │               │               ├── SharedPreferencesPlugin$clear$1.class
│       │   │               │               ├── SharedPreferencesPlugin$dataStoreSetString$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getAll$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getBool$1$invokeSuspend$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getBool$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getDouble$1$invokeSuspend$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getDouble$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getInt$1$invokeSuspend$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getInt$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getKeys$prefs$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getPrefs$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getString$1$invokeSuspend$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getString$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$getValueByKey$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1$2$1.class
│       │   │               │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1$2.class
│       │   │               │               ├── SharedPreferencesPlugin$readAllKeys$$inlined$map$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setBool$1$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setBool$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setDeprecatedStringList$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setDouble$1$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setDouble$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setEncodedStringList$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setInt$1$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setInt$1.class
│       │   │               │               ├── SharedPreferencesPlugin$setString$1.class
│       │   │               │               ├── SharedPreferencesPlugin.class
│       │   │               │               ├── SharedPreferencesPluginKt.class
│       │   │               │               ├── StringListLookupResultType$Companion.class
│       │   │               │               ├── StringListLookupResultType.class
│       │   │               │               ├── StringListObjectInputStream.class
│       │   │               │               ├── StringListResult$Companion.class
│       │   │               │               └── StringListResult.class
│       │   │               └── META-INF
│       │   │                   └── shared_preferences_android_debug.kotlin_module
│       │   ├── sqflite_android
│       │   │   ├── generated
│       │   │   │   ├── ap_generated_sources
│       │   │   │   │   └── debug
│       │   │   │   │       └── out
│       │   │   │   └── res
│       │   │   │       ├── pngs
│       │   │   │       │   └── debug
│       │   │   │       └── resValues
│       │   │   │           └── debug
│       │   │   ├── intermediates
│       │   │   │   ├── aapt_friendly_merged_manifests
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── aapt
│       │   │   │   │               ├── AndroidManifest.xml
│       │   │   │   │               └── output-metadata.json
│       │   │   │   ├── aar_libs_directory
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── libs
│       │   │   │   ├── aar_main_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── syncDebugLibJars
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── aar_metadata
│       │   │   │   │   └── debug
│       │   │   │   │       └── writeDebugAarMetadata
│       │   │   │   │           └── aar-metadata.properties
│       │   │   │   ├── annotation_processor_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── javaPreCompileDebug
│       │   │   │   │           └── annotationProcessors.json
│       │   │   │   ├── annotations_typedef_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   │           └── typedefs.txt
│       │   │   │   ├── annotations_zip
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDebugAnnotations
│       │   │   │   ├── assets
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugAssets
│       │   │   │   ├── compiled_local_resources
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugLibraryResources
│       │   │   │   │           └── out
│       │   │   │   ├── compile_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibCompileToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   ├── compile_r_class_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.jar
│       │   │   │   ├── compile_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugRFile
│       │   │   │   │           └── R.txt
│       │   │   │   ├── data_binding_layout_info_type_package
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   │           └── out
│       │   │   │   ├── generated_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugGeneratedProguardFiles
│       │   │   │   ├── incremental
│       │   │   │   │   ├── debug
│       │   │   │   │   │   └── packageDebugResources
│       │   │   │   │   │       ├── compile-file-map.properties
│       │   │   │   │   │       ├── merged.dir
│       │   │   │   │   │       ├── merger.xml
│       │   │   │   │   │       └── stripped.dir
│       │   │   │   │   ├── debug-mergeJavaRes
│       │   │   │   │   │   ├── merge-state
│       │   │   │   │   │   └── zip-cache
│       │   │   │   │   ├── mergeDebugAssets
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   ├── mergeDebugJniLibFolders
│       │   │   │   │   │   └── merger.xml
│       │   │   │   │   └── mergeDebugShaders
│       │   │   │   │       └── merger.xml
│       │   │   │   ├── javac
│       │   │   │   │   └── debug
│       │   │   │   │       └── compileDebugJavaWithJavac
│       │   │   │   │           └── classes
│       │   │   │   │               └── com
│       │   │   │   │                   └── tekartik
│       │   │   │   │                       └── sqflite
│       │   │   │   │                           ├── Constant.class
│       │   │   │   │                           ├── Database$1.class
│       │   │   │   │                           ├── Database.class
│       │   │   │   │                           ├── DatabaseDelegate.class
│       │   │   │   │                           ├── DatabaseTask.class
│       │   │   │   │                           ├── DatabaseWorker.class
│       │   │   │   │                           ├── DatabaseWorkerPool$1.class
│       │   │   │   │                           ├── DatabaseWorkerPool.class
│       │   │   │   │                           ├── DatabaseWorkerPoolImpl.class
│       │   │   │   │                           ├── dev
│       │   │   │   │                           │   └── Debug.class
│       │   │   │   │                           ├── LogLevel.class
│       │   │   │   │                           ├── operation
│       │   │   │   │                           │   ├── BaseOperation.class
│       │   │   │   │                           │   ├── BaseReadOperation.class
│       │   │   │   │                           │   ├── BatchOperation$BatchOperationResult.class
│       │   │   │   │                           │   ├── BatchOperation.class
│       │   │   │   │                           │   ├── MethodCallOperation$Result.class
│       │   │   │   │                           │   ├── MethodCallOperation.class
│       │   │   │   │                           │   ├── Operation.class
│       │   │   │   │                           │   ├── OperationResult.class
│       │   │   │   │                           │   ├── OperationRunnable.class
│       │   │   │   │                           │   ├── QueuedOperation.class
│       │   │   │   │                           │   └── SqlErrorInfo.class
│       │   │   │   │                           ├── SingleDatabaseWorkerPoolImpl.class
│       │   │   │   │                           ├── SqfliteCursor.class
│       │   │   │   │                           ├── SqflitePlugin$1.class
│       │   │   │   │                           ├── SqflitePlugin$2.class
│       │   │   │   │                           ├── SqflitePlugin.class
│       │   │   │   │                           ├── SqlCommand.class
│       │   │   │   │                           └── Utils.class
│       │   │   │   ├── library_and_local_jars_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectAndLocalJars
│       │   │   │   │           └── jni
│       │   │   │   ├── library_art_profile
│       │   │   │   │   └── debug
│       │   │   │   │       └── prepareDebugArtProfile
│       │   │   │   ├── library_jni
│       │   │   │   │   └── debug
│       │   │   │   │       └── copyDebugJniLibsProjectOnly
│       │   │   │   │           └── jni
│       │   │   │   ├── lint_publish_jar
│       │   │   │   │   └── global
│       │   │   │   │       └── prepareLintJarForPublish
│       │   │   │   ├── local_only_symbol_list
│       │   │   │   │   └── debug
│       │   │   │   │       └── parseDebugLocalResources
│       │   │   │   │           └── R-def.txt
│       │   │   │   ├── manifest_merge_blame_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   │   ├── merged_consumer_proguard_file
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugConsumerProguardFiles
│       │   │   │   ├── merged_java_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJavaResource
│       │   │   │   │           └── feature-sqflite_android.jar
│       │   │   │   ├── merged_jni_libs
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugJniLibFolders
│       │   │   │   │           └── out
│       │   │   │   ├── merged_manifest
│       │   │   │   │   └── debug
│       │   │   │   │       └── processDebugManifest
│       │   │   │   │           └── AndroidManifest.xml
│       │   │   │   ├── merged_shaders
│       │   │   │   │   └── debug
│       │   │   │   │       └── mergeDebugShaders
│       │   │   │   │           └── out
│       │   │   │   ├── navigation_json
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksDebug
│       │   │   │   │           └── navigation.json
│       │   │   │   ├── navigation_json_for_aar
│       │   │   │   │   └── debug
│       │   │   │   │       └── extractDeepLinksForAarDebug
│       │   │   │   ├── nested_resources_validation_report
│       │   │   │   │   └── debug
│       │   │   │   │       └── generateDebugResources
│       │   │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   │   ├── packaged_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── public_res
│       │   │   │   │   └── debug
│       │   │   │   │       └── packageDebugResources
│       │   │   │   ├── runtime_library_classes_dir
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToDirDebug
│       │   │   │   │           └── com
│       │   │   │   │               └── tekartik
│       │   │   │   │                   └── sqflite
│       │   │   │   │                       ├── Constant.class
│       │   │   │   │                       ├── Database$1.class
│       │   │   │   │                       ├── Database.class
│       │   │   │   │                       ├── DatabaseDelegate.class
│       │   │   │   │                       ├── DatabaseTask.class
│       │   │   │   │                       ├── DatabaseWorker.class
│       │   │   │   │                       ├── DatabaseWorkerPool$1.class
│       │   │   │   │                       ├── DatabaseWorkerPool.class
│       │   │   │   │                       ├── DatabaseWorkerPoolImpl.class
│       │   │   │   │                       ├── dev
│       │   │   │   │                       │   └── Debug.class
│       │   │   │   │                       ├── LogLevel.class
│       │   │   │   │                       ├── operation
│       │   │   │   │                       │   ├── BaseOperation.class
│       │   │   │   │                       │   ├── BaseReadOperation.class
│       │   │   │   │                       │   ├── BatchOperation$BatchOperationResult.class
│       │   │   │   │                       │   ├── BatchOperation.class
│       │   │   │   │                       │   ├── MethodCallOperation$Result.class
│       │   │   │   │                       │   ├── MethodCallOperation.class
│       │   │   │   │                       │   ├── Operation.class
│       │   │   │   │                       │   ├── OperationResult.class
│       │   │   │   │                       │   ├── OperationRunnable.class
│       │   │   │   │                       │   ├── QueuedOperation.class
│       │   │   │   │                       │   └── SqlErrorInfo.class
│       │   │   │   │                       ├── SingleDatabaseWorkerPoolImpl.class
│       │   │   │   │                       ├── SqfliteCursor.class
│       │   │   │   │                       ├── SqflitePlugin$1.class
│       │   │   │   │                       ├── SqflitePlugin$2.class
│       │   │   │   │                       ├── SqflitePlugin.class
│       │   │   │   │                       ├── SqlCommand.class
│       │   │   │   │                       └── Utils.class
│       │   │   │   ├── runtime_library_classes_jar
│       │   │   │   │   └── debug
│       │   │   │   │       └── bundleLibRuntimeToJarDebug
│       │   │   │   │           └── classes.jar
│       │   │   │   └── symbol_list_with_package_name
│       │   │   │       └── debug
│       │   │   │           └── generateDebugRFile
│       │   │   │               └── package-aware-r.txt
│       │   │   ├── outputs
│       │   │   │   ├── aar
│       │   │   │   │   └── sqflite_android-debug.aar
│       │   │   │   └── logs
│       │   │   │       └── manifest-merger-debug-report.txt
│       │   │   └── tmp
│       │   │       └── compileDebugJavaWithJavac
│       │   │           └── previous-compilation-data.bin
│       │   ├── test_cache
│       │   │   └── build
│       │   │       └── 89a6598c8854ed031dfc25d83c80860e.cache.dill.track.dill
│       │   └── unit_test_assets
│       │       ├── AssetManifest.bin
│       │       ├── FontManifest.json
│       │       ├── fonts
│       │       │   └── MaterialIcons-Regular.otf
│       │       ├── NativeAssetsManifest.json
│       │       ├── NOTICES.Z
│       │       └── shaders
│       │           ├── ink_sparkle.frag
│       │           └── stretch_effect.frag
│       ├── ios
│       │   ├── Flutter
│       │   │   ├── AppFrameworkInfo.plist
│       │   │   ├── Debug.xcconfig
│       │   │   ├── ephemeral
│       │   │   │   ├── flutter_lldb_helper.py
│       │   │   │   └── flutter_lldbinit
│       │   │   ├── flutter_export_environment.sh
│       │   │   ├── Generated.xcconfig
│       │   │   └── Release.xcconfig
│       │   ├── Runner
│       │   │   ├── AppDelegate.swift
│       │   │   ├── Assets.xcassets
│       │   │   │   ├── AppIcon.appiconset
│       │   │   │   │   ├── Contents.json
│       │   │   │   │   ├── Icon-App-1024x1024@1x.png
│       │   │   │   │   ├── Icon-App-20x20@1x.png
│       │   │   │   │   ├── Icon-App-20x20@2x.png
│       │   │   │   │   ├── Icon-App-20x20@3x.png
│       │   │   │   │   ├── Icon-App-29x29@1x.png
│       │   │   │   │   ├── Icon-App-29x29@2x.png
│       │   │   │   │   ├── Icon-App-29x29@3x.png
│       │   │   │   │   ├── Icon-App-40x40@1x.png
│       │   │   │   │   ├── Icon-App-40x40@2x.png
│       │   │   │   │   ├── Icon-App-40x40@3x.png
│       │   │   │   │   ├── Icon-App-60x60@2x.png
│       │   │   │   │   ├── Icon-App-60x60@3x.png
│       │   │   │   │   ├── Icon-App-76x76@1x.png
│       │   │   │   │   ├── Icon-App-76x76@2x.png
│       │   │   │   │   └── Icon-App-83.5x83.5@2x.png
│       │   │   │   └── LaunchImage.imageset
│       │   │   │       ├── Contents.json
│       │   │   │       ├── LaunchImage@2x.png
│       │   │   │       ├── LaunchImage@3x.png
│       │   │   │       ├── LaunchImage.png
│       │   │   │       └── README.md
│       │   │   ├── Base.lproj
│       │   │   │   ├── LaunchScreen.storyboard
│       │   │   │   └── Main.storyboard
│       │   │   ├── GeneratedPluginRegistrant.h
│       │   │   ├── GeneratedPluginRegistrant.m
│       │   │   ├── Info.plist
│       │   │   ├── Runner-Bridging-Header.h
│       │   │   └── SceneDelegate.swift
│       │   ├── RunnerTests
│       │   │   └── RunnerTests.swift
│       │   ├── Runner.xcodeproj
│       │   │   ├── project.pbxproj
│       │   │   ├── project.xcworkspace
│       │   │   │   ├── contents.xcworkspacedata
│       │   │   │   └── xcshareddata
│       │   │   │       ├── IDEWorkspaceChecks.plist
│       │   │   │       └── WorkspaceSettings.xcsettings
│       │   │   └── xcshareddata
│       │   │       └── xcschemes
│       │   │           └── Runner.xcscheme
│       │   └── Runner.xcworkspace
│       │       ├── contents.xcworkspacedata
│       │       └── xcshareddata
│       │           ├── IDEWorkspaceChecks.plist
│       │           └── WorkspaceSettings.xcsettings
│       ├── lib
│       │   ├── core
│       │   │   ├── auth
│       │   │   │   └── auth_state_notifier.dart
│       │   │   ├── network
│       │   │   │   └── api_client.dart
│       │   │   ├── router
│       │   │   │   └── app_router.dart
│       │   │   └── theme
│       │   │       └── app_theme.dart
│       │   ├── features
│       │   │   ├── academics
│       │   │   │   └── presentation
│       │   │   │       └── academics_screen.dart
│       │   │   ├── attendance
│       │   │   │   └── presentation
│       │   │   │       └── attendance_screen.dart
│       │   │   ├── auth
│       │   │   │   ├── application
│       │   │   │   │   └── auth_repository.dart
│       │   │   │   └── presentation
│       │   │   │       └── login_screen.dart
│       │   │   ├── home
│       │   │   │   └── presentation
│       │   │   │       └── home_screen.dart
│       │   │   ├── profile
│       │   │   │   └── presentation
│       │   │   │       └── profile_screen.dart
│       │   │   └── results
│       │   │       └── presentation
│       │   │           └── results_screen.dart
│       │   └── main.dart
│       ├── pubspec.lock
│       ├── pubspec.yaml
│       ├── README.md
│       ├── ssams_student.iml
│       └── test
│           └── widget_test.dart
├── RULES.md
├── scripts
│   └── verify_staging.sh
└── tests
    ├── load
    │   └── k6-artms-load-test.js
    └── smoke
        └── production_smoke_test.sh

2022 directories, 2105 files
