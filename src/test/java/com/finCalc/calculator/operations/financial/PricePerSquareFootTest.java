package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Price Per Square Foot calculation.
 */
class PricePerSquareFootTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicPPSF() {
        // 2,000 sq ft, $300,000 property
        stack.push(BigNumber.of("2000"));
        stack.push(BigNumber.of("300000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $150 per square foot
        assertEquals(150.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighPricePPSF() {
        // 1,500 sq ft, $600,000 property (expensive area)
        stack.push(BigNumber.of("1500"));
        stack.push(BigNumber.of("600000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $400 per square foot
        assertEquals(400.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowPricePPSF() {
        // 3,000 sq ft, $180,000 property (affordable area)
        stack.push(BigNumber.of("3000"));
        stack.push(BigNumber.of("180000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $60 per square foot
        assertEquals(60.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallProperty() {
        // 800 sq ft, $120,000 condo
        stack.push(BigNumber.of("800"));
        stack.push(BigNumber.of("120000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $150 per square foot
        assertEquals(150.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeProperty() {
        // 10,000 sq ft, $2,500,000 property
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("2500000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $250 per square foot
        assertEquals(250.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroSquareFeet() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("300000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("square feet must be positive"));
    }

    @Test
    void testNegativeSquareFeet() {
        stack.push(BigNumber.of("-2000"));
        stack.push(BigNumber.of("300000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("square feet must be positive"));
    }

    @Test
    void testZeroPropertyPrice() {
        stack.push(BigNumber.of("2000"));
        stack.push(BigNumber.of("0"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property price must be positive"));
    }

    @Test
    void testNegativePropertyPrice() {
        stack.push(BigNumber.of("2000"));
        stack.push(BigNumber.of("-300000"));

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property price must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("2000"));
        // Missing property price

        PricePerSquareFoot.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
