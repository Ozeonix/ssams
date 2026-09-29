# Test Cases

## TC-001 Login
**Given:** valid active user  
**When:** login  
**Then:** access and refresh tokens returned.

## TC-002 Wrong password
Returns 401 without revealing whether username exists.

## TC-003 Tenant isolation
User A from Tenant A requests Tenant B student ID.
**Expected:** 404/403 according to resource disclosure policy; no data.

## TC-004 Marks exceed full marks
Full marks 40, input 41.
**Expected:** validation failure.

## TC-005 Negative marks
Input -1.
**Expected:** validation failure.

## TC-006 Absent
Status ABSENT, raw marks null.
**Expected:** accepted and represented as absent, not zero.

## TC-007 Practical + theory
Configure theory 40 and practical 60.
Enter valid marks.
**Expected:** subject total and final grade calculated according to configuration.

## TC-008 Grade boundary
Test every min/max boundary.
**Expected:** exact configured grade.

## TC-009 Overlapping grade bands
Attempt overlapping bands.
**Expected:** configuration rejected.

## TC-010 GPA
Credits 4 and 2, grade points 3 and 4.
**Expected:** `(4*3 + 2*4)/6 = 3.333...`, rounded according to scheme.

## TC-011 Missing marks
Exam has missing required component.
**Expected:** result cannot be approved unless policy explicitly permits.

## TC-012 Publish before approval
**Expected:** workflow error.

## TC-013 Published result visibility
Before publish: student cannot see.
After publish: student can see.

## TC-014 Published result mutation
Direct mark edit against published snapshot.
**Expected:** rejected.

## TC-015 Correction workflow
Authorized correction request → approval → new result version → audit.

## TC-016 Audit
Every result approval has actor/time/reason/event.

## TC-017 Timetable conflict
Same teacher/time/overlap.
**Expected:** conflict detected.

## TC-018 Attendance percentage
Correct numerator/denominator according to configured attendance rules.

## TC-019 Notification
Publish result.
**Expected:** outbox event + notification.

## TC-020 WebSocket unauthorized subscription
Attempt subscription to another class.
**Expected:** rejected.

## TC-021 Import validation
CSV contains duplicate registration number.
**Expected:** preview error; no commit.

## TC-022 Import transaction
Processing fails halfway.
**Expected:** defined transactional/batch behavior, no silent partial corruption.

## TC-023 PDF
Generate grade sheet from immutable snapshot.
**Expected:** values match snapshot exactly.

## TC-024 Role boundary
Teacher tries to approve result.
**Expected:** forbidden unless explicitly granted.

## TC-025 Optimistic locking
Two admins edit same marks row.
**Expected:** stale second update returns conflict.

## TC-026 Refresh token rotation
Reuse old refresh token.
**Expected:** rejected.

## TC-027 Rate limiting
Repeated failed logins.
**Expected:** throttling/lock policy.

## TC-028 Backup restore
Restore latest backup to isolated environment.
**Expected:** application can start and academic data verifies.

## TC-029 Mobile offline
Open cached published result offline.
**Expected:** result visible with stale/offline indicator.

## TC-030 Real-time reconnect
Disconnect WebSocket, publish notice, reconnect.
**Expected:** missed data synchronized through REST.
