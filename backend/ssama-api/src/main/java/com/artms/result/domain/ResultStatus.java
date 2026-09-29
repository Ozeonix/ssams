package com.artms.result.domain;

/**
 * Result status matching CHECK constraint in result_snapshot table.
 */
public enum ResultStatus {
    PASS,
    FAIL,
    CONDITIONAL_PASS,
    INCOMPLETE,
    WITHHELD,
    EXPELLED,
    ABSENT
}
