package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class SubtractTest {

    @Test
    void execute_WithTwoPositiveNumbers_ReturnsDifference() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("3"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("7", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeResult_ReturnsCorrectDifference() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));
        stack.push(BigNumber.of("10"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-5", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimals_ReturnsCorrectDifference() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("7.5"));
        stack.push(BigNumber.of("2.3"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("5.2", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_SubtractingZero_ReturnsOriginalValue() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));
        stack.push(BigNumber.of("0"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("42", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(2, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("SUBTRACT requires 2 operand"));
    }

    @Test
    void execute_SubtractingSameNumber_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("123.456"));
        stack.push(BigNumber.of("123.456"));

        stack = Subtract.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0.000", ((BigNumber) stack.peek()).value().toString());
    }
}
