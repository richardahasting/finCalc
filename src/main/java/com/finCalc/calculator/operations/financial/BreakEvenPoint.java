package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Break-Even Point calculation: units needed to cover all costs.
 *
 * <p>Stack transformation: {@code [... FixedCosts PricePerUnit VariableCostPerUnit BEP]} → {@code [... units]}
 *
 * <p>Formula: Break-Even Units = Fixed Costs / (Price per Unit - Variable Cost per Unit)
 *
 * <p>Where:
 * <ul>
 *   <li>Fixed Costs = Total fixed costs (rent, salaries, etc.)</li>
 *   <li>Price per Unit = Selling price per unit</li>
 *   <li>Variable Cost per Unit = Variable cost to produce one unit</li>
 * </ul>
 *
 * <p>Example: $50,000 fixed costs, $100 price, $60 variable cost per unit
 * <pre>Stack: [50000, 100, 60, BEP] → [1250] (need to sell 1,250 units)</pre>
 *
 * <p><strong>Note:</strong> Result is in units (can be fractional)
 * <p>Contribution margin = Price - Variable Cost must be positive
 */
public enum BreakEvenPoint implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("BEP", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem variableCostItem = stack.pop();
        StackItem priceItem = stack.pop();
        StackItem fixedCostsItem = stack.pop();

        if (variableCostItem instanceof BigNumber varCostVal &&
            priceItem instanceof BigNumber priceVal &&
            fixedCostsItem instanceof BigNumber fixedVal) {

            double fixedCosts = fixedVal.value().doubleValue();
            double pricePerUnit = priceVal.value().doubleValue();
            double variableCostPerUnit = varCostVal.value().doubleValue();

            // Validate inputs
            if (fixedCosts < 0) {
                stack.push(Error.domainError("BEP", "fixed costs cannot be negative"));
                return stack;
            }

            if (pricePerUnit <= 0) {
                stack.push(Error.domainError("BEP", "price per unit must be positive"));
                return stack;
            }

            if (variableCostPerUnit < 0) {
                stack.push(Error.domainError("BEP", "variable cost cannot be negative"));
                return stack;
            }

            double contributionMargin = pricePerUnit - variableCostPerUnit;

            if (contributionMargin <= 0) {
                stack.push(Error.domainError("BEP", "price must be greater than variable cost"));
                return stack;
            }

            // Break-Even Point = Fixed Costs / Contribution Margin
            double breakEvenUnits = fixedCosts / contributionMargin;

            if (Double.isNaN(breakEvenUnits) || Double.isInfinite(breakEvenUnits)) {
                stack.push(Error.domainError("BEP", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(breakEvenUnits));
            }
        } else {
            stack.push(new Error("BEP requires numeric operands"));
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
            new OperandDescriptor("FixedCosts", "Total fixed costs (rent, salaries, etc.)"),
            new OperandDescriptor("PricePerUnit", "Selling price per unit"),
            new OperandDescriptor("VariableCostPerUnit", "Variable cost to produce one unit")
        );
    }

    @Override
    public String getDescription() {
        return "Break-Even Point";
    }

    @Override
    public String getExample() {
        return """
            Example: $50,000 fixed costs, $100 price, $60 variable cost per unit
              Enter: 50000 ENTER 100 ENTER 60 ENTER BEP
              Result: 1250 (need to sell 1,250 units to break even)
            """;
    }

    @Override
    public String getSymbol() {
        return "BEP";
    }
}
