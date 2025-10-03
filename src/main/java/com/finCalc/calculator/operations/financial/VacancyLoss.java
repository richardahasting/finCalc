package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Vacancy Loss calculation: income lost due to unoccupied units.
 *
 * <p>Stack transformation: {@code [... PotentialGrossIncome VacancyRate VACANCY]} → {@code [... vacancyLoss]}
 *
 * <p>Formula: Vacancy Loss = Potential Gross Income × Vacancy Rate
 *
 * <p>Where:
 * <ul>
 *   <li>Potential Gross Income = Maximum rental income if 100% occupied</li>
 *   <li>Vacancy Rate = Expected percentage of unoccupied units (as decimal)</li>
 * </ul>
 *
 * <p>Example: $100,000 potential gross income, 5% vacancy rate
 * <pre>Stack: [100000, 0.05, VACANCY] → [5000] (Vacancy loss of $5,000)</pre>
 *
 * <p><strong>Note:</strong> Vacancy rate should be entered as decimal (0.05 = 5%)
 * <p>Effective Gross Income = Potential Gross Income - Vacancy Loss
 */
public enum VacancyLoss implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("VACANCY", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem vacancyRateItem = stack.pop();
        StackItem potentialGrossIncomeItem = stack.pop();

        if (vacancyRateItem instanceof BigNumber rateVal &&
            potentialGrossIncomeItem instanceof BigNumber incomeVal) {

            double potentialGrossIncome = incomeVal.value().doubleValue();
            double vacancyRate = rateVal.value().doubleValue();

            // Validate inputs
            if (potentialGrossIncome < 0) {
                stack.push(Error.domainError("VACANCY", "potential gross income cannot be negative"));
                return stack;
            }

            if (vacancyRate < 0 || vacancyRate > 1) {
                stack.push(Error.domainError("VACANCY", "vacancy rate must be between 0 and 1"));
                return stack;
            }

            // Vacancy Loss = Potential Gross Income × Vacancy Rate
            double vacancyLoss = potentialGrossIncome * vacancyRate;

            if (Double.isNaN(vacancyLoss) || Double.isInfinite(vacancyLoss)) {
                stack.push(Error.domainError("VACANCY", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(vacancyLoss));
            }
        } else {
            stack.push(new Error("VACANCY requires numeric operands"));
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
            new OperandDescriptor("PotentialGrossIncome", "Maximum rental income at 100% occupancy"),
            new OperandDescriptor("VacancyRate", "Expected vacancy rate (as decimal, e.g., 0.05 for 5%)")
        );
    }

    @Override
    public String getDescription() {
        return "Vacancy Loss";
    }

    @Override
    public String getExample() {
        return """
            Example: $100,000 potential gross income, 5% vacancy rate
              Enter: 100000 ENTER 0.05 ENTER VACANCY
              Result: 5000 (Vacancy loss of $5,000)
            """;
    }

    @Override
    public String getSymbol() {
        return "VACANCY";
    }
}
