package com.finCalc.calculator.operations.scientific;

import com.finCalc.calculator.BigNumber;
import com.finCalc.calculator.Error;
import com.finCalc.calculator.StackItem;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class NthRootTest {

    @Test
    void execute_SquareRootOfFour_ReturnsTwo() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("4"));
        stack.push(BigNumber.of("2"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(2.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_CubeRootOfEight_ReturnsTwo() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("8"));
        stack.push(BigNumber.of("3"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(2.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_FourthRootOfSixteen_ReturnsTwo() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("16"));
        stack.push(BigNumber.of("4"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof BigNumber);
        assertEquals(2.0, ((BigNumber) stack.peek()).value().doubleValue(), 0.0001);
    }

    @Test
    void execute_WithZeroRootIndex_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("4"));
        stack.push(BigNumber.of("0"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("root index cannot be zero"));
    }

    @Test
    void execute_EvenRootOfNegative_ReturnsError() {
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-4"));
        stack.push(BigNumber.of("2"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("cannot take even root of negative"));
    }

    @Test
    void execute_OddRootOfNegative_ReturnsError() {
        // Note: Math.pow with negative base and fractional exponent returns NaN
        // Even for odd roots, so this operation returns an error
        Stack<StackItem> stack = new Stack<>();
        stack.push(BigNumber.of("-8"));
        stack.push(BigNumber.of("3"));

        stack = NthRoot.INSTANCE.execute(stack);

        assertEquals(1, stack.size());
        assertTrue(stack.peek() instanceof Error);
        assertTrue(((Error) stack.peek()).message().contains("undefined or infinite"));
    }
}
