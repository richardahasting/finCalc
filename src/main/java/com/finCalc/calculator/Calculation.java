package com.finCalc.calculator;

import java.util.List;
import java.util.Stack;

/**
 * Interface for calculator operations that execute on the RPN stack.
 *
 * <p>Each calculation:
 * <ol>
 *   <li>Validates stack has sufficient operands</li>
 *   <li>Pops required operands from the stack</li>
 *   <li>Performs the calculation</li>
 *   <li>Pushes result (or Error) back onto the stack</li>
 *   <li>Returns the modified stack</li>
 * </ol>
 *
 * <p>Example: {@code [3, 4, ADD]} → {@code [7]}
 *
 * <p>All calculator operations are implemented as enums with a single INSTANCE
 * to ensure thread-safety and prevent instantiation.
 */
public non-sealed interface Calculation extends StackItem {

    /**
     * Executes this calculation on the provided stack.
     *
     * @param stack the stack containing operands
     * @return the modified stack with result or error
     */
    Stack<StackItem> execute(Stack<StackItem> stack);

    /**
     * Returns the number of operands required by this operation.
     *
     * <p>Most operations require 2 operands (default), but some require
     * 1 (unary) or more than 2 (complex financial calculations).
     *
     * @return number of stack items consumed by this operation
     */
    default int getOperandCount() {
        return 2;
    }

    /**
     * Returns descriptors for each operand, explaining what they represent.
     *
     * <p>The list is ordered from bottom of stack to top. For example,
     * in {@code [... a b ADD]}, operand 0 is 'a', operand 1 is 'b'.
     *
     * <p>Default implementation returns generic "x" and "y" descriptors
     * for binary operations. Override for operations with specific
     * semantic meaning (e.g., "principal", "rate", "months").
     *
     * @return list of operand descriptors, size matches {@link #getOperandCount()}
     */
    default List<OperandDescriptor> getOperandDescriptors() {
        return getOperandCount() == 1
            ? List.of(OperandDescriptor.X)
            : List.of(OperandDescriptor.X, OperandDescriptor.Y);
    }

    /**
     * Returns a brief description of this operation.
     *
     * <p>Default implementation returns empty string. Override to provide
     * a user-friendly description for help displays.
     *
     * @return human-readable description of the operation
     */
    default String getDescription() {
        return "";
    }

    /**
     * Returns an example demonstrating how to use this operation.
     *
     * <p>Default implementation returns empty string. Override to provide
     * a concrete example with sample values and expected results.
     *
     * @return example usage with input and output
     */
    default String getExample() {
        return "";
    }

    /**
     * Returns the symbolic representation of this operation.
     *
     * <p>This is the short string used to identify the operation in the UI
     * and in RPN expressions (e.g., "PMT", "CAP", "+", "√").
     *
     * <p>Default implementation returns empty string. Override to provide
     * the operation's symbol.
     *
     * @return the operation's symbol/abbreviation
     */
    default String getSymbol() {
        return "";
    }
}
