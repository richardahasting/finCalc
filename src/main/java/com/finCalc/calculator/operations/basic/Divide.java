package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Division operation: pops two operands, pushes their quotient.
 *
 * <p>Stack transformation: {@code [... a b DIVIDE]} → {@code [... (a/b)]}
 *
 * <p>Result is calculated using the global precision and rounding mode
 * from {@link BigNumber#getPrecision()} and {@link BigNumber#getRoundingMode()}.
 */
public enum Divide implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("DIVIDE", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem second = stack.pop();
        StackItem first = stack.pop();

        if (second instanceof BigNumber b2 && first instanceof BigNumber b1) {
            if (b2.value().compareTo(BigDecimal.ZERO) == 0) {
                stack.push(Error.divisionByZero());
            } else {
                stack.push(new BigNumber(
                    b1.value().divide(b2.value(), BigNumber.getPrecision(), BigNumber.getRoundingMode())));
            }
        } else {
            stack.push(new Error("DIVIDE requires numeric operands"));
        }

        return stack;
    }

    @Override
    public String getSymbol() {
        return "÷";
    }
}
