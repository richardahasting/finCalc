package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ArcTangentTest {

    @Test
    void execute_WithZero_ReturnsZero() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("0"));

        stack = ArcTangent.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(0.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithOne_ReturnsPiOverFour() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1"));

        stack = ArcTangent.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(Math.PI / 4, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithNegativeOne_ReturnsNegativePiOverFour() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-1"));

        stack = ArcTangent.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(-Math.PI / 4, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithLargePositive_ReturnsApproachingPiOverTwo() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("1000"));

        stack = ArcTangent.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(1.5698, ((BigNumber) stack.peek()).value().doubleValue(), 0.001);
    }

    @Test
    void execute_WithInsufficientOperands_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();

        stack = ArcTangent.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("ATAN requires 1 operand"));
    }
}
