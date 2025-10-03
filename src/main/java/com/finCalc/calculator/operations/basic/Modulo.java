package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * Modulo operation: pops two operands (dividend and divisor), pushes the remainder.
 *
 * <p>Stack transformation: {@code [... dividend divisor MOD]} → {@code [... dividend % divisor]}
 *
 * <p>Uses BigDecimal.remainder() which preserves the sign of the dividend.
 */
public enum Modulo implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("MOD", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem divisor = stack.pop();
        StackItem dividend = stack.pop();

        if (divisor instanceof BigNumber div && dividend instanceof BigNumber num) {
            if (div.value().compareTo(BigDecimal.ZERO) == 0) {
                stack.push(new Error("Modulo by zero"));
            } else {
                stack.push(new BigNumber(num.value().remainder(div.value())));
            }
        } else {
            stack.push(new Error("MOD requires numeric operands"));
        }

        return stack;
    }

    @Override
    public List<OperandDescriptor> getOperandDescriptors() {
        return List.of(
            new OperandDescriptor("dividend", "The value to be divided"),
            new OperandDescriptor("divisor", "The value to divide by")
        );
    }
}
