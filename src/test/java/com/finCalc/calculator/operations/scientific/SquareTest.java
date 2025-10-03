package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void execute_WithPositiveInteger_ReturnsSquare() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Square.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("25", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeNumber_ReturnsPositiveSquare() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-4"));

        stack = Square.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("16", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimal_ReturnsCorrectSquare() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2.5"));

        stack = Square.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("6.25", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithZero_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Square.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = Square.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("SQUARE requires 1 operand"));
    }
}
