package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Calculation;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.OperandDescriptor;
import com.finCalc.calculator.StackItem;

import java.util.List;
import java.util.Stack;

/**
 * Bond Price calculation: present value of bond's cash flows.
 *
 * <p>Stack transformation: {@code [... FaceValue CouponRate YieldRate Periods BONDPRICE]} → {@code [... price]}
 *
 * <p>Formula: Bond Price = PV(coupons) + PV(face value)
 * <p>= C × [1 - (1 + y)^-n] / y + F / (1 + y)^n
 *
 * <p>Where:
 * <ul>
 *   <li>F = Face value</li>
 *   <li>C = Coupon payment per period (Face Value × Coupon Rate / periods per year)</li>
 *   <li>y = Yield rate per period</li>
 *   <li>n = Number of periods to maturity</li>
 * </ul>
 *
 * <p>Example: $1,000 face, 6% coupon (semi-annual), 5% yield, 20 periods (10 years)
 * <pre>Stack: [1000, 0.03, 0.025, 20, BONDPRICE] → [1078.27]</pre>
 *
 * <p><strong>Note:</strong> When yield > coupon rate, bond trades at discount (price < face)
 * <p>When yield < coupon rate, bond trades at premium (price > face)
 */
public enum BondPrice implements Calculation {
    INSTANCE;

    private static final int REQUIRED_OPERANDS = 4;

    @Override
    public Stack<StackItem> execute(Stack<StackItem> stack) {
        if (stack.size() < REQUIRED_OPERANDS) {
            stack.push(Error.insufficientOperands("BONDPRICE", REQUIRED_OPERANDS, stack.size()));
            return stack;
        }

        StackItem periodsItem = stack.pop();
        StackItem yieldRateItem = stack.pop();
        StackItem couponRateItem = stack.pop();
        StackItem faceValueItem = stack.pop();

        if (periodsItem instanceof BigNumber periodsVal &&
            yieldRateItem instanceof BigNumber yieldVal &&
            couponRateItem instanceof BigNumber couponVal &&
            faceValueItem instanceof BigNumber faceVal) {

            double faceValue = faceVal.value().doubleValue();
            double couponRate = couponVal.value().doubleValue();
            double yieldRate = yieldVal.value().doubleValue();
            double periods = periodsVal.value().doubleValue();

            // Validate inputs
            if (faceValue <= 0) {
                stack.push(Error.domainError("BONDPRICE", "face value must be positive"));
                return stack;
            }

            if (couponRate < 0) {
                stack.push(Error.domainError("BONDPRICE", "coupon rate cannot be negative"));
                return stack;
            }

            if (yieldRate < 0) {
                stack.push(Error.domainError("BONDPRICE", "yield rate cannot be negative"));
                return stack;
            }

            if (periods <= 0) {
                stack.push(Error.domainError("BONDPRICE", "periods must be positive"));
                return stack;
            }

            // Calculate coupon payment
            double couponPayment = faceValue * couponRate;

            // Handle zero yield special case
            if (yieldRate == 0) {
                double price = couponPayment * periods + faceValue;
                stack.push(BigNumber.of(price));
                return stack;
            }

            // Bond Price = C × [1 - (1 + y)^-n] / y + F / (1 + y)^n
            double discountFactor = Math.pow(1 + yieldRate, -periods);
            double pvCoupons = couponPayment * (1 - discountFactor) / yieldRate;
            double pvFaceValue = faceValue * discountFactor;
            double bondPrice = pvCoupons + pvFaceValue;

            if (Double.isNaN(bondPrice) || Double.isInfinite(bondPrice)) {
                stack.push(Error.domainError("BONDPRICE", "result is undefined or infinite"));
            } else {
                stack.push(BigNumber.of(bondPrice));
            }
        } else {
            stack.push(new Error("BONDPRICE requires numeric operands"));
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
            new OperandDescriptor("FaceValue", "Par value of the bond"),
            new OperandDescriptor("CouponRate", "Coupon rate per period (e.g., 0.03 for 3% semi-annual)"),
            new OperandDescriptor("YieldRate", "Market yield rate per period"),
            new OperandDescriptor("Periods", "Number of periods to maturity")
        );
    }

    @Override
    public String getDescription() {
        return "Bond Price";
    }

    @Override
    public String getExample() {
        return """
            Example: $1,000 face, 3% per period coupon, 2.5% yield, 20 periods
              Enter: 1000 ENTER 0.03 ENTER 0.025 ENTER 20 ENTER BONDPRICE
              Result: 1078.27 (Bond trades at premium)
            """;
    }

    @Override
    public String getSymbol() {
        return "BONDPRICE";
    }
}
