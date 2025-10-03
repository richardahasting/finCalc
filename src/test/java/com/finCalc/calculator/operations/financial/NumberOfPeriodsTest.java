package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Number of Periods (NPER) calculation.
 */
class NumberOfPeriodsTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicMortgageNPER() {
        // $200,000 loan, $1,199.10 payment, 0.5% monthly rate
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("0.005"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~360 months
        assertEquals(360.0, result.value().doubleValue(), 0.1);
    }

    @Test
    void testZeroInterestRate() {
        // $12,000 loan, $1,000 payment, 0% rate
        stack.push(BigNumber.of("12000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 12 months
        assertEquals(12.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallLoanNPER() {
        // $5,000 loan, $861.33 payment, 1% monthly
        stack.push(BigNumber.of("5000"));
        stack.push(BigNumber.of("861.33"));
        stack.push(BigNumber.of("0.01"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~6 months
        assertEquals(6.0, result.value().doubleValue(), 0.1);
    }

    @Test
    void testCarLoanNPER() {
        // $30,000 loan, $649.32 payment, 0.009 monthly (0.9%)
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("649.32"));
        stack.push(BigNumber.of("0.009"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~60 months
        assertEquals(60.0, result.value().doubleValue(), 0.5);
    }

    @Test
    void testHighPaymentNPER() {
        // $10,000 loan, $2,000 payment, 1% monthly
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("2000"));
        stack.push(BigNumber.of("0.01"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~5.15 months
        assertEquals(5.15, result.value().doubleValue(), 0.05);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1000"));
        // Missing rate

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testNegativeRate() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("-0.005"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate cannot be negative"));
    }

    @Test
    void testPaymentTooSmall() {
        // Payment less than interest - loan would never be repaid
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("400")); // Min payment would be 500 at 0.5%
        stack.push(BigNumber.of("0.005"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("payment too small"));
    }

    @Test
    void testZeroPV() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.005"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("PV and PMT must be positive"));
    }

    @Test
    void testZeroPMT() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0.005"));

        NumberOfPeriods.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("PV and PMT must be positive"));
    }
}
