package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Power operation: pops two operands (base and exponent), pushes base^exponent.
 *
 * <p>Stack transformation: {@code [... base exponent POW]} → {@code [... base^exponent]}
 *
 * <p><strong>Note:</strong> Uses double internally for calculation to support
 * fractional exponents.
 */
public enum Power implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("POW", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem exponent = stack.pop();
        StackItem base = stack.pop();

        if (exponent instanceof BigNumber exp && base instanceof BigNumber b) {
            double result = Math.pow(b.value().doubleValue(), exp.value().doubleValue());

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                stack.push(Error.domainError("POW", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(result));
            }
        } else {
            stack.push(new Error("POW requires numeric operands"));
        }

        return stack;
    }

    @Override
    public List<OperandDescriptor> getOperandDescriptors() {
        return List.of(
            OperandDescriptor.BASE,
            OperandDescriptor.EXPONENT
        );
    }

    @Override
    public String getSymbol() {
        return "xⁿ";
    }
}
