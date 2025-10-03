package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Future Value (FV) calculation.
 */
class FutureValueTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicInvestmentFV() {
        // $10,000 invested at 8% annual for 10 years
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("10"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$21,589.25
        assertEquals(21589.25, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroInterestRate() {
        // $5,000 at 0% for 5 years
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("5"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $5,000 (no growth)
        assertEquals(5000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testMonthlyCompounding() {
        // $1,000 at 0.5% monthly for 12 months
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("12"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$1,061.68
        assertEquals(1061.68, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLongTermInvestment() {
        // $25,000 at 7% annual for 30 years
        stack.push(BigNumber.of("25000"));
        stack.push(BigNumber.of("0.07"));
        stack.push(BigNumber.of("30"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$190,306.11
        assertEquals(190306.11, result.value().doubleValue(), 0.50);
    }

    @Test
    void testHighRateFV() {
        // $100 at 10% for 5 periods
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("0.10"));
        stack.push(BigNumber.of("5"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$161.05
        assertEquals(161.05, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroPeriods() {
        // $1,000 at 5% for 0 periods
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("0"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $1,000 (no time elapsed)
        assertEquals(1000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("0.08"));
        // Missing n

        FutureValue.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testNegativePeriods() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("-10"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods cannot be negative"));
    }

    @Test
    void testRateLessThanNegative100Percent() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("-1.5")); // -150%
        stack.push(BigNumber.of("10"));

        FutureValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate cannot be less than -100%"));
    }
}
