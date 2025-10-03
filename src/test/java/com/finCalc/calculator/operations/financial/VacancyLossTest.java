package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Vacancy Loss calculation.
 */
class VacancyLossTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicVacancyLoss() {
        // $100,000 potential gross income, 5% vacancy rate
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0.05"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $5,000
        assertEquals(5000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowVacancyRate() {
        // $200,000 income, 2% vacancy rate
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.02"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $4,000
        assertEquals(4000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighVacancyRate() {
        // $150,000 income, 15% vacancy rate
        stack.push(BigNumber.of("150000"));
        stack.push(BigNumber.of("0.15"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $22,500
        assertEquals(22500.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroVacancy() {
        // 100% occupancy
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $0
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testFullVacancy() {
        // 100% vacancy rate
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1.0"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $100,000 (all income lost)
        assertEquals(100000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testTypicalVacancy() {
        // $80,000 income, 7% vacancy rate (typical)
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("0.07"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $5,600
        assertEquals(5600.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallProperty() {
        // $24,000 annual rent, 10% vacancy
        stack.push(BigNumber.of("24000"));
        stack.push(BigNumber.of("0.10"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $2,400
        assertEquals(2400.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeProperty() {
        // $1M potential income, 8% vacancy
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("0.08"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $80,000
        assertEquals(80000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroIncome() {
        // No potential income
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0.05"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $0
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeIncome() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("0.05"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("potential gross income cannot be negative"));
    }

    @Test
    void testNegativeVacancyRate() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-0.05"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("vacancy rate must be between 0 and 1"));
    }

    @Test
    void testVacancyRateAboveOne() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1.5"));

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("vacancy rate must be between 0 and 1"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        // Missing vacancy rate

        VacancyLoss.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
