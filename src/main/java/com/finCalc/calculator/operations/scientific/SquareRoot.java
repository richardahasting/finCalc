package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Square root operation: pops one operand, pushes its square root.
 *
 * <p>Stack transformation: {@code [... x SQRT]} → {@code [... √x]}
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum SquareRoot implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("SQRT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            if (bn.value().compareTo(BigDecimal.ZERO) < 0) {
                stack.push(Error.domainError("SQRT", "cannot take square root of negative number"));
            } else {
                double result = Math.sqrt(bn.value().doubleValue());
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("SQRT requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "√";
    }
}
