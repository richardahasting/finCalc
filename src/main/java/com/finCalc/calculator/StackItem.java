package com.finCalc.calculator;

/**
 * Sealed interface representing items that can be placed on the RPN calculator stack.
 *
 * <p>Valid stack items are:
 * <ul>
 *   <li>{@link BigNumber} - Numeric values (wraps BigDecimal)</li>
 *   <li>{@link Calculation} - Operations that execute on the stack</li>
 *   <li>{@link Error} - Error messages from failed operations</li>
 * </ul>
 *
 * <p>The sealed nature ensures exhaustive pattern matching and type safety.
 */
public sealed interface StackItem permits BigNumber, Calculation, Error {
}
