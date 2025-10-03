package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Gross Rent Multiplier (GRM) calculation: quick property valuation metric.
 *
 * <p>Stack transformation: {@code [... GrossAnnualRent PropertyPrice GRM]} → {@code [... grm]}
 *
 * <p>Formula: GRM = Property Price / Gross Annual Rent
 *
 * <p>Where:
 * <ul>
 *   <li>Property Price = Purchase price or market value</li>
 *   <li>Gross Annual Rent = Total annual rental income (before expenses)</li>
 * </ul>
 *
 * <p>Example: $200,000 property, $24,000 annual rent
 * <pre>Stack: [24000, 200000, GRM] → [8.333] (GRM of 8.33)</pre>
 *
 * <p><strong>Note:</strong> Lower GRM typically indicates better value
 * <p>Can be used to estimate property value: Estimated Value = GRM × Annual Rent
 */
public enum GrossRentMultiplier implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("GRM", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem propertyPriceItem = stack.pop();
        StackItem grossRentItem = stack.pop();

        if (propertyPriceItem instanceof BigNumber priceVal &&
            grossRentItem instanceof BigNumber rentVal) {

            double grossRent = rentVal.value().doubleValue();
            double propertyPrice = priceVal.value().doubleValue();

            // Validate inputs
            if (grossRent <= 0) {
                stack.push(Error.domainError("GRM", "gross annual rent must be positive"));
                return stack;
            }

            if (propertyPrice <= 0) {
                stack.push(Error.domainError("GRM", "property price must be positive"));
                return stack;
            }

            // GRM = Property Price / Gross Annual Rent
            double grm = propertyPrice / grossRent;

            if (Double.isNaN(grm) || Double.isInfinite(grm)) {
                stack.push(Error.domainError("GRM", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(grm));
            }
        } else {
            stack.push(new Error("GRM requires numeric operands"));
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
            new OperandDescriptor("GrossAnnualRent", "Total annual rental income (before expenses)"),
            new OperandDescriptor("PropertyPrice", "Purchase price or current market value")
        );
    }

    @Override
    public String getDescription() {
        return "Gross Rent Multiplier";
    }

    @Override
    public String getExample() {
        return """
            Example: $24,000 annual rent, $200,000 property price
              Enter: 24000 ENTER 200000 ENTER GRM
              Result: 8.333 (GRM of 8.33)
            """;
    }

    @Override
    public String getSymbol() {
        return "GRM";
    }
}
