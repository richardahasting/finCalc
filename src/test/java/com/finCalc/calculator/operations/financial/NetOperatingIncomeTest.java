package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Net Operating Income (NOI) calculation.
 */
class NetOperatingIncomeTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicNOI() {
        // $30,000 gross income, $12,000 operating expenses
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("12000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $18,000
        assertEquals(18000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighExpenses() {
        // $50,000 gross income, $35,000 operating expenses
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("35000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $15,000
        assertEquals(15000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowExpenses() {
        // $100,000 gross income, $20,000 operating expenses
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("20000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $80,000
        assertEquals(80000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeNOI() {
        // Operating at a loss
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("40000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -$10,000 (loss)
        assertEquals(-10000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroExpenses() {
        // No operating expenses
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("0"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $50,000 (all income is NOI)
        assertEquals(50000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testBreakEven() {
        // Income equals expenses
        stack.push(BigNumber.of("40000"));
        stack.push(BigNumber.of("40000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $0 (break even)
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("30000"));
        // Missing operating expenses

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testNegativeIncome() {
        // Negative income scenario
        stack.push(BigNumber.of("-10000"));
        stack.push(BigNumber.of("5000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -$15,000
        assertEquals(-15000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeExpenses() {
        // Negative expenses (e.g., rebates)
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("-5000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $35,000
        assertEquals(35000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeValues() {
        // $2M gross income, $600K operating expenses
        stack.push(BigNumber.of("2000000"));
        stack.push(BigNumber.of("600000"));

        NetOperatingIncome.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $1,400,000
        assertEquals(1400000.0, result.value().doubleValue(), 0.01);
    }
}
