package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Effective Tax Rate calculation.
 */
class EffectiveTaxRateTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicEffectiveTaxRate() {
        // $100,000 income, $18,000 total tax paid
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("18000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.18 (18% effective tax rate)
        assertEquals(0.18, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowTaxRate() {
        // $50,000 income, $5,000 tax (10%)
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("5000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10%)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighTaxRate() {
        // $200,000 income, $70,000 tax (35%)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("70000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.35 (35%)
        assertEquals(0.35, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroTax() {
        // No tax paid (exemptions, deductions, etc.)
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0%)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSmallIncome() {
        // $25,000 income, $2,500 tax (10%)
        stack.push(BigNumber.of("25000"));
        stack.push(BigNumber.of("2500"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10%)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeIncome() {
        // $1M income, $350K tax (35%)
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("350000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.35 (35%)
        assertEquals(0.35, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testTypicalMiddleClass() {
        // $75,000 income, $12,000 tax (16%)
        stack.push(BigNumber.of("75000"));
        stack.push(BigNumber.of("12000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.16 (16%)
        assertEquals(0.16, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testProgressiveTaxSystem() {
        // $150,000 income, $33,000 tax (22%)
        stack.push(BigNumber.of("150000"));
        stack.push(BigNumber.of("33000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.22 (22%)
        assertEquals(0.22, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroIncome() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("18000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("total income must be positive"));
    }

    @Test
    void testNegativeIncome() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("18000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("total income must be positive"));
    }

    @Test
    void testNegativeTax() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-18000"));

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("total tax cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        // Missing total tax

        EffectiveTaxRate.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
