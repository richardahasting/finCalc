package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Present Value (PV) calculation.
 */
class PresentValueTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicMortgagePV() {
        // $1,199.10 payment at 0.5% monthly for 360 months
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("0.005"));
        stack.push(BigNumber.of("360"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$200,000
        assertEquals(200000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testZeroInterestRate() {
        // $1,000 payment at 0% for 12 months
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("12"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $12,000
        assertEquals(12000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallLoanPV() {
        // $862.74 payment at 1% monthly for 6 months
        stack.push(BigNumber.of("862.74"));
        stack.push(BigNumber.of("0.01"));
        stack.push(BigNumber.of("6"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$5,000
        assertEquals(5000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testAnnuityPV() {
        // $10,000 annual payment at 6% for 20 years
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("0.06"));
        stack.push(BigNumber.of("20"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$114,699.21
        assertEquals(114699.21, result.value().doubleValue(), 0.50);
    }

    @Test
    void testHighRatePV() {
        // $500 payment at 2% for 24 periods
        stack.push(BigNumber.of("500"));
        stack.push(BigNumber.of("0.02"));
        stack.push(BigNumber.of("24"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$9,456.96
        assertEquals(9456.96, result.value().doubleValue(), 0.50);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        // Missing n

        PresentValue.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testNegativeRate() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("-0.01"));
        stack.push(BigNumber.of("12"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("interest rate cannot be negative"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("0"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }

    @Test
    void testNegativePeriods() {
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0.05"));
        stack.push(BigNumber.of("-12"));

        PresentValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }
}
