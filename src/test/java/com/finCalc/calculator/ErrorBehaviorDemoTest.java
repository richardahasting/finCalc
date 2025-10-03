package com.finCalc.calculator;

import com.finCalc.calculator.operations.basic.Add;
import com.finCalc.calculator.operations.basic.Divide;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates error handling behavior:
 * - Single operation: preserves stack context
 * - Multiple operations: clears stack, returns only error
 */
class ErrorBehaviorDemoTest {

    @Test
    void errorInInput_ClearsStack() {
        // When error is IN the input stack (always clears)
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("20"));
        stack.push(new Error("Pre-existing error"));
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        System.out.println("Error in input - Result stack size: " + result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  [" + i + "]: " + result.get(i));
        }

        // Working stack is cleared, only the error remains
        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
    }

    @Test
    void singleOperation_ErrorKeepsContext() {
        // Single operation: preserves working stack context
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("20"));
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE); // This produces an error

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        System.out.println("Single operation error - Result stack size: " + result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  [" + i + "]: " + result.get(i));
        }

        // Working stack keeps the 10, plus the error on top
        assertEquals(2, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("10", ((BigNumber) result.get(0)).value().toString());
    }

    @Test
    void multipleOperations_ErrorClearsStack() {
        // Multiple operations: clears working stack
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("5"));
        stack.push(Add.INSTANCE);      // 15
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);   // Error: division by zero

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        System.out.println("Multiple operations error - Result stack size: " + result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  [" + i + "]: " + result.get(i));
        }

        // Working stack is cleared, only the error remains
        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }

    @Test
    void multipleOperations_ErrorInMiddle_ClearsStack() {
        // Multiple operations with error in the middle
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("100"));
        stack.push(BigNumber.of("4"));
        stack.push(Divide.INSTANCE);      // 25
        stack.push(BigNumber.of("0"));
        stack.push(Divide.INSTANCE);      // Error: division by zero
        stack.push(BigNumber.of("100"));
        stack.push(Add.INSTANCE);         // Should not execute

        Stack<StackItem> result = StackEvaluator.evaluate(stack);

        System.out.println("Multiple ops, error in middle - Result stack size: " + result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  [" + i + "]: " + result.get(i));
        }

        // Working stack is cleared, only the error remains
        assertEquals(1, result.size());
        assertTrue(result.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) result.peek()).message());
    }
}
