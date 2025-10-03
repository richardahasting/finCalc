package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Debt-to-Income Ratio (DTI) calculation.
 */
class DebtToIncomeRatioTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicDTI() {
        // $8,000 monthly income, $2,400 monthly debt payments
        stack.push(BigNumber.of("8000"));
        stack.push(BigNumber.of("2400"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.30 (30% DTI)
        assertEquals(0.30, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowDTI() {
        // $10,000 income, $2,000 debt (20% - excellent)
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("2000"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.20 (20% DTI)
        assertEquals(0.20, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighDTI() {
        // $5,000 income, $2,500 debt (50% - very high)
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("2500"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.50 (50% DTI - risky)
        assertEquals(0.50, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testQualifiedMortgageLimit() {
        // $8,000 income, $3,440 debt (43% - qualified mortgage limit)
        stack.push(BigNumber.of("8000"));
        stack.push(BigNumber.of("3440"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.43 (43% DTI)
        assertEquals(0.43, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroDebt() {
        // No debt
        stack.push(BigNumber.of("8000"));
        stack.push(BigNumber.of("0"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% DTI)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testDebtEqualsIncome() {
        // 100% DTI (unsustainable)
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("5000"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 (100% DTI)
        assertEquals(1.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testDebtExceedsIncome() {
        // DTI over 100%
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("6000"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.2 (120% DTI - unsustainable)
        assertEquals(1.2, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testSmallIncome() {
        // $3,000 income, $1,200 debt
        stack.push(BigNumber.of("3000"));
        stack.push(BigNumber.of("1200"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.40 (40% DTI)
        assertEquals(0.40, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeIncome() {
        // $20,000 income, $5,000 debt
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("5000"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.25 (25% DTI)
        assertEquals(0.25, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroIncome() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("2400"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross monthly income must be positive"));
    }

    @Test
    void testNegativeIncome() {
        stack.push(BigNumber.of("-8000"));
        stack.push(BigNumber.of("2400"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("gross monthly income must be positive"));
    }

    @Test
    void testNegativeDebt() {
        stack.push(BigNumber.of("8000"));
        stack.push(BigNumber.of("-2400"));

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("total monthly debt cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("8000"));
        // Missing total monthly debt

        DebtToIncomeRatio.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
