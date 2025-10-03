package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Break-Even Point calculation.
 */
class BreakEvenPointTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicBreakEvenPoint() {
        // $50,000 fixed costs, $100 price, $60 variable cost per unit
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1250 units
        assertEquals(1250.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighMargin() {
        // $30,000 fixed costs, $200 price, $50 variable cost (high margin)
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("200"));
        stack.push(BigNumber.of("50"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 200 units
        assertEquals(200.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowMargin() {
        // $100,000 fixed costs, $50 price, $45 variable cost (low margin)
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("45"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 20,000 units
        assertEquals(20000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroFixedCosts() {
        // No fixed costs
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0 units (no fixed costs to cover)
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroVariableCost() {
        // $10,000 fixed costs, $50 price, $0 variable cost
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("0"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 200 units
        assertEquals(200.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeFixedCosts() {
        stack.push(BigNumber.of("-50000"));
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("fixed costs cannot be negative"));
    }

    @Test
    void testZeroPrice() {
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("price per unit must be positive"));
    }

    @Test
    void testPriceEqualsVariableCost() {
        // No contribution margin
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("price must be greater than variable cost"));
    }

    @Test
    void testPriceLessThanVariableCost() {
        // Negative contribution margin
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("60"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("price must be greater than variable cost"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("100"));
        // Missing variable cost

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testLargeScale() {
        // $5M fixed costs, $1000 price, $700 variable cost
        stack.push(BigNumber.of("5000000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("700"));

        BreakEvenPoint.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 16,666.67 units
        assertEquals(16666.67, result.value().doubleValue(), 0.1);
    }
}
