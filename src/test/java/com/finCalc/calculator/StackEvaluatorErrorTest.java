package com.finCalc.calculator;

import com.finCalc.calculator.operations.basic.*;
import com.finCalc.calculator.operations.scientific.*;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests error handling and propagation in the StackEvaluator.
 *
 * <p>Verifies that errors from various sources are properly handled:
 * <ul>
 *   <li>Insufficient operands</li>
 *   <li>Domain errors (division by zero, negative logarithms, etc.)</li>
 *   <li>Error propagation through evaluation chains</li>
 *   <li>Errors present in input stack</li>
 * </ul>
 */
class StackEvaluatorErrorTest {

    @Test
    void evaluate_DivisionByZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_ModuloByZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));
        stack.push(Modulo.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Modulo by zero", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_InsufficientOperandsForAdd_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        // Single operation: preserves context
        assertEquals(2, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("operand"));
    }

    @Test
    void evaluate_InsufficientOperandsForAbsoluteValue_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(AbsoluteValue.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        // Single operation: preserves context (empty in this case)
        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("operand"));
    }

    @Test
    void evaluate_NegativeLogarithm_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-5"));
        stack.push(NaturalLog.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("non-positive"));
    }

    @Test
    void evaluate_ZeroLogarithm_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));
        stack.push(Log10.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("non-positive"));
    }

    @Test
    void evaluate_NegativeSquareRoot_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-4"));
        stack.push(SquareRoot.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("negative"));
    }

    @Test
    void evaluate_ArcSineOutOfRange_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2"));
        stack.push(ArcSine.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("range"));
    }

    @Test
    void evaluate_ArcCosineOutOfRange_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-1.5"));
        stack.push(ArcCosine.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("range"));
    }

    @Test
    void evaluate_NthRootWithZeroIndex_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("8"));
        stack.push(BigNumber.of("0"));
        stack.push(NthRoot.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("zero"));
    }

    @Test
    void evaluate_EvenRootOfNegative_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-8"));
        stack.push(BigNumber.of("2"));
        stack.push(NthRoot.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("even root"));
    }

    @Test
    void evaluate_ReciprocalOfZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));
        stack.push(Reciprocal.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("zero"));
    }

    @Test
    void evaluate_ErrorInInputStack_ReturnsImmediately() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(new Error("Pre-existing error"));
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Pre-existing error", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_ErrorStopsChainEvaluation_DoesNotProcessRemaining() {
        // [10, 5, ADD, 0, DIV, 100, ADD] should stop at division by zero
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);      // 15
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);   // Error: division by zero
        stack.push(BigNumber.of("100"));
        stack.push(Add.INSTANCE);      // Should not execute

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_ComplexChainWithMidError_StopsAtError() {
        // Valid ops, then error, then more valid ops that shouldn't execute
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("4"));
        stack.push(Divide.INSTANCE);      // 25
        stack.push(SquareRoot.INSTANCE);  // 5
        stack.push(BigNumber.of("3"));
        stack.push(Multiply.INSTANCE);    // 15
        stack.push(BigNumber.of("-1"));
        stack.push(SquareRoot.INSTANCE);  // Error: negative square root
        stack.push(BigNumber.of("100"));
        stack.push(Add.INSTANCE);         // Should not execute

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        // Multiple operations: working stack is cleared, only error remains
        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("negative"));
    }

    @Test
    void evaluateToNumber_WithError_ReturnsNull() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);

        BigNumber result = StackEvaluator.evaluateToNumber(stack);

        assertNull(result);
    }

    @Test
    void evaluateForError_WithValidResult_ReturnsNull() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);

        Error error = StackEvaluator.evaluateForError(stack);

        assertNull(error);
    }

    @Test
    void evaluateForError_WithError_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));
        stack.push(Modulo.INSTANCE);

        Error error = StackEvaluator.evaluateForError(stack);

        assertNotNull(error);
        assertEquals("Modulo by zero", error.message());
    }

    @Test
    void evaluate_MultipleErrorConditions_ReturnsFirstError() {
        // This tests that we stop at the first error and don't continue
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);    // First error
        stack.push(BigNumber.of("0"));
        stack.push(Reciprocal.INSTANCE); // Would be second error if we continued

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_PowerResultingInInfinity_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("1000"));
        stack.push(Power.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("undefined or infinite"));
    }

    @Test
    void evaluate_VeryLargeExponentiation_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("999"));
        stack.push(BigNumber.of("999"));
        stack.push(Power.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertTrue(((Error) result.peek()).message().contains("infinite"));
    }
}
