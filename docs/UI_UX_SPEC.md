# UI/UX Specification

## Design Principles
- Clean, professional academic administration.
- Desktop-first for staff.
- Mobile-first for students.
- Fast bulk operations.
- Clear status and workflow states.
- Never hide critical academic calculations.
- Minimize destructive actions.

## Student Mobile

### Navigation
1. Home
2. Academics
3. Attendance
4. Results
5. More

### Home
Cards:
- current class;
- next timetable;
- attendance;
- latest GPA/result;
- notices;
- unread notifications.

### Result Screen
Show:
- exam name;
- result publication date;
- GPA;
- overall result;
- subject list;
- credit;
- theory;
- practical;
- total;
- grade;
- grade point;
- remarks.

Do not display a result until backend marks it published.

### Offline
Cache:
- last known timetable;
- notices;
- published results;
- profile.
Never cache editable confidential admin data.

## College Desktop

### Dashboard
- active students;
- today's attendance;
- upcoming exams;
- pending mark verification;
- unpublished results;
- notifications;
- system alerts.

### Data Tables
Required:
- search;
- filters;
- pagination;
- column visibility;
- CSV export;
- bulk selection;
- keyboard-friendly navigation.

### Marks Entry
Spreadsheet-like grid:
- sticky student column;
- component tabs;
- numeric validation;
- absent/withheld controls;
- unsaved indicator;
- save state;
- optimistic locking warning.

### Result Approval
Display:
- exam;
- cohort;
- number of students;
- pass/fail;
- missing marks;
- calculation errors;
- GPA distribution;
- verification status.

Approval requires explicit confirmation and reason.

## Dynamic Admin Settings
Grouped:
- Institution
- Academic
- Grading
- Exams
- Attendance
- Notifications
- Documents
- Roles
- Integrations
- Security
- Feature Flags

## Visual Language
Use a consistent spacing scale, typography hierarchy, semantic statuses and accessible contrast. Do not encode meaning by color alone.

## States
Every screen must define:
- loading;
- empty;
- error;
- unauthorized;
- offline;
- stale data;
- success;
- partial failure.

## UX Safety
- Destructive operations require confirmation.
- Published-result changes must show impact and require reason.
- Bulk operations show affected count.
- Unsaved edits have explicit state.
