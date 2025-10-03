package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Compound Annual Growth Rate (CAGR) calculation: measures average annual growth rate.
 *
 * <p>Stack transformation: {@code [... BeginningValue EndingValue Years CAGR]} → {@code [... cagr]}
 *
 * <p>Formula: CAGR = (Ending Value / Beginning Value)^(1/Years) - 1
 *
 * <p>Where:
 * <ul>
 *   <li>Beginning Value = Initial investment or starting value</li>
 *   <li>Ending Value = Final value or current value</li>
 *   <li>Years = Number of years between beginning and ending</li>
 * </ul>
 *
 * <p>Example: $10,000 invested grows to $15,000 in 5 years
 * <pre>Stack: [10000, 15000, 5, CAGR] → [0.0845] (8.45% annual growth)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.0845 = 8.45%)
 * <p>CAGR smooths out volatility to show average annual return
 */
public enum CompoundAnnualGrowthRate implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 3;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CAGR", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem yearsItem = stack.pop();
        StackItem endingValueItem = stack.pop();
        StackItem beginningValueItem = stack.pop();

        if (yearsItem instanceof BigNumber yearsVal &&
            endingValueItem instanceof BigNumber endingVal &&
            beginningValueItem instanceof BigNumber beginningVal) {

            double beginningValue = beginningVal.value().doubleValue();
            double endingValue = endingVal.value().doubleValue();
            double years = yearsVal.value().doubleValue();

            // Validate inputs
            if (beginningValue <= 0) {
                stack.push(Error.domainError("CAGR", "beginning value must be positive"));
                return stack;
            }

            if (endingValue <= 0) {
                stack.push(Error.domainError("CAGR", "ending value must be positive"));
                return stack;
            }

            if (years <= 0) {
                stack.push(Error.domainError("CAGR", "years must be positive"));
                return stack;
            }

            // CAGR = (Ending Value / Beginning Value)^(1/Years) - 1
            double cagr = Math.pow(endingValue / beginningValue, 1.0 / years) - 1.0;

            if (Double.isNaN(cagr) || Double.isInfinite(cagr)) {
                stack.push(Error.domainError("CAGR", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(cagr));
            }
        } else {
            stack.push(new Error("CAGR requires numeric operands"));
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
            new OperandDescriptor("BeginningValue", "Initial investment or starting value"),
            new OperandDescriptor("EndingValue", "Final value or current value"),
            new OperandDescriptor("Years", "Number of years")
        );
    }

    @Override
    public String getDescription() {
        return "Compound Annual Growth Rate";
    }

    @Override
    public String getExample() {
        return """
            Example: $10,000 investment grows to $15,000 in 5 years
              Enter: 10000 ENTER 15000 ENTER 5 ENTER CAGR
              Result: 0.0845 (8.45% average annual growth)
            """;
    }

    @Override
    public String getSymbol() {
        return "CAGR";
    }
}
