package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Yield to Maturity (YTM) calculation: total return if bond held to maturity.
 *
 * <p>Stack transformation: {@code [... CurrentPrice FaceValue AnnualCoupon YearsToMaturity YTM]} → {@code [... ytm]}
 *
 * <p>Approximation Formula: YTM ≈ [C + (F - P) / n] / [(F + P) / 2]
 *
 * <p>Where:
 * <ul>
 *   <li>C = Annual coupon payment</li>
 *   <li>F = Face value (par value)</li>
 *   <li>P = Current price</li>
 *   <li>n = Years to maturity</li>
 * </ul>
 *
 * <p>Example: $950 price, $1,000 face value, $60 coupon, 10 years
 * <pre>Stack: [950, 1000, 60, 10, YTM] → [0.0662] (6.62% YTM)</pre>
 *
 * <p><strong>Note:</strong> This is an approximation; exact YTM requires iterative calculation
 * <p>Result is expressed as a decimal (0.0662 = 6.62%)
 */
public enum YieldToMaturity implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 4;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("YTM", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem yearsToMaturityItem = stack.pop();
        StackItem annualCouponItem = stack.pop();
        StackItem faceValueItem = stack.pop();
        StackItem currentPriceItem = stack.pop();

        if (yearsToMaturityItem instanceof BigNumber yearsVal &&
            annualCouponItem instanceof BigNumber couponVal &&
            faceValueItem instanceof BigNumber faceVal &&
            currentPriceItem instanceof BigNumber priceVal) {

            double currentPrice = priceVal.value().doubleValue();
            double faceValue = faceVal.value().doubleValue();
            double annualCoupon = couponVal.value().doubleValue();
            double yearsToMaturity = yearsVal.value().doubleValue();

            // Validate inputs
            if (currentPrice <= 0) {
                stack.push(Error.domainError("YTM", "current price must be positive"));
                return stack;
            }

            if (faceValue <= 0) {
                stack.push(Error.domainError("YTM", "face value must be positive"));
                return stack;
            }

            if (annualCoupon < 0) {
                stack.push(Error.domainError("YTM", "annual coupon cannot be negative"));
                return stack;
            }

            if (yearsToMaturity <= 0) {
                stack.push(Error.domainError("YTM", "years to maturity must be positive"));
                return stack;
            }

            // YTM approximation: [C + (F - P) / n] / [(F + P) / 2]
            double ytm = (annualCoupon + (faceValue - currentPrice) / yearsToMaturity) /
                         ((faceValue + currentPrice) / 2);

            if (Double.isNaN(ytm) || Double.isInfinite(ytm)) {
                stack.push(Error.domainError("YTM", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(ytm));
            }
        } else {
            stack.push(new Error("YTM requires numeric operands"));
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
            new OperandDescriptor("FaceValue", "Par value of the bond (typically $1,000)"),
            new OperandDescriptor("AnnualCoupon", "Annual coupon payment"),
            new OperandDescriptor("YearsToMaturity", "Years until bond matures")
        );
    }

    @Override
    public String getDescription() {
        return "Yield to Maturity (approximation)";
    }

    @Override
    public String getExample() {
        return """
            Example: $950 price, $1,000 face value, $60 coupon, 10 years
              Enter: 950 ENTER 1000 ENTER 60 ENTER 10 ENTER YTM
              Result: 0.0662 (6.62% yield to maturity)
            """;
    }

    @Override
    public String getSymbol() {
        return "YTM";
    }
}
