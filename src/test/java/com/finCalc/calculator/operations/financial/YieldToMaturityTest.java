package com.finCalc.calculator.operations.financial;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Yield to Maturity (YTM) calculation.
 */
class YieldToMaturityTest {

    private Stack<StackItem> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void testBasicYTM() {
        // $950 price, $1,000 face value, $60 coupon, 10 years
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.0662 (6.62% yield to maturity)
        assertEquals(0.0662, result.value().doubleValue(), 0.001);
    }

    @Test
    void testBondAtPar() {
        // $1,000 price, $1,000 face, $50 coupon, 10 years
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: 0.05 (5% - same as coupon rate when at par)
        assertEquals(0.05, result.value().doubleValue(), 0.001);
    }

    @Test
    void testBondAtDiscount() {
        // $900 price, $1,000 face, $40 coupon, 5 years
        stack.push(BigNumber.of("900"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("40"));
        stack.push(BigNumber.of("5"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: > coupon rate (capital gain adds to yield)
        assertTrue(result.value().doubleValue() > 0.04);
    }

    @Test
    void testBondAtPremium() {
        // $1,100 price, $1,000 face, $80 coupon, 10 years
        stack.push(BigNumber.of("1100"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("80"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: < coupon rate (capital loss reduces yield)
        assertTrue(result.value().doubleValue() < 0.08);
    }

    @Test
    void testZeroCoupon() {
        // Zero coupon bond: $600 price, $1,000 face, $0 coupon, 10 years
        stack.push(BigNumber.of("600"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: positive yield from discount only
        // (0 + (1000-600)/10) / ((1000+600)/2) = 40/800 = 0.05
        assertEquals(0.05, result.value().doubleValue(), 0.001);
    }

    @Test
    void testShortMaturity() {
        // 1 year to maturity
        stack.push(BigNumber.of("980"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("50"));
        stack.push(BigNumber.of("1"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: (50 + (1000-980)/1) / ((1000+980)/2) = 70/990 = 0.0707
        assertEquals(0.0707, result.value().doubleValue(), 0.001);
    }

    @Test
    void testLongMaturity() {
        // 30 years to maturity
        stack.push(BigNumber.of("800"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("40"));
        stack.push(BigNumber.of("30"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: (40 + (1000-800)/30) / ((1000+800)/2)
        // = (40 + 6.67) / 900 = 0.0519
        assertEquals(0.0519, result.value().doubleValue(), 0.001);
    }

    @Test
    void testDeepDiscount() {
        // $500 price, $1,000 face, $20 coupon, 15 years
        stack.push(BigNumber.of("500"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("20"));
        stack.push(BigNumber.of("15"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        BigNumber result = (BigNumber) stack.peek();
        // Expected: high yield due to large discount
        assertTrue(result.value().doubleValue() > 0.05);
    }

    @Test
    void testZeroCurrentPrice() {
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("current price must be positive"));
    }

    @Test
    void testZeroFaceValue() {
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("0"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("face value must be positive"));
    }

    @Test
    void testNegativeCoupon() {
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("-60"));
        stack.push(BigNumber.of("10"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("annual coupon cannot be negative"));
    }

    @Test
    void testZeroYearsToMaturity() {
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("60"));
        stack.push(BigNumber.of("0"));

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("years to maturity must be positive"));
    }

    @Test
    void testInsufficientOperands() {
        stack.push(BigNumber.of("950"));
        stack.push(BigNumber.of("1000"));
        stack.push(BigNumber.of("60"));
        // Missing years to maturity

        YieldToMaturity.INSTANCE.execute(stack);

        assertEquals(4, stack.size()); // 3 operands + 1 error
        assertTrue(stack.peek() instanceof Error);
        Error error = (Error) stack.peek();
        assertTrue(error.message().contains("requires"));
    }
}
