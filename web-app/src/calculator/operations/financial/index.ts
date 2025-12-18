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

/**
 * Payback Period: time to recover initial investment
 * Stack: [... investment cashflow PBP] -> [... years]
 */
export const PaybackPeriod: Calculation = {
  symbol: 'PBP',
  description: 'Payback Period',
  example: `Example: $100,000 investment, $25,000 annual cash flow
  Enter: 100000 ENTER 25000 ENTER PBP
  Result: 4.0 (4 years to recover investment)`,
  operandDescriptors: [
    { name: 'investment', description: 'Initial investment amount' },
    { name: 'cashflow', description: 'Annual cash flow (assumed constant)' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('PBP requires 2 operands'));
      return;
    }
    const cashflow = stack.pop()!;
    const investment = stack.pop()!;
    if (isBigNumber(investment) && isBigNumber(cashflow)) {
      if (investment.value.lte(0)) {
        stack.push(calculationError('PBP: investment must be positive'));
      } else if (cashflow.value.lte(0)) {
        stack.push(calculationError('PBP: cash flow must be positive'));
      } else {
        stack.push(bigNumber(investment.value.dividedBy(cashflow.value)));
      }
    } else {
      stack.push(calculationError('PBP requires numeric operands'));
    }
  },
};

// ============================================================================
// BOND OPERATIONS
// ============================================================================

/**
 * Current Yield: bond's annual return based on current price
 * Stack: [... price coupon YIELD] -> [... yield]
 */
export const CurrentYield: Calculation = {
  symbol: 'YIELD',
  description: 'Current Yield',
  example: `Example: $950 current price, $60 annual coupon
  Enter: 950 ENTER 60 ENTER YIELD
  Result: 0.0632 (6.32% current yield)`,
  operandDescriptors: [
    { name: 'price', description: 'Current market price of bond' },
    { name: 'coupon', description: 'Annual coupon payment' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('YIELD requires 2 operands'));
      return;
    }
    const coupon = stack.pop()!;
    const price = stack.pop()!;
    if (isBigNumber(price) && isBigNumber(coupon)) {
      if (price.value.lte(0)) {
        stack.push(calculationError('YIELD: price must be positive'));
      } else {
        stack.push(bigNumber(coupon.value.dividedBy(price.value)));
      }
    } else {
      stack.push(calculationError('YIELD requires numeric operands'));
    }
  },
};

/**
 * Yield to Maturity (approximation)
 * Stack: [... price face coupon years YTM] -> [... ytm]
 */
export const YieldToMaturity: Calculation = {
  symbol: 'YTM',
  description: 'Yield to Maturity',
  example: `Example: $950 price, $1,000 face, $60 coupon, 10 years
  Enter: 950 ENTER 1000 ENTER 60 ENTER 10 ENTER YTM
  Result: 0.0662 (6.62% yield to maturity)`,
  operandDescriptors: [
    { name: 'price', description: 'Current market price' },
    { name: 'face', description: 'Face value (par value)' },
    { name: 'coupon', description: 'Annual coupon payment' },
    { name: 'years', description: 'Years to maturity' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 4) {
      stack.push(calculationError('YTM requires 4 operands'));
      return;
    }
    const yearsItem = stack.pop()!;
    const couponItem = stack.pop()!;
    const faceItem = stack.pop()!;
    const priceItem = stack.pop()!;
    if (isBigNumber(priceItem) && isBigNumber(faceItem) && isBigNumber(couponItem) && isBigNumber(yearsItem)) {
      const price = priceItem.value.toNumber();
      const face = faceItem.value.toNumber();
      const coupon = couponItem.value.toNumber();
      const years = yearsItem.value.toNumber();
      if (price <= 0 || face <= 0 || years <= 0) {
        stack.push(calculationError('YTM: price, face, and years must be positive'));
        return;
      }
      // YTM approximation: [C + (F - P) / n] / [(F + P) / 2]
      const ytm = (coupon + (face - price) / years) / ((face + price) / 2);
      stack.push(bigNumber(ytm));
    } else {
      stack.push(calculationError('YTM requires numeric operands'));
    }
  },
};

/**
 * Bond Price: present value of bond's cash flows
 * Stack: [... face couponRate yieldRate periods BPRC] -> [... price]
 */
export const BondPrice: Calculation = {
  symbol: 'BPRC',
  description: 'Bond Price',
  example: `Example: $1,000 face, 3% coupon rate, 2.5% yield, 20 periods
  Enter: 1000 ENTER 0.03 ENTER 0.025 ENTER 20 ENTER BPRC
  Result: 1078.27 (Bond trades at premium)`,
  operandDescriptors: [
    { name: 'face', description: 'Face value of bond' },
    { name: 'couponRate', description: 'Coupon rate per period' },
    { name: 'yieldRate', description: 'Market yield rate per period' },
    { name: 'periods', description: 'Number of periods to maturity' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 4) {
      stack.push(calculationError('BPRC requires 4 operands'));
      return;
    }
    const periodsItem = stack.pop()!;
    const yieldItem = stack.pop()!;
    const couponItem = stack.pop()!;
    const faceItem = stack.pop()!;
    if (isBigNumber(faceItem) && isBigNumber(couponItem) && isBigNumber(yieldItem) && isBigNumber(periodsItem)) {
      const face = faceItem.value.toNumber();
      const couponRate = couponItem.value.toNumber();
      const yieldRate = yieldItem.value.toNumber();
      const periods = periodsItem.value.toNumber();
      if (face <= 0 || periods <= 0) {
        stack.push(calculationError('BPRC: face and periods must be positive'));
        return;
      }
      const couponPayment = face * couponRate;
      if (yieldRate === 0) {
        stack.push(bigNumber(couponPayment * periods + face));
        return;
      }
      const discountFactor = Math.pow(1 + yieldRate, -periods);
      const pvCoupons = couponPayment * (1 - discountFactor) / yieldRate;
      const pvFace = face * discountFactor;
      stack.push(bigNumber(pvCoupons + pvFace));
    } else {
      stack.push(calculationError('BPRC requires numeric operands'));
    }
  },
};

// ============================================================================
// LOAN OPERATIONS
// ============================================================================

/**
 * Remaining Balance: outstanding principal on loan
 * Stack: [... pv rate nper paymentsMade BAL] -> [... balance]
 */
export const RemainingBalance: Calculation = {
  symbol: 'BAL',
  description: 'Remaining Loan Balance',
  example: `Example: $200,000 loan at 0.5% monthly, 360 periods, 60 payments made
  Enter: 200000 ENTER 0.005 ENTER 360 ENTER 60 ENTER BAL
  Result: 188202.50`,
  operandDescriptors: [
    { name: 'pv', description: 'Original loan amount' },
    { name: 'rate', description: 'Interest rate per period' },
    { name: 'nper', description: 'Total number of periods' },
    { name: 'paymentsMade', description: 'Payments already made' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 4) {
      stack.push(calculationError('BAL requires 4 operands'));
      return;
    }
    const pmtsMadeItem = stack.pop()!;
    const nperItem = stack.pop()!;
    const rateItem = stack.pop()!;
    const pvItem = stack.pop()!;
    if (isBigNumber(pvItem) && isBigNumber(rateItem) && isBigNumber(nperItem) && isBigNumber(pmtsMadeItem)) {
      const pv = pvItem.value.toNumber();
      const rate = rateItem.value.toNumber();
      const nper = nperItem.value.toNumber();
      const pmtsMade = pmtsMadeItem.value.toNumber();
      if (rate <= 0 || nper <= 0 || pv <= 0) {
        stack.push(calculationError('BAL: rate, periods, and PV must be positive'));
        return;
      }
      if (pmtsMade < 0 || pmtsMade > nper) {
        stack.push(calculationError('BAL: payments made must be between 0 and total periods'));
        return;
      }
      const factor = Math.pow(1 + rate, nper);
      const payment = pv * (rate * factor) / (factor - 1);
      const factorN = Math.pow(1 + rate, pmtsMade);
      const balance = pv * factorN - payment * ((factorN - 1) / rate);
      stack.push(bigNumber(balance));
    } else {
      stack.push(calculationError('BAL requires numeric operands'));
    }
  },
};

/**
 * Total Interest Paid over life of loan
 * Stack: [... pv pmt nper TINT] -> [... totalInterest]
 */
export const TotalInterestPaid: Calculation = {
  symbol: 'TINT',
  description: 'Total Interest Paid',
  example: `Example: $200,000 loan, $1,199.10 payment, 360 months
  Enter: 200000 ENTER 1199.10 ENTER 360 ENTER TINT
  Result: 231676`,
  operandDescriptors: [
    { name: 'pv', description: 'Original loan amount' },
    { name: 'pmt', description: 'Payment per period' },
    { name: 'nper', description: 'Total number of periods' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('TINT requires 3 operands'));
      return;
    }
    const nperItem = stack.pop()!;
    const pmtItem = stack.pop()!;
    const pvItem = stack.pop()!;
    if (isBigNumber(pvItem) && isBigNumber(pmtItem) && isBigNumber(nperItem)) {
      const pv = pvItem.value.toNumber();
      const pmt = pmtItem.value.toNumber();
      const nper = nperItem.value.toNumber();
      if (pv <= 0 || pmt <= 0 || nper <= 0) {
        stack.push(calculationError('TINT: all values must be positive'));
        return;
      }
      stack.push(bigNumber((pmt * nper) - pv));
    } else {
      stack.push(calculationError('TINT requires numeric operands'));
    }
  },
};

/**
 * APR to APY conversion
 * Stack: [... apr periods APY] -> [... apy]
 */
export const AprToApy: Calculation = {
  symbol: 'APY',
  description: 'APR to APY Conversion',
  example: `Example: 6% APR compounded monthly
  Enter: 0.06 ENTER 12 ENTER APY
  Result: 0.0617 (6.17% APY)`,
  operandDescriptors: [
    { name: 'apr', description: 'Annual Percentage Rate (as decimal)' },
    { name: 'periods', description: 'Compounding periods per year' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('APY requires 2 operands'));
      return;
    }
    const periodsItem = stack.pop()!;
    const aprItem = stack.pop()!;
    if (isBigNumber(aprItem) && isBigNumber(periodsItem)) {
      const apr = aprItem.value.toNumber();
      const n = periodsItem.value.toNumber();
      if (apr < 0) {
        stack.push(calculationError('APY: APR cannot be negative'));
        return;
      }
      if (n <= 0) {
        stack.push(calculationError('APY: compounding periods must be positive'));
        return;
      }
      stack.push(bigNumber(Math.pow(1 + apr / n, n) - 1));
    } else {
      stack.push(calculationError('APY requires numeric operands'));
    }
  },
};

/**
 * Debt-to-Income Ratio
 * Stack: [... income debt DTI] -> [... ratio]
 */
export const DebtToIncomeRatio: Calculation = {
  symbol: 'DTI',
  description: 'Debt-to-Income Ratio',
  example: `Example: $8,000 monthly income, $2,400 monthly debt
  Enter: 8000 ENTER 2400 ENTER DTI
  Result: 0.30 (30% DTI)`,
  operandDescriptors: [
    { name: 'income', description: 'Gross monthly income' },
    { name: 'debt', description: 'Total monthly debt payments' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('DTI requires 2 operands'));
      return;
    }
    const debtItem = stack.pop()!;
    const incomeItem = stack.pop()!;
    if (isBigNumber(incomeItem) && isBigNumber(debtItem)) {
      if (incomeItem.value.lte(0)) {
        stack.push(calculationError('DTI: income must be positive'));
      } else {
        stack.push(bigNumber(debtItem.value.dividedBy(incomeItem.value)));
      }
    } else {
      stack.push(calculationError('DTI requires numeric operands'));
    }
  },
};

// ============================================================================
// OPTIONS TRADING
// ============================================================================

/**
 * Annualized Option Return (cash-secured puts)
 * Stack: [... strike premium days AOPT] -> [... return]
 */
export const AnnualizedOptionReturn: Calculation = {
  symbol: 'AOPT',
  description: 'Annualized Option Return',
  example: `Example: $12.50 strike, $0.26 premium, 10 days
  Enter: 12.50 ENTER 0.26 ENTER 10 ENTER AOPT
  Result: 0.7592 (75.92% annualized)`,
  operandDescriptors: [
    { name: 'strike', description: 'Strike price (capital at risk)' },
    { name: 'premium', description: 'Premium received' },
    { name: 'days', description: 'Days to expiration' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 3) {
      stack.push(calculationError('AOPT requires 3 operands'));
      return;
    }
    const daysItem = stack.pop()!;
    const premiumItem = stack.pop()!;
    const strikeItem = stack.pop()!;
    if (isBigNumber(strikeItem) && isBigNumber(premiumItem) && isBigNumber(daysItem)) {
      const strike = strikeItem.value.toNumber();
      const premium = premiumItem.value.toNumber();
      const days = daysItem.value.toNumber();
      if (strike <= 0 || premium <= 0 || days <= 0) {
        stack.push(calculationError('AOPT: all values must be positive'));
        return;
      }
      const annualized = (premium / days) * 365 / strike;
      stack.push(bigNumber(annualized));
    } else {
      stack.push(calculationError('AOPT requires numeric operands'));
    }
  },
};

/**
 * Covered Call Return
 * Stack: [... cost strike premium days CCR] -> [... return]
 */
export const CoveredCallReturn: Calculation = {
  symbol: 'CCR',
  description: 'Covered Call Return',
  example: `Example: $50 cost, $52 strike, $1.50 premium, 30 days
  Enter: 50 ENTER 52 ENTER 1.50 ENTER 30 ENTER CCR
  Result: 0.8517 (85.17% annualized if called)`,
  operandDescriptors: [
    { name: 'cost', description: 'Stock cost basis' },
    { name: 'strike', description: 'Call strike price' },
    { name: 'premium', description: 'Premium received' },
    { name: 'days', description: 'Days to expiration' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 4) {
      stack.push(calculationError('CCR requires 4 operands'));
      return;
    }
    const daysItem = stack.pop()!;
    const premiumItem = stack.pop()!;
    const strikeItem = stack.pop()!;
    const costItem = stack.pop()!;
    if (isBigNumber(costItem) && isBigNumber(strikeItem) && isBigNumber(premiumItem) && isBigNumber(daysItem)) {
      const cost = costItem.value.toNumber();
      const strike = strikeItem.value.toNumber();
      const premium = premiumItem.value.toNumber();
      const days = daysItem.value.toNumber();
      if (cost <= 0 || strike <= 0 || days <= 0) {
        stack.push(calculationError('CCR: cost, strike, and days must be positive'));
        return;
      }
      const totalReturn = premium + (strike - cost);
      const returnPct = totalReturn / cost;
      const annualized = returnPct * (365 / days);
      stack.push(bigNumber(annualized));
    } else {
      stack.push(calculationError('CCR requires numeric operands'));
    }
  },
};

// ============================================================================
// TAX & RETIREMENT
// ============================================================================

/**
 * Effective Tax Rate
 * Stack: [... income tax ETR] -> [... rate]
 */
export const EffectiveTaxRate: Calculation = {
  symbol: 'ETR',
  description: 'Effective Tax Rate',
  example: `Example: $100,000 income, $18,000 tax paid
  Enter: 100000 ENTER 18000 ENTER ETR
  Result: 0.18 (18% effective tax rate)`,
  operandDescriptors: [
    { name: 'income', description: 'Total gross income' },
    { name: 'tax', description: 'Total tax paid' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('ETR requires 2 operands'));
      return;
    }
    const taxItem = stack.pop()!;
    const incomeItem = stack.pop()!;
    if (isBigNumber(incomeItem) && isBigNumber(taxItem)) {
      if (incomeItem.value.lte(0)) {
        stack.push(calculationError('ETR: income must be positive'));
      } else {
        stack.push(bigNumber(taxItem.value.dividedBy(incomeItem.value)));
      }
    } else {
      stack.push(calculationError('ETR requires numeric operands'));
    }
  },
};

/**
 * After-Tax Return
 * Stack: [... pretaxReturn taxRate ATR] -> [... afterTaxReturn]
 */
export const AfterTaxReturn: Calculation = {
  symbol: 'ATR',
  description: 'After-Tax Return',
  example: `Example: 8% pre-tax return, 25% tax rate
  Enter: 0.08 ENTER 0.25 ENTER ATR
  Result: 0.06 (6% after-tax return)`,
  operandDescriptors: [
    { name: 'pretaxReturn', description: 'Pre-tax return (as decimal)' },
    { name: 'taxRate', description: 'Tax rate (as decimal)' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('ATR requires 2 operands'));
      return;
    }
    const taxRateItem = stack.pop()!;
    const returnItem = stack.pop()!;
    if (isBigNumber(returnItem) && isBigNumber(taxRateItem)) {
      const pretax = returnItem.value.toNumber();
      const taxRate = taxRateItem.value.toNumber();
      if (taxRate < 0 || taxRate > 1) {
        stack.push(calculationError('ATR: tax rate must be between 0 and 1'));
        return;
      }
      stack.push(bigNumber(pretax * (1 - taxRate)));
    } else {
      stack.push(calculationError('ATR requires numeric operands'));
    }
  },
};

/**
 * Required Minimum Distribution
 * Stack: [... balance period RMD] -> [... rmd]
 */
export const RequiredMinimumDistribution: Calculation = {
  symbol: 'RMD',
  description: 'Required Minimum Distribution',
  example: `Example: $500,000 balance, 25.6 distribution period (age 73)
  Enter: 500000 ENTER 25.6 ENTER RMD
  Result: 19531.25`,
  operandDescriptors: [
    { name: 'balance', description: 'Account balance as of Dec 31' },
    { name: 'period', description: 'Distribution period from IRS table' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('RMD requires 2 operands'));
      return;
    }
    const periodItem = stack.pop()!;
    const balanceItem = stack.pop()!;
    if (isBigNumber(balanceItem) && isBigNumber(periodItem)) {
      if (periodItem.value.lte(0)) {
        stack.push(calculationError('RMD: distribution period must be positive'));
      } else {
        stack.push(bigNumber(balanceItem.value.dividedBy(periodItem.value)));
      }
    } else {
      stack.push(calculationError('RMD requires numeric operands'));
    }
  },
};

// ============================================================================
// ADDITIONAL REAL ESTATE OPERATIONS
// ============================================================================

/**
 * Operating Expense Ratio
 * Stack: [... income expenses OER] -> [... ratio]
 */
export const OperatingExpenseRatio: Calculation = {
  symbol: 'OER',
  description: 'Operating Expense Ratio',
  example: `Example: $80,000 gross income, $32,000 operating expenses
  Enter: 80000 ENTER 32000 ENTER OER
  Result: 0.40 (40% OER)`,
  operandDescriptors: [
    { name: 'income', description: 'Gross operating income' },
    { name: 'expenses', description: 'Total operating expenses' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('OER requires 2 operands'));
      return;
    }
    const expensesItem = stack.pop()!;
    const incomeItem = stack.pop()!;
    if (isBigNumber(incomeItem) && isBigNumber(expensesItem)) {
      if (incomeItem.value.lte(0)) {
        stack.push(calculationError('OER: income must be positive'));
      } else {
        stack.push(bigNumber(expensesItem.value.dividedBy(incomeItem.value)));
      }
    } else {
      stack.push(calculationError('OER requires numeric operands'));
    }
  },
};

/**
 * Effective Gross Income
 * Stack: [... vacancy potential EGI] -> [... egi]
 */
export const EffectiveGrossIncome: Calculation = {
  symbol: 'EGI',
  description: 'Effective Gross Income',
  example: `Example: $5,000 vacancy loss, $100,000 potential gross income
  Enter: 5000 ENTER 100000 ENTER EGI
  Result: 95000`,
  operandDescriptors: [
    { name: 'vacancy', description: 'Vacancy and credit losses' },
    { name: 'potential', description: 'Potential gross income at 100% occupancy' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('EGI requires 2 operands'));
      return;
    }
    const potentialItem = stack.pop()!;
    const vacancyItem = stack.pop()!;
    if (isBigNumber(vacancyItem) && isBigNumber(potentialItem)) {
      stack.push(bigNumber(potentialItem.value.minus(vacancyItem.value)));
    } else {
      stack.push(calculationError('EGI requires numeric operands'));
    }
  },
};

/**
 * Cash Flow After Taxes
 * Stack: [... cfbt tax CFAT] -> [... cfat]
 */
export const CashFlowAfterTaxes: Calculation = {
  symbol: 'CFAT',
  description: 'Cash Flow After Taxes',
  example: `Example: $15,000 cash flow before taxes, $3,000 tax
  Enter: 15000 ENTER 3000 ENTER CFAT
  Result: 12000`,
  operandDescriptors: [
    { name: 'cfbt', description: 'Cash flow before taxes' },
    { name: 'tax', description: 'Tax liability' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('CFAT requires 2 operands'));
      return;
    }
    const taxItem = stack.pop()!;
    const cfbtItem = stack.pop()!;
    if (isBigNumber(cfbtItem) && isBigNumber(taxItem)) {
      stack.push(bigNumber(cfbtItem.value.minus(taxItem.value)));
    } else {
      stack.push(calculationError('CFAT requires numeric operands'));
    }
  },
};

/**
 * Vacancy Loss
 * Stack: [... potential rate VAC] -> [... loss]
 */
export const VacancyLoss: Calculation = {
  symbol: 'VAC',
  description: 'Vacancy Loss',
  example: `Example: $100,000 potential income, 5% vacancy rate
  Enter: 100000 ENTER 0.05 ENTER VAC
  Result: 5000`,
  operandDescriptors: [
    { name: 'potential', description: 'Potential gross income' },
    { name: 'rate', description: 'Vacancy rate (as decimal)' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('VAC requires 2 operands'));
      return;
    }
    const rateItem = stack.pop()!;
    const potentialItem = stack.pop()!;
    if (isBigNumber(potentialItem) && isBigNumber(rateItem)) {
      const rate = rateItem.value.toNumber();
      if (rate < 0 || rate > 1) {
        stack.push(calculationError('VAC: vacancy rate must be between 0 and 1'));
        return;
      }
      stack.push(bigNumber(potentialItem.value.times(rateItem.value)));
    } else {
      stack.push(calculationError('VAC requires numeric operands'));
    }
  },
};

/**
 * Price Per Square Foot
 * Stack: [... sqft price PPSF] -> [... pricePerSqft]
 */
export const PricePerSquareFoot: Calculation = {
  symbol: 'PPSF',
  description: 'Price Per Square Foot',
  example: `Example: 2,000 sq ft, $300,000 price
  Enter: 2000 ENTER 300000 ENTER PPSF
  Result: 150 ($150/sqft)`,
  operandDescriptors: [
    { name: 'sqft', description: 'Total square footage' },
    { name: 'price', description: 'Property price' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('PPSF requires 2 operands'));
      return;
    }
    const priceItem = stack.pop()!;
    const sqftItem = stack.pop()!;
    if (isBigNumber(sqftItem) && isBigNumber(priceItem)) {
      if (sqftItem.value.lte(0)) {
        stack.push(calculationError('PPSF: square feet must be positive'));
      } else {
        stack.push(bigNumber(priceItem.value.dividedBy(sqftItem.value)));
      }
    } else {
      stack.push(calculationError('PPSF requires numeric operands'));
    }
  },
};

/**
 * Rent Per Square Foot
 * Stack: [... sqft rent RPSF] -> [... rentPerSqft]
 */
export const RentPerSquareFoot: Calculation = {
  symbol: 'RPSF',
  description: 'Rent Per Square Foot',
  example: `Example: 1,500 sq ft, $36,000 annual rent
  Enter: 1500 ENTER 36000 ENTER RPSF
  Result: 24 ($24/sqft annually)`,
  operandDescriptors: [
    { name: 'sqft', description: 'Rentable square footage' },
    { name: 'rent', description: 'Annual rent' },
  ],
  execute(stack: StackItem[]): void {
    if (stack.length < 2) {
      stack.push(calculationError('RPSF requires 2 operands'));
      return;
    }
    const rentItem = stack.pop()!;
    const sqftItem = stack.pop()!;
    if (isBigNumber(sqftItem) && isBigNumber(rentItem)) {
      if (sqftItem.value.lte(0)) {
        stack.push(calculationError('RPSF: square feet must be positive'));
      } else {
        stack.push(bigNumber(rentItem.value.dividedBy(sqftItem.value)));
      }
    } else {
      stack.push(calculationError('RPSF requires numeric operands'));
    }
  },
};
