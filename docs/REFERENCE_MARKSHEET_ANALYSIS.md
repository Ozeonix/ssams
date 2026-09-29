# Reference Marksheet Analysis

This document records the academic data patterns visible in the supplied sample marksheets. It is a requirements reference, not a database seed file.

## Observed Academic Patterns

### Grade/Class 9 Technical & Vocational
The supplied Class 9 marksheet demonstrates a mixed general + technical curriculum with separate theory/practical marks for several subjects.

Observed subject pattern:
- Nepali
- English
- Mathematics
- Science
- Engineering Drawing
- Computer Fundamentals
- C Programming
- Fundamentals of Digital System
- Electrical Engineering
- Web Page Development

Observed data fields:
- subject serial number;
- subject name;
- full marks theory/practical;
- pass marks theory/practical;
- obtained marks theory/practical;
- total;
- grand total;
- percentage;
- division/result;
- attendance;
- issue/checking signatures.

### Grade 10
The supplied Grade 10 grade sheet demonstrates a grade-based national/secondary examination structure.

Observed technical subjects include patterns such as:
- Computer Repair & Maintenance
- Computer Networks
- Database Management System
- Electronic Devices & Circuits
- Microprocessor
- Object Oriented Programming
- Extra Mathematics

The sheet includes:
- credit hour;
- obtained grade;
- theory/practical markers;
- final grade;
- GPA.

### Grade 11
The supplied Grade 11 sheet demonstrates:
- compulsory English;
- Physics theory/practical;
- Chemistry theory/practical;
- Mathematics;
- Hardware and Architecture theory/practical;
- Computer Programming theory/practical;
- credit hour;
- grade point;
- grade;
- final grade;
- GPA.

### Grade 12 / School-Leaving Style
The supplied school-leaving grade sheet demonstrates another grade-based result layout with:
- code;
- subject;
- credit hour;
- grade point;
- grade;
- final grade;
- remarks;
- GPA.

## System Implications

The product must support both:
1. **Marks-first model** — full marks, pass marks, theory/practical obtained marks, total, percentage/division.
2. **Grade-first model** — credit hours, grade point, letter grade and final grade.

Do not force every institution into one calculation pipeline.

Use an assessment abstraction:

```text
Exam
  └── Exam Subject
       └── Assessment Component
            ├── Theory
            ├── Practical
            ├── Internal
            ├── Project
            └── Other configured component
```

Then use a versioned grading/result engine to calculate:
- total marks;
- percentage;
- grade;
- grade point;
- final grade;
- GPA;
- result status.

## Important Product Decision

The sample documents show that institutional formats differ. Therefore:
- document templates must be configurable;
- grading rules must be configurable;
- subject components must be configurable;
- credit hours must be configurable;
- historical result snapshots must remain stable.

No personal identifiers from the supplied samples should be copied into source code, demo data or automated tests.
