package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Number of Periods (NPER) calculation: computes number of payment periods.
 *
 * <p>Stack transformation: {@code [... PV PMT rate NPER]} → {@code [... n]}
 *
 * <p>Formula: n = log(PMT / (PMT - PV * rate)) / log(1 + rate)
 * <p>For rate = 0: n = PV / PMT
 *
 * <p>Where:
 * <ul>
 *   <li>PV = Present Value (loan amount)</li>
 *   <li>PMT = Periodic payment</li>
 *   <li>rate = Interest rate per period</li>
 * </ul>
 *
 * <p>Example: $200,000 loan, $1,199.10/month at 0.5% monthly → 360 months
 * <pre>Stack: [200000, 1199.10, 0.005, NPER] → [360]</pre>
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum NumberOfPeriods implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("NPER", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem rateItem = stack.pop();
        StackItem pmtItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (rateItem instanceof BigNumber rateVal &&
            pmtItem instanceof BigNumber pmtVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double pmt = pmtVal.value().doubleValue();
            double rate = rateVal.value().doubleValue();

            // Validate inputs
            if (pv <= 0 || pmt <= 0) {
                stack.push(Error.domainError("NPER", "PV and PMT must be positive"));
                return stack;
            }

            if (rate < 0) {
                stack.push(Error.domainError("NPER", "interest rate cannot be negative"));
                return stack;
            }

            double n;

            if (rate == 0) {
                // Special case: zero interest rate
                n = pv / pmt;
            } else {
                // Check if payment is sufficient
                double minPayment = pv * rate;
                if (pmt <= minPayment) {
                    stack.push(Error.domainError("NPER",
                        "payment too small - loan would never be repaid (min: " + minPayment + ")"));
                    return stack;
                }

                // Standard formula: n = log(PMT / (PMT - PV * rate)) / log(1 + rate)
                double numerator = Math.log(pmt / (pmt - pv * rate));
                double denominator = Math.log(1 + rate);
                n = numerator / denominator;
            }

            if (Double.isNaN(n) || Double.isInfinite(n) || n < 0) {
                stack.push(Error.domainError("NPER", "result is undefined or invalid"));
            } else {
                stack.push(BigNumber.of(n));
            }
        } else {
            stack.push(new Error("NPER requires numeric operands"));
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
            new OperandDescriptor("PV", "Present Value (loan amount)"),
            new OperandDescriptor("PMT", "Periodic payment"),
            new OperandDescriptor("rate", "Interest rate per period")
        );
    }

    @Override
    public String getDescription() {
        return "Number of Periods Calculation";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 loan, $1,199.10 payment, 0.5% monthly rate
              Enter: 200000 ENTER 1199.10 ENTER 0.005 ENTER NPER
              Result: 360 months (30 years)
            """;
    }

    @Override
    public String getSymbol() {
        return "NPER";
    }
}
