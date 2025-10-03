package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * Payment (PMT) calculation: computes periodic payment for a loan.
 *
 * <p>Stack transformation: {@code [... PV rate n PMT]} → {@code [... payment]}
 *
 * <p>Formula: PMT = PV * (rate * (1 + rate)^n) / ((1 + rate)^n - 1)
 * <p>For rate = 0: PMT = PV / n
 *
 * <p>Where:
 * <ul>
 *   <li>PV = Present Value (loan amount)</li>
 *   <li>rate = Interest rate per period (e.g., monthly rate = annual/12)</li>
 *   <li>n = Number of periods (e.g., months)</li>
 * </ul>
 *
 * <p>Example: $200,000 loan at 6% annual (0.5% monthly) for 30 years (360 months)
 * <pre>Stack: [200000, 0.005, 360, PMT] → [1199.10]</pre>
 *
 * <p><strong>Note:</strong> Uses double internally for calculation.
 */
public enum Payment implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("PMT", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem nItem = stack.pop();
        StackItem rateItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (nItem instanceof BigNumber nVal &&
            rateItem instanceof BigNumber rateVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double rate = rateVal.value().doubleValue();
            double n = nVal.value().doubleValue();

            // Validate inputs
            if (n <= 0) {
                stack.push(Error.domainError("PMT", "number of periods must be positive"));
                return stack;
            }

            if (rate < 0) {
                stack.push(Error.domainError("PMT", "interest rate cannot be negative"));
                return stack;
            }

            double payment;

            if (rate == 0) {
                // Special case: zero interest rate
                payment = pv / n;
            } else {
                // Standard formula: PMT = PV * (rate * (1 + rate)^n) / ((1 + rate)^n - 1)
                double factor = Math.pow(1 + rate, n);
                payment = pv * (rate * factor) / (factor - 1);
            }

            if (Double.isNaN(payment) || Double.isInfinite(payment)) {
                stack.push(Error.domainError("PMT", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(payment));
            }
        } else {
            stack.push(new Error("PMT requires numeric operands"));
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
            new OperandDescriptor("rate", "Interest rate per period"),
            new OperandDescriptor("n", "Number of periods")
        );
    }

    @Override
    public String getDescription() {
        return "Payment Calculation";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 loan at 6% annual (0.5% monthly) for 30 years (360 months)
              Enter: 200000 ENTER 0.005 ENTER 360 ENTER PMT
              Result: $1,199.10/month
            """;
    }

    @Override
    public String getSymbol() {
        return "PMT";
    }
}
