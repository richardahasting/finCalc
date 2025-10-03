package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ReciprocalTest {

    @Test
    void execute_WithPositiveInteger_ReturnsReciprocal() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("4"));

        stack = Reciprocal.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0.2500000000", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeNumber_ReturnsNegativeReciprocal() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-2"));

        stack = Reciprocal.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-0.5000000000", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimal_ReturnsCorrectReciprocal() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0.5"));

        stack = Reciprocal.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("2.0000000000", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Reciprocal.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) stack.peek()).message());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = Reciprocal.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("RECIPROCAL requires 1 operand"));
    }
}
