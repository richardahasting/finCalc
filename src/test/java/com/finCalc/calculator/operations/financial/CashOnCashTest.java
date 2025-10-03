package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cash-on-Cash Return calculation.
 */
class CashOnCashTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCashOnCash() {
        // $50,000 invested, $4,000 annual cash flow
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("4000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.08 (8% cash-on-cash return)
        assertEquals(0.08, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighCashOnCash() {
        // $30,000 invested, $6,000 annual cash flow
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("6000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.20 (20% cash-on-cash return)
        assertEquals(0.20, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowCashOnCash() {
        // $100,000 invested, $2,500 annual cash flow
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("2500"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.025 (2.5% cash-on-cash return)
        assertEquals(0.025, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testNegativeCashFlow() {
        // Investment with negative cash flow (operating at loss)
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("-2000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -0.04 (negative 4% return)
        assertEquals(-0.04, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroCashFlow() {
        // Break-even investment
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("0"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% return)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroCashInvested() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("4000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("cash invested must be positive"));
    }

    @Test
    void testNegativeCashInvested() {
        stack.push(BigNumber.of("-50000"));
        stack.push(BigNumber.of("4000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("cash invested must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("50000"));
        // Missing annual cash flow

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testSmallInvestment() {
        // $5,000 invested, $500 annual cash flow
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("500"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.10 (10% cash-on-cash return)
        assertEquals(0.10, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLargeInvestment() {
        // $500,000 invested, $40,000 annual cash flow
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("40000"));

        CashOnCash.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.08 (8% cash-on-cash return)
        assertEquals(0.08, result.value().doubleValue(), 0.0001);
    }
}
