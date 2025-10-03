# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**finCalc** is a macOS-native financial calculator application built in Java/JavaFX, focused on real estate and securities investment analysis with tax and cash flow calculations.

### Key Design Principles

- **Precision**: ALL financial calculations use `BigDecimal` with appropriate rounding modes (typically `RoundingMode.HALF_UP` for currency, `RoundingMode.HALF_EVEN` for rates)
- **Scale Management**: Money values use scale of 2, interest rates use scale of 6, intermediate calculations may use higher precision
- **Immutability**: Financial calculation classes should be immutable where possible
- **DRY Code**: Java class files must be under 500 lines; extract reusable logic aggressively

## Technology Stack

- **Language**: Java 17+ (or latest LTS)
- **UI Framework**: JavaFX
- **Build Tool**: Maven
- **Calculation Base**: `java.math.BigDecimal` exclusively for all financial math

## Architecture

### RPN Stack-Based Calculator Engine

finCalc uses a **Reverse Polish Notation (RPN)** stack-based architecture:

- **Stack Model**: Mixed stack containing values (BigNumber) and operations (Calculation)
- **Execution**: Evaluator processes stack items; when encountering a Calculation, it executes on accumulated values
- **Example**: `[3, 4, ADD]` → `[7]`
- **Pure Functions**: Each Calculation consumes required operands, performs operation, pushes result

#### Sealed Interface Hierarchy

```java
sealed interface StackItem permits BigNumber, Calculation, Error

record BigNumber(BigDecimal value) implements StackItem

sealed interface Calculation extends StackItem {
    Stack<StackItem> execute(Stack<StackItem> stack);
}

record Error(String message) implements StackItem
```

**Design Benefits:**
- Type safety with exhaustive pattern matching (Java 17)
- Compiler-enforced handling of all StackItem types
- Immutable by design (records for value objects)
- No wrapper class overhead

### Module Structure

```
src/main/java/com/finCalc/
├── ui/                    # JavaFX UI components and controllers
├── model/                 # Domain models (immutable where possible)
├── calculator/
│   ├── operations/        # Calculator operations
│   │   ├── basic/        # Arithmetic (ADD, SUBTRACT, MULTIPLY, DIVIDE) - BigDecimal
│   │   ├── scientific/   # Engineering (SIN, COS, TAN, LN, SQRT, POW) - double internally
│   │   └── financial/    # Financial (MORTGAGE_PMT, NPV, IRR, etc.) - pure BigDecimal
│   ├── mortgage/         # Mortgage-related calculations
│   ├── cashflow/         # Cash flow analysis
│   ├── tax/              # Tax calculation utilities
│   └── securities/       # Securities investment calculations
└── util/                 # Shared utilities (BigDecimal helpers, formatters)
```

### Calculation Categories

**Primary Focus: Financial Calculations (Pure BigDecimal)**

*Real Estate Investment:*
- Mortgage payment calculations (principal, interest, amortization)
- Extra payment impact analysis
- Refinance break-even analysis
- Cap rate, cash-on-cash return, ROI
- Rental property cash flow (NOI, PITI)
- Property tax and insurance estimates

*Tax & Equity:*
- Mortgage interest deduction
- Home equity tracking
- Depreciation schedules (rental properties)
- Tax basis calculations

*Cash Flow Analysis:*
- Net operating income
- Monthly/annual cash flow projections
- Break-even analysis
- Comparison tools (multiple properties)

*Securities Investment:*
- (To be defined as project progresses)

**Secondary Focus: Engineering Calculations (Double-based internally)**

*Basic Arithmetic:*
- ADD, SUBTRACT, MULTIPLY, DIVIDE (BigDecimal-based)

*Scientific Operations:*
- Power/Root: SQUARE, SQRT, POW (X^N), RECIPROCAL (1/x)
- Logarithmic: LN (natural log), LOG10 (log base 10), EXP10 (10^x)
- Trigonometric: SIN, COS, TAN, ASIN, ACOS, ATAN

**Note**: Scientific operations use `double` internally for computation but maintain BigNumber stack interface. These are for engineering use and are NOT used in financial calculations.

## Development Commands

```bash
# Build the project
mvn clean install

# Run the application
mvn javafx:run

# Run tests
mvn test

# Run specific test
mvn test -Dtest=ClassName#methodName

# Package for macOS
mvn jpackage:jpackage
```

## Code Standards

### BigDecimal Usage

```java
// ALWAYS specify scale and rounding mode
BigDecimal result = value1.divide(value2, 2, RoundingMode.HALF_UP);

// Use constants for common values
public static final BigDecimal ZERO = BigDecimal.ZERO;
public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
public static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("12");

// NEVER use double constructors
BigDecimal wrong = new BigDecimal(0.1);  // ❌ WRONG
BigDecimal right = new BigDecimal("0.1"); // ✅ CORRECT
```

### JavaDoc Requirements

All public methods in calculator classes MUST have comprehensive JavaDoc including:
- Method purpose
- `@param` for each parameter with units (e.g., "annual interest rate as decimal (0.05 = 5%)")
- `@return` with units and precision
- `@throws` for any exceptions
- Example usage if non-obvious

### Testing

- Unit tests for ALL calculation methods
- Test edge cases: zero values, very large values, negative values
- Test precision: verify scale and rounding behavior
- Use descriptive test method names: `calculateMonthlyPayment_With5PercentRate_Returns536_82()`

## Financial Calculation Notes

- Interest rates: Store as decimal (0.05 for 5%), not percentage (5)
- Periods: Be explicit about monthly vs annual (variable naming: `monthlyRate`, `annualRate`)
- Amortization: Month 1 is first payment, not month 0
- Property values and prices: Always positive BigDecimal, scale 2
- Tax rates: Scale 6 for precision (e.g., 0.062500 for 6.25%)

## Claude Code Automatic Behaviors

**REQUIRED**: Claude must automatically follow these behaviors in every session:

### 0. Persistent Memory System
- **ALWAYS load memory at session start** by reading from ~/.claude/memory/
- **Check conversation history** in ~/.claude/memory/conversations/ for previous context
- **Review user preferences** in ~/.claude/memory/context/user_preferences.md
- **Save important information** to appropriate memory folders during the session
- **Update conversation log** at session end with key accomplishments and context

### 1. Planning and Reflection
- **Plan thoroughly before execution** - outline the approach and expected outcomes
- **Document expected outcomes** before implementing any solution
- **Reflect on actual outcomes** after completion and compare with expectations
- **Note any deviations or learnings** from the planned approach

### 2. Developer Log Documentation
- **Record all user prompts** verbatim in developer-log.md (or project-specific log file)
- **Document all work progress** including decisions, implementations, and fixes
- **Update the log throughout each session**, not just at the end
- **Include session statistics** and next steps for continuity

### 3. GitHub Issues Management
- **Create missing labels automatically** when needed for proper categorization
- **Apply comprehensive labeling** to all GitHub issues
- **Create GitHub issues for every fix/improvement** if one doesn't already exist
- **Reference related issues** and dependencies appropriately

### 4. Todo List Management
- **Use TodoWrite/TodoRead** for any multi-step or complex tasks
- **Update task statuses in real-time** as work progresses
- **Mark tasks complete immediately** upon finishing each item
- **ALWAYS include "Update work-in-progress.md" as the FIRST task** in every todo list
- **ALWAYS include "Create feature branch for this work" as the SECOND task** (before any code changes)
- **ALWAYS include a "Document work in developer-log.md" task** for any coding session
- **Never consider work complete** until developer log documentation is finished

### 5. Issue Tracking for All Changes
- **Check for existing issues** before making any bug fix or improvement
- **Create new GitHub issue** if none exists for the work being done
- **Properly describe the issue** with context and solution approach
- **Reference issues in commits** when implementing fixes
- **Close issues when validated** and working correctly

### 6. Code Quality Standards
- **Follow existing code conventions** and patterns in the codebase
- **Add comprehensive JavaDoc** to new methods and classes
- **Apply consistent formatting** following project standards
- **Run linting and type checking** before completing tasks (when available)
- **If unsure about code or files, open them** – do not hallucinate

### 7. Testing and Validation
- **Run existing tests** to ensure changes don't break functionality
- **Create tests for new functionality** when appropriate
- **Validate all changes compile** and work as expected

### 8. Markdown Display
- **Always use `mdview -b`** to display markdown files to the user
- This provides proper formatting and rendering in the browser

These behaviors ensure complete traceability, proper project management, and comprehensive documentation of all development work.
