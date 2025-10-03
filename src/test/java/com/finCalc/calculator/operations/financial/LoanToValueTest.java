package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Loan-to-Value (LTV) Ratio calculation.
 */
class LoanToValueTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicLTV() {
        // $200,000 property, $160,000 loan
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("160000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.80 (80% loan-to-value)
        assertEquals(0.80, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testHighLTV() {
        // $200,000 property, $190,000 loan (95% LTV - risky)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("190000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.95 (95% LTV)
        assertEquals(0.95, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testLowLTV() {
        // $300,000 property, $150,000 loan (50% LTV - low risk)
        stack.push(BigNumber.of("300000"));
        stack.push(BigNumber.of("150000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.50 (50% LTV)
        assertEquals(0.50, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroLoan() {
        // Cash purchase, no loan
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("0"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (0% LTV - no loan)
        assertEquals(0.0, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testConventionalLTV() {
        // $250,000 property, $200,000 loan (80% conventional)
        stack.push(BigNumber.of("250000"));
        stack.push(BigNumber.of("200000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.80 (80% LTV)
        assertEquals(0.80, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testZeroPropertyValue() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("160000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property value must be positive"));
    }

    @Test
    void testNegativePropertyValue() {
        stack.push(BigNumber.of("-200000"));
        stack.push(BigNumber.of("160000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("property value must be positive"));
    }

    @Test
    void testNegativeLoanAmount() {
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("-160000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("loan amount cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("200000"));
        // Missing loan amount

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }

    @Test
    void testLargeValues() {
        // $1M property, $800K loan
        stack.push(BigNumber.of("1000000"));
        stack.push(BigNumber.of("800000"));

        LoanToValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.80 (80% LTV)
        assertEquals(0.80, result.value().doubleValue(), 0.0001);
    }
}
