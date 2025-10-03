package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Total Interest Paid calculation.
 */
class TotalInterestPaidTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicTotalInterest() {
        // $200,000 loan, $1,199.10 monthly payment, 360 months
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("360"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: ~$231,676 total interest
        // (1199.10 * 360) - 200000 = 431676 - 200000 = 231676
        assertEquals(231676.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testShortTermLoan() {
        // $50,000 loan, $4,500 monthly payment, 12 months
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("4500"));
        stack.push(BigNumber.of("12"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $4,000 total interest
        // (4500 * 12) - 50000 = 54000 - 50000 = 4000
        assertEquals(4000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testLongTermMortgage() {
        // $300,000 loan, $1,610 monthly payment, 360 months
        stack.push(BigNumber.of("300000"));
        stack.push(BigNumber.of("1610"));
        stack.push(BigNumber.of("360"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $279,600 total interest
        // (1610 * 360) - 300000 = 579600 - 300000 = 279600
        assertEquals(279600.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testCarLoan() {
        // $30,000 car loan, $550 monthly, 60 months
        stack.push(BigNumber.of("30000"));
        stack.push(BigNumber.of("550"));
        stack.push(BigNumber.of("60"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $3,000 total interest
        // (550 * 60) - 30000 = 33000 - 30000 = 3000
        assertEquals(3000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testSmallLoan() {
        // $10,000 loan, $200 monthly, 60 months
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("200"));
        stack.push(BigNumber.of("60"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $2,000 total interest
        // (200 * 60) - 10000 = 12000 - 10000 = 2000
        assertEquals(2000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testHighInterestLoan() {
        // $20,000 loan, $500 monthly, 60 months (high interest)
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("500"));
        stack.push(BigNumber.of("60"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $10,000 total interest
        // (500 * 60) - 20000 = 30000 - 20000 = 10000
        assertEquals(10000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testFifteenYearMortgage() {
        // $200,000 loan, $1,400 monthly, 180 months (15-year)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1400"));
        stack.push(BigNumber.of("180"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $52,000 total interest
        // (1400 * 180) - 200000 = 252000 - 200000 = 52000
        assertEquals(52000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testLargeLoan() {
        // $500,000 loan, $3,000 monthly, 360 months
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("3000"));
        stack.push(BigNumber.of("360"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $580,000 total interest
        // (3000 * 360) - 500000 = 1080000 - 500000 = 580000
        assertEquals(580000.0, result.value().doubleValue(), 1.0);
    }

    @Test
    void testZeroPV() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("360"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("present value must be positive"));
    }

    @Test
    void testZeroPayment() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("360"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("payment must be positive"));
    }

    @Test
    void testZeroPeriods() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1199.10"));
        stack.push(BigNumber.of("0"));

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("number of periods must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("1199.10"));
        // Missing periods

        TotalInterestPaid.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // 2 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
