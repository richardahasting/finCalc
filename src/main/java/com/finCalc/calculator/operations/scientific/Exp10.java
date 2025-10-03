package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Base-10 exponential operation: pops one operand, pushes 10^x.
 *
 * <p>Stack transformation: {@code [... x EXP10]} → {@code [... 10^x]}
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum Exp10 implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("EXP10", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            double result = Math.pow(10, bn.value().doubleValue());

            if (Double.isInfinite(result)) {
                stack.push(Error.domainError("EXP10", "result is infinite"));
            } else {
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("EXP10 requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "10^x";
    }
}
