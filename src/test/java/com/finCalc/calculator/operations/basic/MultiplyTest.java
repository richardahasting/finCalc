package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class MultiplyTest {

    @Test
    void execute_WithTwoPositiveNumbers_ReturnsProduct() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("3"));
        stack.push(BigNumber.of("4"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("12", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeNumber_ReturnsNegativeProduct() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-5"));
        stack.push(BigNumber.of("3"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-15", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimals_ReturnsCorrectProduct() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2.5"));
        stack.push(BigNumber.of("4"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("10.0", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithZero_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));
        stack.push(BigNumber.of("0"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(2, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("MULTIPLY requires 2 operand"));
    }

    @Test
    void execute_WithLargeNumbers_ReturnsCorrectProduct() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("123456789"));
        stack.push(BigNumber.of("987654321"));

        stack = Multiply.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("121932631112635269", ((BigNumber) stack.peek()).value().toString());
    }
}
