package com.arcad.atumerlin.common;

/**
 * Thrown when a domain/business rule migrated from the original RPG programs is
 * violated (e.g. ordering an article that does not exist, or an unknown VAT code).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
