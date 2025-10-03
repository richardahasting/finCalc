package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Net Operating Income (NOI) calculation: income after operating expenses.
 *
 * <p>Stack transformation: {@code [... GrossIncome OpEx NOI]} → {@code [... noi]}
 *
 * <p>Formula: NOI = Gross Income - Operating Expenses
 *
 * <p>Where:
 * <ul>
 *   <li>Gross Income = Total rental income (annual)</li>
 *   <li>OpEx = Operating Expenses (property tax, insurance, maintenance, etc.)</li>
 * </ul>
 *
 * <p>Example: $30,000 annual rent, $8,000 operating expenses
 * <pre>Stack: [30000, 8000, NOI] → [22000]</pre>
 *
 * <p><strong>Note:</strong> Does NOT include debt service (mortgage payments)
 */
public enum NetOperatingIncome implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("NOI", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem opExItem = stack.pop();
        StackItem grossIncomeItem = stack.pop();

        if (opExItem instanceof BigNumber opExVal &&
            grossIncomeItem instanceof BigNumber grossIncomeVal) {

            double grossIncome = grossIncomeVal.value().doubleValue();
            double opEx = opExVal.value().doubleValue();

            // NOI = Gross Income - Operating Expenses
            double noi = grossIncome - opEx;

            if (Double.isNaN(noi) || Double.isInfinite(noi)) {
                stack.push(Error.domainError("NOI", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(noi));
            }
        } else {
            stack.push(new Error("NOI requires numeric operands"));
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
            new OperandDescriptor("GrossIncome", "Total rental income (annual)"),
            new OperandDescriptor("OpEx", "Operating expenses (property tax, insurance, maintenance)")
        );
    }

    @Override
    public String getDescription() {
        return "Net Operating Income";
    }

    @Override
    public String getExample() {
        return """
            Example: $30,000 gross income, $12,000 operating expenses
              Enter: 30000 ENTER 12000 ENTER NOI
              Result: $18,000 (Net Operating Income)
            """;
    }

    @Override
    public String getSymbol() {
        return "NOI";
    }
}
