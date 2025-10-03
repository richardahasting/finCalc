package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Calculates annualized return for covered call positions.
 *
 * <p>Stack transformation: {@code [... StockCost StrikePrice Premium Days CCR]} → {@code [... annualizedReturn]}
 *
 * <p>Formula: Annualized Return = [(Premium + (Strike - Stock Cost)) / Stock Cost] × (365 / Days)
 *
 * <p>This includes both:
 * <ul>
 *   <li>Call premium received</li>
 *   <li>Capital appreciation if called away at strike price</li>
 * </ul>
 *
 * <p>Example: Buy stock at $50, sell $52 call for $1.50 premium, 30 days to expiration:
 * <pre>Stack: [50, 52, 1.50, 30, CCR] → [0.8517] (85.17% annualized return if called)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.8517 = 85.17%)
 */
public enum CoveredCallReturn implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 4;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CCR", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem daysItem = stack.pop();
        StackItem premiumItem = stack.pop();
        StackItem strikePriceItem = stack.pop();
        StackItem stockCostItem = stack.pop();

        if (daysItem instanceof BigNumber daysVal &&
            premiumItem instanceof BigNumber premiumVal &&
            strikePriceItem instanceof BigNumber strikePriceVal &&
            stockCostItem instanceof BigNumber stockCostVal) {

            double stockCost = stockCostVal.value().doubleValue();
            double strikePrice = strikePriceVal.value().doubleValue();
            double premium = premiumVal.value().doubleValue();
            double daysToExpiration = daysVal.value().doubleValue();

            // Validate inputs
            if (stockCost <= 0) {
                stack.push(Error.domainError("CCR", "stock cost must be positive"));
                return stack;
            }
            if (strikePrice <= 0) {
                stack.push(Error.domainError("CCR", "strike price must be positive"));
                return stack;
            }
            if (premium < 0) {
                stack.push(Error.domainError("CCR", "premium cannot be negative"));
                return stack;
            }
            if (daysToExpiration <= 0) {
                stack.push(Error.domainError("CCR", "days to expiration must be positive"));
                return stack;
            }

            // Calculate covered call return
            // Formula: [(Premium + (Strike - Stock Cost)) / Stock Cost] × (365 / Days)
            double capitalGain = strikePrice - stockCost;
            double totalReturn = premium + capitalGain;
            double returnPercent = totalReturn / stockCost;
            double annualizedReturn = returnPercent * (365.0 / daysToExpiration);

            if (Double.isNaN(annualizedReturn) || Double.isInfinite(annualizedReturn)) {
                stack.push(Error.domainError("CCR", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(annualizedReturn));
            }
        } else {
            stack.push(new Error("CCR requires numeric operands"));
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
            new OperandDescriptor("StockCost", "Cost basis of stock"),
            new OperandDescriptor("StrikePrice", "Call strike price"),
            new OperandDescriptor("Premium", "Call premium received"),
            new OperandDescriptor("Days", "Days to expiration")
        );
    }

    @Override
    public String getDescription() {
        return "Covered Call Return";
    }

    @Override
    public String getExample() {
        return """
            Example: Buy stock at $50, sell $52 call for $1.50, 30 days
              Enter: 50 ENTER 52 ENTER 1.50 ENTER 30 ENTER CCR
              Result: 0.8517 (85.17% annualized if called away)
            """;
    }

    @Override
    public String getSymbol() {
        return "CCR";
    }
}
