package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Rent Per Square Foot calculation: rental rate metric for commercial properties.
 *
 * <p>Stack transformation: {@code [... SquareFeet AnnualRent RPSF]} → {@code [... rentPerSqFt]}
 *
 * <p>Formula: Rent Per SF = Annual Rent / Square Feet
 *
 * <p>Where:
 * <ul>
 *   <li>Annual Rent = Total annual rental income</li>
 *   <li>Square Feet = Rentable square footage</li>
 * </ul>
 *
 * <p>Example: $36,000 annual rent, 1,500 square feet
 * <pre>Stack: [1500, 36000, RPSF] → [24] ($24 per square foot annually)</pre>
 *
 * <p><strong>Note:</strong> Commonly used for commercial real estate leasing
 * <p>Can be expressed annually or monthly depending on market convention
 */
public enum RentPerSquareFoot implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("RPSF", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem annualRentItem = stack.pop();
        StackItem squareFeetItem = stack.pop();

        if (annualRentItem instanceof BigNumber rentVal &&
            squareFeetItem instanceof BigNumber sqftVal) {

            double squareFeet = sqftVal.value().doubleValue();
            double annualRent = rentVal.value().doubleValue();

            // Validate inputs
            if (squareFeet <= 0) {
                stack.push(Error.domainError("RPSF", "square feet must be positive"));
                return stack;
            }

            if (annualRent < 0) {
                stack.push(Error.domainError("RPSF", "annual rent cannot be negative"));
                return stack;
            }

            // Rent Per SF = Annual Rent / Square Feet
            double rpsf = annualRent / squareFeet;

            if (Double.isNaN(rpsf) || Double.isInfinite(rpsf)) {
                stack.push(Error.domainError("RPSF", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(rpsf));
            }
        } else {
            stack.push(new Error("RPSF requires numeric operands"));
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
            new OperandDescriptor("SquareFeet", "Rentable square footage"),
            new OperandDescriptor("AnnualRent", "Total annual rental income")
        );
    }

    @Override
    public String getDescription() {
        return "Rent Per Square Foot";
    }

    @Override
    public String getExample() {
        return """
            Example: $36,000 annual rent, 1,500 square feet
              Enter: 1500 ENTER 36000 ENTER RPSF
              Result: 24 ($24 per square foot annually)
            """;
    }

    @Override
    public String getSymbol() {
        return "RPSF";
    }
}
