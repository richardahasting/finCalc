package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.util.Stack;

/**
 * Multiplication operation: pops two operands, pushes their product.
 *
 * <p>Stack transformation: {@code [... a b MULTIPLY]} → {@code [... (a*b)]}
 */
public enum Multiply implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("MULTIPLY", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem second = stack.pop();
        StackItem first = stack.pop();

        if (second instanceof BigNumber b2 && first instanceof BigNumber b1) {
            stack.push(new BigNumber(b1.value().multiply(b2.value())));
        } else {
            stack.push(new Error("MULTIPLY requires numeric operands"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "×";
    }
}
