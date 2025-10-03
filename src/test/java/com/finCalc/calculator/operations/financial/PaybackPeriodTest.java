package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Payback Period calculation.
 */
class PaybackPeriodTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicPaybackPeriod() {
        // $100,000 investment, $25,000 annual cash flow
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("25000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 4.0 years
        assertEquals(4.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testQuickPayback() {
        // $50,000 investment, $25,000 annual cash flow
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("25000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 2.0 years (quick recovery)
        assertEquals(2.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSlowPayback() {
        // $200,000 investment, $15,000 annual cash flow
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("15000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 13.33 years (slow recovery)
        assertEquals(13.33, result.value().doubleValue(), 0.01);
    }

    @Test
    void testFractionalYears() {
        // $75,000 investment, $20,000 annual cash flow
        stack.push(BigNumber.of("75000"));
        stack.push(BigNumber.of("20000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 3.75 years
        assertEquals(3.75, result.value().doubleValue(), 0.01);
    }

    @Test
    void testOneYearPayback() {
        // $30,000 investment, $30,000 annual cash flow
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("30000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 year
        assertEquals(1.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallInvestment() {
        // $5,000 investment, $2,500 annual cash flow
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("2500"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 2.0 years
        assertEquals(2.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeInvestment() {
        // $5M investment, $800K annual cash flow
        stack.push(BigNumber.of("5000000"));
        stack.push(BigNumber.of("800000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 6.25 years
        assertEquals(6.25, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroInvestment() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("25000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("initial investment must be positive"));
    }

    @Test
    void testNegativeInvestment() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("25000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("initial investment must be positive"));
    }

    @Test
    void testZeroCashFlow() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual cash flow must be positive"));
    }

    @Test
    void testNegativeCashFlow() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-25000"));

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual cash flow must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        // Missing annual cash flow

        PaybackPeriod.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
