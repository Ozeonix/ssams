# Grading Engine Specification

## Goal
Provide deterministic, versioned academic calculation.

## Inputs
- grading scheme version;
- subject/component configuration;
- full marks;
- pass marks;
- weight;
- credit;
- raw marks/status;
- rounding policy;
- inclusion policy.

## Processing

```text
Raw Component Marks
 -> Validate
 -> Component Percentage
 -> Component/Subject Aggregation
 -> Grade Band
 -> Grade Point
 -> Pass Rule
 -> Credit Weight
 -> Result Snapshot
 -> GPA
```

## Status Handling
Supported baseline:
- PRESENT
- ABSENT
- WITHHELD
- EXPELLED
- NOT_APPLICABLE
- MISSING

Each institution may configure how a status affects final result, but behavior must be explicit.

## Grade Band
A band contains:
- min inclusive;
- max inclusive;
- letter;
- point;
- pass flag.

Validation:
- no overlaps;
- intended range covered;
- min <= max.

## Rounding
Store unrounded intermediate values where useful.
Apply configured rounding only at defined boundaries.
Document whether GPA is rounded:
- per subject;
- final GPA;
- displayed only.

## GPA
Baseline weighted formula:

`GPA = sum(credit_hours * grade_point) / sum(credit_hours)`

Institution-specific policies may exclude non-credit components.

## Historical Stability
When a grading scheme is changed:
- create new version;
- existing result snapshots keep old version;
- never recompute historical published records implicitly.

## Test Requirements
Every grade boundary and every special status requires a test.
