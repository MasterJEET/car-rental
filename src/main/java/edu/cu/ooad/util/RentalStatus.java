package edu.cu.ooad.util;

/**
 * Enumerates all possible state of a car rental
 */
public enum RentalStatus {
    /**
     * Unknown status
     */
    DEFAULT,
    /**
     * Customer has the Cars, rent period is active
     */
    ACTIVE,
    /**
     * Customer has returned the Cars after completion of rental duration
     */
    COMPLETE
}