package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Operating Expense Ratio (OER) calculation: operating costs as percentage of income.
 *
 * <p>Stack transformation: {@code [... GrossOperatingIncome OperatingExpenses OER]} → {@code [... oer]}
 *
 * <p>Formula: OER = Operating Expenses / Gross Operating Income
 *
 * <p>Where:
 * <ul>
 *   <li>Operating Expenses = Property taxes, insurance, maintenance, management fees, utilities</li>
 *   <li>Gross Operating Income = Effective Gross Income (after vacancy losses)</li>
 * </ul>
 *
 * <p>Example: $80,000 gross income, $32,000 operating expenses
 * <pre>Stack: [80000, 32000, OER] → [0.40] (40% operating expense ratio)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.40 = 40%)
 * <p>Lower OER typically indicates more efficient property management
 */
public enum OperatingExpenseRatio implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("OER", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem operatingExpensesItem = stack.pop();
        StackItem grossOperatingIncomeItem = stack.pop();

        if (operatingExpensesItem instanceof BigNumber expensesVal &&
            grossOperatingIncomeItem instanceof BigNumber incomeVal) {

            double grossOperatingIncome = incomeVal.value().doubleValue();
            double operatingExpenses = expensesVal.value().doubleValue();

            // Validate inputs
            if (grossOperatingIncome <= 0) {
                stack.push(Error.domainError("OER", "gross operating income must be positive"));
                return stack;
            }

            if (operatingExpenses < 0) {
                stack.push(Error.domainError("OER", "operating expenses cannot be negative"));
                return stack;
            }

            // OER = Operating Expenses / Gross Operating Income
            double oer = operatingExpenses / grossOperatingIncome;

            if (Double.isNaN(oer) || Double.isInfinite(oer)) {
                stack.push(Error.domainError("OER", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(oer));
            }
        } else {
            stack.push(new Error("OER requires numeric operands"));
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
            new OperandDescriptor("GrossOperatingIncome", "Effective gross income after vacancy losses"),
            new OperandDescriptor("OperatingExpenses", "Total operating expenses (taxes, insurance, maintenance, etc.)")
        );
    }

    @Override
    public String getDescription() {
        return "Operating Expense Ratio";
    }

    @Override
    public String getExample() {
        return """
            Example: $80,000 gross operating income, $32,000 operating expenses
              Enter: 80000 ENTER 32000 ENTER OER
              Result: 0.40 (40% operating expense ratio)
            """;
    }

    @Override
    public String getSymbol() {
        return "OER";
    }
}
