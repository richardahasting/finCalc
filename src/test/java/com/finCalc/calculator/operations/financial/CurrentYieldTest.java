package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Current Yield calculation.
 */
class CurrentYieldTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCurrentYield() {
        // $950 current price, $60 annual coupon
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("60"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0632 (6.32% current yield)
        assertEquals(0.0632, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testBondAtPar() {
        // $1,000 bond at par, $50 coupon (5%)
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("50"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.05 (5% - same as coupon rate when at par)
        assertEquals(0.05, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testBondAtPremium() {
        // $1,100 current price (premium), $50 annual coupon
        stack.push(BigNumber.of("1100"));
        stack.push(BigNumber.of("50"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0455 (4.55% - lower than coupon rate)
        assertEquals(0.0455, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testBondAtDiscount() {
        // $900 current price (discount), $60 annual coupon
        stack.push(BigNumber.of("900"));
        stack.push(BigNumber.of("60"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0667 (6.67% - higher than coupon rate)
        assertEquals(0.0667, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroCoupon() {
        // Zero coupon bond
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("0"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (no current yield)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighCouponBond() {
        // $1,000 price, $100 annual coupon (10%)
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("100"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10%)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testDeepDiscountBond() {
        // $600 price, $40 coupon
        stack.push(BigNumber.of("600"));
        stack.push(BigNumber.of("40"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0667 (6.67%)
        assertEquals(0.0667, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSmallCoupon() {
        // $1,000 price, $10 annual coupon
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("10"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.01 (1%)
        assertEquals(0.01, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroCurrentPrice() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("60"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("current price must be positive"));
    }

    @Test
    void testNegativeCurrentPrice() {
        stack.push(BigNumber.of("-950"));
        stack.push(BigNumber.of("60"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("current price must be positive"));
    }

    @Test
    void testNegativeCoupon() {
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("-60"));

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual coupon cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("950"));
        // Missing annual coupon

        CurrentYield.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
