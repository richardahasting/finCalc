package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class AddTest {

    @Test
    void execute_WithTwoPositiveNumbers_ReturnsSum() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("7", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeNumbers_ReturnsCorrectSum() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-5"));
        stack.push(BigNumber.of("3"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-2", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimals_ReturnsCorrectSum() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3.14"));
        stack.push(BigNumber.of("2.86"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("6.00", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithZero_ReturnsOtherOperand() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));
        stack.push(BigNumber.of("0"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("42", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(2, stack.size()); // Original item + error
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("ADD requires 2 operand"));
    }

    @Test
    void execute_WithLargeNumbers_ReturnsCorrectSum() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("999999999999999999"));
        stack.push(BigNumber.of("1"));

        stack = Add.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("1000000000000000000", ((BigNumber) stack.peek()).value().toString());
    }
}
