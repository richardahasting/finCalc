package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * After-Tax Return calculation: investment return after tax considerations.
 *
 * <p>Stack transformation: {@code [... PreTaxReturn TaxRate AFTAXRET]} → {@code [... afterTaxReturn]}
 *
 * <p>Formula: After-Tax Return = Pre-Tax Return × (1 - Tax Rate)
 *
 * <p>Where:
 * <ul>
 *   <li>Pre-Tax Return = Investment return before taxes (as decimal)</li>
 *   <li>Tax Rate = Applicable tax rate on investment income (as decimal)</li>
 * </ul>
 *
 * <p>Example: 8% pre-tax return, 25% tax rate
 * <pre>Stack: [0.08, 0.25, AFTAXRET] → [0.06] (6% after-tax return)</pre>
 *
 * <p><strong>Note:</strong> Both inputs and result are in decimal form (0.08 = 8%)
 * <p>Important for comparing taxable vs tax-advantaged investments
 */
public enum AfterTaxReturn implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("AFTAXRET", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem taxRateItem = stack.pop();
        StackItem preTaxReturnItem = stack.pop();

        if (taxRateItem instanceof BigNumber taxVal &&
            preTaxReturnItem instanceof BigNumber returnVal) {

            double preTaxReturn = returnVal.value().doubleValue();
            double taxRate = taxVal.value().doubleValue();

            // Validate inputs
            if (taxRate < 0 || taxRate > 1) {
                stack.push(Error.domainError("AFTAXRET", "tax rate must be between 0 and 1"));
                return stack;
            }

            // After-Tax Return = Pre-Tax Return × (1 - Tax Rate)
            double afterTaxReturn = preTaxReturn * (1 - taxRate);

            if (Double.isNaN(afterTaxReturn) || Double.isInfinite(afterTaxReturn)) {
                stack.push(Error.domainError("AFTAXRET", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(afterTaxReturn));
            }
        } else {
            stack.push(new Error("AFTAXRET requires numeric operands"));
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
            new OperandDescriptor("PreTaxReturn", "Investment return before taxes (as decimal)"),
            new OperandDescriptor("TaxRate", "Applicable tax rate (as decimal, e.g., 0.25 for 25%)")
        );
    }

    @Override
    public String getDescription() {
        return "After-Tax Return";
    }

    @Override
    public String getExample() {
        return """
            Example: 8% pre-tax return, 25% tax rate
              Enter: 0.08 ENTER 0.25 ENTER AFTAXRET
              Result: 0.06 (6% after-tax return)
            """;
    }

    @Override
    public String getSymbol() {
        return "AFTAXRET";
    }
}
