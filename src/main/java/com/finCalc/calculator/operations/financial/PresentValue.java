package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Present Value (PV) calculation: computes current value of future payments.
 *
 * <p>Stack transformation: {@code [... PMT rate n PV]} → {@code [... presentValue]}
 *
 * <p>Formula: PV = PMT * ((1 + rate)^n - 1) / (rate * (1 + rate)^n)
 * <p>For rate = 0: PV = PMT * n
 *
 * <p>Where:
 * <ul>
 *   <li>PMT = Periodic payment amount</li>
 *   <li>rate = Interest rate per period</li>
 *   <li>n = Number of periods</li>
 * </ul>
 *
 * <p>Example: What's the present value of $1,199.10/month for 360 months at 0.5% monthly?
 * <pre>Stack: [1199.10, 0.005, 360, PV] → [200000]</pre>
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum PresentValue implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("PV", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem nItem = stack.pop();
        StackItem rateItem = stack.pop();
        StackItem pmtItem = stack.pop();

        if (nItem instanceof BigNumber nVal &&
            rateItem instanceof BigNumber rateVal &&
            pmtItem instanceof BigNumber pmtVal) {

            double pmt = pmtVal.value().doubleValue();
            double rate = rateVal.value().doubleValue();
            double n = nVal.value().doubleValue();

            // Validate inputs
            if (n <= 0) {
                stack.push(Error.domainError("PV", "number of periods must be positive"));
                return stack;
            }

            if (rate < 0) {
                stack.push(Error.domainError("PV", "interest rate cannot be negative"));
                return stack;
            }

            double pv;

            if (rate == 0) {
                // Special case: zero interest rate
                pv = pmt * n;
            } else {
                // Standard formula: PV = PMT * ((1 + rate)^n - 1) / (rate * (1 + rate)^n)
                double factor = Math.pow(1 + rate, n);
                pv = pmt * (factor - 1) / (rate * factor);
            }

            if (Double.isNaN(pv) || Double.isInfinite(pv)) {
                stack.push(Error.domainError("PV", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(pv));
            }
        } else {
            stack.push(new Error("PV requires numeric operands"));
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
            new OperandDescriptor("PMT", "Periodic payment amount"),
            new OperandDescriptor("rate", "Interest rate per period"),
            new OperandDescriptor("n", "Number of periods")
        );
    }

    @Override
    public String getDescription() {
        return "Present Value Calculation";
    }

    @Override
    public String getExample() {
        return """
            Example: What's the present value of $1,199.10/month for 360 months at 0.5% monthly?
              Enter: 1199.10 ENTER 0.005 ENTER 360 ENTER PV
              Result: $200,000
            """;
    }

    @Override
    public String getSymbol() {
        return "PV";
    }
}
