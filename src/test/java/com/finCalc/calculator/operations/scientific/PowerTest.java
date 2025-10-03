package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class PowerTest {

    @Test
    void execute_WithIntegerExponent_ReturnsCorrectPower() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2"));
        stack.push(BigNumber.of("3"));

        stack = Power.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(8.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithFractionalExponent_ReturnsCorrectPower() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("4"));
        stack.push(BigNumber.of("0.5"));

        stack = Power.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(2.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithZeroExponent_ReturnsOne() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));
        stack.push(BigNumber.of("0"));

        stack = Power.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithNegativeExponent_ReturnsReciprocal() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2"));
        stack.push(BigNumber.of("-2"));

        stack = Power.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.25, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Power.INSTANCE.execute(stack);

        assertEquals(2, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("POW requires 2 operand"));
    }
}
