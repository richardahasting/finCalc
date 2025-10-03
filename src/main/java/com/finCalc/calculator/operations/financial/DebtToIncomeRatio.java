package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Debt-to-Income Ratio (DTI) calculation: measures debt burden vs income.
 *
 * <p>Stack transformation: {@code [... GrossMonthlyIncome TotalMonthlyDebt DTI]} → {@code [... dti]}
 *
 * <p>Formula: DTI = Total Monthly Debt Payments / Gross Monthly Income
 *
 * <p>Where:
 * <ul>
 *   <li>Total Monthly Debt = Sum of all monthly debt payments</li>
 *   <li>Gross Monthly Income = Monthly income before taxes</li>
 * </ul>
 *
 * <p>Example: $8,000 monthly income, $2,400 monthly debt payments
 * <pre>Stack: [8000, 2400, DTI] → [0.30] (30% DTI)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.30 = 30%)
 * <p>Lenders typically prefer DTI ≤ 43% for qualified mortgages
 */
public enum DebtToIncomeRatio implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("DTI", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem totalMonthlyDebtItem = stack.pop();
        StackItem grossMonthlyIncomeItem = stack.pop();

        if (totalMonthlyDebtItem instanceof BigNumber debtVal &&
            grossMonthlyIncomeItem instanceof BigNumber incomeVal) {

            double grossMonthlyIncome = incomeVal.value().doubleValue();
            double totalMonthlyDebt = debtVal.value().doubleValue();

            // Validate inputs
            if (grossMonthlyIncome <= 0) {
                stack.push(Error.domainError("DTI", "gross monthly income must be positive"));
                return stack;
            }

            if (totalMonthlyDebt < 0) {
                stack.push(Error.domainError("DTI", "total monthly debt cannot be negative"));
                return stack;
            }

            // DTI = Total Monthly Debt / Gross Monthly Income
            double dti = totalMonthlyDebt / grossMonthlyIncome;

            if (Double.isNaN(dti) || Double.isInfinite(dti)) {
                stack.push(Error.domainError("DTI", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(dti));
            }
        } else {
            stack.push(new Error("DTI requires numeric operands"));
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
            new OperandDescriptor("GrossMonthlyIncome", "Monthly income before taxes"),
            new OperandDescriptor("TotalMonthlyDebt", "Sum of all monthly debt payments")
        );
    }

    @Override
    public String getDescription() {
        return "Debt-to-Income Ratio";
    }

    @Override
    public String getExample() {
        return """
            Example: $8,000 monthly income, $2,400 monthly debt payments
              Enter: 8000 ENTER 2400 ENTER DTI
              Result: 0.30 (30% debt-to-income ratio)
            """;
    }

    @Override
    public String getSymbol() {
        return "DTI";
    }
}
