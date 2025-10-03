package com.finCalc.calculator.operations.basic;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ModuloTest {

    @Test
    void execute_SimpleModulo_ReturnsRemainder() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("3"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("1", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_EvenDivision_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("5"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("0", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_NegativeDividend_PreservesSign() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-10"));
        stack.push(BigNumber.of("3"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("-1", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_DecimalModulo_ReturnsDecimalRemainder() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10.5"));
        stack.push(BigNumber.of("3"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals("1.5", ((BigNumber) stack.peek()).value().toString());
    }

    @Test
    void execute_DivisionByZero_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));
        stack.push(BigNumber.of("0"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertEquals("Modulo by zero", ((Error) stack.peek()).message());
    }

    @Test
    void execute_InsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("10"));

        stack = Modulo.INSTANCE.execute(stack);

        assertEquals(2, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("operand"));
    }

    @Test
    void getOperandCount_ReturnsTwo() {
        assertEquals(2, Modulo.INSTANCE.getOperandCount());
    }
}
