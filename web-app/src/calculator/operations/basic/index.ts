import type { StackItem, Calculation } from '../../types';
import {
  bigNumber,
  calculationError,
  isBigNumber,
} from '../../types';

/**
 * Addition operation: pops two operands, pushes their sum.
 * Stack transformation: [... a b ADD] -> [... (a+b)]
 */
export const Add: Calculation = {
  symbol: '+',
  description: 'Addition',
  example: 'Enter: 3 ENTER 4 ENTER +\nResult: 7',
  operandDescriptors: [
    { name: 'x', description: 'First operand' },
    { name: 'y', description: 'Second operand' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError(`ADD requires 2 operands, got ${stack.length}`));
      return;
    }
    const second = stack.pop()!;
    const first = stack.pop()!;
    if (isBigNumber(first) && isBigNumber(second)) {
      stack.push(bigNumber(first.value.plus(second.value)));
    } else {
      stack.push(calculationError('ADD requires numeric operands'));
    }
  },
};

/**
 * Subtraction operation: pops two operands, pushes their difference.
 * Stack transformation: [... a b SUBTRACT] -> [... (a-b)]
 */
export const Subtract: Calculation = {
  symbol: '-',
  description: 'Subtraction',
  example: 'Enter: 10 ENTER 3 ENTER -\nResult: 7',
  operandDescriptors: [
    { name: 'x', description: 'Minuend' },
    { name: 'y', description: 'Subtrahend' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError(`SUBTRACT requires 2 operands, got ${stack.length}`));
      return;
    }
    const second = stack.pop()!;
    const first = stack.pop()!;
    if (isBigNumber(first) && isBigNumber(second)) {
      stack.push(bigNumber(first.value.minus(second.value)));
    } else {
      stack.push(calculationError('SUBTRACT requires numeric operands'));
    }
  },
};

/**
 * Multiplication operation: pops two operands, pushes their product.
 * Stack transformation: [... a b MULTIPLY] -> [... (a*b)]
 */
export const Multiply: Calculation = {
  symbol: '*',
  description: 'Multiplication',
  example: 'Enter: 6 ENTER 7 ENTER *\nResult: 42',
  operandDescriptors: [
    { name: 'x', description: 'First factor' },
    { name: 'y', description: 'Second factor' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError(`MULTIPLY requires 2 operands, got ${stack.length}`));
      return;
    }
    const second = stack.pop()!;
    const first = stack.pop()!;
    if (isBigNumber(first) && isBigNumber(second)) {
      stack.push(bigNumber(first.value.times(second.value)));
    } else {
      stack.push(calculationError('MULTIPLY requires numeric operands'));
    }
  },
};

/**
 * Division operation: pops two operands, pushes their quotient.
 * Stack transformation: [... a b DIVIDE] -> [... (a/b)]
 */
export const Divide: Calculation = {
  symbol: '/',
  description: 'Division',
  example: 'Enter: 15 ENTER 3 ENTER /\nResult: 5',
  operandDescriptors: [
    { name: 'x', description: 'Dividend' },
    { name: 'y', description: 'Divisor' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError(`DIVIDE requires 2 operands, got ${stack.length}`));
      return;
    }
    const second = stack.pop()!;
    const first = stack.pop()!;
    if (isBigNumber(first) && isBigNumber(second)) {
      if (second.value.isZero()) {
        stack.push(calculationError('Division by zero'));
      } else {
        stack.push(bigNumber(first.value.dividedBy(second.value)));
      }
    } else {
      stack.push(calculationError('DIVIDE requires numeric operands'));
    }
  },
};

/**
 * Modulo operation: pops two operands, pushes the remainder.
 * Stack transformation: [... a b MOD] -> [... (a mod b)]
 */
export const Modulo: Calculation = {
  symbol: 'MOD',
  description: 'Modulo (Remainder)',
  example: 'Enter: 17 ENTER 5 ENTER MOD\nResult: 2',
  operandDescriptors: [
    { name: 'x', description: 'Dividend' },
    { name: 'y', description: 'Divisor' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError(`MOD requires 2 operands, got ${stack.length}`));
      return;
    }
    const second = stack.pop()!;
    const first = stack.pop()!;
    if (isBigNumber(first) && isBigNumber(second)) {
      if (second.value.isZero()) {
        stack.push(calculationError('Modulo by zero'));
      } else {
        stack.push(bigNumber(first.value.mod(second.value)));
      }
    } else {
      stack.push(calculationError('MOD requires numeric operands'));
    }
  },
};

/**
 * Absolute Value operation: pops one operand, pushes its absolute value.
 * Stack transformation: [... x ABS] -> [... |x|]
 */
export const AbsoluteValue: Calculation = {
  symbol: 'ABS',
  description: 'Absolute Value',
  example: 'Enter: -5 ENTER ABS\nResult: 5',
  operandDescriptors: [{ name: 'x', description: 'Value' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('ABS requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(item.value.abs()));
    } else {
      stack.push(calculationError('ABS requires numeric operand'));
    }
  },
};
