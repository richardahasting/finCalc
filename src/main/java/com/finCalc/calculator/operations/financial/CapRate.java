package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Capitalization Rate (Cap Rate) calculation: measures return on real estate investment.
 *
 * <p>Stack transformation: {@code [... PropertyValue NOI CAP]} → {@code [... capRate]}
 *
 * <p>Formula: Cap Rate = NOI / Property Value
 *
 * <p>Where:
 * <ul>
 *   <li>NOI = Net Operating Income (annual)</li>
 *   <li>Property Value = Current market value or purchase price</li>
 * </ul>
 *
 * <p>Example: $200,000 property with $15,000 annual NOI
 * <pre>Stack: [200000, 15000, CAP] → [0.075] (7.5% cap rate)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.075 = 7.5%)
 */
public enum CapRate implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CAP", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem noiItem = stack.pop();
        StackItem propertyValueItem = stack.pop();

        if (noiItem instanceof BigNumber noiVal &&
            propertyValueItem instanceof BigNumber propertyVal) {

            double propertyValue = propertyVal.value().doubleValue();
            double noi = noiVal.value().doubleValue();

            // Validate inputs
            if (propertyValue <= 0) {
                stack.push(Error.domainError("CAP", "property value must be positive"));
                return stack;
            }

            // Cap Rate = NOI / Property Value
            double capRate = noi / propertyValue;

            if (Double.isNaN(capRate) || Double.isInfinite(capRate)) {
                stack.push(Error.domainError("CAP", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(capRate));
            }
        } else {
            stack.push(new Error("CAP requires numeric operands"));
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
            new OperandDescriptor("PropertyValue", "Current market value or purchase price"),
            new OperandDescriptor("NOI", "Net Operating Income (annual)")
        );
    }

    @Override
    public String getDescription() {
        return "Capitalization Rate";
    }

    @Override
    public String getExample() {
        return """
            Example: $200,000 property with $15,000 annual NOI
              Enter: 200000 ENTER 15000 ENTER CAP
              Result: 0.075 (7.5% cap rate)
            """;
    }

    @Override
    public String getSymbol() {
        return "CAP";
    }
}
