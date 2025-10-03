package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Capitalization Rate (Cap Rate) calculation.
 */
class CapRateTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCapRate() {
        // $200,000 property with $15,000 annual NOI
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("15000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.075 (7.5% cap rate)
        assertEquals(0.075, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighCapRate() {
        // $100,000 property with $12,000 annual NOI
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("12000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.12 (12% cap rate)
        assertEquals(0.12, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowCapRate() {
        // $500,000 property with $20,000 annual NOI
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("20000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.04 (4% cap rate)
        assertEquals(0.04, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeNOI() {
        // Property with negative NOI (operating at loss)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("-5000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -0.025 (negative cap rate)
        assertEquals(-0.025, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroNOI() {
        // Property breaking even
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% cap rate)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroPropertyValue() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("15000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property value must be positive"));
    }

    @Test
    void testNegativePropertyValue() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("15000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property value must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("200000"));
        // Missing NOI

        CapRate.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testVerySmallNOI() {
        // Very small but positive NOI
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("100"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0001 (0.01% cap rate)
        assertEquals(0.0001, result.value().doubleValue(), 0.000001);
    }

    @Test
    void testLargeValues() {
        // $10M property with $800K NOI
        stack.push(BigNumber.of("10000000"));
        stack.push(BigNumber.of("800000"));

        CapRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.08 (8% cap rate)
        assertEquals(0.08, result.value().doubleValue(), 0.0001);
    }
}
