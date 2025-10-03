package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * APR to APY Conversion: converts Annual Percentage Rate to Annual Percentage Yield.
 *
 * <p>Stack transformation: {@code [... APR CompoundingPeriods APY]} → {@code [... apy]}
 *
 * <p>Formula: APY = (1 + APR/n)^n - 1
 *
 * <p>Where:
 * <ul>
 *   <li>APR = Annual Percentage Rate (as decimal)</li>
 *   <li>n = Number of compounding periods per year</li>
 * </ul>
 *
 * <p>Example: 6% APR compounded monthly (12 times per year)
 * <pre>Stack: [0.06, 12, APY] → [0.0617] (6.17% APY)</pre>
 *
 * <p><strong>Note:</strong> APY accounts for compounding effect, always ≥ APR
 * <p>Common compounding periods: Monthly=12, Quarterly=4, Semi-annually=2, Daily=365
 */
public enum AprToApy implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("APY", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem compoundingPeriodsItem = stack.pop();
        StackItem aprItem = stack.pop();

        if (compoundingPeriodsItem instanceof BigNumber periodsVal &&
            aprItem instanceof BigNumber aprVal) {

            double apr = aprVal.value().doubleValue();
            double n = periodsVal.value().doubleValue();

            // Validate inputs
            if (apr < 0) {
                stack.push(Error.domainError("APY", "APR cannot be negative"));
                return stack;
            }

            if (n <= 0) {
                stack.push(Error.domainError("APY", "compounding periods must be positive"));
                return stack;
            }

            // APY = (1 + APR/n)^n - 1
            double apy = Math.pow(1 + apr / n, n) - 1;

            if (Double.isNaN(apy) || Double.isInfinite(apy)) {
                stack.push(Error.domainError("APY", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(apy));
            }
        } else {
            stack.push(new Error("APY requires numeric operands"));
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
            new OperandDescriptor("APR", "Annual Percentage Rate (as decimal, e.g., 0.06 for 6%)"),
            new OperandDescriptor("CompoundingPeriods", "Number of compounding periods per year")
        );
    }

    @Override
    public String getDescription() {
        return "APR to APY Conversion";
    }

    @Override
    public String getExample() {
        return """
            Example: 6% APR compounded monthly (12 times per year)
              Enter: 0.06 ENTER 12 ENTER APY
              Result: 0.0617 (6.17% APY)
            """;
    }

    @Override
    public String getSymbol() {
        return "APY";
    }
}
