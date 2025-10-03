package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Compound Annual Growth Rate (CAGR) calculation.
 */
class CompoundAnnualGrowthRateTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCAGR() {
        // $10,000 investment grows to $15,000 in 5 years
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("5"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0845 (8.45% annual growth)
        assertEquals(0.0845, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighGrowth() {
        // $5,000 grows to $20,000 in 10 years
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("10"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.1487 (14.87% annual growth)
        assertEquals(0.1487, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowGrowth() {
        // $100,000 grows to $110,000 in 5 years
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("110000"));
        stack.push(BigNumber.of("5"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0193 (1.93% annual growth)
        assertEquals(0.0193, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeGrowth() {
        // Value decreased from $10,000 to $8,000 in 3 years
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("8000"));
        stack.push(BigNumber.of("3"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: negative CAGR (decline)
        assertTrue(result.value().doubleValue() < 0);
        assertEquals(-0.0717, result.value().doubleValue(), 0.001);
    }

    @Test
    void testDoubling() {
        // Investment doubled in 7 years
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("7"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.1041 (10.41% - Rule of 72 approximation)
        assertEquals(0.1041, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testOneYear() {
        // $10,000 to $11,000 in 1 year
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("11000"));
        stack.push(BigNumber.of("1"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10% growth)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroBeginningValue() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("5"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("beginning value must be positive"));
    }

    @Test
    void testZeroEndingValue() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("5"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("ending value must be positive"));
    }

    @Test
    void testZeroYears() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("0"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("years must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("15000"));
        // Missing years

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testLargeValues() {
        // $1M grows to $2.5M in 15 years
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("2500000"));
        stack.push(BigNumber.of("15"));

        CompoundAnnualGrowthRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.063 (6.3% annual growth)
        assertEquals(0.063, result.value().doubleValue(), 0.001);
    }
}
