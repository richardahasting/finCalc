package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Cash Flow After Taxes (CFAT) calculation: net cash flow after tax implications.
 *
 * <p>Stack transformation: {@code [... CashFlowBeforeTaxes TaxLiability CFAT]} → {@code [... cfat]}
 *
 * <p>Formula: CFAT = Cash Flow Before Taxes - Tax Liability
 *
 * <p>Where:
 * <ul>
 *   <li>Cash Flow Before Taxes = NOI - Debt Service + Principal Paydown</li>
 *   <li>Tax Liability = Taxable Income × Tax Rate</li>
 * </ul>
 *
 * <p>Example: $15,000 cash flow before taxes, $3,000 tax liability
 * <pre>Stack: [15000, 3000, CFAT] → [12000] (CFAT of $12,000)</pre>
 *
 * <p><strong>Note:</strong> Positive CFAT indicates profit after all expenses and taxes
 */
public enum CashFlowAfterTaxes implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CFAT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem taxLiabilityItem = stack.pop();
        StackItem cashFlowBeforeTaxesItem = stack.pop();

        if (taxLiabilityItem instanceof BigNumber taxVal &&
            cashFlowBeforeTaxesItem instanceof BigNumber cfbtVal) {

            double taxLiability = taxVal.value().doubleValue();
            double cashFlowBeforeTaxes = cfbtVal.value().doubleValue();

            // CFAT = Cash Flow Before Taxes - Tax Liability
            double cfat = cashFlowBeforeTaxes - taxLiability;

            if (Double.isNaN(cfat) || Double.isInfinite(cfat)) {
                stack.push(Error.domainError("CFAT", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(cfat));
            }
        } else {
            stack.push(new Error("CFAT requires numeric operands"));
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
            new OperandDescriptor("CashFlowBeforeTaxes", "Net cash flow before tax implications"),
            new OperandDescriptor("TaxLiability", "Total tax owed on the investment")
        );
    }

    @Override
    public String getDescription() {
        return "Cash Flow After Taxes";
    }

    @Override
    public String getExample() {
        return """
            Example: $15,000 cash flow before taxes, $3,000 tax liability
              Enter: 15000 ENTER 3000 ENTER CFAT
              Result: 12000 (Cash Flow After Taxes of $12,000)
            """;
    }

    @Override
    public String getSymbol() {
        return "CFAT";
    }
}
