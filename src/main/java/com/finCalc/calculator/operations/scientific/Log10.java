package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Base-10 logarithm operation: pops one operand, pushes log₁₀(x).
 *
 * <p>Stack transformation: {@code [... x LOG10]} → {@code [... log₁₀(x)]}
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum Log10 implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("LOG10", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            if (bn.value().compareTo(BigDecimal.ZERO) <= 0) {
                stack.push(Error.domainError("LOG10", "cannot take logarithm of non-positive number"));
            } else {
                double result = Math.log10(bn.value().doubleValue());
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("LOG10 requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "LOG";
    }
}
