package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Exponential operation: pops one operand, pushes e raised to that power.
 *
 * <p>Stack transformation: {@code [... x EXP]} → {@code [... e^x]}
 *
 * <p>This is the inverse of natural logarithm (ln).
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum Exponential implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("EXP", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber num) {
            double x = num.value().doubleValue();
            double result = Math.exp(x);

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                stack.push(Error.domainError("EXP", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("EXP requires numeric operand"));
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

    @Override
    public String getSymbol() {
        return "e^x";
    }
}
