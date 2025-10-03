package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class DivideTest {

    @Test
    void execute_WithTwoPositiveNumbers_ReturnsQuotient() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("12"));
        stack.push(BigNumber.of("3"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("4.0000000000", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithNegativeDivisor_ReturnsNegativeQuotient() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("15"));
        stack.push(BigNumber.of("-3"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-5.0000000000", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_WithDecimalResult_ReturnsRoundedQuotient() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("3"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        // Default precision is 10, HALF_UP rounding
        assertEquals("3.3333333333", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_ByZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42"));
        stack.push(BigNumber.of("0"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertEquals("Division by zero", ((Error) stack.peek()).message());
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("5"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(2, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("DIVIDE requires 2 operand"));
    }

    @Test
    void execute_DivideByOne_ReturnsOriginalValue() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("42.5"));
        stack.push(BigNumber.of("1"));

        stack = Divide.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("42.5000000000", ((BigNumber) stack.peek()).value().toString());
    }
}
