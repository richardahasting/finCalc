package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Debt Service Coverage Ratio (DSCR) calculation.
 */
class DebtServiceCoverageRatioTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicDSCR() {
        // $18,000 annual debt service, $22,000 NOI
        stack.push(BigNumber.of("18000"));
        stack.push(BigNumber.of("22000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.222 (DSCR of 1.22)
        assertEquals(1.222, result.value().doubleValue(), 0.001);
    }

    @Test
    void testHealthyDSCR() {
        // $15,000 annual debt service, $22,500 NOI
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("22500"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.5 (healthy DSCR)
        assertEquals(1.5, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testMinimalDSCR() {
        // $20,000 annual debt service, $20,000 NOI
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("20000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 (break-even DSCR)
        assertEquals(1.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowDSCR() {
        // $25,000 annual debt service, $20,000 NOI (under 1.0 - risky)
        stack.push(BigNumber.of("25000"));
        stack.push(BigNumber.of("20000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.8 (risky DSCR)
        assertEquals(0.8, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeNOI() {
        // Property operating at loss
        stack.push(BigNumber.of("18000"));
        stack.push(BigNumber.of("-5000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: negative DSCR
        assertTrue(result.value().doubleValue() < 0);
    }

    @Test
    void testZeroDebtService() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("22000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual debt service must be positive"));
    }

    @Test
    void testNegativeDebtService() {
        stack.push(BigNumber.of("-18000"));
        stack.push(BigNumber.of("22000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual debt service must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("18000"));
        // Missing NOI

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testHighDSCR() {
        // $10,000 annual debt service, $30,000 NOI
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("30000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 3.0 (very strong DSCR)
        assertEquals(3.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeValues() {
        // $250,000 annual debt service, $350,000 NOI
        stack.push(BigNumber.of("250000"));
        stack.push(BigNumber.of("350000"));

        DebtServiceCoverageRatio.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.4 (healthy DSCR)
        assertEquals(1.4, result.value().doubleValue(), 0.0001);
    }
}
