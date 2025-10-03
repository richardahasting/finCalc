package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Square operation: pops one operand, pushes its square.
 *
 * <p>Stack transformation: {@code [... x SQUARE]} → {@code [... x²]}
 */
public enum Square implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("SQUARE", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            stack.push(new BigNumber(bn.value().multiply(bn.value())));
        } else {
            stack.push(new Error("SQUARE requires numeric operand"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "x²";
    }
}
