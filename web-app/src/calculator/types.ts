import Decimal from 'decimal.js';

// Configure Decimal.js for financial calculations
// Matches Java BigDecimal behavior with HALF_UP rounding
Decimal.set({
  precision: 50,
  rounding: Decimal.ROUND_HALF_UP,
});

/**
 * Discriminated union for stack items.
 * Mirrors Java's sealed interface pattern.
 */
export type StackItem = BigNumber | CalculationError;

/**
 * Represents a numeric value on the stack.
 * Uses Decimal.js for arbitrary precision arithmetic.
 */
export interface BigNumber {
  readonly type: 'BigNumber';
  readonly value: Decimal;
}

/**
 * Represents an error on the stack.
 */
export interface CalculationError {
  readonly type: 'Error';
  readonly message: string;
}

/**
 * Describes an operand for a calculation.
 */
export interface OperandDescriptor {
  readonly name: string;
  readonly description: string;
}

/**
 * Interface for all calculator operations.
 * Each operation consumes operands from the stack and pushes results.
 */
export interface Calculation {
  /** Unique symbol for the operation (e.g., "+", "PMT") */
  readonly symbol: string;

  /** Human-readable description */
  readonly description: string;

  /** Example usage */
  readonly example: string;

  /** Describes required operands (bottom to top of stack) */
  readonly operandDescriptors: OperandDescriptor[];

  /** Execute the operation on the stack (mutates in place) */
  execute(stack: StackItem[]): void;
}

// Factory functions for creating stack items
export function bigNumber(value: Decimal | string | number): BigNumber {
  return {
    type: 'BigNumber',
    value: new Decimal(value),
  };
}

export function calculationError(message: string): CalculationError {
  return {
    type: 'Error',
    message,
  };
}

// Type guards
export function isBigNumber(item: StackItem): item is BigNumber {
  return item.type === 'BigNumber';
}

export function isError(item: StackItem): item is CalculationError {
  return item.type === 'Error';
}

// Global precision settings (mirrors Java BigNumber class)
let displayPrecision = 10;
let roundingMode: Decimal.Rounding = Decimal.ROUND_HALF_UP;

export function getPrecision(): number {
  return displayPrecision;
}

export function setPrecision(precision: number): void {
  displayPrecision = Math.max(1, Math.min(50, precision));
}

export function getRoundingMode(): Decimal.Rounding {
  return roundingMode;
}

export function setRoundingMode(mode: Decimal.Rounding): void {
  roundingMode = mode;
}

/**
 * Format a BigNumber for display using current precision settings.
 */
export function formatBigNumber(num: BigNumber): string {
  return num.value.toFixed(displayPrecision);
}

/**
 * Format any stack item for display.
 */
export function formatStackItem(item: StackItem): string {
  if (isBigNumber(item)) {
    return formatBigNumber(item);
  }
  return `ERROR: ${item.message}`;
}
