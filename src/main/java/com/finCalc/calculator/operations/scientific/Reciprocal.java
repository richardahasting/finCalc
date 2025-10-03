package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * Reciprocal operation: pops one operand, pushes 1/x.
 *
 * <p>Stack transformation: {@code [... x RECIPROCAL]} → {@code [... 1/x]}
 *
 * <p>Result is calculated using the global precision and rounding mode
 * from {@link BigNumber#getPrecision()} and {@link BigNumber#getRoundingMode()}.
 */
public enum Reciprocal implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 1;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("RECIPROCAL", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operand = stack.pop();

        if (operand instanceof BigNumber bn) {
            if (bn.value().compareTo(BigDecimal.ZERO) == 0) {
                stack.push(Error.divisionByZero());
            } else {
                stack.push(new BigNumber(
                    BigDecimal.ONE.divide(bn.value(), BigNumber.getPrecision(), BigNumber.getRoundingMode())));
            }
        } else {
            stack.push(new Error("RECIPROCAL requires numeric operand"));
        }

        return stack;
    }

    @Override
    public int getOperandCount() {
        return 1;
    }

    @Override
    public String getSymbol() {
        return "1/x";
    }
}
