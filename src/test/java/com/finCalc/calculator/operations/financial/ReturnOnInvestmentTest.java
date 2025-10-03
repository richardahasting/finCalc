package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Return on Investment (ROI) calculation.
 */
class ReturnOnInvestmentTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicROI() {
        // Bought for $200,000, sold for $250,000
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("250000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.25 (25% return on investment)
        assertEquals(0.25, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighROI() {
        // Bought for $100,000, sold for $200,000
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("200000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 (100% ROI - doubled)
        assertEquals(1.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowROI() {
        // Bought for $100,000, sold for $105,000
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("105000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.05 (5% ROI)
        assertEquals(0.05, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeROI() {
        // Bought for $200,000, sold for $150,000 (loss)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("150000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -0.25 (-25% ROI - loss)
        assertEquals(-0.25, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testBreakEvenROI() {
        // Bought for $100,000, sold for $100,000
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("100000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% ROI - break even)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testTotalLoss() {
        // Bought for $50,000, worth $0 (total loss)
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("0"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -1.0 (-100% ROI - total loss)
        assertEquals(-1.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSmallInvestment() {
        // $5,000 investment, sold for $6,000
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("6000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.20 (20% ROI)
        assertEquals(0.20, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeInvestment() {
        // $1M investment, sold for $1.35M
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("1350000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.35 (35% ROI)
        assertEquals(0.35, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroCost() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("250000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("cost of investment must be positive"));
    }

    @Test
    void testNegativeCost() {
        stack.push(BigNumber.of("-200000"));
        stack.push(BigNumber.of("250000"));

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("cost of investment must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("200000"));
        // Missing gain

        ReturnOnInvestment.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
