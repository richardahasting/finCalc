// Core types
export * from './types';

// Operations
export * as BasicOps from './operations/basic';
export * as ScientificOps from './operations/scientific';
export * as FinancialOps from './operations/financial';

// Import all operations for registry
import * as Basic from './operations/basic';
import * as Scientific from './operations/scientific';
import * as Financial from './operations/financial';
import type { Calculation } from './types';

/**
 * Registry of all operations, keyed by symbol.
 * Similar to Java's OperationRegistry.
 */
export const OperationRegistry: Map<string, Calculation> = new Map([
  // Basic operations
  ['+', Basic.Add],
  ['-', Basic.Subtract],
  ['*', Basic.Multiply],
  ['×', Basic.Multiply],
  ['/', Basic.Divide],
  ['÷', Basic.Divide],
  ['MOD', Basic.Modulo],
  ['ABS', Basic.AbsoluteValue],
  ['|x|', Basic.AbsoluteValue],

  // Scientific operations
  ['√', Scientific.SquareRoot],
  ['SQRT', Scientific.SquareRoot],
  ['x²', Scientific.Square],
  ['SQ', Scientific.Square],
  ['xⁿ', Scientific.Power],
  ['POW', Scientific.Power],
  ['1/x', Scientific.Reciprocal],
  ['ⁿ√x', Scientific.NthRoot],
  ['NTHRT', Scientific.NthRoot],
  ['ln', Scientific.NaturalLog],
  ['LN', Scientific.NaturalLog],
  ['LOG', Scientific.Log10],
  ['e^x', Scientific.Exponential],
  ['EXP', Scientific.Exponential],
  ['10^x', Scientific.Exp10],
  ['sin', Scientific.Sine],
  ['SIN', Scientific.Sine],
  ['cos', Scientific.Cosine],
  ['COS', Scientific.Cosine],
  ['tan', Scientific.Tangent],
  ['TAN', Scientific.Tangent],
  ['asin', Scientific.ArcSine],
  ['ASIN', Scientific.ArcSine],
  ['acos', Scientific.ArcCosine],
  ['ACOS', Scientific.ArcCosine],
  ['atan', Scientific.ArcTangent],
  ['ATAN', Scientific.ArcTangent],

  // Financial - TVM
  ['PMT', Financial.Payment],
  ['PV', Financial.PresentValue],
  ['FV', Financial.FutureValue],
  ['RATE', Financial.InterestRate],
  ['NPER', Financial.NumberOfPeriods],

  // Financial - Real Estate
  ['CAP', Financial.CapRate],
  ['NOI', Financial.NetOperatingIncome],
  ['CoC', Financial.CashOnCash],
  ['DSCR', Financial.DebtServiceCoverageRatio],
  ['LTV', Financial.LoanToValue],
  ['GRM', Financial.GrossRentMultiplier],
  ['ROI', Financial.ReturnOnInvestment],

  // Financial - Investment Analysis
  ['CAGR', Financial.CompoundAnnualGrowthRate],
  ['BEP', Financial.BreakEvenPoint],
  ['PI', Financial.ProfitabilityIndex],
  ['PBP', Financial.PaybackPeriod],

  // Financial - Bonds
  ['YIELD', Financial.CurrentYield],
  ['YTM', Financial.YieldToMaturity],
  ['BPRC', Financial.BondPrice],

  // Financial - Loans
  ['BAL', Financial.RemainingBalance],
  ['TINT', Financial.TotalInterestPaid],
  ['APY', Financial.AprToApy],
  ['DTI', Financial.DebtToIncomeRatio],

  // Financial - Options
  ['AOPT', Financial.AnnualizedOptionReturn],
  ['CCR', Financial.CoveredCallReturn],

  // Financial - Tax & Retirement
  ['ETR', Financial.EffectiveTaxRate],
  ['ATR', Financial.AfterTaxReturn],
  ['RMD', Financial.RequiredMinimumDistribution],

  // Financial - Real Estate (additional)
  ['OER', Financial.OperatingExpenseRatio],
  ['EGI', Financial.EffectiveGrossIncome],
  ['CFAT', Financial.CashFlowAfterTaxes],
  ['VAC', Financial.VacancyLoss],
  ['PPSF', Financial.PricePerSquareFoot],
  ['RPSF', Financial.RentPerSquareFoot],
]);

/**
 * Get an operation by its symbol.
 */
export function getOperation(symbol: string): Calculation | undefined {
  return OperationRegistry.get(symbol);
}

/**
 * Get all operations grouped by category.
 */
export function getOperationsByCategory(): Record<string, Calculation[]> {
  return {
    'Basic': [Basic.Add, Basic.Subtract, Basic.Multiply, Basic.Divide, Basic.Modulo, Basic.AbsoluteValue],
    'Scientific': [
      Scientific.SquareRoot, Scientific.Square, Scientific.Power, Scientific.Reciprocal,
      Scientific.NthRoot, Scientific.NaturalLog, Scientific.Log10, Scientific.Exponential,
      Scientific.Exp10, Scientific.Sine, Scientific.Cosine, Scientific.Tangent,
      Scientific.ArcSine, Scientific.ArcCosine, Scientific.ArcTangent,
    ],
    'Time Value of Money': [
      Financial.Payment, Financial.PresentValue, Financial.FutureValue,
      Financial.InterestRate, Financial.NumberOfPeriods,
    ],
    'Real Estate': [
      Financial.CapRate, Financial.NetOperatingIncome, Financial.CashOnCash,
      Financial.DebtServiceCoverageRatio, Financial.LoanToValue, Financial.GrossRentMultiplier,
      Financial.ReturnOnInvestment,
    ],
    'Investment Analysis': [
      Financial.CompoundAnnualGrowthRate, Financial.BreakEvenPoint, Financial.ProfitabilityIndex,
    ],
  };
}
