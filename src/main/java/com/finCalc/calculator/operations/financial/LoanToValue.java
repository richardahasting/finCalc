package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Loan-to-Value (LTV) Ratio calculation: measures loan amount vs property value.
 *
 * <p>Stack transformation: {@code [... PropertyValue LoanAmount LTV]} → {@code [... ltv]}
 *
 * <p>Formula: LTV = Loan Amount / Property Value
 *
 * <p>Where:
 * <ul>
 *   <li>Loan Amount = Mortgage loan amount</li>
 *   <li>Property Value = Appraised value or purchase price</li>
 * </ul>
 *
 * <p>Example: $160,000 loan on $200,000 property
 * <pre>Stack: [200000, 160000, LTV] → [0.80] (80% LTV)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.80 = 80%)
 * <p>Lower LTV = less risk for lender, typically better loan terms
 */
public enum LoanToValue implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("LTV", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem loanAmountItem = stack.pop();
        StackItem propertyValueItem = stack.pop();

        if (loanAmountItem instanceof BigNumber loanVal &&
            propertyValueItem instanceof BigNumber propertyVal) {

            double propertyValue = propertyVal.value().doubleValue();
            double loanAmount = loanVal.value().doubleValue();

            // Validate inputs
            if (propertyValue <= 0) {
                stack.push(Error.domainError("LTV", "property value must be positive"));
                return stack;
            }

            if (loanAmount < 0) {
                stack.push(Error.domainError("LTV", "loan amount cannot be negative"));
                return stack;
            }

            // LTV = Loan Amount / Property Value
            double ltv = loanAmount / propertyValue;

            if (Double.isNaN(ltv) || Double.isInfinite(ltv)) {
                stack.push(Error.domainError("LTV", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(ltv));
            }
        } else {
            stack.push(new Error("LTV requires numeric operands"));
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
            new OperandDescriptor("PropertyValue", "Appraised value or purchase price"),
            new OperandDescriptor("LoanAmount", "Mortgage loan amount")
        );
    }

    @Override
    public String getDescription() {
        return "Loan-to-Value Ratio";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 property, $160,000 loan
              Enter: 200000 ENTER 160000 ENTER LTV
              Result: 0.80 (80% loan-to-value)
            """;
    }

    @Override
    public String getSymbol() {
        return "LTV";
    }
}
