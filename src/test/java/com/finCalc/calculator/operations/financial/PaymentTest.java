package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Payment (PMT) calculation.
 */
class PaymentTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicMortgagePayment() {
        // $200,000 loan at 0.5% monthly rate for 360 months
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$1199.10
        assertEquals(1199.10, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroInterestRate() {
        // $12,000 loan at 0% for 12 months
        stack.push(BigNumber.of("12000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("12"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $1000 per month
        assertEquals(1000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallLoan() {
        // $5,000 loan at 1% monthly for 6 months
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("0.01"));
        stack.push(BigNumber.of("6"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$862.74
        assertEquals(862.74, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeLoan() {
        // $1,000,000 loan at 0.004 monthly rate for 240 months
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("0.004"));
        stack.push(BigNumber.of("240"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$6489.57
        assertEquals(6489.57, result.value().doubleValue(), 0.01);
    }

    @Test
    void testShortTermHighRate() {
        // $1,000 loan at 5% monthly for 12 months
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("12"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$112.83
        assertEquals(112.83, result.value().doubleValue(), 0.01);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0.005"));
        // Missing n

        Payment.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testNegativeRate() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-0.01"));
        stack.push(BigNumber.of("360"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate cannot be negative"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("0"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }

    @Test
    void testNegativePeriods() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("-12"));

        Payment.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }
}
