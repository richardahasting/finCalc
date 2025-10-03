package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ExponentialTest {

    @Test
    void execute_ExpOfZero_ReturnsOne() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = Exponential.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_ExpOfOne_ReturnsE() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1"));

        stack = Exponential.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(Math.E, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_ExpOfTwo_ReturnsESquared() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("2"));

        stack = Exponential.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(Math.E * Math.E, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_ExpOfNegativeOne_ReturnsOneOverE() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-1"));

        stack = Exponential.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.0 / Math.E, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_InsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = Exponential.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("operand"));
    }

    @Test
    void getOperandCount_ReturnsOne() {
        assertEquals(1, Exponential.INSTANCE.getOperandCount());
    }
}
