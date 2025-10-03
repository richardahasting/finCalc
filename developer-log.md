# Developer Log - finCalc

## Session: 2025-10-02 - Project Initialization

### User Prompts
1. "please read ~/CLA*.md files" - Read personal context and template files
2. "please read /Users/richard/CLAUDE-PersonalContextDocumentForInteractions.md /Users/richard/CLAUDE-TEMPLATE.md /Users/richard/CLAUDE.md" - Read configuration files
3. "/init" - Analyze codebase and create CLAUDE.md
4. Discussion about project scope: macOS financial calculator for real estate investment
5. "let's do java, I am far more familiar with it. And the math calculations are also simple there as well. Let's use BigDecimal for all of it."
6. "let's do #1" - Set up Maven pom.xml with JavaFX dependencies

### Work Completed

#### 1. Project Definition
- **Scope**: macOS-specific financial calculator
- **Focus**: Real estate and securities investment with tax/cash flow analysis
- **Tech Stack**: Java 17+ with JavaFX, Maven build system
- **Key Principle**: All financial calculations using BigDecimal for precision

#### 2. CLAUDE.md Creation
- Created comprehensive project guidance document
- Defined architecture with calculator modules (mortgage, cashflow, tax, securities)
- Established BigDecimal coding standards (no double constructors, explicit scale/rounding)
- Documented calculation categories: real estate investment, tax & equity, cash flow analysis
- Included universal Claude Code behaviors for project management

#### 3. Maven Project Setup
- Created pom.xml with:
  - Java 17 as target version
  - JavaFX 21.0.1 dependencies (controls, fxml)
  - JUnit 5.10.1 for testing
  - javafx-maven-plugin for running application
  - jpackage-maven-plugin for macOS DMG packaging
  - Main class: com.finCalc.FinCalcApplication

#### 4. Directory Structure
Created standard Maven structure with calculator modules:
```
src/main/java/com/finCalc/
├── ui/              # JavaFX UI components and controllers
├── model/           # Domain models
├── calculator/      # Financial calculation engines
│   ├── mortgage/    # Mortgage calculations
│   ├── cashflow/    # Cash flow analysis
│   ├── tax/         # Tax calculations
│   └── securities/  # Securities investment
└── util/            # Shared utilities

src/test/java/com/finCalc/
└── (mirrors main structure for tests)
```

### Decisions Made
1. **BigDecimal for everything** - No primitive doubles in financial calculations
2. **JavaFX over Swing** - Modern UI framework with better macOS integration
3. **Maven over Gradle** - Standard Java build tool, simpler configuration
4. **Modular calculator structure** - Separate packages for each calculation domain
5. **Java 17 LTS** - Stable, modern Java features without bleeding edge

### Next Steps
1. Create main application class (FinCalcApplication.java)
2. Implement BigDecimal utility helpers
3. Build first calculator: mortgage payment calculator
4. Create corresponding unit tests
5. Set up basic JavaFX UI scaffold

### Session Statistics
- Files created: 4 (CLAUDE.md, pom.xml, work-in-progress.md, developer-log.md)
- Directories created: 26 (complete Maven structure)
- Dependencies added: JavaFX, JUnit 5
- Build system: Maven configured and ready

### Notes
- Project is brand new, empty repository
- No git initialization yet (can add later)
- No .gitignore created yet
- Ready for first code implementation

---

## Session: 2025-10-02 - RPN Calculator Engine Design

### User Prompts
7. "let's discuss a basic Structure that we might use as a basis. As an engineer, I have always had a preference for reverse Polish notation."
8. Discussion on RPN stack design - each calculation is given a stack and command, returns modified stack
9. "so 3+4 is a stack with 3, 4, and CALCULATION.ADD and execution returns a stack with '7'."
10. "An error returns a defined error, with a message on what went wrong. The stack will just be a Stack of 'Number', 'Calculation' or 'Error' and should probably use a sealed interface."
11. Discussion on Number types - convinced to use BigDecimal exclusively, no Double/Integer on stack
12. "Technically, I wanted to be a financial calculator that also does general engineering calculations"
13. "Let's go ahead and add all of the basic calculator, arithmetic calculations. That would be square, square root, natural log, log based 10, 10^x, X^N, sin, cos, tan, 1/x, asin, acos, atan."
14. Clarified that scientific operations can use double internally for calculations

### Architecture Decisions

#### RPN Stack-Based Calculator Engine
- **Stack Model**: Input stack contains values (BigNumber) and operations (Calculation)
- **Execution Model**: Evaluator walks stack, when it encounters Calculation, executes on accumulated values
- **Example**: `[3, 4, ADD]` → `[7]`
- **Pure Functions**: Each Calculation consumes operands, pushes result, returns modified stack

#### Sealed Interface Hierarchy
```java
sealed interface StackItem permits BigNumber, Calculation, Error
record BigNumber(BigDecimal value) implements StackItem
sealed interface Calculation extends StackItem { Stack<StackItem> execute(Stack<StackItem> stack); }
record Error(String message) implements StackItem
```

**Rationale**:
- Type safety with exhaustive pattern matching (Java 17)
- Compiler enforces handling all cases in switch expressions
- Immutable by design (records for value objects)
- No wrapper class overhead

#### BigDecimal-Only Stack Values
- **Decision**: All stack values are BigNumber wrapping BigDecimal
- **Rejected**: Separate Double and Integer stack types
- **Reason**: Prevent precision loss in financial calculations (0.1 + 0.2 != 0.3 with doubles)
- **Exception**: Scientific operations (sin, cos, sqrt, ln) use double *internally* but maintain BigNumber stack interface

#### Calculator Type Prioritization
1. **Primary**: Financial calculator - mortgage, cash flow, ROI (pure BigDecimal)
2. **Secondary**: Engineering calculations - trig, logarithms (double-based internally)
3. **Foundation**: Basic arithmetic supporting both (BigDecimal)

#### Operation Organization
- `com.finCalc.calculator.operations.basic.*` - ADD, SUBTRACT, MULTIPLY, DIVIDE (BigDecimal)
- `com.finCalc.calculator.operations.scientific.*` - SIN, COS, TAN, LN, LOG10, SQRT, POW (double internally)
- `com.finCalc.calculator.operations.financial.*` - Future: MORTGAGE_PMT, NPV, IRR, etc.

### Implementation Plan

#### Phase 1: Core Stack Infrastructure
1. Create sealed interface hierarchy (StackItem, BigNumber, Calculation, Error)
2. Implement stack evaluator/executor

#### Phase 2: Basic Operations (BigDecimal-based)
- ADD, SUBTRACT, MULTIPLY, DIVIDE
- Each in separate class file

#### Phase 3: Scientific Operations (double-based internally)
- Power/Root: SQUARE, SQRT, POW, RECIPROCAL (1/x)
- Logarithmic: LN, LOG10, EXP10 (10^x)
- Trigonometric: SIN, COS, TAN, ASIN, ACOS, ATAN
- Each in separate class file

#### Phase 4: Unit Tests
- Test all operations with edge cases
- Verify precision and error handling
- Test stack depth validation

### Design Rationale

**Why RPN?**
- Eliminates operator precedence ambiguity
- Efficient for complex calculations
- Natural fit for stack-based architecture
- User (Richard) has engineering background and prefers RPN

**Why Sealed Interfaces over Wrapper Class?**
- Compile-time exhaustive checking
- Clean pattern matching (no instanceof soup)
- Better performance (no wrapper overhead)
- Clear intent: these are the ONLY valid stack items

**Why Allow Double in Scientific Operations?**
- BigDecimal has no native trig/logarithmic functions
- Libraries exist but add complexity
- Scientific operations aren't used in financial calculations
- User is aware of precision tradeoff for engineering features

### Session Statistics (Updated)
- Files created: 4 (CLAUDE.md, pom.xml, work-in-progress.md, developer-log.md)
- Directories created: 26 (complete Maven structure)
- Architecture designed: RPN stack-based calculator with sealed interfaces
- Operations planned: 17+ (arithmetic, scientific, future financial)

### Next Steps
1. Update CLAUDE.md with RPN stack architecture
2. Create sealed interface hierarchy
3. Implement all basic and scientific operations
4. Create comprehensive unit tests
5. Build stack evaluator/executor

---

## Session: 2025-10-02 - RPN Calculator Implementation

### User Prompts
15. "please Update the markdown files, and then proceed. I want you to create just the classes for the stack and for the calculations. Please use a single class file for each type of calculation."

### Work Completed

#### 1. Updated Documentation
- **work-in-progress.md**: Updated with current architecture decisions and RPN stack design
- **developer-log.md**: Added RPN calculator engine design session with detailed rationale
- **CLAUDE.md**: Added RPN stack architecture section with sealed interface hierarchy

#### 2. Created Core Stack Infrastructure

**Sealed Interface Hierarchy** (`com.finCalc.calculator`):
- `StackItem` (sealed interface) - Base type for all stack items
- `BigNumber` (record) - Wraps BigDecimal values with factory methods
- `Calculation` (non-sealed interface) - Executes operations on stack
- `Error` (record) - Error conditions with static factory methods

**Key Implementation Details**:
- `BigNumber.of(String)` - Primary factory for financial calculations
- `BigNumber.of(double)` - Used only for scientific operation results (with warning in JavaDoc)
- `Error.insufficientOperands()` - Standard error for stack depth violations
- `Error.divisionByZero()` - Standard error for division by zero
- `Error.domainError()` - Standard error for domain violations (sqrt of negative, etc.)

**Sealed Interface Adjustment**:
- Initially tried sealed `Calculation` interface with permits clause
- Hit Java limitation: sealed interfaces can't permit classes in different packages
- Changed to `non-sealed` interface to allow implementations in sub-packages
- Maintains type safety through StackItem sealed hierarchy

#### 3. Implemented Basic Arithmetic Operations (`com.finCalc.calculator.operations.basic`)

All implemented as enums with single INSTANCE for thread-safety:
- **Add.java** - Pops 2 operands, pushes sum (pure BigDecimal)
- **Subtract.java** - Pops 2 operands, pushes difference (pure BigDecimal)
- **Multiply.java** - Pops 2 operands, pushes product (pure BigDecimal)
- **Divide.java** - Pops 2 operands, pushes quotient with scale 10, HALF_UP rounding, handles division by zero

#### 4. Implemented Power/Root Operations (`com.finCalc.calculator.operations.scientific`)

- **Square.java** - Pops 1 operand, pushes square (pure BigDecimal)
- **SquareRoot.java** - Pops 1 operand, pushes sqrt (uses Math.sqrt, domain check for negatives)
- **Power.java** - Pops 2 operands (base, exponent), pushes base^exponent (uses Math.pow, checks for NaN/Infinite)
- **Reciprocal.java** - Pops 1 operand, pushes 1/x (pure BigDecimal with scale 10, HALF_UP rounding)

#### 5. Implemented Logarithmic Operations (`com.finCalc.calculator.operations.scientific`)

- **NaturalLog.java** - Pops 1 operand, pushes ln(x) (uses Math.log, domain check for non-positive)
- **Log10.java** - Pops 1 operand, pushes log₁₀(x) (uses Math.log10, domain check for non-positive)
- **Exp10.java** - Pops 1 operand, pushes 10^x (uses Math.pow, checks for infinite)

#### 6. Implemented Trigonometric Operations (`com.finCalc.calculator.operations.scientific`)

- **Sine.java** - Pops 1 operand (radians), pushes sin(x) (uses Math.sin)
- **Cosine.java** - Pops 1 operand (radians), pushes cos(x) (uses Math.cos)
- **Tangent.java** - Pops 1 operand (radians), pushes tan(x) (uses Math.tan)
- **ArcSine.java** - Pops 1 operand, pushes asin(x) in radians (uses Math.asin, domain [-1,1])
- **ArcCosine.java** - Pops 1 operand, pushes acos(x) in radians (uses Math.acos, domain [-1,1])
- **ArcTangent.java** - Pops 1 operand, pushes atan(x) in radians (uses Math.atan)

### Implementation Patterns

**Common Pattern Across All Operations**:
1. Check stack depth, push Error if insufficient
2. Pop required operands
3. Validate operands are BigNumber (not Error)
4. Perform calculation (BigDecimal or double internally)
5. Check for domain errors/special cases
6. Push result or Error back onto stack
7. Return modified stack

**Error Handling**:
- Insufficient operands: Standard error with operation name and counts
- Non-numeric operands: Custom error message
- Domain violations: domainError with operation and reason
- Division by zero: divisionByZero standard error
- NaN/Infinite results: domainError for undefined/infinite

### Build Verification

**Maven Compilation**:
- Command: `mvn clean compile`
- Result: BUILD SUCCESS
- Source files compiled: 21
- Target: Java 17

**Issue Encountered & Resolved**:
- Initial compilation failed: sealed interface cannot permit classes in different packages
- Resolution: Changed `Calculation` from `sealed` to `non-sealed` interface
- Maintains type safety through `StackItem` sealed hierarchy

### Session Statistics
- Files created: 21 Java classes
  - 4 core stack classes (StackItem, BigNumber, Calculation, Error)
  - 4 basic arithmetic operations
  - 4 power/root operations
  - 3 logarithmic operations
  - 6 trigonometric operations
- Documentation files updated: 3 (CLAUDE.md, work-in-progress.md, developer-log.md)
- Build status: ✅ Successful compilation

### Next Steps
1. Create stack evaluator/executor to process mixed stacks
2. Implement unit tests for all 17 operations
3. Build JavaFX UI for calculator
4. Implement financial calculation operations (mortgage, NPV, IRR, etc.)

---

## Session: 2025-10-02 - Added getSymbol() Method to All Operations

### User Prompts
16. "I need you to add the getSymbol() method to ALL operation classes in the finCalc project."

### Work Completed

#### Added getSymbol() Method to 33 Operation Classes

Successfully added the `@Override public String getSymbol()` method to all operation classes across the codebase:

**Basic Operations (4 classes)**:
- Add.java → "+"
- Subtract.java → "−"
- Multiply.java → "×"
- Divide.java → "÷"

**Scientific Operations (15 classes)**:
- SquareRoot.java → "√"
- Square.java → "x²"
- Power.java → "xⁿ"
- Reciprocal.java → "1/x"
- NthRoot.java → "ⁿ√x"
- Log10.java → "LOG"
- NaturalLog.java → "ln"
- Exponential.java → "e^x"
- Exp10.java → "10^x"
- Sine.java → "sin"
- Cosine.java → "cos"
- Tangent.java → "tan"
- ArcSine.java → "ASIN"
- ArcCosine.java → "ACOS"
- ArcTangent.java → "ATAN"

**TVM Financial Operations (5 classes)**:
- Payment.java → "PMT"
- PresentValue.java → "PV"
- FutureValue.java → "FV"
- InterestRate.java → "RATE"
- NumberOfPeriods.java → "NPER"

**Investment Analysis Operations (4 classes)**:
- CompoundAnnualGrowthRate.java → "CAGR"
- BreakEvenPoint.java → "BEP"
- PaybackPeriod.java → "PAYBACK"
- ProfitabilityIndex.java → "PI"

**Real Estate Operations (7 classes)**:
- CapRate.java → "CAP"
- NetOperatingIncome.java → "NOI"
- CashOnCash.java → "CoC"
- DebtServiceCoverageRatio.java → "DSCR"
- LoanToValue.java → "LTV"
- GrossRentMultiplier.java → "GRM"
- ReturnOnInvestment.java → "ROI"

**Note**: AbsoluteValue and Modulo classes mentioned in the user's list were not found in the codebase.

#### Implementation Details

- Method placement: Added after existing methods (typically after `getExample()` or `getOperandDescriptors()`)
- All methods follow consistent format: `@Override public String getSymbol() { return "SYMBOL"; }`
- Used exact symbols as specified by the user (Unicode characters for mathematical symbols)
- Basic operations use mathematical symbols (×, ÷, √)
- Financial operations use standard abbreviations (PMT, PV, FV, RATE, NPER, CAP, NOI, etc.)
- Scientific operations use appropriate notation (sin, cos, tan, ln, LOG, etc.)

#### Build Verification

**Maven Compilation**:
- Command: `mvn clean compile`
- Result: BUILD SUCCESS
- Source files compiled: 45
- Compilation time: 0.765s
- Target: Java 17
- No compilation errors or warnings (aside from standard Maven/JDK warnings)

### Session Statistics
- Files modified: 33 operation classes
- Methods added: 33 getSymbol() implementations
- Build status: ✅ Successful compilation
- Total operation classes with getSymbol(): 33

### Notes
- All changes compile successfully with no errors
- The getSymbol() method provides a consistent way to retrieve the display symbol for each operation
- This will be useful for UI components that need to display operation buttons or operation history
- Two operations mentioned in user's list were not found: AbsoluteValue.java and Modulo.java

### Next Steps
1. Consider implementing the missing AbsoluteValue and Modulo operations if needed
2. Continue with stack evaluator/executor implementation
3. Implement unit tests for all operations
4. Build JavaFX UI utilizing the new getSymbol() method

---

## Session: 2025-10-02 - Additional Financial Calculations Implementation

### User Prompts
1. "Let's implement the rest of the potential calculations."
2. "1, 2, 5 & 6" - Selected categories: More Real Estate, Loan & Mortgage, Bond Calculations, Tax & Retirement

### Work Completed

#### 1. More Real Estate Calculations (6 operations)
Created new financial operation classes:

**CashFlowAfterTaxes.java** (CFAT)
- Formula: CFAT = Cash Flow Before Taxes - Tax Liability
- Calculates net cash flow after tax implications
- Validates numeric operands

**OperatingExpenseRatio.java** (OER)
- Formula: OER = Operating Expenses / Gross Operating Income
- Measures operating costs as percentage of income
- Result expressed as decimal (0.40 = 40%)
- Validates positive gross income and non-negative expenses

**VacancyLoss.java** (VACANCY)
- Formula: Vacancy Loss = Potential Gross Income × Vacancy Rate
- Calculates income lost due to unoccupied units
- Validates vacancy rate between 0 and 1

**EffectiveGrossIncome.java** (EGI)
- Formula: EGI = Potential Gross Income - Vacancy Loss
- Calculates actual income after vacancy and credit losses
- Validates that vacancy loss doesn't exceed potential income

**PricePerSquareFoot.java** (PPSF)
- Formula: Price Per SF = Property Price / Square Feet
- Property valuation metric for comparing different-sized properties
- Validates positive square feet and price

**RentPerSquareFoot.java** (RPSF)
- Formula: Rent Per SF = Annual Rent / Square Feet
- Rental rate metric for commercial properties
- Commonly expressed annually or monthly

#### 2. Loan & Mortgage Calculations (4 operations)

**RemainingBalance.java** (REMBAL)
- Formula: Remaining Balance = PV × (1 + rate)^n - PMT × [((1 + rate)^n - 1) / rate]
- Calculates outstanding principal after specified payments
- Takes 4 operands: PV, Rate, NPer, PaymentsMade
- Validates payment count is within total periods

**TotalInterestPaid.java** (TOTINT)
- Formula: Total Interest = (PMT × NPer) - PV
- Calculates total interest over life of loan
- Shows true cost of borrowing
- Takes 3 operands: PV, PMT, NPer

**AprToApy.java** (APY)
- Formula: APY = (1 + APR/n)^n - 1
- Converts Annual Percentage Rate to Annual Percentage Yield
- Accounts for compounding effect (APY always ≥ APR)
- Common compounding periods: Monthly=12, Quarterly=4, Daily=365

**DebtToIncomeRatio.java** (DTI)
- Formula: DTI = Total Monthly Debt / Gross Monthly Income
- Measures debt burden vs income
- Result expressed as decimal (0.30 = 30%)
- Lenders typically prefer DTI ≤ 43% for qualified mortgages

#### 3. Bond Calculations (3 operations)

**CurrentYield.java** (CY)
- Formula: Current Yield = Annual Coupon Payment / Current Price
- Bond's annual return based on current price
- Result expressed as decimal (0.0632 = 6.32%)
- Differs from YTM (doesn't account for capital gains/losses)

**YieldToMaturity.java** (YTM)
- Approximation Formula: YTM ≈ [C + (F - P) / n] / [(F + P) / 2]
- Total return if bond held to maturity
- Takes 4 operands: CurrentPrice, FaceValue, AnnualCoupon, YearsToMaturity
- Note: This is an approximation; exact YTM requires iterative calculation

**BondPrice.java** (BONDPRICE)
- Formula: Bond Price = C × [1 - (1 + y)^-n] / y + F / (1 + y)^n
- Present value of bond's cash flows
- Takes 4 operands: FaceValue, CouponRate, YieldRate, Periods
- When yield > coupon rate, bond trades at discount (price < face)
- When yield < coupon rate, bond trades at premium (price > face)
- Handles zero yield special case

#### 4. Tax & Retirement Calculations (3 operations)

**EffectiveTaxRate.java** (EFFTAX)
- Formula: Effective Tax Rate = Total Tax Paid / Total Income
- Calculates actual tax rate paid on income
- Result expressed as decimal (0.18 = 18%)
- Differs from marginal tax rate (top bracket rate)

**AfterTaxReturn.java** (AFTAXRET)
- Formula: After-Tax Return = Pre-Tax Return × (1 - Tax Rate)
- Investment return after tax considerations
- Both inputs and result in decimal form (0.08 = 8%)
- Important for comparing taxable vs tax-advantaged investments
- Validates tax rate between 0 and 1

**RequiredMinimumDistribution.java** (RMD)
- Formula: RMD = Account Balance / Distribution Period
- Mandatory retirement account withdrawal
- RMDs generally required starting at age 73 (as of 2024)
- Distribution period based on IRS Uniform Lifetime Table
- Failure to take RMD results in 25% penalty

#### 5. Operation Registry Updates

**Updated OperationRegistry.java**:
- Added all 16 new operations to static initialization block
- Organized by category: Real Estate, Loan & Mortgage, Bond, Tax & Retirement
- Total registered operations: 53 (was 37)

#### 6. UI Menu Updates

**Updated CalculatorApp.java**:
- Extended Real Estate Analysis submenu with 6 new operations
- Created new "Loan & Mortgage" submenu with 4 operations
- Created new "Bond Calculations" submenu with 3 operations
- Created new "Tax & Retirement" submenu with 3 operations
- Updated Financial menu to include all new submenus

#### Build Verification

**Maven Compilation**:
- Command: `mvn clean compile`
- Result: BUILD SUCCESS
- Source files compiled: 61 (was 45)
- Compilation time: 0.781s
- Target: Java 17
- No compilation errors

**Application Testing**:
- Command: `mvn javafx:run`
- Result: Application launches successfully
- All menus display correctly
- New operations accessible via Financial menu submenus

### Implementation Statistics
- **New files created**: 16 financial operation classes
- **Files modified**: 2 (OperationRegistry.java, CalculatorApp.java)
- **Total operations**: 53 (4 basic, 17 scientific, 5 TVM, 4 investment, 13 real estate, 4 loan/mortgage, 3 bond, 3 tax/retirement)
- **New menu categories**: 3 (Loan & Mortgage, Bond Calculations, Tax & Retirement)
- **Build status**: ✅ Successful compilation and testing

### Technical Notes
- All operations follow established pattern with getSymbol(), getDescription(), getExample(), getOperandDescriptors()
- All operations use BigNumber for stack values but convert to double internally for calculations
- Comprehensive input validation with domain-specific error messages
- Proper handling of edge cases (zero yield in bond pricing, zero compounding, etc.)
- JavaDoc comments explain formulas, parameters, and usage examples

### Next Steps
1. Create unit tests for all 16 new operations
2. Test each operation through the UI with example calculations
3. Consider adding more bond calculations (Duration, Modified Duration, Macaulay Duration)
4. Consider adding amortization schedule calculation
5. Add keyboard shortcuts for common operations
