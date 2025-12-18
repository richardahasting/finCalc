# Work In Progress - finCalc

## Current Session: 2025-12-17

### Completed: Web App Conversion Project
Successfully converted finCalc from JavaFX to a modern web application:

**Core Implementation:**
- Ported RPN stack-based calculator engine to TypeScript
- Used decimal.js for BigDecimal-equivalent precision
- All financial operations working: PMT, PV, FV, RATE, NPER, CAP, NOI, ROI, CAGR, etc.
- Wizard dialogs for all 15 financial operations
- Tooltips explaining each financial operation in plain English

**UI/UX Features:**
- 5 accent color themes (gold, emerald, sapphire, rose, mono)
- 4 background themes (dark, midnight, charcoal, oled)
- 5 stack color options for colorblind accessibility (green, cyan, yellow, white, blue)
- Adjustable button and font size scales (0.50 / 0.95 defaults)
- Settings panel with gear icon toggle
- Reset to defaults option
- All settings persist to localStorage

**Technical Stack:**
- React 19 + TypeScript + Vite
- decimal.js for arbitrary precision arithmetic
- Electron wrapper for desktop app (400x700 fixed window)
- Production build ready for web server deployment

**Files Created:**
- `web-app/` - Complete web application directory
- `web-app/README.md` - Full documentation with deployment instructions

### Previous Session: 2025-10-02

### Previous Tasks (Paused)
- Implementing additional financial calculations:
  - More Real Estate (6 operations): CFAT, Operating Expense Ratio, Vacancy Loss, Effective Gross Income, Price Per Square Foot, Rent Per Square Foot
  - Loan & Mortgage (4 operations): Remaining Balance, Total Interest Paid, APR to APY Conversion, Debt-to-Income Ratio
  - Bond Calculations (3 operations): Bond Yield, Current Yield, Yield to Maturity
  - Tax & Retirement (3 operations): Effective Tax Rate, After-Tax Return, Required Minimum Distribution

### Recently Completed
- Added getSymbol(), getDescription(), getExample() to all 37 operation classes
- Created OperationRegistry with HashMap-based lookup (eliminated 35-case switch statement)
- Fixed stack display to show [0] at bottom with higher indices above
- Moved all help text from CalculatorApp into individual operation classes

### Project Initialization - COMPLETE
- Created CLAUDE.md with project guidelines and architecture
- Defined focus: Financial calculator (primary) with engineering calculations (secondary)
- Tech stack: Java 17+ with JavaFX, BigDecimal for all financial calculations
- Maven pom.xml configured with JavaFX and JUnit 5
- Full directory structure created

### Architecture Decisions
- **RPN Stack Design**: Each calculation consumes stack items, performs operation, pushes result
- **Sealed Interface**: StackItem permits BigNumber, Calculation, Error
- **BigDecimal Only**: All stack values are BigNumber (wraps BigDecimal) - no Double/Integer types
- **Scientific Calculations**: Use double internally but maintain BigNumber stack interface
- **Calculation Organization**:
  - `operations.basic.*` - Arithmetic (BigDecimal-based)
  - `operations.scientific.*` - Engineering functions (double-based internally)
  - `operations.financial.*` - Financial calculations (pure BigDecimal)

### Next Steps
- Create sealed interface hierarchy (StackItem, BigNumber, Calculation, Error)
- Implement all basic arithmetic operations
- Implement scientific/engineering operations
- Build unit tests for all operations
- Move to financial calculations (mortgage, cash flow, etc.)
