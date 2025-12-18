import Decimal from 'decimal.js';
import type { StackItem, Calculation } from '../../types';
import {
  bigNumber,
  calculationError,
  isBigNumber,
} from '../../types';

/**
 * Square Root operation: pops one operand, pushes its square root.
 * Stack transformation: [... x SQRT] -> [... sqrt(x)]
 */
export const SquareRoot: Calculation = {
  symbol: '√',
  description: 'Square Root',
  example: 'Enter: 16 ENTER √\nResult: 4',
  operandDescriptors: [{ name: 'x', description: 'Value (must be non-negative)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('SQRT requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      if (item.value.isNegative()) {
        stack.push(calculationError('Cannot compute square root of negative number'));
      } else {
        stack.push(bigNumber(item.value.sqrt()));
      }
    } else {
      stack.push(calculationError('SQRT requires numeric operand'));
    }
  },
};

/**
 * Square operation: pops one operand, pushes its square.
 * Stack transformation: [... x SQ] -> [... x^2]
 */
export const Square: Calculation = {
  symbol: 'x²',
  description: 'Square',
  example: 'Enter: 5 ENTER x²\nResult: 25',
  operandDescriptors: [{ name: 'x', description: 'Value' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('SQUARE requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(item.value.pow(2)));
    } else {
      stack.push(calculationError('SQUARE requires numeric operand'));
    }
  },
};

/**
 * Power operation: pops two operands, pushes x raised to power y.
 * Stack transformation: [... x y POW] -> [... x^y]
 */
export const Power: Calculation = {
  symbol: 'xⁿ',
  description: 'Power (x^y)',
  example: 'Enter: 2 ENTER 10 ENTER xⁿ\nResult: 1024',
  operandDescriptors: [
    { name: 'x', description: 'Base' },
    { name: 'y', description: 'Exponent' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('POW requires 2 operands'));
      return;
    }
    const y = stack.pop()!;
    const x = stack.pop()!;
    if (isBigNumber(x) && isBigNumber(y)) {
      try {
        stack.push(bigNumber(x.value.pow(y.value)));
      } catch {
        stack.push(calculationError('Invalid power operation'));
      }
    } else {
      stack.push(calculationError('POW requires numeric operands'));
    }
  },
};

/**
 * Reciprocal operation: pops one operand, pushes 1/x.
 * Stack transformation: [... x 1/x] -> [... 1/x]
 */
export const Reciprocal: Calculation = {
  symbol: '1/x',
  description: 'Reciprocal (1/x)',
  example: 'Enter: 4 ENTER 1/x\nResult: 0.25',
  operandDescriptors: [{ name: 'x', description: 'Value (cannot be zero)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('RECIPROCAL requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      if (item.value.isZero()) {
        stack.push(calculationError('Cannot compute reciprocal of zero'));
      } else {
        stack.push(bigNumber(new Decimal(1).dividedBy(item.value)));
      }
    } else {
      stack.push(calculationError('RECIPROCAL requires numeric operand'));
    }
  },
};

/**
 * Nth Root operation: pops two operands, pushes the nth root of x.
 * Stack transformation: [... x n NTHRT] -> [... x^(1/n)]
 */
export const NthRoot: Calculation = {
  symbol: 'ⁿ√x',
  description: 'Nth Root',
  example: 'Enter: 27 ENTER 3 ENTER ⁿ√x\nResult: 3 (cube root)',
  operandDescriptors: [
    { name: 'x', description: 'Value' },
    { name: 'n', description: 'Root index' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('NTHROOT requires 2 operands'));
      return;
    }
    const n = stack.pop()!;
    const x = stack.pop()!;
    if (isBigNumber(x) && isBigNumber(n)) {
      if (n.value.isZero()) {
        stack.push(calculationError('Cannot compute 0th root'));
      } else {
        const result = x.value.pow(new Decimal(1).dividedBy(n.value));
        stack.push(bigNumber(result));
      }
    } else {
      stack.push(calculationError('NTHROOT requires numeric operands'));
    }
  },
};

/**
 * Natural Log operation: pops one operand, pushes ln(x).
 * Stack transformation: [... x LN] -> [... ln(x)]
 */
export const NaturalLog: Calculation = {
  symbol: 'ln',
  description: 'Natural Logarithm (ln)',
  example: 'Enter: 2.71828 ENTER ln\nResult: ~1',
  operandDescriptors: [{ name: 'x', description: 'Value (must be positive)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('LN requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      if (item.value.isNegative() || item.value.isZero()) {
        stack.push(calculationError('Cannot compute ln of non-positive number'));
      } else {
        stack.push(bigNumber(item.value.ln()));
      }
    } else {
      stack.push(calculationError('LN requires numeric operand'));
    }
  },
};

/**
 * Log base 10 operation: pops one operand, pushes log10(x).
 * Stack transformation: [... x LOG10] -> [... log10(x)]
 */
export const Log10: Calculation = {
  symbol: 'LOG',
  description: 'Logarithm Base 10',
  example: 'Enter: 100 ENTER LOG\nResult: 2',
  operandDescriptors: [{ name: 'x', description: 'Value (must be positive)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('LOG10 requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      if (item.value.isNegative() || item.value.isZero()) {
        stack.push(calculationError('Cannot compute log of non-positive number'));
      } else {
        stack.push(bigNumber(item.value.log(10)));
      }
    } else {
      stack.push(calculationError('LOG10 requires numeric operand'));
    }
  },
};

/**
 * Exponential operation: pops one operand, pushes e^x.
 * Stack transformation: [... x EXP] -> [... e^x]
 */
export const Exponential: Calculation = {
  symbol: 'e^x',
  description: 'Exponential (e^x)',
  example: 'Enter: 1 ENTER e^x\nResult: 2.71828...',
  operandDescriptors: [{ name: 'x', description: 'Exponent' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('EXP requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(item.value.exp()));
    } else {
      stack.push(calculationError('EXP requires numeric operand'));
    }
  },
};

/**
 * 10^x operation: pops one operand, pushes 10^x.
 * Stack transformation: [... x EXP10] -> [... 10^x]
 */
export const Exp10: Calculation = {
  symbol: '10^x',
  description: 'Power of 10 (10^x)',
  example: 'Enter: 3 ENTER 10^x\nResult: 1000',
  operandDescriptors: [{ name: 'x', description: 'Exponent' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('EXP10 requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(new Decimal(10).pow(item.value)));
    } else {
      stack.push(calculationError('EXP10 requires numeric operand'));
    }
  },
};

// Trigonometric functions (use native Math for these as precision isn't as critical)
export const Sine: Calculation = {
  symbol: 'sin',
  description: 'Sine (radians)',
  example: 'Enter: 0 ENTER sin\nResult: 0',
  operandDescriptors: [{ name: 'x', description: 'Angle in radians' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('SIN requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(Math.sin(item.value.toNumber())));
    } else {
      stack.push(calculationError('SIN requires numeric operand'));
    }
  },
};

export const Cosine: Calculation = {
  symbol: 'cos',
  description: 'Cosine (radians)',
  example: 'Enter: 0 ENTER cos\nResult: 1',
  operandDescriptors: [{ name: 'x', description: 'Angle in radians' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('COS requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(Math.cos(item.value.toNumber())));
    } else {
      stack.push(calculationError('COS requires numeric operand'));
    }
  },
};

export const Tangent: Calculation = {
  symbol: 'tan',
  description: 'Tangent (radians)',
  example: 'Enter: 0 ENTER tan\nResult: 0',
  operandDescriptors: [{ name: 'x', description: 'Angle in radians' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('TAN requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(Math.tan(item.value.toNumber())));
    } else {
      stack.push(calculationError('TAN requires numeric operand'));
    }
  },
};

export const ArcSine: Calculation = {
  symbol: 'asin',
  description: 'Arc Sine (result in radians)',
  example: 'Enter: 0.5 ENTER asin\nResult: 0.5236 (30 degrees)',
  operandDescriptors: [{ name: 'x', description: 'Value (-1 to 1)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('ASIN requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      const val = item.value.toNumber();
      if (val < -1 || val > 1) {
        stack.push(calculationError('ASIN domain error: value must be between -1 and 1'));
      } else {
        stack.push(bigNumber(Math.asin(val)));
      }
    } else {
      stack.push(calculationError('ASIN requires numeric operand'));
    }
  },
};

export const ArcCosine: Calculation = {
  symbol: 'acos',
  description: 'Arc Cosine (result in radians)',
  example: 'Enter: 0.5 ENTER acos\nResult: 1.0472 (60 degrees)',
  operandDescriptors: [{ name: 'x', description: 'Value (-1 to 1)' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('ACOS requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      const val = item.value.toNumber();
      if (val < -1 || val > 1) {
        stack.push(calculationError('ACOS domain error: value must be between -1 and 1'));
      } else {
        stack.push(bigNumber(Math.acos(val)));
      }
    } else {
      stack.push(calculationError('ACOS requires numeric operand'));
    }
  },
};

export const ArcTangent: Calculation = {
  symbol: 'atan',
  description: 'Arc Tangent (result in radians)',
  example: 'Enter: 1 ENTER atan\nResult: 0.7854 (45 degrees)',
  operandDescriptors: [{ name: 'x', description: 'Value' }],
  execute(stack: StackItem[]): void {
    if (stack.length < 1) {
      stack.push(calculationError('ATAN requires 1 operand'));
      return;
    }
    const item = stack.pop()!;
    if (isBigNumber(item)) {
      stack.push(bigNumber(Math.atan(item.value.toNumber())));
    } else {
      stack.push(calculationError('ATAN requires numeric operand'));
    }
  },
};
