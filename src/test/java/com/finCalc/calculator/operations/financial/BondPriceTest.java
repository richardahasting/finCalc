package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bond Price calculation.
 */
class BondPriceTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicBondPrice() {
        // $1,000 face, 3% per period coupon, 2.5% yield, 20 periods
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("0.025"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1078.27 (Bond trades at premium since yield < coupon)
        assertEquals(1078.27, result.value().doubleValue(), 1.0);
    }

    @Test
    void testBondAtPremium() {
        // Yield < Coupon Rate → Bond trades above par
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("0.04"));
        stack.push(BigNumber.of("10"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: > 1000 (premium)
        assertTrue(result.value().doubleValue() > 1000);
    }

    @Test
    void testBondAtDiscount() {
        // Yield > Coupon Rate → Bond trades below par
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.04"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("10"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: < 1000 (discount)
        assertTrue(result.value().doubleValue() < 1000);
    }

    @Test
    void testBondAtPar() {
        // Yield = Coupon Rate → Bond trades at par
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("10"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1000 (at par)
        assertEquals(1000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testZeroYield() {
        // Zero yield special case
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: face value + all coupon payments
        // 1000 + (1000 * 0.03 * 20) = 1000 + 600 = 1600
        assertEquals(1600.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testZeroCoupon() {
        // Zero coupon bond
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: discounted face value only
        // 1000 / (1.05)^20 = 376.89
        assertEquals(376.89, result.value().doubleValue(), 1.0);
    }

    @Test
    void testShortMaturity() {
        // 1 period to maturity
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("0.04"));
        stack.push(BigNumber.of("1"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: (50 + 1000) / 1.04 = 1009.62
        assertEquals(1009.62, result.value().doubleValue(), 1.0);
    }

    @Test
    void testLongMaturity() {
        // 40 periods to maturity
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.04"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("40"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: < 1000 (deep discount due to long maturity and higher yield)
        assertTrue(result.value().doubleValue() < 1000);
    }

    @Test
    void testZeroFaceValue() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("0.025"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("face value must be positive"));
    }

    @Test
    void testNegativeCouponRate() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("-0.03"));
        stack.push(BigNumber.of("0.025"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("coupon rate cannot be negative"));
    }

    @Test
    void testNegativeYieldRate() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("-0.025"));
        stack.push(BigNumber.of("20"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("yield rate cannot be negative"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("0.025"));
        stack.push(BigNumber.of("0"));

        BondPrice.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("periods must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.03"));
        stack.push(BigNumber.of("0.025"));
        // Missing periods

        BondPrice.INSTANCE.execute(stack);

        assertEquals(4, stack.size()); // 3 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
