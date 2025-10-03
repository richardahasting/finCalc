package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Total Interest Paid calculation: total interest over life of loan.
 *
 * <p>Stack transformation: {@code [... PV PMT NPer TOTINT]} → {@code [... totalInterest]}
 *
 * <p>Formula: Total Interest = (PMT × NPer) - PV
 *
 * <p>Where:
 * <ul>
 *   <li>PMT = Payment amount per period</li>
 *   <li>NPer = Total number of payment periods</li>
 *   <li>PV = Original loan amount</li>
 * </ul>
 *
 * <p>Example: $200,000 loan, $1,199.10 monthly payment, 360 months
 * <pre>Stack: [200000, 1199.10, 360, TOTINT] → [231,676] (Total interest paid)</pre>
 *
 * <p><strong>Note:</strong> Shows the true cost of borrowing over the loan term
 */
public enum TotalInterestPaid implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("TOTINT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem nperItem = stack.pop();
        StackItem pmtItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (nperItem instanceof BigNumber nperVal &&
            pmtItem instanceof BigNumber pmtVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double pmt = pmtVal.value().doubleValue();
            double nper = nperVal.value().doubleValue();

            // Validate inputs
            if (pv <= 0) {
                stack.push(Error.domainError("TOTINT", "present value must be positive"));
                return stack;
            }

            if (pmt <= 0) {
                stack.push(Error.domainError("TOTINT", "payment must be positive"));
                return stack;
            }

            if (nper <= 0) {
                stack.push(Error.domainError("TOTINT", "number of periods must be positive"));
                return stack;
            }

            // Total Interest = (PMT × NPer) - PV
            double totalInterest = (pmt * nper) - pv;

            if (Double.isNaN(totalInterest) || Double.isInfinite(totalInterest)) {
                stack.push(Error.domainError("TOTINT", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(totalInterest));
            }
        } else {
            stack.push(new Error("TOTINT requires numeric operands"));
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
            new OperandDescriptor("PV", "Original loan amount"),
            new OperandDescriptor("PMT", "Payment amount per period"),
            new OperandDescriptor("NPer", "Total number of payment periods")
        );
    }

    @Override
    public String getDescription() {
        return "Total Interest Paid";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 loan, $1,199.10 monthly payment, 360 months
              Enter: 200000 ENTER 1199.10 ENTER 360 ENTER TOTINT
              Result: 231676 (Total interest paid over loan term)
            """;
    }

    @Override
    public String getSymbol() {
        return "TOTINT";
    }
}
