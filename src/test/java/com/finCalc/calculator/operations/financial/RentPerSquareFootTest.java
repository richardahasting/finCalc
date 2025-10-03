package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Rent Per Square Foot calculation.
 */
class RentPerSquareFootTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicRPSF() {
        // 1,500 sq ft, $36,000 annual rent
        stack.push(BigNumber.of("1500"));
        stack.push(BigNumber.of("36000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $24 per square foot annually
        assertEquals(24.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighRentRPSF() {
        // 1,000 sq ft, $60,000 annual rent (premium location)
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("60000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $60 per square foot
        assertEquals(60.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowRentRPSF() {
        // 2,500 sq ft, $30,000 annual rent (affordable area)
        stack.push(BigNumber.of("2500"));
        stack.push(BigNumber.of("30000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $12 per square foot
        assertEquals(12.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroRent() {
        // No rent (vacant or free use)
        stack.push(BigNumber.of("1500"));
        stack.push(BigNumber.of("0"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $0 per square foot
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallSpace() {
        // 500 sq ft, $18,000 annual rent
        stack.push(BigNumber.of("500"));
        stack.push(BigNumber.of("18000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $36 per square foot
        assertEquals(36.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeSpace() {
        // 50,000 sq ft warehouse, $500,000 annual rent
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("500000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $10 per square foot
        assertEquals(10.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroSquareFeet() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("36000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("square feet must be positive"));
    }

    @Test
    void testNegativeSquareFeet() {
        stack.push(BigNumber.of("-1500"));
        stack.push(BigNumber.of("36000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("square feet must be positive"));
    }

    @Test
    void testNegativeRent() {
        stack.push(BigNumber.of("1500"));
        stack.push(BigNumber.of("-36000"));

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual rent cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("1500"));
        // Missing annual rent

        RentPerSquareFoot.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
