package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Return on Investment (ROI) calculation: measures profitability of investment.
 *
 * <p>Stack transformation: {@code [... CostOfInvestment Gain ROI]} → {@code [... roi]}
 *
 * <p>Formula: ROI = (Gain - Cost) / Cost = Net Profit / Cost
 *
 * <p>Where:
 * <ul>
 *   <li>Cost of Investment = Initial investment amount</li>
 *   <li>Gain = Current value or sale proceeds</li>
 * </ul>
 *
 * <p>Example: Bought for $200,000, sold for $250,000
 * <pre>Stack: [200000, 250000, ROI] → [0.25] (25% ROI)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.25 = 25%)
 * <p>Positive ROI = profit, Negative ROI = loss
 */
public enum ReturnOnInvestment implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("ROI", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem gainItem = stack.pop();
        StackItem costItem = stack.pop();

        if (gainItem instanceof BigNumber gainVal &&
            costItem instanceof BigNumber costVal) {

            double cost = costVal.value().doubleValue();
            double gain = gainVal.value().doubleValue();

            // Validate inputs
            if (cost <= 0) {
                stack.push(Error.domainError("ROI", "cost of investment must be positive"));
                return stack;
            }

            // ROI = (Gain - Cost) / Cost
            double roi = (gain - cost) / cost;

            if (Double.isNaN(roi) || Double.isInfinite(roi)) {
                stack.push(Error.domainError("ROI", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(roi));
            }
        } else {
            stack.push(new Error("ROI requires numeric operands"));
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
            new OperandDescriptor("CostOfInvestment", "Initial investment amount"),
            new OperandDescriptor("Gain", "Current value or sale proceeds")
        );
    }

    @Override
    public String getDescription() {
        return "Return on Investment";
    }

    @Override
    public String getExample() {
        return """
            Example: Bought for $200,000, sold for $250,000
              Enter: 200000 ENTER 250000 ENTER ROI
              Result: 0.25 (25% return on investment)
            """;
    }

    @Override
    public String getSymbol() {
        return "ROI";
    }
}
