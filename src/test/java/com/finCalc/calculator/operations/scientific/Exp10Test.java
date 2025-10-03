package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class Exp10Test {

    @Test
    void execute_WithZero_ReturnsOne() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Exp10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithOne_ReturnsTen() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1"));

        stack = Exp10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(10.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithTwo_ReturnsHundred() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2"));

        stack = Exp10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(100.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithNegativeOne_ReturnsOneTenth() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-1"));

        stack = Exp10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.1, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithLargeExponent_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1000"));

        stack = Exp10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("infinite"));
    }
}
