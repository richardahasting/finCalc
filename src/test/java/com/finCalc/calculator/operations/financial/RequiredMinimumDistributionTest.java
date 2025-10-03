package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Required Minimum Distribution (RMD) calculation.
 */
class RequiredMinimumDistributionTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicRMD() {
        // $500,000 account balance, 25.6 distribution period (age 73)
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("25.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $19,531.25
        assertEquals(19531.25, result.value().doubleValue(), 0.01);
    }

    @Test
    void testAge75RMD() {
        // $750,000 balance, 24.6 distribution period (age 75)
        stack.push(BigNumber.of("750000"));
        stack.push(BigNumber.of("24.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $30,487.80
        assertEquals(30487.80, result.value().doubleValue(), 0.01);
    }

    @Test
    void testAge80RMD() {
        // $400,000 balance, 20.2 distribution period (age 80)
        stack.push(BigNumber.of("400000"));
        stack.push(BigNumber.of("20.2"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $19,801.98
        assertEquals(19801.98, result.value().doubleValue(), 0.01);
    }

    @Test
    void testAge90RMD() {
        // $200,000 balance, 12.2 distribution period (age 90)
        stack.push(BigNumber.of("200000"));
        stack.push(BigNumber.of("12.2"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $16,393.44
        assertEquals(16393.44, result.value().doubleValue(), 0.01);
    }

    @Test
    void testSmallBalance() {
        // $50,000 balance, 25.6 distribution period
        stack.push(BigNumber.of("50000"));
        stack.push(BigNumber.of("25.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $1,953.13
        assertEquals(1953.13, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeBalance() {
        // $2M balance, 27.4 distribution period (age 72)
        stack.push(BigNumber.of("2000000"));
        stack.push(BigNumber.of("27.4"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $72,992.70
        assertEquals(72992.70, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroBalance() {
        // Empty retirement account
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("25.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $0 (no RMD if no balance)
        assertEquals(0.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLongDistributionPeriod() {
        // Younger age, longer distribution period
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("30.0"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $16,666.67
        assertEquals(16666.67, result.value().doubleValue(), 0.01);
    }

    @Test
    void testShortDistributionPeriod() {
        // Advanced age, shorter distribution period
        stack.push(BigNumber.of("300000"));
        stack.push(BigNumber.of("10.0"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $30,000
        assertEquals(30000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeBalance() {
        stack.push(BigNumber.of("-500000"));
        stack.push(BigNumber.of("25.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("account balance cannot be negative"));
    }

    @Test
    void testZeroDistributionPeriod() {
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("0"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("distribution period must be positive"));
    }

    @Test
    void testNegativeDistributionPeriod() {
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("-25.6"));

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("distribution period must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("500000"));
        // Missing distribution period

        RequiredMinimumDistribution.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
