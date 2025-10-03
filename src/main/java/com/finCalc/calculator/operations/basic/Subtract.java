package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Subtraction operation: pops two operands, pushes their difference.
 *
 * <p>Stack transformation: {@code [... a b SUBTRACT]} → {@code [... (a-b)]}
 */
public enum Subtract implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("SUBTRACT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem second = stack.pop();
        StackItem first = stack.pop();

        if (second instanceof BigNumber b2 && first instanceof BigNumber b1) {
            stack.push(new BigNumber(b1.value().subtract(b2.value())));
        } else {
            stack.push(new Error("SUBTRACT requires numeric operands"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "−";
    }
}
