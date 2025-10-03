package com.finCalc.calculator;

import com.finCalc.calculator.operations.basic.Add;
import com.finCalc.calculator.operations.basic.Divide;
import com.finCalc.calculator.operations.basic.Multiply;
import com.finCalc.calculator.operations.scientific.Power;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class StackEvaluatorTest {

    @Test
    void evaluate_SimpleAddition_ReturnsSum() {
        // [3, 4, ADD] → [7]
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));
        stack.push(Add.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof BigNumber);
        assertEquals("7", ((BigNumber) result.peek()).value().toString());
    }

    @Test
    void evaluate_ChainedOperations_ReturnsCorrectResult() {
        // [3, 4, ADD, 5, MULTIPLY] → [35]
        // (3 + 4) * 5 = 35
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));
        stack.push(Add.INSTANCE);
        stack.push(BigNumber.of("5"));
        stack.push(Multiply.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof BigNumber);
        assertEquals("35", ((BigNumber) result.peek()).value().toString());
    }

    @Test
    void evaluate_ComplexExpression_ReturnsCorrectResult() {
        // [3, 4, ADD, 3, POW, 4.3, DIVIDE]
        // ((3 + 4) ^ 3) / 4.3 = 343 / 4.3 ≈ 79.767
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));
        stack.push(Add.INSTANCE);
        stack.push(BigNumber.of("3"));
        stack.push(Power.INSTANCE);
        stack.push(BigNumber.of("4.3"));
        stack.push(Divide.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof BigNumber);
        assertEquals(79.767, ((BigNumber) result.peek()).value().doubleValue(), 0.01);
    }

    @Test
    void evaluate_WithError_ReturnsError() {
        // [3, 0, DIVIDE] → [Error]
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    @Test
    void evaluate_ErrorStopsEvaluation_DoesNotProcessRemaining() {
        // [3, 0, DIVIDE, 5, ADD] → [Error]
        // Should stop at division by zero, not try to add 5
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
    }

    @Test
    void evaluateToNumber_WithValidResult_ReturnsNumber() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));
        stack.push(Add.INSTANCE);

        BigNumber result = StackEvaluator.evaluateToNumber(stack);

        assertNotNull(result);
        assertEquals("7", result.value().toString());
    }

    @Test
    void evaluateToNumber_WithError_ReturnsNull() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);

        BigNumber result = StackEvaluator.evaluateToNumber(stack);

        assertNull(result);
    }

    @Test
    void evaluateForError_WithError_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);

        Error error = StackEvaluator.evaluateForError(stack);

        assertNotNull(error);
        assertEquals("Division by zero", error.message());
    }

    @Test
    void evaluateForError_WithValidResult_ReturnsNull() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));
        stack.push(Add.INSTANCE);

        Error error = StackEvaluator.evaluateForError(stack);

        assertNull(error);
    }
}
