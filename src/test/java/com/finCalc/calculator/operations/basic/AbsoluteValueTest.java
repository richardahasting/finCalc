package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class AbsoluteValueTest {

    @Test
    void execute_PositiveNumber_ReturnsSameNumber() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));

        stack = AbsoluteValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("42", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_NegativeNumber_ReturnsPositive() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-42"));

        stack = AbsoluteValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("42", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_Zero_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = AbsoluteValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_NegativeDecimal_ReturnsPositiveDecimal() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-3.14159"));

        stack = AbsoluteValue.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("3.14159", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_InsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = AbsoluteValue.INSTANCE.execute(stack);

        assertFalse(stack.isEmpty());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("operand"));
    }

    @Test
    void getOperandCount_ReturnsOne() {
        assertEquals(1, AbsoluteValue.INSTANCE.getOperandCount());
    }
}
