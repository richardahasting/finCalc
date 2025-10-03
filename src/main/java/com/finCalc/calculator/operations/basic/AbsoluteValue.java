package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Absolute value operation: pops one operand, pushes its absolute value.
 *
 * <p>Stack transformation: {@code [... x ABS]} → {@code [... |x|]}
 */
public enum AbsoluteValue implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("ABS", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem x = stack.pop();

        if (x instanceof BigNumber num) {
            stack.push(new BigNumber(num.value().abs()));
        } else {
            stack.push(new Error("ABS requires numeric operand"));
        }

        return stack;
    }

    @Override
    public int getOperandCount() {
        return REQUIRED_OPERANDS;
    }

    @Override
    public List<OperandDescriptor> getOperandDescriptors() {
        return List.of(OperandDescriptor.X);
    }
}
