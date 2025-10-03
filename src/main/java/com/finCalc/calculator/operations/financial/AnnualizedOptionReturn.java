package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Calculates annualized return for options strategies (e.g., cash-secured puts, covered calls).
 *
 * <p>Stack transformation: {@code [... StrikePrice Premium Days AOPT]} → {@code [... annualizedReturn]}
 *
 * <p>Formula: Annualized Return = (Premium / Days) × 365 / Strike Price
 *
 * <p>Example: Sell $12.50 put for $0.26 premium with 10 days to expiration:
 * <pre>Stack: [12.50, 0.26, 10, AOPT] → [0.7592] (75.92% annualized return)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.7592 = 75.92%)
 */
public enum AnnualizedOptionReturn implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("AOPT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem daysItem = stack.pop();
        StackItem premiumItem = stack.pop();
        StackItem strikePriceItem = stack.pop();

        if (daysItem instanceof BigNumber daysVal &&
            premiumItem instanceof BigNumber premiumVal &&
            strikePriceItem instanceof BigNumber strikePriceVal) {

            double strikePrice = strikePriceVal.value().doubleValue();
            double premium = premiumVal.value().doubleValue();
            double daysToExpiration = daysVal.value().doubleValue();

            // Validate inputs
            if (strikePrice <= 0) {
                stack.push(Error.domainError("AOPT", "strike price must be positive"));
                return stack;
            }
            if (premium <= 0) {
                stack.push(Error.domainError("AOPT", "premium must be positive"));
                return stack;
            }
            if (daysToExpiration <= 0) {
                stack.push(Error.domainError("AOPT", "days to expiration must be positive"));
                return stack;
            }

            // Calculate annualized return
            // Formula: (Premium / Days) × 365 / Strike Price
            double dailyReturn = premium / daysToExpiration;
            double annualReturn = dailyReturn * 365;
            double annualizedYield = annualReturn / strikePrice;

            if (Double.isNaN(annualizedYield) || Double.isInfinite(annualizedYield)) {
                stack.push(Error.domainError("AOPT", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(annualizedYield));
            }
        } else {
            stack.push(new Error("AOPT requires numeric operands"));
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
            new OperandDescriptor("StrikePrice", "Strike price (capital at risk)"),
            new OperandDescriptor("Premium", "Premium received"),
            new OperandDescriptor("Days", "Days to expiration")
        );
    }

    @Override
    public String getDescription() {
        return "Annualized Option Return";
    }

    @Override
    public String getExample() {
        return """
            Example: Sell $12.50 put for $0.26 premium, 10 days to expiration
              Enter: 12.50 ENTER 0.26 ENTER 10 ENTER AOPT
              Result: 0.7592 (75.92% annualized return)
            """;
    }

    @Override
    public String getSymbol() {
        return "AOPT";
    }
}
