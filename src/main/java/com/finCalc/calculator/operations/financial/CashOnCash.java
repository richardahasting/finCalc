package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Cash-on-Cash Return (CoC) calculation: measures cash income vs cash invested.
 *
 * <p>Stack transformation: {@code [... CashInvested AnnualCashFlow CoC]} → {@code [... cocReturn]}
 *
 * <p>Formula: CoC Return = Annual Cash Flow / Total Cash Invested
 *
 * <p>Where:
 * <ul>
 *   <li>Total Cash Invested = Down payment + closing costs + repairs</li>
 *   <li>Annual Cash Flow = NOI - Annual Debt Service</li>
 * </ul>
 *
 * <p>Example: $50,000 invested, $4,000 annual cash flow
 * <pre>Stack: [50000, 4000, CoC] → [0.08] (8% cash-on-cash return)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.08 = 8%)
 */
public enum CashOnCash implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CoC", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem annualCashFlowItem = stack.pop();
        StackItem cashInvestedItem = stack.pop();

        if (annualCashFlowItem instanceof BigNumber cashFlowVal &&
            cashInvestedItem instanceof BigNumber cashInvestedVal) {

            double cashInvested = cashInvestedVal.value().doubleValue();
            double annualCashFlow = cashFlowVal.value().doubleValue();

            // Validate inputs
            if (cashInvested <= 0) {
                stack.push(Error.domainError("CoC", "cash invested must be positive"));
                return stack;
            }

            // CoC = Annual Cash Flow / Total Cash Invested
            double cocReturn = annualCashFlow / cashInvested;

            if (Double.isNaN(cocReturn) || Double.isInfinite(cocReturn)) {
                stack.push(Error.domainError("CoC", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(cocReturn));
            }
        } else {
            stack.push(new Error("CoC requires numeric operands"));
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
            new OperandDescriptor("CashInvested", "Total cash invested (down payment + closing costs + repairs)"),
            new OperandDescriptor("AnnualCashFlow", "Annual pre-tax cash flow (NOI - debt service)")
        );
    }

    @Override
    public String getDescription() {
        return "Cash-on-Cash Return";
    }

    @Override
    public String getExample() {
        return """
            Example: $50,000 invested, $4,000 annual cash flow
              Enter: 50000 ENTER 4000 ENTER CoC
              Result: 0.08 (8% cash-on-cash return)
            """;
    }

    @Override
    public String getSymbol() {
        return "CoC";
    }
}
