package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for After-Tax Return calculation.
 */
class AfterTaxReturnTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicAfterTaxReturn() {
        // 8% pre-tax return, 25% tax rate
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("0.25"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.06 (6% after-tax return)
        assertEquals(0.06, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighTaxRate() {
        // 10% pre-tax return, 40% tax rate
        stack.push(BigNumber.of("0.10"));
        stack.push(BigNumber.of("0.40"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.06 (6% after-tax)
        assertEquals(0.06, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowTaxRate() {
        // 8% pre-tax return, 15% tax rate
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("0.15"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.068 (6.8% after-tax)
        assertEquals(0.068, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroTaxRate() {
        // Tax-exempt return
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("0"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.08 (same as pre-tax)
        assertEquals(0.08, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testMaxTaxRate() {
        // 10% pre-tax return, 100% tax rate (confiscatory)
        stack.push(BigNumber.of("0.10"));
        stack.push(BigNumber.of("1.0"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (no after-tax return)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativePreTaxReturn() {
        // Loss scenario
        stack.push(BigNumber.of("-0.05"));
        stack.push(BigNumber.of("0.25"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -0.0375 (loss reduced by tax benefit)
        assertEquals(-0.0375, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighReturn() {
        // 20% pre-tax return, 30% tax rate
        stack.push(BigNumber.of("0.20"));
        stack.push(BigNumber.of("0.30"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.14 (14% after-tax)
        assertEquals(0.14, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testTaxRateAboveOne() {
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("1.1"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("tax rate must be between 0 and 1"));
    }

    @Test
    void testNegativeTaxRate() {
        stack.push(BigNumber.of("0.08"));
        stack.push(BigNumber.of("-0.25"));

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("tax rate must be between 0 and 1"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("0.08"));
        // Missing tax rate

        AfterTaxReturn.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
