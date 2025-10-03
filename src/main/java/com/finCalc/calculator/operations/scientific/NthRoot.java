package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * Nth root operation: pops two operands (x and n), pushes the nth root of x.
 *
 * <p>Stack transformation: {@code [... x n NTHROOT]} → {@code [... ⁿ√x]}
 *
 * <p>Equivalent to x^(1/n).
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum NthRoot implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("NTHROOT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem n = stack.pop();
        StackItem x = stack.pop();

        if (n instanceof BigNumber nVal && x instanceof BigNumber xVal) {
            double xDouble = xVal.value().doubleValue();
            double nDouble = nVal.value().doubleValue();

            // Check for invalid cases
            if (nDouble == 0) {
                stack.push(Error.domainError("NTHROOT", "root index cannot be zero"));
            } else if (xDouble < 0 && Math.abs(nDouble % 2) < 0.0001) {
                // Check if n is approximately even (modulo 2 ≈ 0)
                stack.push(Error.domainError("NTHROOT", "cannot take even root of negative number"));
            } else {
                // Calculate nth root as x^(1/n)
                double result = Math.pow(xDouble, 1.0 / nDouble);

                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    stack.push(Error.domainError("NTHROOT", "result is undefined or infinite"));
                } else {
                    stack.push(BigNumber.of(result));
                }
            }
        } else {
            stack.push(new Error("NTHROOT requires numeric operands"));
        }

        return stack;
    }

    @Override
    public List<OperandDescriptor> getOperandDescriptors() {
        return List.of(
            OperandDescriptor.X,
            OperandDescriptor.ROOT_INDEX
        );
    }

    @Override
    public String getSymbol() {
        return "ⁿ√x";
    }
}
