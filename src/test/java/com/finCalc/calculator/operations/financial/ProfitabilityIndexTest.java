package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Profitability Index calculation.
 */
class ProfitabilityIndexTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicProfitabilityIndex() {
        // $100,000 investment, $120,000 PV of future cash flows
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("120000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.20 (PI > 1.0, accept project)
        assertEquals(1.20, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighPI() {
        // $50,000 investment, $100,000 PV of future cash flows
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("100000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 2.0 (very profitable)
        assertEquals(2.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowPI() {
        // $100,000 investment, $105,000 PV of future cash flows
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("105000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.05 (marginally profitable)
        assertEquals(1.05, result.value().doubleValue(), 0.01);
    }

    @Test
    void testBreakEvenPI() {
        // $100,000 investment, $100,000 PV of future cash flows
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("100000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0 (break even, indifferent)
        assertEquals(1.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testUnprofitablePI() {
        // $100,000 investment, $80,000 PV of future cash flows
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("80000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.8 (PI < 1.0, reject project)
        assertEquals(0.8, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroPVCashFlows() {
        // No future cash flows
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("0"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0 (no return)
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallInvestment() {
        // $10,000 investment, $15,000 PV
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("15000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.5
        assertEquals(1.5, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeInvestment() {
        // $5M investment, $6.5M PV
        stack.push(BigNumber.of("5000000"));
        stack.push(BigNumber.of("6500000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.3
        assertEquals(1.3, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroInvestment() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("120000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("initial investment must be positive"));
    }

    @Test
    void testNegativeInvestment() {
        stack.push(BigNumber.of("-100000"));
        stack.push(BigNumber.of("120000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("initial investment must be positive"));
    }

    @Test
    void testNegativePVCashFlows() {
        stack.push(BigNumber.of("100000"));
        stack.push(BigNumber.of("-120000"));

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("PV of future cash flows cannot be negative"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("100000"));
        // Missing PV of future cash flows

        ProfitabilityIndex.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
