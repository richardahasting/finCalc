package com.finCalc.calculator;

import java.util.Stack;

/**
 * Evaluates a stack containing mixed BigNumbers and Calculations in RPN order.
 *
 * <p>Example: {@code [3, 4, ADD, 5, MULTIPLY]} evaluates to {@code [35]}
 *
 * <p>The evaluator processes the stack left to right (bottom to top):
 * <ol>
 *   <li>BigNumbers are accumulated on a working stack</li>
 *   <li>When a Calculation is encountered, it executes on the working stack</li>
 *   <li>Errors halt evaluation and remain on the stack</li>
 * </ol>
 */
public class StackEvaluator {

    /**
     * Evaluates a stack containing BigNumbers and Calculations.
     *
     * <p>The input stack is processed in order (bottom to top). Each item is:
     * <ul>
     *   <li>BigNumber - pushed onto working stack</li>
     *   <li>Calculation - executed on working stack</li>
     *   <li>Error - evaluation halts, error returned on stack</li>
     * </ul>
     *
     * <p>Error handling behavior:
     * <ul>
     *   <li>Single operation: Preserves working stack context with error on top</li>
     *   <li>Multiple operations: Clears working stack, returns only the error</li>
     * </ul>
     *
     * @param input stack containing BigNumbers and Calculations in RPN order
     * @return stack containing the result(s) or an Error
     */
    public static Stack<StackItem> evaluate(Stack<StackItem> input) {
        Stack<StackItem> workingStack = new Stack<>();

        // Count the number of Calculation operations in input
        long calculationCount = input.stream()
            .filter(item -> item instanceof Calculation)
            .count();

        boolean multipleOperations = calculationCount > 1;

        // Process each item in the input stack
        for (StackItem item : input) {
            if (item instanceof BigNumber) {
                workingStack.push(item);
            } else if (item instanceof Calculation calc) {
                workingStack = calc.execute(workingStack);

                // If execution produced an error, stop evaluation
                if (!workingStack.isEmpty() && workingStack.peek() instanceof Error) {
                    if (multipleOperations) {
                        // Multiple operations: clear stack and return only error
                        Error error = (Error) workingStack.peek();
                        workingStack.clear();
                        workingStack.push(error);
                    }
                    // Single operation: keep working stack context
                    return workingStack;
                }
            } else if (item instanceof Error) {
                // Error in input - clear and return it
                workingStack.clear();
                workingStack.push(item);
                return workingStack;
            }
        }

        return workingStack;
    }

    /**
     * Evaluates a stack and returns the top result as a BigNumber, or null if error.
     *
     * <p>Convenience method for cases where a single numeric result is expected.
     *
     * @param input stack containing BigNumbers and Calculations
     * @return the top BigNumber result, or null if result is Error or stack is empty
     */
    public static BigNumber evaluateToNumber(Stack<StackItem> input) {
        Stack<StackItem> result = evaluate(input);

        if (result.isEmpty()) {
            return null;
        }

        StackItem top = result.peek();
        return top instanceof BigNumber ? (BigNumber) top : null;
    }

    /**
     * Evaluates a stack and returns any error, or null if successful.
     *
     * <p>Convenience method for error checking.
     *
     * @param input stack containing BigNumbers and Calculations
     * @return Error if evaluation failed, null if successful
     */
    public static Error evaluateForError(Stack<StackItem> input) {
        Stack<StackItem> result = evaluate(input);

        if (result.isEmpty()) {
            return null;
        }

        StackItem top = result.peek();
        return top instanceof Error ? (Error) top : null;
    }
}
