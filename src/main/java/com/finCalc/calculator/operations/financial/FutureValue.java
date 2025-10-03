package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Future Value (FV) calculation: computes future value of an investment.
 *
 * <p>Stack transformation: {@code [... PV rate n FV]} → {@code [... futureValue]}
 *
 * <p>Formula: FV = PV * (1 + rate)^n
 *
 * <p>Where:
 * <ul>
 *   <li>PV = Present Value (initial investment)</li>
 *   <li>rate = Interest rate per period</li>
 *   <li>n = Number of periods</li>
 * </ul>
 *
 * <p>Example: $10,000 invested at 8% annual for 10 years
 * <pre>Stack: [10000, 0.08, 10, FV] → [21589.25]</pre>
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum FutureValue implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("FV", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem nItem = stack.pop();
        StackItem rateItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (nItem instanceof BigNumber nVal &&
            rateItem instanceof BigNumber rateVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double rate = rateVal.value().doubleValue();
            double n = nVal.value().doubleValue();

            // Validate inputs
            if (n < 0) {
                stack.push(Error.domainError("FV", "number of periods cannot be negative"));
                return stack;
            }

            if (rate < -1) {
                stack.push(Error.domainError("FV", "interest rate cannot be less than -100%"));
                return stack;
            }

            // FV = PV * (1 + rate)^n
            double fv = pv * Math.pow(1 + rate, n);

            if (Double.isNaN(fv) || Double.isInfinite(fv)) {
                stack.push(Error.domainError("FV", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(fv));
            }
        } else {
            stack.push(new Error("FV requires numeric operands"));
        }

        return stack;
    }

    @Override
    public int getOperandCount() {
        return REQUIRED_OPERANDS;
    }

    @Override
    public List<OperandDescriptor> getOperandDescriptors() {
        return List.of(
            new OperandDescriptor("PV", "Present Value (initial investment)"),
            new OperandDescriptor("rate", "Interest rate per period"),
            new OperandDescriptor("n", "Number of periods")
        );
    }

    @Override
    public String getDescription() {
        return "Future Value Calculation";
    }

    @Override
    public String getExample() {
        return """
            Example: $10,000 invested at 8% annual for 10 years
              Enter: 10000 ENTER 0.08 ENTER 10 ENTER FV
              Result: $21,589.25
            """;
    }

    @Override
    public String getSymbol() {
        return "FV";
    }
}
