package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Cosine operation: pops one operand (in radians), pushes cos(x).
 *
 * <p>Stack transformation: {@code [... x COS]} → {@code [... cos(x)]}
 *
 * <p><strong>Note:</strong> Input is expected in radians. Uses double internally for calculation.
 */
public enum Cosine implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("COS", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            double result = Math.cos(bn.value().doubleValue());
            stack.push(BigNumber.of(result));
        } else {
            stack.push(new Error("COS requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "cos";
    }
}
