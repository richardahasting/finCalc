package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Price Per Square Foot calculation: property valuation metric.
 *
 * <p>Stack transformation: {@code [... SquareFeet PropertyPrice PPSF]} → {@code [... pricePerSqFt]}
 *
 * <p>Formula: Price Per SF = Property Price / Square Feet
 *
 * <p>Where:
 * <ul>
 *   <li>Property Price = Purchase price or market value</li>
 *   <li>Square Feet = Total square footage of the property</li>
 * </ul>
 *
 * <p>Example: $300,000 property, 2,000 square feet
 * <pre>Stack: [2000, 300000, PPSF] → [150] ($150 per square foot)</pre>
 *
 * <p><strong>Note:</strong> Used for comparing properties of different sizes
 * <p>Market averages vary significantly by location and property type
 */
public enum PricePerSquareFoot implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("PPSF", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem propertyPriceItem = stack.pop();
        StackItem squareFeetItem = stack.pop();

        if (propertyPriceItem instanceof BigNumber priceVal &&
            squareFeetItem instanceof BigNumber sqftVal) {

            double squareFeet = sqftVal.value().doubleValue();
            double propertyPrice = priceVal.value().doubleValue();

            // Validate inputs
            if (squareFeet <= 0) {
                stack.push(Error.domainError("PPSF", "square feet must be positive"));
                return stack;
            }

            if (propertyPrice <= 0) {
                stack.push(Error.domainError("PPSF", "property price must be positive"));
                return stack;
            }

            // Price Per SF = Property Price / Square Feet
            double ppsf = propertyPrice / squareFeet;

            if (Double.isNaN(ppsf) || Double.isInfinite(ppsf)) {
                stack.push(Error.domainError("PPSF", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(ppsf));
            }
        } else {
            stack.push(new Error("PPSF requires numeric operands"));
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
            new OperandDescriptor("SquareFeet", "Total square footage of the property"),
            new OperandDescriptor("PropertyPrice", "Purchase price or current market value")
        );
    }

    @Override
    public String getDescription() {
        return "Price Per Square Foot";
    }

    @Override
    public String getExample() {
        return """
            Example: $300,000 property, 2,000 square feet
              Enter: 2000 ENTER 300000 ENTER PPSF
              Result: 150 ($150 per square foot)
            """;
    }

    @Override
    public String getSymbol() {
        return "PPSF";
    }
}
