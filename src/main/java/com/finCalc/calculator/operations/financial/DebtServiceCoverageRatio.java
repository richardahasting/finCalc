package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Debt Service Coverage Ratio (DSCR) calculation: measures ability to service debt.
 *
 * <p>Stack transformation: {@code [... AnnualDebtService NOI DSCR]} → {@code [... dscr]}
 *
 * <p>Formula: DSCR = NOI / Annual Debt Service
 *
 * <p>Where:
 * <ul>
 *   <li>NOI = Net Operating Income (annual)</li>
 *   <li>Annual Debt Service = Total annual loan payments (principal + interest)</li>
 * </ul>
 *
 * <p>Example: $22,000 NOI, $18,000 annual debt service
 * <pre>Stack: [18000, 22000, DSCR] → [1.222] (DSCR of 1.22)</pre>
 *
 * <p><strong>Note:</strong> Lenders typically require DSCR ≥ 1.20-1.25
 * <p>DSCR &lt; 1.0 means property doesn't generate enough income to cover debt
 */
public enum DebtServiceCoverageRatio implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("DSCR", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem noiItem = stack.pop();
        StackItem debtServiceItem = stack.pop();

        if (noiItem instanceof BigNumber noiVal &&
            debtServiceItem instanceof BigNumber debtServiceVal) {

            double debtService = debtServiceVal.value().doubleValue();
            double noi = noiVal.value().doubleValue();

            // Validate inputs
            if (debtService <= 0) {
                stack.push(Error.domainError("DSCR", "annual debt service must be positive"));
                return stack;
            }

            // DSCR = NOI / Annual Debt Service
            double dscr = noi / debtService;

            if (Double.isNaN(dscr) || Double.isInfinite(dscr)) {
                stack.push(Error.domainError("DSCR", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(dscr));
            }
        } else {
            stack.push(new Error("DSCR requires numeric operands"));
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
            new OperandDescriptor("AnnualDebtService", "Total annual loan payments (principal + interest)"),
            new OperandDescriptor("NOI", "Net Operating Income (annual)")
        );
    }

    @Override
    public String getDescription() {
        return "Debt Service Coverage Ratio";
    }

    @Override
    public String getExample() {
        return """
            Example: $18,000 annual debt service, $22,000 NOI
              Enter: 18000 ENTER 22000 ENTER DSCR
              Result: 1.222 (DSCR of 1.22)
            """;
    }

    @Override
    public String getSymbol() {
        return "DSCR";
    }
}
