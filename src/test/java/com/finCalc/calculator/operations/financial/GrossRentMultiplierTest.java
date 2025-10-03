package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Gross Rent Multiplier (GRM) calculation.
 */
class GrossRentMultiplierTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicGRM() {
        // $24,000 annual rent, $200,000 property price
        stack.push(BigNumber.of("24000"));
        stack.push(BigNumber.of("200000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 8.333 (GRM of 8.33)
        assertEquals(8.333, result.value().doubleValue(), 0.001);
    }

    @Test
    void testLowGRM() {
        // $50,000 annual rent, $300,000 property price (good value)
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("300000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 6.0 (lower GRM = better value)
        assertEquals(6.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighGRM() {
        // $20,000 annual rent, $300,000 property price (expensive)
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("300000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 15.0 (higher GRM = less attractive)
        assertEquals(15.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSmallProperty() {
        // $12,000 annual rent, $96,000 property price
        stack.push(BigNumber.of("12000"));
        stack.push(BigNumber.of("96000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 8.0
        assertEquals(8.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeProperty() {
        // $500,000 annual rent, $5,000,000 property price
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("5000000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 10.0
        assertEquals(10.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroGrossRent() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("200000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross annual rent must be positive"));
    }

    @Test
    void testNegativeGrossRent() {
        stack.push(BigNumber.of("-24000"));
        stack.push(BigNumber.of("200000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross annual rent must be positive"));
    }

    @Test
    void testZeroPropertyPrice() {
        stack.push(BigNumber.of("24000"));
        stack.push(BigNumber.of("0"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property price must be positive"));
    }

    @Test
    void testNegativePropertyPrice() {
        stack.push(BigNumber.of("24000"));
        stack.push(BigNumber.of("-200000"));

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property price must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("24000"));
        // Missing property price

        GrossRentMultiplier.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
