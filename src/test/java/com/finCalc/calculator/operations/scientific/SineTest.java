package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class SineTest {

    @Test
    void execute_WithZero_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Sine.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithPiOverTwo_ReturnsOne() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of(String.valueOf(Math.PI / 2)));

        stack = Sine.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithPi_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of(String.valueOf(Math.PI)));

        stack = Sine.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithPiOverSix_ReturnsHalf() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of(String.valueOf(Math.PI / 6)));

        stack = Sine.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.5, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = Sine.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("SIN requires 1 operand"));
    }
}
