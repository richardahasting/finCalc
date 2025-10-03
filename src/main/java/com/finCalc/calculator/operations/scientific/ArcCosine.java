package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Arccosine operation: pops one operand, pushes acos(x) in radians.
 *
 * <p>Stack transformation: {@code [... x ACOS]} → {@code [... acos(x)]}
 *
 * <p><strong>Note:</strong> Domain is [-1, 1]. Result in radians. Uses double internally for calculation.
 */
public enum ArcCosine implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("ACOS", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            BigDecimal value = bn.value();
            if (value.compareTo(new BigDecimal("-1")) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
                stack.push(Error.domainError("ACOS", "input must be in range [-1, 1]"));
            } else {
                double result = Math.acos(value.doubleValue());
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("ACOS requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "ACOS";
    }
}
