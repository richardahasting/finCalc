package com.finCalc.calculator;

/**
 * Describes a single operand for a calculator operation.
 *
 * <p>Provides semantic meaning to stack positions, helping users understand
 * what each operand represents in a calculation.
 *
 * @param name short name for the operand (e.g., "x", "rate", "principal")
 * @param description longer description of what the operand represents
 */
public record OperandDescriptor(String name, String description) {

    /**
     * Generic operand descriptor for simple operations.
     */
    public static final OperandDescriptor X = new OperandDescriptor("x", "operand");

    /**
     * Generic second operand descriptor for binary operations.
     */
    public static final OperandDescriptor Y = new OperandDescriptor("y", "operand");

    /**
     * Base value for power/root operations.
     */
    public static final OperandDescriptor BASE = new OperandDescriptor("base", "base value");

    /**
     * Exponent for power operations.
     */
    public static final OperandDescriptor EXPONENT = new OperandDescriptor("exponent", "exponent value");

    /**
     * Root index for nth root operations.
     */
    public static final OperandDescriptor ROOT_INDEX = new OperandDescriptor("n", "root index");
}
