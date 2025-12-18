import type { StackItem, Calculation } from '../../types';
import {
  bigNumber,
  calculationError,
  isBigNumber,
} from '../../types';

// ============================================================================
// TIME VALUE OF MONEY (TVM) OPERATIONS
// ============================================================================

/**
 * Payment (PMT) calculation: computes periodic payment for a loan.
 * Stack: [... PV rate n PMT] -> [... payment]
 * Formula: PMT = PV * (rate * (1 + rate)^n) / ((1 + rate)^n - 1)
 */
export const Payment: Calculation = {
  symbol: 'PMT',
  description: 'Payment Calculation',
  example: `Example: $200,000 loan at 6% annual (0.5% monthly) for 30 years (360 months)
  Enter: 200000 ENTER 0.005 ENTER 360 ENTER PMT
  Result: $1,199.10/month`,
  operandDescriptors: [
    { name: 'PV', description: 'Present Value (loan amount)' },
    { name: 'rate', description: 'Interest rate per period' },
    { name: 'n', description: 'Number of periods' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('PMT requires 3 operands'));
      return;
    }
    const nItem = stack.pop()!;
    const rateItem = stack.pop()!;
    const pvItem = stack.pop()!;

    if (isBigNumber(pvItem) && isBigNumber(rateItem) && isBigNumber(nItem)) {
      const pv = pvItem.value.toNumber();
      const rate = rateItem.value.toNumber();
      const n = nItem.value.toNumber();

      if (n <= 0) {
        stack.push(calculationError('PMT: number of periods must be positive'));
        return;
      }
      if (rate < 0) {
        stack.push(calculationError('PMT: interest rate cannot be negative'));
        return;
      }

      let payment: number;
      if (rate === 0) {
        payment = pv / n;
      } else {
        const factor = Math.pow(1 + rate, n);
        payment = pv * (rate * factor) / (factor - 1);
      }

      if (!isFinite(payment)) {
        stack.push(calculationError('PMT: result is undefined or infinite'));
      } else {
        stack.push(bigNumber(payment));
      }
    } else {
      stack.push(calculationError('PMT requires numeric operands'));
    }
  },
};

/**
 * Present Value (PV) calculation.
 * Stack: [... PMT rate n PV] -> [... present_value]
 * Formula: PV = PMT * ((1 + rate)^n - 1) / (rate * (1 + rate)^n)
 */
export const PresentValue: Calculation = {
  symbol: 'PV',
  description: 'Present Value Calculation',
  example: `Example: What loan amount for $1,199.10/month at 0.5% monthly for 360 months?
  Enter: 1199.10 ENTER 0.005 ENTER 360 ENTER PV
  Result: $200,000`,
  operandDescriptors: [
    { name: 'PMT', description: 'Payment per period' },
    { name: 'rate', description: 'Interest rate per period' },
    { name: 'n', description: 'Number of periods' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('PV requires 3 operands'));
      return;
    }
    const nItem = stack.pop()!;
    const rateItem = stack.pop()!;
    const pmtItem = stack.pop()!;

    if (isBigNumber(pmtItem) && isBigNumber(rateItem) && isBigNumber(nItem)) {
      const pmt = pmtItem.value.toNumber();
      const rate = rateItem.value.toNumber();
      const n = nItem.value.toNumber();

      if (n <= 0) {
        stack.push(calculationError('PV: number of periods must be positive'));
        return;
      }

      let pv: number;
      if (rate === 0) {
        pv = pmt * n;
      } else {
        const factor = Math.pow(1 + rate, n);
        pv = pmt * (factor - 1) / (rate * factor);
      }

      if (!isFinite(pv)) {
        stack.push(calculationError('PV: result is undefined or infinite'));
      } else {
        stack.push(bigNumber(pv));
      }
    } else {
      stack.push(calculationError('PV requires numeric operands'));
    }
  },
};

/**
 * Future Value (FV) calculation.
 * Stack: [... PV rate n FV] -> [... future_value]
 * Formula: FV = PV * (1 + rate)^n
 */
export const FutureValue: Calculation = {
  symbol: 'FV',
  description: 'Future Value Calculation',
  example: `Example: $10,000 invested at 8% annual for 10 years
  Enter: 10000 ENTER 0.08 ENTER 10 ENTER FV
  Result: $21,589.25`,
  operandDescriptors: [
    { name: 'PV', description: 'Present Value (initial investment)' },
    { name: 'rate', description: 'Interest rate per period' },
    { name: 'n', description: 'Number of periods' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('FV requires 3 operands'));
      return;
    }
    const nItem = stack.pop()!;
    const rateItem = stack.pop()!;
    const pvItem = stack.pop()!;

    if (isBigNumber(pvItem) && isBigNumber(rateItem) && isBigNumber(nItem)) {
      const pv = pvItem.value.toNumber();
      const rate = rateItem.value.toNumber();
      const n = nItem.value.toNumber();

      const fv = pv * Math.pow(1 + rate, n);

      if (!isFinite(fv)) {
        stack.push(calculationError('FV: result is undefined or infinite'));
      } else {
        stack.push(bigNumber(fv));
      }
    } else {
      stack.push(calculationError('FV requires numeric operands'));
    }
  },
};

/**
 * Interest Rate (RATE) calculation using Newton-Raphson iteration.
 * Stack: [... PV PMT n RATE] -> [... rate]
 */
export const InterestRate: Calculation = {
  symbol: 'RATE',
  description: 'Interest Rate Calculation',
  example: `Example: $200,000 loan, $1,199.10 payment, 360 months
  Enter: 200000 ENTER 1199.10 ENTER 360 ENTER RATE
  Result: 0.005 (0.5% monthly = 6% annual)`,
  operandDescriptors: [
    { name: 'PV', description: 'Present Value (loan amount)' },
    { name: 'PMT', description: 'Payment per period' },
    { name: 'n', description: 'Number of periods' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('RATE requires 3 operands'));
      return;
    }
    const nItem = stack.pop()!;
    const pmtItem = stack.pop()!;
    const pvItem = stack.pop()!;

    if (isBigNumber(pvItem) && isBigNumber(pmtItem) && isBigNumber(nItem)) {
      const pv = pvItem.value.toNumber();
      const pmt = pmtItem.value.toNumber();
      const n = nItem.value.toNumber();

      if (n <= 0) {
        stack.push(calculationError('RATE: number of periods must be positive'));
        return;
      }

      // Newton-Raphson iteration to solve for rate
      let rate = 0.1 / 12; // Initial guess: ~10% annual
      const maxIterations = 100;
      const tolerance = 1e-10;

      for (let i = 0; i < maxIterations; i++) {
        const factor = Math.pow(1 + rate, n);
        const f = pv * rate * factor - pmt * (factor - 1);
        const df = pv * factor * (1 + rate * n / (1 + rate)) - pmt * n * Math.pow(1 + rate, n - 1);

        if (Math.abs(df) < 1e-15) break;

        const newRate = rate - f / df;
        if (Math.abs(newRate - rate) < tolerance) {
          rate = newRate;
          break;
        }
        rate = newRate;
      }

      if (!isFinite(rate) || rate < 0) {
        stack.push(calculationError('RATE: could not converge to a valid rate'));
      } else {
        stack.push(bigNumber(rate));
      }
    } else {
      stack.push(calculationError('RATE requires numeric operands'));
    }
  },
};

/**
 * Number of Periods (NPER) calculation.
 * Stack: [... PV PMT rate NPER] -> [... n]
 */
export const NumberOfPeriods: Calculation = {
  symbol: 'NPER',
  description: 'Number of Periods Calculation',
  example: `Example: $200,000 loan, $1,199.10 payment, 0.5% monthly rate
  Enter: 200000 ENTER 1199.10 ENTER 0.005 ENTER NPER
  Result: 360 months (30 years)`,
  operandDescriptors: [
    { name: 'PV', description: 'Present Value (loan amount)' },
    { name: 'PMT', description: 'Payment per period' },
    { name: 'rate', description: 'Interest rate per period' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('NPER requires 3 operands'));
      return;
    }
    const rateItem = stack.pop()!;
    const pmtItem = stack.pop()!;
    const pvItem = stack.pop()!;

    if (isBigNumber(pvItem) && isBigNumber(pmtItem) && isBigNumber(rateItem)) {
      const pv = pvItem.value.toNumber();
      const pmt = pmtItem.value.toNumber();
      const rate = rateItem.value.toNumber();

      if (pmt <= 0) {
        stack.push(calculationError('NPER: payment must be positive'));
        return;
      }

      let n: number;
      if (rate === 0) {
        n = pv / pmt;
      } else {
        // n = ln(pmt / (pmt - pv * rate)) / ln(1 + rate)
        const x = pmt / (pmt - pv * rate);
        if (x <= 0) {
          stack.push(calculationError('NPER: payment too small to pay off loan'));
          return;
        }
        n = Math.log(x) / Math.log(1 + rate);
      }

      if (!isFinite(n) || n < 0) {
        stack.push(calculationError('NPER: could not calculate valid number of periods'));
      } else {
        stack.push(bigNumber(n));
      }
    } else {
      stack.push(calculationError('NPER requires numeric operands'));
    }
  },
};

// ============================================================================
// REAL ESTATE OPERATIONS
// ============================================================================

/**
 * Cap Rate: NOI / Property Value
 * Stack: [... value NOI CAP] -> [... cap_rate]
 */
export const CapRate: Calculation = {
  symbol: 'CAP',
  description: 'Capitalization Rate',
  example: `Example: $200,000 property with $15,000 annual NOI
  Enter: 200000 ENTER 15000 ENTER CAP
  Result: 0.075 (7.5% cap rate)`,
  operandDescriptors: [
    { name: 'value', description: 'Property value' },
    { name: 'NOI', description: 'Net Operating Income' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('CAP requires 2 operands'));
      return;
    }
    const noi = stack.pop()!;
    const value = stack.pop()!;
    if (isBigNumber(value) && isBigNumber(noi)) {
      if (value.value.isZero()) {
        stack.push(calculationError('CAP: property value cannot be zero'));
      } else {
        stack.push(bigNumber(noi.value.dividedBy(value.value)));
      }
    } else {
      stack.push(calculationError('CAP requires numeric operands'));
    }
  },
};

/**
 * Net Operating Income: Gross Income - Operating Expenses
 * Stack: [... gross expenses NOI] -> [... noi]
 */
export const NetOperatingIncome: Calculation = {
  symbol: 'NOI',
  description: 'Net Operating Income',
  example: `Example: $30,000 gross income, $12,000 operating expenses
  Enter: 30000 ENTER 12000 ENTER NOI
  Result: $18,000`,
  operandDescriptors: [
    { name: 'gross', description: 'Gross income' },
    { name: 'expenses', description: 'Operating expenses' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('NOI requires 2 operands'));
      return;
    }
    const expenses = stack.pop()!;
    const gross = stack.pop()!;
    if (isBigNumber(gross) && isBigNumber(expenses)) {
      stack.push(bigNumber(gross.value.minus(expenses.value)));
    } else {
      stack.push(calculationError('NOI requires numeric operands'));
    }
  },
};

/**
 * Cash-on-Cash Return: Annual Cash Flow / Total Cash Invested
 * Stack: [... invested cashflow CoC] -> [... return]
 */
export const CashOnCash: Calculation = {
  symbol: 'CoC',
  description: 'Cash-on-Cash Return',
  example: `Example: $50,000 invested, $4,000 annual cash flow
  Enter: 50000 ENTER 4000 ENTER CoC
  Result: 0.08 (8% cash-on-cash return)`,
  operandDescriptors: [
    { name: 'invested', description: 'Total cash invested' },
    { name: 'cashflow', description: 'Annual cash flow' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('CoC requires 2 operands'));
      return;
    }
    const cashflow = stack.pop()!;
    const invested = stack.pop()!;
    if (isBigNumber(invested) && isBigNumber(cashflow)) {
      if (invested.value.isZero()) {
        stack.push(calculationError('CoC: invested amount cannot be zero'));
      } else {
        stack.push(bigNumber(cashflow.value.dividedBy(invested.value)));
      }
    } else {
      stack.push(calculationError('CoC requires numeric operands'));
    }
  },
};

/**
 * Debt Service Coverage Ratio: NOI / Debt Service
 * Stack: [... debt noi DSCR] -> [... ratio]
 */
export const DebtServiceCoverageRatio: Calculation = {
  symbol: 'DSCR',
  description: 'Debt Service Coverage Ratio',
  example: `Example: $18,000 annual debt service, $22,000 NOI
  Enter: 18000 ENTER 22000 ENTER DSCR
  Result: 1.222`,
  operandDescriptors: [
    { name: 'debt', description: 'Annual debt service' },
    { name: 'noi', description: 'Net Operating Income' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('DSCR requires 2 operands'));
      return;
    }
    const noi = stack.pop()!;
    const debt = stack.pop()!;
    if (isBigNumber(debt) && isBigNumber(noi)) {
      if (debt.value.isZero()) {
        stack.push(calculationError('DSCR: debt service cannot be zero'));
      } else {
        stack.push(bigNumber(noi.value.dividedBy(debt.value)));
      }
    } else {
      stack.push(calculationError('DSCR requires numeric operands'));
    }
  },
};

/**
 * Loan-to-Value: Loan Amount / Property Value
 * Stack: [... value loan LTV] -> [... ratio]
 */
export const LoanToValue: Calculation = {
  symbol: 'LTV',
  description: 'Loan-to-Value Ratio',
  example: `Example: $200,000 property, $160,000 loan
  Enter: 200000 ENTER 160000 ENTER LTV
  Result: 0.80 (80% LTV)`,
  operandDescriptors: [
    { name: 'value', description: 'Property value' },
    { name: 'loan', description: 'Loan amount' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('LTV requires 2 operands'));
      return;
    }
    const loan = stack.pop()!;
    const value = stack.pop()!;
    if (isBigNumber(value) && isBigNumber(loan)) {
      if (value.value.isZero()) {
        stack.push(calculationError('LTV: property value cannot be zero'));
      } else {
        stack.push(bigNumber(loan.value.dividedBy(value.value)));
      }
    } else {
      stack.push(calculationError('LTV requires numeric operands'));
    }
  },
};

/**
 * Gross Rent Multiplier: Property Price / Annual Rent
 * Stack: [... rent price GRM] -> [... multiplier]
 */
export const GrossRentMultiplier: Calculation = {
  symbol: 'GRM',
  description: 'Gross Rent Multiplier',
  example: `Example: $24,000 annual rent, $200,000 property price
  Enter: 24000 ENTER 200000 ENTER GRM
  Result: 8.333`,
  operandDescriptors: [
    { name: 'rent', description: 'Annual gross rent' },
    { name: 'price', description: 'Property price' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('GRM requires 2 operands'));
      return;
    }
    const price = stack.pop()!;
    const rent = stack.pop()!;
    if (isBigNumber(rent) && isBigNumber(price)) {
      if (rent.value.isZero()) {
        stack.push(calculationError('GRM: annual rent cannot be zero'));
      } else {
        stack.push(bigNumber(price.value.dividedBy(rent.value)));
      }
    } else {
      stack.push(calculationError('GRM requires numeric operands'));
    }
  },
};

/**
 * Return on Investment: (Gain - Cost) / Cost
 * Stack: [... cost gain ROI] -> [... return]
 */
export const ReturnOnInvestment: Calculation = {
  symbol: 'ROI',
  description: 'Return on Investment',
  example: `Example: Bought for $200,000, sold for $250,000
  Enter: 200000 ENTER 250000 ENTER ROI
  Result: 0.25 (25% ROI)`,
  operandDescriptors: [
    { name: 'cost', description: 'Initial investment cost' },
    { name: 'gain', description: 'Final value or sale price' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('ROI requires 2 operands'));
      return;
    }
    const gain = stack.pop()!;
    const cost = stack.pop()!;
    if (isBigNumber(cost) && isBigNumber(gain)) {
      if (cost.value.isZero()) {
        stack.push(calculationError('ROI: cost cannot be zero'));
      } else {
        stack.push(bigNumber(gain.value.minus(cost.value).dividedBy(cost.value)));
      }
    } else {
      stack.push(calculationError('ROI requires numeric operands'));
    }
  },
};

// ============================================================================
// INVESTMENT ANALYSIS
// ============================================================================

/**
 * Compound Annual Growth Rate
 * Stack: [... start end years CAGR] -> [... rate]
 */
export const CompoundAnnualGrowthRate: Calculation = {
  symbol: 'CAGR',
  description: 'Compound Annual Growth Rate',
  example: `Example: $10,000 grows to $15,000 in 5 years
  Enter: 10000 ENTER 15000 ENTER 5 ENTER CAGR
  Result: 0.0845 (8.45% average annual growth)`,
  operandDescriptors: [
    { name: 'start', description: 'Starting value' },
    { name: 'end', description: 'Ending value' },
    { name: 'years', description: 'Number of years' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('CAGR requires 3 operands'));
      return;
    }
    const years = stack.pop()!;
    const end = stack.pop()!;
    const start = stack.pop()!;
    if (isBigNumber(start) && isBigNumber(end) && isBigNumber(years)) {
      const s = start.value.toNumber();
      const e = end.value.toNumber();
      const y = years.value.toNumber();

      if (s <= 0 || e <= 0) {
        stack.push(calculationError('CAGR: values must be positive'));
        return;
      }
      if (y <= 0) {
        stack.push(calculationError('CAGR: years must be positive'));
        return;
      }

      const cagr = Math.pow(e / s, 1 / y) - 1;
      stack.push(bigNumber(cagr));
    } else {
      stack.push(calculationError('CAGR requires numeric operands'));
    }
  },
};

/**
 * Break-Even Point: Fixed Costs / (Price - Variable Cost)
 * Stack: [... fixed price variable BEP] -> [... units]
 */
export const BreakEvenPoint: Calculation = {
  symbol: 'BEP',
  description: 'Break-Even Point',
  example: `Example: $50,000 fixed costs, $100 price, $60 variable cost
  Enter: 50000 ENTER 100 ENTER 60 ENTER BEP
  Result: 1250 units`,
  operandDescriptors: [
    { name: 'fixed', description: 'Fixed costs' },
    { name: 'price', description: 'Unit price' },
    { name: 'variable', description: 'Variable cost per unit' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('BEP requires 3 operands'));
      return;
    }
    const variable = stack.pop()!;
    const price = stack.pop()!;
    const fixed = stack.pop()!;
    if (isBigNumber(fixed) && isBigNumber(price) && isBigNumber(variable)) {
      const margin = price.value.minus(variable.value);
      if (margin.isZero() || margin.isNegative()) {
        stack.push(calculationError('BEP: price must exceed variable cost'));
      } else {
        stack.push(bigNumber(fixed.value.dividedBy(margin)));
      }
    } else {
      stack.push(calculationError('BEP requires numeric operands'));
    }
  },
};

/**
 * Profitability Index: PV of Future Cash Flows / Initial Investment
 * Stack: [... investment pv PI] -> [... index]
 */
export const ProfitabilityIndex: Calculation = {
  symbol: 'PI',
  description: 'Profitability Index',
  example: `Example: $100,000 investment, $120,000 PV of future cash flows
  Enter: 100000 ENTER 120000 ENTER PI
  Result: 1.20 (PI > 1.0, accept project)`,
  operandDescriptors: [
    { name: 'investment', description: 'Initial investment' },
    { name: 'pv', description: 'Present value of future cash flows' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('PI requires 2 operands'));
      return;
    }
    const pv = stack.pop()!;
    const investment = stack.pop()!;
    if (isBigNumber(investment) && isBigNumber(pv)) {
      if (investment.value.isZero()) {
        stack.push(calculationError('PI: investment cannot be zero'));
      } else {
        stack.push(bigNumber(pv.value.dividedBy(investment.value)));
      }
    } else {
      stack.push(calculationError('PI requires numeric operands'));
    }
  },
};
