package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Effective Gross Income (EGI) calculation.
 */
class EffectiveGrossIncomeTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicEGI() {
        // $100,000 potential gross income, $5,000 vacancy loss
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 95000 (Effective Gross Income of $95,000)
        assertEquals(95000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighVacancy() {
        // $100,000 potential income, $15,000 vacancy loss (15% vacancy)
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 85000
        assertEquals(85000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowVacancy() {
        // $200,000 potential income, $2,000 vacancy loss (1% vacancy)
        stack.push(BigNumber.of("2000"));
        stack.push(BigNumber.of("200000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 198000
        assertEquals(198000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroVacancy() {
        // 100% occupied
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 100000 (no vacancy loss)
        assertEquals(100000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testFullVacancy() {
        // 100% vacant
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0 (fully vacant)
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativePotentialIncome() {
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("-100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("potential gross income cannot be negative"));
    }

    @Test
    void testNegativeVacancyLoss() {
        stack.push(BigNumber.of("-5000"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("vacancy loss cannot be negative"));
    }

    @Test
    void testVacancyExceedsIncome() {
        // Vacancy loss greater than potential income
        stack.push(BigNumber.of("150000"));
        stack.push(BigNumber.of("100000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("vacancy loss cannot exceed potential gross income"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("5000"));
        // Missing potential gross income

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testLargeValues() {
        // $5M potential income, $250K vacancy loss
        stack.push(BigNumber.of("250000"));
        stack.push(BigNumber.of("5000000"));

        EffectiveGrossIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 4750000
        assertEquals(4750000.0, result.value().doubleValue(), 0.01);
    }
}
