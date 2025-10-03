package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CoveredCallReturn calculation.
 */
class CoveredCallReturnTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCoveredCall() {
        // Buy stock at $50, sell $52 call for $1.50, 30 days
        // Return: ($1.50 + $2) / $50 = 7% over 30 days
        // Annualized: 7% × (365/30) = 85.17%
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("1.50"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.8517 (85.17%)
        assertEquals(0.8517, result.value().doubleValue(), 0.001);
    }

    @Test
    void testAtTheMoneyCall() {
        // Buy stock at $100, sell $100 call for $2, 14 days
        // No capital gain, only premium
        // Return: $2 / $100 = 2% over 14 days
        // Annualized: 2% × (365/14) = 52.14%
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("2"));
        stack.push(BigNumber.of("14"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.5214 (52.14%)
        assertEquals(0.5214, result.value().doubleValue(), 0.001);
    }

    @Test
    void testDeepInTheMoneyCall() {
        // Stock at $50, sell $45 call (ITM) for $6, 7 days
        // Capital loss: -$5, Premium: $6, Net: $1
        // Return: $1 / $50 = 2% over 7 days
        // Annualized: 2% × (365/7) = 104.29%
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("45"));
        stack.push(BigNumber.of("6"));
        stack.push(BigNumber.of("7"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.0429 (104.29%)
        assertEquals(1.0429, result.value().doubleValue(), 0.001);
    }

    @Test
    void testFarOutOfTheMoneyCall() {
        // Stock at $50, sell $60 call (OTM) for $0.25, 45 days
        // Large potential gain: $10, Small premium: $0.25
        // Return: ($0.25 + $10) / $50 = 20.5% over 45 days
        // Annualized: 20.5% × (365/45) = 166.28%
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("0.25"));
        stack.push(BigNumber.of("45"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 1.6628 (166.28%)
        assertEquals(1.6628, result.value().doubleValue(), 0.001);
    }

    @Test
    void testWeeklyCoveredCall() {
        // Weekly option: 7 days to expiration
        // Stock at $75, sell $77 call for $1.25
        // Return: ($1.25 + $2) / $75 = 4.33% over 7 days
        // Annualized: 4.33% × (365/7) = 225.83%
        stack.push(BigNumber.of("75"));
        stack.push(BigNumber.of("77"));
        stack.push(BigNumber.of("1.25"));
        stack.push(BigNumber.of("7"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 2.2583 (225.83%)
        assertEquals(2.2583, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowPremiumLongDuration() {
        // Stock at $25, sell $26 call for $0.15, 60 days
        // Return: ($0.15 + $1) / $25 = 4.6% over 60 days
        // Annualized: 4.6% × (365/60) = 27.96%
        stack.push(BigNumber.of("25"));
        stack.push(BigNumber.of("26"));
        stack.push(BigNumber.of("0.15"));
        stack.push(BigNumber.of("60"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.2796 (27.96%)
        assertEquals(0.2796, result.value().doubleValue(), 0.001);
    }

    @Test
    void testZeroPremium() {
        // Edge case: selling call for no premium (unlikely but possible)
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Return only from capital gain: $2/$50 = 4%, annualized = 48.67%
        assertEquals(0.4867, result.value().doubleValue(), 0.001);
    }

    @Test
    void testZeroStockCost() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("stock cost must be positive"));
    }

    @Test
    void testNegativeStockCost() {
        stack.push(BigNumber.of("-50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
    }

    @Test
    void testZeroStrikePrice() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("strike price must be positive"));
    }

    @Test
    void testNegativePremium() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("-1"));
        stack.push(BigNumber.of("30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("premium cannot be negative"));
    }

    @Test
    void testZeroDaysToExpiration() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("0"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("days to expiration must be positive"));
    }

    @Test
    void testNegativeDaysToExpiration() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("-30"));

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("52"));
        stack.push(BigNumber.of("1"));
        // Missing fourth operand

        CoveredCallReturn.INSTANCE.execute(stack);

        assertEquals(4, stack.size()); // Original 3 + error
        assertTrue(stack.peek() instanceof Error);
    }
}
