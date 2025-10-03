package com.finCalc.calculator;

/**
 * Represents an error condition on the calculator stack.
 *
 * <p>Errors are created when operations fail (e.g., insufficient operands,
 * division by zero, domain errors). Once an error appears on the stack,
 * subsequent operations should propagate it rather than execute.
 *
 * @param message description of the error condition
 */
public record Error(String message) implements StackItem {

    /**
     * Standard error for insufficient stack depth.
     *
     * @param operation name of the operation
     * @param required number of operands required
     * @param actual actual stack depth
     * @return Error with formatted message
     */
    public static Error insufficientOperands(String operation, int required, int actual) {
        return new Error(String.format("%s requires %d operand(s), but stack has %d",
            operation, required, actual));
    }

    /**
     * Standard error for division by zero.
     *
     * @return Error for division by zero
     */
    public static Error divisionByZero() {
        return new Error("Division by zero");
    }

    /**
     * Standard error for domain violations (e.g., sqrt of negative).
     *
     * @param operation name of the operation
     * @param reason description of the domain violation
     * @return Error with formatted message
     */
    public static Error domainError(String operation, String reason) {
        return new Error(String.format("%s: %s", operation, reason));
    }
}
