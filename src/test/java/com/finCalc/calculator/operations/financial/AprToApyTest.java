package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for APR to APY Conversion.
 */
class AprToApyTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicAPRtoAPY() {
        // 6% APR compounded monthly (12 times per year)
        stack.push(BigNumber.of("0.06"));
        stack.push(BigNumber.of("12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0617 (6.17% APY)
        assertEquals(0.0617, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testQuarterlyCompounding() {
        // 8% APR compounded quarterly (4 times per year)
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("4"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0824 (8.24% APY)
        assertEquals(0.0824, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testDailyCompounding() {
        // 5% APR compounded daily (365 times per year)
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("365"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0513 (5.13% APY)
        assertEquals(0.0513, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSemiAnnualCompounding() {
        // 6% APR compounded semi-annually (2 times per year)
        stack.push(BigNumber.of("0.06"));
        stack.push(BigNumber.of("2"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0609 (6.09% APY)
        assertEquals(0.0609, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testAnnualCompounding() {
        // 10% APR compounded annually (1 time per year)
        stack.push(BigNumber.of("0.10"));
        stack.push(BigNumber.of("1"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10% APY - same as APR with annual compounding)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighFrequencyCompounding() {
        // 12% APR compounded monthly
        stack.push(BigNumber.of("0.12"));
        stack.push(BigNumber.of("12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.1268 (12.68% APY)
        assertEquals(0.1268, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowAPR() {
        // 1% APR compounded monthly
        stack.push(BigNumber.of("0.01"));
        stack.push(BigNumber.of("12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.01005 (1.005% APY)
        assertEquals(0.01005, result.value().doubleValue(), 0.00001);
    }

    @Test
    void testZeroAPR() {
        // 0% APR
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% APY)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeAPR() {
        stack.push(BigNumber.of("-0.06"));
        stack.push(BigNumber.of("12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("APR cannot be negative"));
    }

    @Test
    void testZeroCompoundingPeriods() {
        stack.push(BigNumber.of("0.06"));
        stack.push(BigNumber.of("0"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("compounding periods must be positive"));
    }

    @Test
    void testNegativeCompoundingPeriods() {
        stack.push(BigNumber.of("0.06"));
        stack.push(BigNumber.of("-12"));

        AprToApy.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("compounding periods must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("0.06"));
        // Missing compounding periods

        AprToApy.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
