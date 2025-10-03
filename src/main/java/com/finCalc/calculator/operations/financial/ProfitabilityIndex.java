package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Profitability Index (PI) calculation: benefit-cost ratio for investments.
 *
 * <p>Stack transformation: {@code [... InitialInvestment PVFutureCashFlows PI]} → {@code [... index]}
 *
 * <p>Formula: PI = Present Value of Future Cash Flows / Initial Investment
 *
 * <p>Where:
 * <ul>
 *   <li>Initial Investment = Upfront cost (typically negative, use absolute value)</li>
 *   <li>PV of Future Cash Flows = Present value of expected returns</li>
 * </ul>
 *
 * <p>Example: $100,000 investment, $120,000 PV of future cash flows
 * <pre>Stack: [100000, 120000, PI] → [1.20] (PI of 1.20)</pre>
 *
 * <p><strong>Decision Rules:</strong>
 * <ul>
 *   <li>PI &gt; 1.0 → Accept project (profitable)</li>
 *   <li>PI = 1.0 → Breakeven (indifferent)</li>
 *   <li>PI &lt; 1.0 → Reject project (unprofitable)</li>
 * </ul>
 *
 * <p><strong>Note:</strong> Higher PI = better investment efficiency
 * <p>Useful for ranking multiple projects when capital is limited
 */
public enum ProfitabilityIndex implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("PI", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem pvCashFlowsItem = stack.pop();
        StackItem investmentItem = stack.pop();

        if (pvCashFlowsItem instanceof BigNumber pvVal &&
            investmentItem instanceof BigNumber investmentVal) {

            double initialInvestment = investmentVal.value().doubleValue();
            double pvFutureCashFlows = pvVal.value().doubleValue();

            // Validate inputs
            if (initialInvestment <= 0) {
                stack.push(Error.domainError("PI", "initial investment must be positive"));
                return stack;
            }

            if (pvFutureCashFlows < 0) {
                stack.push(Error.domainError("PI", "PV of future cash flows cannot be negative"));
                return stack;
            }

            // PI = PV of Future Cash Flows / Initial Investment
            double profitabilityIndex = pvFutureCashFlows / initialInvestment;

            if (Double.isNaN(profitabilityIndex) || Double.isInfinite(profitabilityIndex)) {
                stack.push(Error.domainError("PI", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(profitabilityIndex));
            }
        } else {
            stack.push(new Error("PI requires numeric operands"));
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
            new OperandDescriptor("PVFutureCashFlows", "Present value of expected future cash flows")
        );
    }

    @Override
    public String getDescription() {
        return "Profitability Index";
    }

    @Override
    public String getExample() {
        return """
            Example: $100,000 investment, $120,000 PV of future cash flows
              Enter: 100000 ENTER 120000 ENTER PI
              Result: 1.20 (PI > 1.0, accept project)
            """;
    }

    @Override
    public String getSymbol() {
        return "PI";
    }
}
