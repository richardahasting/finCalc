package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnnualizedOptionReturn calculation.
 */
class AnnualizedOptionReturnTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testUNGPutExample() {
        // Example: Sell $12.50 put on UNG for $0.26 premium, 10 days to expiration
        // Expected: 75.92% annualized return
        stack.push(BigNumber.of("12.50"));
        stack.push(BigNumber.of("0.26"));
        stack.push(BigNumber.of("10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Should be approximately 0.7592 (75.92%)
        assertEquals(0.7592, result.value().doubleValue(), 0.0001);
    }

    @Test
    void testCoveredCallExample() {
        // Example: Sell $50 covered call for $1.50 premium, 30 days to expiration
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1.50"));
        stack.push(BigNumber.of("30"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Should be approximately 0.365 (36.5%)
        assertEquals(0.365, result.value().doubleValue(), 0.001);
    }

    @Test
    void testHighPremiumShortDuration() {
        // High premium, short duration = very high annualized return
        // $100 strike, $5 premium, 7 days
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("5"));
        stack.push(BigNumber.of("7"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // (5/7) × 365 / 100 = 2.607 (260.7%)
        assertEquals(2.607, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowPremiumLongDuration() {
        // Low premium, long duration = lower annualized return
        // $25 strike, $0.10 premium, 45 days
        stack.push(BigNumber.of("25"));
        stack.push(BigNumber.of("0.10"));
        stack.push(BigNumber.of("45"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // (0.10/45) × 365 / 25 = 0.0324 (3.24%)
        assertEquals(0.0324, result.value().doubleValue(), 0.001);
    }

    @Test
    void testWeeklyOption() {
        // Weekly option: 7 days to expiration
        stack.push(BigNumber.of("150"));
        stack.push(BigNumber.of("2"));
        stack.push(BigNumber.of("7"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // (2/7) × 365 / 150 = 0.695 (69.5%)
        assertEquals(0.695, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroStrikePrice() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("0.50"));
        stack.push(BigNumber.of("10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("strike price must be positive"));
    }

    @Test
    void testNegativeStrikePrice() {
        stack.push(BigNumber.of("-50"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
    }

    @Test
    void testZeroPremium() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("premium must be positive"));
    }

    @Test
    void testNegativePremium() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("-1"));
        stack.push(BigNumber.of("10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
    }

    @Test
    void testZeroDaysToExpiration() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("0"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("days to expiration must be positive"));
    }

    @Test
    void testNegativeDaysToExpiration() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("-10"));

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1"));
        // Missing third operand

        AnnualizedOptionReturn.INSTANCE.execute(stack);

        assertEquals(3, stack.size()); // Original 2 + error
        assertTrue(stack.peek() instanceof Error);
    }
}
