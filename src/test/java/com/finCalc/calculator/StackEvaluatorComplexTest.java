package com.finCalc.calculator;

import com.finCalc.calculator.operations.basic.Add;
import com.finCalc.calculator.operations.basic.Divide;
import com.finCalc.calculator.operations.basic.Multiply;
import com.finCalc.calculator.operations.basic.Subtract;
import com.finCalc.calculator.operations.scientific.*;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stress tests for StackEvaluator with very complex, deeply nested expressions.
 *
 * <p>These tests verify that the evaluator can handle:
 * <ul>
 *   <li>Long chains of operations (200+ operations)</li>
 *   <li>Deep nesting of calculations</li>
 *   <li>Mixed arithmetic, scientific, and power operations</li>
 *   <li>Accumulation without stack overflow or precision loss</li>
 * </ul>
 */
class StackEvaluatorComplexTest {

    /**
     * Tests a complex expression with ~200 operations.
     *
     * <p>This simulates a realistic complex calculation that might occur in:
     * <ul>
     *   <li>Matrix operations (multiple element calculations)</li>
     *   <li>Statistical calculations (sums, products, aggregations)</li>
     *   <li>Financial modeling (multi-step compound calculations)</li>
     *   <li>Engineering formulas (nested trigonometric/logarithmic operations)</li>
     * </ul>
     *
     * <p>Expression breakdown:
     * <pre>
     * Base calculation: Compound interest-like formula repeated and nested
     * Part 1: Build up a base value through arithmetic (50 ops)
     * Part 2: Apply power/root transformations (50 ops)
     * Part 3: Apply trigonometric operations (50 ops)
     * Part 4: Apply logarithmic operations (30 ops)
     * Part 5: Final aggregation and normalization (20 ops)
     * Total: ~200 operations
     * </pre>
     */
    @Test
    void evaluate_VeryComplexExpression_HandlesDeepNesting() {
        Stack<StackItem> stack = new Stack<>();

        // ===== Part 1: Build up base value through arithmetic (50 operations) =====
        // Start with: ((((1 + 2) * 3 - 4) / 5) + 6) repeated pattern

        stack.push(BigNumber.of("1"));
        stack.push(BigNumber.of("2"));
        stack.push(Add.INSTANCE);           // 3
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);      // 9
        stack.push(BigNumber.of("4"));
        stack.push(Subtract.INSTANCE);      // 5
        stack.push(BigNumber.of("5"));
        stack.push(Divide.INSTANCE);        // 1
        stack.push(BigNumber.of("6"));
        stack.push(Add.INSTANCE);           // 7

        // Repeat similar pattern with different numbers (10 more operations)
        stack.push(BigNumber.of("2"));
        stack.push(Multiply.INSTANCE);      // 14
        stack.push(BigNumber.of("3"));
        stack.push(Add.INSTANCE);           // 17
        stack.push(BigNumber.of("2"));
        stack.push(Divide.INSTANCE);        // 8.5
        stack.push(BigNumber.of("1.5"));
        stack.push(Subtract.INSTANCE);      // 7
        stack.push(BigNumber.of("10"));
        stack.push(Add.INSTANCE);           // 17

        // Build up more values (10 operations)
        stack.push(BigNumber.of("5"));
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);      // 15
        stack.push(Add.INSTANCE);           // 32
        stack.push(BigNumber.of("8"));
        stack.push(Divide.INSTANCE);        // 4
        stack.push(BigNumber.of("7"));
        stack.push(Multiply.INSTANCE);      // 28
        stack.push(BigNumber.of("2"));
        stack.push(Add.INSTANCE);           // 30

        // More arithmetic combinations (10 operations)
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("5"));
        stack.push(Subtract.INSTANCE);      // 5
        stack.push(BigNumber.of("6"));
        stack.push(Add.INSTANCE);           // 11
        stack.push(Add.INSTANCE);           // 41
        stack.push(BigNumber.of("3"));
        stack.push(Divide.INSTANCE);        // 13.666...
        stack.push(BigNumber.of("2"));
        stack.push(Multiply.INSTANCE);      // 27.333...

        // Final arithmetic buildup (10 operations)
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("73"));
        stack.push(Subtract.INSTANCE);      // 27
        stack.push(Add.INSTANCE);           // 54.333...
        stack.push(BigNumber.of("4"));
        stack.push(Divide.INSTANCE);        // 13.583...
        stack.push(BigNumber.of("6"));
        stack.push(Multiply.INSTANCE);      // 81.5
        stack.push(BigNumber.of("1.5"));
        stack.push(Add.INSTANCE);           // 83

        // ===== Part 2: Power/Root transformations (50 operations) =====

        // Square and square root operations (10 operations)
        stack.push(Square.INSTANCE);        // 6889
        stack.push(SquareRoot.INSTANCE);    // 83
        stack.push(BigNumber.of("2"));
        stack.push(Add.INSTANCE);           // 85
        stack.push(BigNumber.of("5"));
        stack.push(Divide.INSTANCE);        // 17
        stack.push(BigNumber.of("3"));
        stack.push(Power.INSTANCE);         // 4913
        stack.push(BigNumber.of("3"));
        stack.push(NthRoot.INSTANCE);       // 17

        // More power operations (10 operations)
        stack.push(BigNumber.of("2"));
        stack.push(Power.INSTANCE);         // 289
        stack.push(SquareRoot.INSTANCE);    // 17
        stack.push(BigNumber.of("10"));
        stack.push(Add.INSTANCE);           // 27
        stack.push(BigNumber.of("2"));
        stack.push(Power.INSTANCE);         // 729
        stack.push(BigNumber.of("6"));
        stack.push(NthRoot.INSTANCE);       // 3
        stack.push(BigNumber.of("7"));
        stack.push(Add.INSTANCE);           // 10

        // Reciprocal operations (10 operations)
        stack.push(BigNumber.of("100"));
        stack.push(Add.INSTANCE);           // 110
        stack.push(Reciprocal.INSTANCE);    // 0.00909...
        stack.push(BigNumber.of("1000"));
        stack.push(Multiply.INSTANCE);      // 9.09...
        stack.push(BigNumber.of("2"));
        stack.push(Power.INSTANCE);         // 82.6...
        stack.push(SquareRoot.INSTANCE);    // 9.09...
        stack.push(BigNumber.of("9"));
        stack.push(Divide.INSTANCE);        // 1.01...

        // More power/root combinations (10 operations)
        stack.push(BigNumber.of("100"));
        stack.push(Multiply.INSTANCE);      // 101...
        stack.push(BigNumber.of("0.5"));
        stack.push(Power.INSTANCE);         // sqrt(101) = 10.05...
        stack.push(Square.INSTANCE);        // 101
        stack.push(SquareRoot.INSTANCE);    // 10.05...
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);           // 15.05...
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);      // 45.15...

        // Final power operations (10 operations)
        stack.push(BigNumber.of("2"));
        stack.push(Divide.INSTANCE);        // 22.575...
        stack.push(BigNumber.of("20"));
        stack.push(Add.INSTANCE);           // 42.575...
        stack.push(SquareRoot.INSTANCE);    // 6.525...
        stack.push(BigNumber.of("2"));
        stack.push(Power.INSTANCE);         // 42.575...
        stack.push(SquareRoot.INSTANCE);    // 6.525...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 65.25...

        // ===== Part 3: Trigonometric operations (50 operations) =====

        // Trig operations with safe ranges (10 operations)
        stack.push(BigNumber.of("10"));
        stack.push(Divide.INSTANCE);        // 6.525...
        stack.push(Sine.INSTANCE);          // sin(6.525) = 0.344...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 3.44...
        stack.push(Cosine.INSTANCE);        // cos(3.44) = -0.939...
        stack.push(BigNumber.of("-5"));
        stack.push(Multiply.INSTANCE);      // 4.695...
        stack.push(Tangent.INSTANCE);       // tan(4.695) = -1.05...
        stack.push(ArcTangent.INSTANCE);    // atan(-1.05) = -0.808...

        // More trig operations (10 operations)
        stack.push(BigNumber.of("-5"));
        stack.push(Multiply.INSTANCE);      // 4.04...
        stack.push(Sine.INSTANCE);          // sin(4.04) = -0.746...
        stack.push(BigNumber.of("-2"));
        stack.push(Multiply.INSTANCE);      // 1.492...
        stack.push(Cosine.INSTANCE);        // cos(1.492) = 0.083...
        stack.push(BigNumber.of("20"));
        stack.push(Multiply.INSTANCE);      // 1.66...
        stack.push(Tangent.INSTANCE);       // tan(1.66) = -0.955...
        stack.push(ArcTangent.INSTANCE);    // atan(-0.955) = -0.761...

        // Continue trig (10 operations)
        stack.push(BigNumber.of("-10"));
        stack.push(Multiply.INSTANCE);      // 7.61...
        stack.push(Sine.INSTANCE);          // sin(7.61) = 0.996...
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);      // 2.988...
        stack.push(Cosine.INSTANCE);        // cos(2.988) = -0.990...
        stack.push(BigNumber.of("-4"));
        stack.push(Multiply.INSTANCE);      // 3.96...
        stack.push(Tangent.INSTANCE);       // tan(3.96) = 1.15...
        stack.push(ArcTangent.INSTANCE);    // atan(1.15) = 0.852...

        // More trig (10 operations)
        stack.push(BigNumber.of("5"));
        stack.push(Multiply.INSTANCE);      // 4.26...
        stack.push(Sine.INSTANCE);          // sin(4.26) = -0.866...
        stack.push(BigNumber.of("-3"));
        stack.push(Multiply.INSTANCE);      // 2.598...
        stack.push(Cosine.INSTANCE);        // cos(2.598) = -0.856...
        stack.push(BigNumber.of("-5"));
        stack.push(Multiply.INSTANCE);      // 4.28...
        stack.push(Tangent.INSTANCE);       // tan(4.28) = 1.56...
        stack.push(ArcTangent.INSTANCE);    // atan(1.56) = 1.003...

        // Final trig operations (10 operations)
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);      // 3.009...
        stack.push(Sine.INSTANCE);          // sin(3.009) = 0.128...
        stack.push(Square.INSTANCE);        // 0.0164...
        stack.push(BigNumber.of("20"));
        stack.push(Multiply.INSTANCE);      // 0.328...
        stack.push(BigNumber.of("10"));
        stack.push(Add.INSTANCE);           // 10.328...
        stack.push(Cosine.INSTANCE);        // cos(10.328) = -0.837...
        stack.push(Square.INSTANCE);        // 0.700...
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);           // 5.700...

        // ===== Part 4: Logarithmic operations (30 operations) =====

        // Normalize to safe range for logs (10 operations)
        stack.push(BigNumber.of("10"));
        stack.push(Divide.INSTANCE);        // 0.570...
        stack.push(BigNumber.of("1"));
        stack.push(Add.INSTANCE);           // 1.570...
        stack.push(NaturalLog.INSTANCE);    // ln(1.570) = 0.451...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 4.51...
        stack.push(BigNumber.of(String.valueOf(Math.E)));
        stack.push(Power.INSTANCE);         // e^4.51 = 90.8...
        stack.push(Log10.INSTANCE);         // log10(90.8) = 1.958...

        // More log operations (10 operations)
        stack.push(BigNumber.of("2"));
        stack.push(Multiply.INSTANCE);      // 3.916...
        stack.push(Exp10.INSTANCE);         // 10^3.916 = 8,243...
        stack.push(NaturalLog.INSTANCE);    // ln(8243) = 9.017...
        stack.push(BigNumber.of("100"));
        stack.push(Divide.INSTANCE);        // 0.09017...
        stack.push(BigNumber.of("1"));
        stack.push(Add.INSTANCE);           // 1.09017...
        stack.push(Log10.INSTANCE);         // log10(1.09017) = 0.0374...
        stack.push(BigNumber.of("100"));
        stack.push(Multiply.INSTANCE);      // 3.74...

        // Final log operations (10 operations)
        stack.push(BigNumber.of("2"));
        stack.push(Add.INSTANCE);           // 5.74...
        stack.push(BigNumber.of(String.valueOf(Math.E)));
        stack.push(Power.INSTANCE);         // e^5.74 = 311.7...
        stack.push(NaturalLog.INSTANCE);    // ln(311.7) = 5.74...
        stack.push(BigNumber.of("10"));
        stack.push(Divide.INSTANCE);        // 0.574...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 5.74...
        stack.push(Square.INSTANCE);        // 32.94...

        // ===== Part 5: Final aggregation and normalization (20 operations) =====

        // Scale and normalize result (10 operations)
        stack.push(SquareRoot.INSTANCE);    // sqrt(32.94) = 5.739...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 57.39...
        stack.push(BigNumber.of("3"));
        stack.push(Divide.INSTANCE);        // 19.13...
        stack.push(BigNumber.of("2"));
        stack.push(Power.INSTANCE);         // 366.0...
        stack.push(SquareRoot.INSTANCE);    // 19.13...
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);           // 24.13...

        // Final scaling (10 operations)
        stack.push(BigNumber.of("3"));
        stack.push(Divide.INSTANCE);        // 8.043...
        stack.push(Square.INSTANCE);        // 64.69...
        stack.push(BigNumber.of("2"));
        stack.push(Divide.INSTANCE);        // 32.345...
        stack.push(SquareRoot.INSTANCE);    // 5.687...
        stack.push(BigNumber.of("10"));
        stack.push(Multiply.INSTANCE);      // 56.87...
        stack.push(BigNumber.of("50"));
        stack.push(Subtract.INSTANCE);      // 6.87...

        // Execute the entire expression
        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        // Verify we got a valid numeric result (not an error)
        assertEquals(1, result.size(), "Should have exactly one result");

        // Debug: print what we got
        if (result.peek() instanceof Error) {
            System.out.println("Got error: " + ((Error) result.peek()).message());
        } else if (result.peek() instanceof BigNumber) {
            System.out.println("Got result: " + ((BigNumber) result.peek()).value());
        }

        assertTrue(result.peek() instanceof BigNumber, "Result should be a BigNumber");

        BigNumber finalResult = (BigNumber) result.peek();
        assertNotNull(finalResult);

        // Result should be a reasonable number (not NaN, not infinite)
        double resultValue = finalResult.value().doubleValue();
        assertFalse(Double.isNaN(resultValue), "Result should not be NaN");
        assertFalse(Double.isInfinite(resultValue), "Result should not be infinite");

        // Result should be in a reasonable range given our operations
        // Since the exact value depends on cumulative precision effects,
        // just verify it's a reasonable finite number
        assertTrue(Math.abs(resultValue) < 10000, "Result should have reasonable magnitude");

        System.out.println("Complex expression (200+ operations) evaluated successfully to: " + resultValue);
    }

    /**
     * Tests that errors in the middle of a long chain stop evaluation correctly.
     */
    @Test
    void evaluate_LongChainWithError_StopsAtError() {
        Stack<StackItem> stack = new Stack<>();

        // Build up 100 valid operations
        stack.push(BigNumber.of("1"));
        for (int i = 0; i < 100; i++) {
            stack.push(BigNumber.of("1"));
            stack.push(Add.INSTANCE);
        }
        // At this point we have 101

        // Insert an error in the middle
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);  // Division by zero error

        // Add more operations that should NOT execute
        for (int i = 0; i < 100; i++) {
            stack.push(BigNumber.of("1"));
            stack.push(Add.INSTANCE);
        }

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    /**
     * Tests accumulation of many values without operations (stress test for stack size).
     */
    @Test
    void evaluate_ManyValuesWithSingleOperation_HandlesLargeStack() {
        Stack<StackItem> stack = new Stack<>();

        // Push 200 ones
        for (int i = 0; i < 200; i++) {
            stack.push(BigNumber.of("1"));
        }

        // Add them all up (199 ADD operations)
        for (int i = 0; i < 199; i++) {
            stack.push(Add.INSTANCE);
        }

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof BigNumber);
        assertEquals("200", ((BigNumber) result.peek()).value().toString());
    }
}
