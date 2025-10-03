package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Required Minimum Distribution (RMD) calculation: mandatory retirement account withdrawal.
 *
 * <p>Stack transformation: {@code [... AccountBalance DistributionPeriod RMD]} → {@code [... rmd]}
 *
 * <p>Formula: RMD = Account Balance / Distribution Period
 *
 * <p>Where:
 * <ul>
 *   <li>Account Balance = Total value of retirement account as of Dec 31</li>
 *   <li>Distribution Period = Life expectancy factor from IRS tables</li>
 * </ul>
 *
 * <p>Example: $500,000 account balance, 25.6 distribution period (age 73)
 * <pre>Stack: [500000, 25.6, RMD] → [19531.25] (Required minimum distribution)</pre>
 *
 * <p><strong>Note:</strong> RMDs generally required starting at age 73 (as of 2024)
 * <p>Distribution period based on IRS Uniform Lifetime Table
 * <p>Failure to take RMD results in 25% penalty on amount not withdrawn
 */
public enum RequiredMinimumDistribution implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("RMD", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem distributionPeriodItem = stack.pop();
        StackItem accountBalanceItem = stack.pop();

        if (distributionPeriodItem instanceof BigNumber periodVal &&
            accountBalanceItem instanceof BigNumber balanceVal) {

            double accountBalance = balanceVal.value().doubleValue();
            double distributionPeriod = periodVal.value().doubleValue();

            // Validate inputs
            if (accountBalance < 0) {
                stack.push(Error.domainError("RMD", "account balance cannot be negative"));
                return stack;
            }

            if (distributionPeriod <= 0) {
                stack.push(Error.domainError("RMD", "distribution period must be positive"));
                return stack;
            }

            // RMD = Account Balance / Distribution Period
            double rmd = accountBalance / distributionPeriod;

            if (Double.isNaN(rmd) || Double.isInfinite(rmd)) {
                stack.push(Error.domainError("RMD", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(rmd));
            }
        } else {
            stack.push(new Error("RMD requires numeric operands"));
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
            new OperandDescriptor("AccountBalance", "Total retirement account value as of Dec 31"),
            new OperandDescriptor("DistributionPeriod", "Life expectancy factor from IRS Uniform Lifetime Table")
        );
    }

    @Override
    public String getDescription() {
        return "Required Minimum Distribution";
    }

    @Override
    public String getExample() {
        return """
            Example: $500,000 account balance, 25.6 distribution period (age 73)
              Enter: 500000 ENTER 25.6 ENTER RMD
              Result: 19531.25 (Required minimum distribution for the year)
            """;
    }

    @Override
    public String getSymbol() {
        return "RMD";
    }
}
