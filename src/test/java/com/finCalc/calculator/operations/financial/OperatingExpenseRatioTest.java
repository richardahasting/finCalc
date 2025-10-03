package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Operating Expense Ratio (OER) calculation.
 */
class OperatingExpenseRatioTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicOER() {
        // $80,000 gross operating income, $32,000 operating expenses
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("32000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.40 (40% operating expense ratio)
        assertEquals(0.40, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowOER() {
        // $100,000 gross income, $25,000 expenses (efficient)
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("25000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.25 (25% OER - efficient)
        assertEquals(0.25, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighOER() {
        // $80,000 gross income, $56,000 expenses (inefficient)
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("56000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.70 (70% OER - high expenses)
        assertEquals(0.70, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroExpenses() {
        // No operating expenses
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("0"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% OER)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testExpensesEqualIncome() {
        // 100% expense ratio
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("80000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 (100% OER - break even on operations)
        assertEquals(1.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testExpensesExceedIncome() {
        // Expenses greater than income
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("100000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.25 (125% OER - operating at loss)
        assertEquals(1.25, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroIncome() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("32000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross operating income must be positive"));
    }

    @Test
    void testNegativeIncome() {
        stack.push(BigNumber.of("-80000"));
        stack.push(BigNumber.of("32000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross operating income must be positive"));
    }

    @Test
    void testNegativeExpenses() {
        stack.push(BigNumber.of("80000"));
        stack.push(BigNumber.of("-32000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("operating expenses cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("80000"));
        // Missing operating expenses

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testLargeValues() {
        // $5M gross income, $2M expenses
        stack.push(BigNumber.of("5000000"));
        stack.push(BigNumber.of("2000000"));

        OperatingExpenseRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.40 (40% OER)
        assertEquals(0.40, result.value().doubleValue(), 0.0001);
    }
}
