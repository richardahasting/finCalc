package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class Log10Test {

    @Test
    void execute_WithTen_ReturnsOne() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));

        stack = Log10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithOne_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1"));

        stack = Log10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithHundred_ReturnsTwo() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("100"));

        stack = Log10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(2.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Log10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("cannot take logarithm of non-positive"));
    }

    @Test
    void execute_WithNegativeNumber_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-10"));

        stack = Log10.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("cannot take logarithm of non-positive"));
    }
}
