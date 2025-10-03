package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Natural logarithm operation: pops one operand, pushes ln(x).
 *
 * <p>Stack transformation: {@code [... x LN]} → {@code [... ln(x)]}
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum NaturalLog implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("LN", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            if (bn.value().compareTo(BigDecimal.ZERO) <= 0) {
                stack.push(Error.domainError("LN", "cannot take logarithm of non-positive number"));
            } else {
                double result = Math.log(bn.value().doubleValue());
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("LN requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "ln";
    }
}
