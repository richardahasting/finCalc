package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Remaining Balance calculation.
 */
class RemainingBalanceTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicRemainingBalance() {
        // $200,000 loan at 0.5% monthly for 360 months, 60 payments made
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("60"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$186,109 (most of principal still remains)
        assertEquals(186109.0, result.value().doubleValue(), 100.0);
    }

    @Test
    void testNoPaymentsMade() {
        // Just originated loan
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("0"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $200,000 (full principal)
        assertEquals(200000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testHalfwayPaid() {
        // Halfway through 360-month loan
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("180"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: > $100,000 (interest-heavy early payments)
        assertTrue(result.value().doubleValue() > 100000);
        assertTrue(result.value().doubleValue() < 200000);
    }

    @Test
    void testNearlyPaidOff() {
        // 350 of 360 payments made
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("350"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: small balance remaining
        assertTrue(result.value().doubleValue() < 20000);
        assertTrue(result.value().doubleValue() > 0);
    }

    @Test
    void testFullyPaid() {
        // All payments made
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("360"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$0 (loan paid off)
        assertEquals(0.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testShortTermLoan() {
        // $50,000 loan, 1% monthly, 60 months, 12 payments made
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("0.01"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("12"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: significant balance remaining
        assertTrue(result.value().doubleValue() > 40000);
        assertTrue(result.value().doubleValue() < 50000);
    }

    @Test
    void testLowInterestRate() {
        // $100,000 loan, 0.2% monthly, 360 months, 60 payments made
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0.002"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("60"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: balance lower than high-rate scenario
        assertTrue(result.value().doubleValue() < 100000);
        assertTrue(result.value().doubleValue() > 0);
    }

    @Test
    void testZeroRate() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("60"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate must be positive"));
    }

    @Test
    void testNegativeRate() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("-0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("60"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate must be positive"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }

    @Test
    void testPaymentsExceedPeriods() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("400"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("payments made must be between 0 and total periods"));
    }

    @Test
    void testNegativePayments() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        stack.push(BigNumber.of("-60"));

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("payments made must be between 0 and total periods"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));
        // Missing payments made

        RemainingBalance.INSTANCE.execute(stack);

        assertEquals(4, stack.size()); // 3 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
