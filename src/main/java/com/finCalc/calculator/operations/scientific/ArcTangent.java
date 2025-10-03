package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Arctangent operation: pops one operand, pushes atan(x) in radians.
 *
 * <p>Stack transformation: {@code [... x ATAN]} → {@code [... atan(x)]}
 *
 * <p><strong>Note:</strong> Result in radians. Uses double internally for calculation.
 */
public enum ArcTangent implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("ATAN", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            double result = Math.atan(bn.value().doubleValue());
            stack.push(BigNumber.of(result));
        } else {
            stack.push(new Error("ATAN requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "ATAN";
    }
}
