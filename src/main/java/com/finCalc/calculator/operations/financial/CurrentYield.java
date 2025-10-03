package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Current Yield calculation: bond's annual return based on current price.
 *
 * <p>Stack transformation: {@code [... CurrentPrice AnnualCouponPayment CY]} → {@code [... currentYield]}
 *
 * <p>Formula: Current Yield = Annual Coupon Payment / Current Price
 *
 * <p>Where:
 * <ul>
 *   <li>Annual Coupon Payment = Bond's annual interest payment</li>
 *   <li>Current Price = Current market price of the bond</li>
 * </ul>
 *
 * <p>Example: $1,000 par bond, $60 annual coupon, trading at $950
 * <pre>Stack: [950, 60, CY] → [0.0632] (6.32% current yield)</pre>
 *
 * <p><strong>Note:</strong> Result is expressed as a decimal (0.0632 = 6.32%)
 * <p>Current yield differs from yield to maturity (YTM) which accounts for capital gains/losses
 */
public enum CurrentYield implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 2;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("CY", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem annualCouponItem = stack.pop();
        StackItem currentPriceItem = stack.pop();

        if (annualCouponItem instanceof BigNumber couponVal &&
            currentPriceItem instanceof BigNumber priceVal) {

            double currentPrice = priceVal.value().doubleValue();
            double annualCoupon = couponVal.value().doubleValue();

            // Validate inputs
            if (currentPrice <= 0) {
                stack.push(Error.domainError("CY", "current price must be positive"));
                return stack;
            }

            if (annualCoupon < 0) {
                stack.push(Error.domainError("CY", "annual coupon cannot be negative"));
                return stack;
            }

            // Current Yield = Annual Coupon Payment / Current Price
            double currentYield = annualCoupon / currentPrice;

            if (Double.isNaN(currentYield) || Double.isInfinite(currentYield)) {
                stack.push(Error.domainError("CY", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(currentYield));
            }
        } else {
            stack.push(new Error("CY requires numeric operands"));
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
            new OperandDescriptor("CurrentPrice", "Current market price of the bond"),
            new OperandDescriptor("AnnualCouponPayment", "Bond's annual interest payment")
        );
    }

    @Override
    public String getDescription() {
        return "Current Yield";
    }

    @Override
    public String getExample() {
        return """
            Example: $950 current price, $60 annual coupon
              Enter: 950 ENTER 60 ENTER CY
              Result: 0.0632 (6.32% current yield)
            """;
    }

    @Override
    public String getSymbol() {
        return "CY";
    }
}
