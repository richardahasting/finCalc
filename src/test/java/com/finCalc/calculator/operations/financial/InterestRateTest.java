package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Interest Rate (RATE) calculation.
 */
class InterestRateTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicMortgageRate() {
        // $200,000 loan, $1,199.10 payment, 360 months
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("360"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~0.005 (0.5% monthly)
        assertEquals(0.005, result.value().doubleValue(), 0.0005);
    }

    @Test
    void testSmallLoanRate() {
        // $5,000 loan, $862.74 payment, 6 months
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("862.74"));
        stack.push(BigNumber.of("6"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~0.01 (1% monthly)
        assertEquals(0.01, result.value().doubleValue(), 0.001);
    }

    @Test
    void testCarLoanRate() {
        // $30,000 loan, $649.32 payment, 60 months
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("649.32"));
        stack.push(BigNumber.of("60"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~0.009 (0.9% monthly)
        assertEquals(0.009, result.value().doubleValue(), 0.001);
    }

    @Test
    void testShortTermRate() {
        // $1,000 loan, $112.83 payment, 12 months
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("112.83"));
        stack.push(BigNumber.of("12"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~0.05 (5% monthly)
        assertEquals(0.05, result.value().doubleValue(), 0.001);
    }

    @Test
    void testLowRateCalculation() {
        // $100,000 loan, $2,224.44 payment, 48 months
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("2224.44"));
        stack.push(BigNumber.of("48"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~0.0025 (0.25% monthly, 3% annual)
        assertEquals(0.0025, result.value().doubleValue(), 0.001);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1000"));
        // Missing n

        InterestRate.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }

    @Test
    void testNegativePV() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("120"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("PV and PMT must be positive"));
    }

    @Test
    void testNegativePMT() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-1000"));
        stack.push(BigNumber.of("120"));

        InterestRate.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("PV and PMT must be positive"));
    }
}
