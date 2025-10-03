package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Effective Gross Income (EGI) calculation: actual income after vacancy and credit losses.
 *
 * <p>Stack transformation: {@code [... VacancyLoss PotentialGrossIncome EGI]} → {@code [... egi]}
 *
 * <p>Formula: EGI = Potential Gross Income - Vacancy Loss - Credit Loss
 *
 * <p>Where:
 * <ul>
 *   <li>Potential Gross Income = Maximum rental income at 100% occupancy</li>
 *   <li>Vacancy Loss = Income lost due to unoccupied units</li>
 * </ul>
 *
 * <p>Example: $100,000 potential income, $5,000 vacancy loss
 * <pre>Stack: [5000, 100000, EGI] → [95000] (Effective Gross Income of $95,000)</pre>
 *
 * <p><strong>Note:</strong> EGI is used to calculate NOI (Net Operating Income)
 * <p>EGI represents realistic income expectations for the property
 */
public enum EffectiveGrossIncome implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("EGI", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem potentialGrossIncomeItem = stack.pop();
        StackItem vacancyLossItem = stack.pop();

        if (potentialGrossIncomeItem instanceof BigNumber incomeVal &&
            vacancyLossItem instanceof BigNumber lossVal) {

            double potentialGrossIncome = incomeVal.value().doubleValue();
            double vacancyLoss = lossVal.value().doubleValue();

            // Validate inputs
            if (potentialGrossIncome < 0) {
                stack.push(Error.domainError("EGI", "potential gross income cannot be negative"));
                return stack;
            }

            if (vacancyLoss < 0) {
                stack.push(Error.domainError("EGI", "vacancy loss cannot be negative"));
                return stack;
            }

            if (vacancyLoss > potentialGrossIncome) {
                stack.push(Error.domainError("EGI", "vacancy loss cannot exceed potential gross income"));
                return stack;
            }

            // EGI = Potential Gross Income - Vacancy Loss
            double egi = potentialGrossIncome - vacancyLoss;

            if (Double.isNaN(egi) || Double.isInfinite(egi)) {
                stack.push(Error.domainError("EGI", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(egi));
            }
        } else {
            stack.push(new Error("EGI requires numeric operands"));
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
            new OperandDescriptor("VacancyLoss", "Income lost due to vacancy and credit losses"),
            new OperandDescriptor("PotentialGrossIncome", "Maximum rental income at 100% occupancy")
        );
    }

    @Override
    public String getDescription() {
        return "Effective Gross Income";
    }

    @Override
    public String getExample() {
        return """
            Example: $100,000 potential gross income, $5,000 vacancy loss
              Enter: 5000 ENTER 100000 ENTER EGI
              Result: 95000 (Effective Gross Income of $95,000)
            """;
    }

    @Override
    public String getSymbol() {
        return "EGI";
    }
}
