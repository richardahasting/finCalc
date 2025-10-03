package com.finCalc.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a numeric value on the calculator stack.
 *
 * <p>All numeric values are stored as {@link BigDecimal} to ensure precision
 * in financial calculations. This prevents floating-point precision errors
 * that occur with primitive double values.
 *
 * @param value the BigDecimal value
 */
public record BigNumber(BigDecimal value) implements StackItem {

    /**
     * Global precision (scale) for all calculator operations.
     *
     * <p>This controls the number of decimal places used in division and
     * other operations that require explicit scale specification.
     *
     * <p>Default is 10 decimal places, which is suitable for most financial
     * and engineering calculations.
     */
    private static volatile int globalPrecision = 10;

    /**
     * Global rounding mode for all calculator operations.
     *
     * <p>Default is HALF_UP, which rounds towards "nearest neighbor" unless
     * both neighbors are equidistant, in which case round up.
     */
    private static volatile RoundingMode globalRoundingMode = RoundingMode.HALF_UP;

    /**
     * Gets the current global precision (scale).
     *
     * @return the number of decimal places used in calculations
     */
    public static int getPrecision() {
        return globalPrecision;
    }

    /**
     * Sets the global precision (scale) for all calculations.
     *
     * <p><strong>Note:</strong> This affects all subsequent calculations.
     * For financial calculations, 2-6 decimal places is typical.
     * For engineering calculations, 10+ may be appropriate.
     *
     * @param precision the number of decimal places (must be >= 0)
     * @throws IllegalArgumentException if precision is negative
     */
    public static void setPrecision(int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("Precision must be non-negative, got: " + precision);
        }
        globalPrecision = precision;
    }

    /**
     * Gets the current global rounding mode.
     *
     * @return the rounding mode used in calculations
     */
    public static RoundingMode getRoundingMode() {
        return globalRoundingMode;
    }

    /**
     * Sets the global rounding mode for all calculations.
     *
     * <p>Common modes:
     * <ul>
     *   <li>{@link RoundingMode#HALF_UP} - Standard rounding (default)</li>
     *   <li>{@link RoundingMode#HALF_EVEN} - Banker's rounding (reduces bias)</li>
     *   <li>{@link RoundingMode#DOWN} - Truncate towards zero</li>
     *   <li>{@link RoundingMode#FLOOR} - Round towards negative infinity</li>
     * </ul>
     *
     * @param roundingMode the rounding mode to use
     * @throws IllegalArgumentException if roundingMode is null
     */
    public static void setRoundingMode(RoundingMode roundingMode) {
        if (roundingMode == null) {
            throw new IllegalArgumentException("Rounding mode cannot be null");
        }
        globalRoundingMode = roundingMode;
    }

    /**
     * Creates a BigNumber from a string representation.
     *
     * @param value string representation of the number
     * @return BigNumber wrapping the parsed BigDecimal
     */
    public static BigNumber of(String value) {
        return new BigNumber(new BigDecimal(value));
    }

    /**
     * Creates a BigNumber from a long value.
     *
     * @param value long value
     * @return BigNumber wrapping the BigDecimal representation
     */
    public static BigNumber of(long value) {
        return new BigNumber(BigDecimal.valueOf(value));
    }

    /**
     * Creates a BigNumber from a double value.
     *
     * <p><strong>WARNING:</strong> This method should only be used for results
     * from scientific/engineering calculations. For financial calculations,
     * always use {@link #of(String)} to avoid precision loss.
     *
     * @param value double value
     * @return BigNumber wrapping the BigDecimal representation
     */
    public static BigNumber of(double value) {
        return new BigNumber(BigDecimal.valueOf(value));
    }

    /**
     * Returns this value with the global precision and rounding mode applied.
     *
     * @return new BigNumber with value scaled to global precision
     */
    public BigNumber withPrecision() {
        return new BigNumber(value.setScale(globalPrecision, globalRoundingMode));
    }
}
