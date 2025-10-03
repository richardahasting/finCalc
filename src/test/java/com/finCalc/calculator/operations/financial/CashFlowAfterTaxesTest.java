package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cash Flow After Taxes (CFAT) calculation.
 */
class CashFlowAfterTaxesTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicCFAT() {
        // $15,000 cash flow before taxes, $3,000 tax liability
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("3000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $12,000
        assertEquals(12000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testHighTaxLiability() {
        // $20,000 CFBT, $8,000 tax liability
        stack.push(BigNumber.of("20000"));
        stack.push(BigNumber.of("8000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $12,000
        assertEquals(12000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLowTaxLiability() {
        // $10,000 CFBT, $1,000 tax liability
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("1000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $9,000
        assertEquals(9000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroTax() {
        // No tax liability
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("0"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $15,000 (same as before taxes)
        assertEquals(15000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeCFBT() {
        // Operating at loss before taxes
        stack.push(BigNumber.of("-5000"));
        stack.push(BigNumber.of("1000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -$6,000 (loss increases)
        assertEquals(-6000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testNegativeTaxLiability() {
        // Tax credit/refund scenario
        stack.push(BigNumber.of("15000"));
        stack.push(BigNumber.of("-2000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $17,000 (tax benefit increases cash flow)
        assertEquals(17000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testTaxExceedsCFBT() {
        // Tax liability greater than cash flow
        stack.push(BigNumber.of("10000"));
        stack.push(BigNumber.of("12000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -$2,000 (negative after-tax cash flow)
        assertEquals(-2000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testZeroCFBT() {
        // Break-even before taxes
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("1000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: -$1,000
        assertEquals(-1000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testLargeValues() {
        // $500,000 CFBT, $150,000 tax liability
        stack.push(BigNumber.of("500000"));
        stack.push(BigNumber.of("150000"));

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: $350,000
        assertEquals(350000.0, result.value().doubleValue(), 0.01);
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("15000"));
        // Missing tax liability

        CashFlowAfterTaxes.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // 1 operand + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
