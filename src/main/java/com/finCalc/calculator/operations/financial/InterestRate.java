package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Interest Rate (RATE) calculation: computes periodic interest rate.
 *
 * <p>Stack transformation: {@code [... PV PMT n RATE]} → {@code [... rate]}
 *
 * <p>This solves for rate in: PMT = PV * (rate * (1 + rate)^n) / ((1 + rate)^n - 1)
 *
 * <p>Where:
 * <ul>
 *   <li>PV = Present Value (loan amount)</li>
 *   <li>PMT = Periodic payment</li>
 *   <li>n = Number of periods</li>
 * </ul>
 *
 * <p>Example: $200,000 loan, $1,199.10/month for 360 months → 0.005 (0.5% monthly)
 * <pre>Stack: [200000, 1199.10, 360, RATE] → [0.005]</pre>
 *
 * <p><strong>Note:</strong> Uses Newton's method for iterative solving.
 */
public enum InterestRate implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;
    private static final int MAX_ITERATIONS = 100;
    private static final double TOLERANCE = 1e-10;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("RATE", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem nItem = stack.pop();
        StackItem pmtItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (nItem instanceof BigNumber nVal &&
            pmtItem instanceof BigNumber pmtVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double pmt = pmtVal.value().doubleValue();
            double n = nVal.value().doubleValue();

            // Validate inputs
            if (n <= 0) {
                stack.push(Error.domainError("RATE", "number of periods must be positive"));
                return stack;
            }

            if (pv <= 0 || pmt <= 0) {
                stack.push(Error.domainError("RATE", "PV and PMT must be positive"));
                return stack;
            }

            // Use Newton's method with numerical derivative for better stability
            double rate = 0.01; // Start with 1%
            double h = 1e-8; // Small step for numerical derivative

            for (int i = 0; i < MAX_ITERATIONS; i++) {
                // Calculate PMT given current rate guess
                double factor = Math.pow(1 + rate, n);
                double calculatedPmt = pv * (rate * factor) / (factor - 1);

                // Error function: difference between actual and calculated payment
                double f = pmt - calculatedPmt;

                // Numerical derivative: f'(rate) ≈ (f(rate + h) - f(rate)) / h
                double factorH = Math.pow(1 + rate + h, n);
                double pmtH = pv * ((rate + h) * factorH) / (factorH - 1);
                double fH = pmt - pmtH;
                double fPrime = (fH - f) / h;

                // Avoid division by zero
                if (Math.abs(fPrime) < 1e-10) {
                    break;
                }

                // Newton's step
                double newRate = rate - f / fPrime;

                // Check convergence
                if (Math.abs(newRate - rate) < TOLERANCE) {
                    stack.push(BigNumber.of(newRate));
                    return stack;
                }

                rate = newRate;

                // Constrain rate to reasonable bounds
                if (rate < 0) rate = 0.0001;
                if (rate > 1) rate = 0.99;
            }

            // Did not converge
            stack.push(Error.domainError("RATE", "could not converge to a solution"));
        } else {
            stack.push(new Error("RATE requires numeric operands"));
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
            new OperandDescriptor("n", "Number of periods")
        );
    }

    @Override
    public String getDescription() {
        return "Interest Rate Calculation";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 loan, $1,199.10 payment, 360 months
              Enter: 200000 ENTER 1199.10 ENTER 360 ENTER RATE
              Result: 0.005 (0.5% monthly = 6% annual)
            """;
    }

    @Override
    public String getSymbol() {
        return "RATE";
    }
}
