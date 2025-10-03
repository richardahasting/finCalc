package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Effective Tax Rate calculation: actual tax rate paid on income.
 *
 * <p>Stack transformation: {@code [... TotalIncome TotalTax EFFTAX]} → {@code [... effectiveTaxRate]}
 *
 * <p>Formula: Effective Tax Rate = Total Tax Paid / Total Income
 *
 * <p>Where:
 * <ul>
 *   <li>Total Tax Paid = Sum of all taxes paid</li>
 *   <li>Total Income = Gross income before taxes</li>
 * </ul>
 *
 * <p>Example: $100,000 income, $18,000 total tax paid
 * <pre>Stack: [100000, 18000, EFFTAX] → [0.18] (18% effective tax rate)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.18 = 18%)
 * <p>Effective tax rate differs from marginal tax rate (top bracket rate)
 */
public enum EffectiveTaxRate implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("EFFTAX", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem totalTaxItem = stack.pop();
        StackItem totalIncomeItem = stack.pop();

        if (totalTaxItem instanceof BigNumber taxVal &&
            totalIncomeItem instanceof BigNumber incomeVal) {

            double totalIncome = incomeVal.value().doubleValue();
            double totalTax = taxVal.value().doubleValue();

            // Validate inputs
            if (totalIncome <= 0) {
                stack.push(Error.domainError("EFFTAX", "total income must be positive"));
                return stack;
            }

            if (totalTax < 0) {
                stack.push(Error.domainError("EFFTAX", "total tax cannot be negative"));
                return stack;
            }

            // Effective Tax Rate = Total Tax / Total Income
            double effectiveTaxRate = totalTax / totalIncome;

            if (Double.isNaN(effectiveTaxRate) || Double.isInfinite(effectiveTaxRate)) {
                stack.push(Error.domainError("EFFTAX", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(effectiveTaxRate));
            }
        } else {
            stack.push(new Error("EFFTAX requires numeric operands"));
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
            new OperandDescriptor("TotalIncome", "Gross income before taxes"),
            new OperandDescriptor("TotalTax", "Sum of all taxes paid")
        );
    }

    @Override
    public String getDescription() {
        return "Effective Tax Rate";
    }

    @Override
    public String getExample() {
        return """
            Example: $100,000 income, $18,000 total tax paid
              Enter: 100000 ENTER 18000 ENTER EFFTAX
              Result: 0.18 (18% effective tax rate)
            """;
    }

    @Override
    public String getSymbol() {
        return "EFFTAX";
    }
}
