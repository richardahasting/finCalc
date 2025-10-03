package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Payback Period calculation: time to recover initial investment.
 *
 * <p>Stack transformation: {@code [... InitialInvestment AnnualCashFlow PAYBACK]} → {@code [... years]}
 *
 * <p>Formula: Payback Period = Initial Investment / Annual Cash Flow
 *
 * <p>Where:
 * <ul>
 *   <li>Initial Investment = Upfront cost or investment amount</li>
 *   <li>Annual Cash Flow = Net cash received per year (assumed constant)</li>
 * </ul>
 *
 * <p>Example: $100,000 investment with $25,000 annual cash flow
 * <pre>Stack: [100000, 25000, PAYBACK] → [4.0] (4 years to break even)</pre>
 *
 * <p><strong>Note:</strong> Result is in years (can be fractional)
 * <p>Assumes constant annual cash flow; for variable cash flows, use NPV analysis
 * <p>Shorter payback period = lower risk, faster capital recovery
 */
public enum PaybackPeriod implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("PAYBACK", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem cashFlowItem = stack.pop();
        StackItem investmentItem = stack.pop();

        if (cashFlowItem instanceof BigNumber cashFlowVal &&
            investmentItem instanceof BigNumber investmentVal) {

            double initialInvestment = investmentVal.value().doubleValue();
            double annualCashFlow = cashFlowVal.value().doubleValue();

            // Validate inputs
            if (initialInvestment <= 0) {
                stack.push(Error.domainError("PAYBACK", "initial investment must be positive"));
                return stack;
            }

            if (annualCashFlow <= 0) {
                stack.push(Error.domainError("PAYBACK", "annual cash flow must be positive"));
                return stack;
            }

            // Payback Period = Initial Investment / Annual Cash Flow
            double paybackPeriod = initialInvestment / annualCashFlow;

            if (Double.isNaN(paybackPeriod) || Double.isInfinite(paybackPeriod)) {
                stack.push(Error.domainError("PAYBACK", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(paybackPeriod));
            }
        } else {
            stack.push(new Error("PAYBACK requires numeric operands"));
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
            new OperandDescriptor("InitialInvestment", "Upfront cost or investment amount"),
            new OperandDescriptor("AnnualCashFlow", "Net cash received per year (assumed constant)")
        );
    }

    @Override
    public String getDescription() {
        return "Payback Period";
    }

    @Override
    public String getExample() {
        return """
            Example: $100,000 investment, $25,000 annual cash flow
              Enter: 100000 ENTER 25000 ENTER PAYBACK
              Result: 4.0 (4 years to recover investment)
            """;
    }

    @Override
    public String getSymbol() {
        return "PAYBACK";
    }
}
