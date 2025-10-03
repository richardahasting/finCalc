package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Remaining Balance calculation: outstanding principal on a loan.
 *
 * <p>Stack transformation: {@code [... PV Rate NPer PaymentsMade REMBAL]} → {@code [... remainingBalance]}
 *
 * <p>Formula: Remaining Balance = PV × (1 + rate)^n - PMT × [((1 + rate)^n - 1) / rate]
 *
 * <p>Where:
 * <ul>
 *   <li>PV = Original loan amount (present value)</li>
 *   <li>Rate = Interest rate per period</li>
 *   <li>NPer = Total number of payment periods</li>
 *   <li>PaymentsMade = Number of payments already made</li>
 * </ul>
 *
 * <p>Example: $200,000 loan at 0.5% monthly for 360 months, 60 payments made
 * <pre>Stack: [200000, 0.005, 360, 60, REMBAL] → [188,202.50]</pre>
 *
 * <p><strong>Note:</strong> Calculates the outstanding principal after specified payments
 */
public enum RemainingBalance implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 4;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("REMBAL", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem paymentsMadeItem = stack.pop();
        StackItem nperItem = stack.pop();
        StackItem rateItem = stack.pop();
        StackItem pvItem = stack.pop();

        if (paymentsMadeItem instanceof BigNumber paymentsVal &&
            nperItem instanceof BigNumber nperVal &&
            rateItem instanceof BigNumber rateVal &&
            pvItem instanceof BigNumber pvVal) {

            double pv = pvVal.value().doubleValue();
            double rate = rateVal.value().doubleValue();
            double nper = nperVal.value().doubleValue();
            double paymentsMade = paymentsVal.value().doubleValue();

            // Validate inputs
            if (rate <= 0) {
                stack.push(Error.domainError("REMBAL", "interest rate must be positive"));
                return stack;
            }

            if (nper <= 0) {
                stack.push(Error.domainError("REMBAL", "number of periods must be positive"));
                return stack;
            }

            if (paymentsMade < 0 || paymentsMade > nper) {
                stack.push(Error.domainError("REMBAL", "payments made must be between 0 and total periods"));
                return stack;
            }

            if (pv <= 0) {
                stack.push(Error.domainError("REMBAL", "present value must be positive"));
                return stack;
            }

            // Calculate payment amount first
            double onePlusR = 1 + rate;
            double factor = Math.pow(onePlusR, nper);
            double payment = pv * (rate * factor) / (factor - 1);

            // Calculate remaining balance
            double factorN = Math.pow(onePlusR, paymentsMade);
            double remainingBalance = pv * factorN - payment * ((factorN - 1) / rate);

            if (Double.isNaN(remainingBalance) || Double.isInfinite(remainingBalance)) {
                stack.push(Error.domainError("REMBAL", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(remainingBalance));
            }
        } else {
            stack.push(new Error("REMBAL requires numeric operands"));
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
            new OperandDescriptor("Rate", "Interest rate per period"),
            new OperandDescriptor("NPer", "Total number of payment periods"),
            new OperandDescriptor("PaymentsMade", "Number of payments already made")
        );
    }

    @Override
    public String getDescription() {
        return "Remaining Loan Balance";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 loan at 0.5% monthly for 360 months, 60 payments made
              Enter: 200000 ENTER 0.005 ENTER 360 ENTER 60 ENTER REMBAL
              Result: 188202.50 (Remaining balance after 60 payments)
            """;
    }

    @Override
    public String getSymbol() {
        return "REMBAL";
    }
}
