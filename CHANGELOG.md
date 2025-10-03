# Changelog

All notable changes to finCalc will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-10-03

### Added
- **Core Calculator Engine**
  - RPN (Reverse Polish Notation) stack-based calculator
  - Arbitrary precision using BigDecimal
  - Comprehensive error handling and validation
  - Stack operations (Drop, Swap, Clear)
  - Configurable precision (1-50 decimal places)

- **Basic Operations** (6 operations)
  - Addition, Subtraction, Multiplication, Division
  - Supports arbitrary precision arithmetic

- **Scientific Functions** (15 operations)
  - Trigonometric: sin, cos, tan, asin, acos, atan
  - Logarithmic: log10, ln, exp, 10^x
  - Power functions: √, x², x^y, nth-root, 1/x
  - Utility: absolute value, modulo

- **Time Value of Money (TVM)** (5 operations)
  - Payment (PMT)
  - Present Value (PV)
  - Future Value (FV)
  - Interest Rate (RATE)
  - Number of Periods (NPER)

- **Real Estate Analysis** (13 operations)
  - Cap Rate (CAP)
  - Net Operating Income (NOI)
  - Cash-on-Cash Return (CoC)
  - Debt Service Coverage Ratio (DSCR)
  - Loan-to-Value (LTV)
  - Gross Rent Multiplier (GRM)
  - Return on Investment (ROI)
  - Cash Flow After Taxes (CFAT)
  - Operating Expense Ratio (OER)
  - Vacancy Loss
  - Effective Gross Income (EGI)
  - Price Per Square Foot (PPSF)
  - Rent Per Square Foot (RPSF)

- **Investment Analysis** (4 operations)
  - Compound Annual Growth Rate (CAGR)
  - Break-Even Point (BEP)
  - Payback Period
  - Profitability Index (PI)

- **Bond Calculations** (3 operations)
  - Current Yield
  - Yield to Maturity (YTM)
  - Bond Price

- **Loan & Mortgage** (4 operations)
  - Remaining Balance
  - Total Interest Paid
  - APR to APY Conversion
  - Debt-to-Income Ratio (DTI)

- **Tax & Retirement** (3 operations)
  - Effective Tax Rate
  - After-Tax Return
  - Required Minimum Distribution (RMD)

- **Options Trading** (2 operations)
  - Annualized Option Return (AOPT) - for cash-secured puts/premium-only
  - Covered Call Return (CCR) - including capital appreciation

- **User Interface**
  - Modern dark theme JavaFX interface
  - Color-coded operation buttons by category
  - Real-time stack display
  - RPN expression visualization
  - Menu-based operation access with wizards
  - Keyboard support for all operations
  - Right-click context help for financial operations

- **macOS Applications**
  - finCalc.app - Lightweight launcher (requires Maven)
  - richCalc.app - Standalone app with bundled Java runtime
  - Custom application icon
  - Native macOS integration

- **Testing**
  - 530 comprehensive unit tests
  - 100% operation coverage (53/53 operations tested)
  - Edge case and error condition testing
  - JUnit 5 test framework

- **Documentation**
  - Comprehensive README.md with examples
  - JavaDoc documentation for all operations
  - Usage examples for RPN, financial, and options calculations
  - MIT License

### Technical Details
- Java 17+ required
- JavaFX 21 for UI
- Maven build system
- BigDecimal for arbitrary precision
- Enum singleton pattern for operations
- Stack-based architecture

### Known Limitations
- macOS apps currently Apple Silicon only (M1/M2/M3)
- No Windows/Linux native apps yet (JAR works cross-platform)
- Financial calculations assume annual compounding unless specified
- Options calculations assume American-style exercise at expiration

[1.0.0]: https://github.com/richardahasting/finCalc/releases/tag/v1.0.0
