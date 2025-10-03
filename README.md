# finCalc / richCalc

A powerful financial calculator with RPN (Reverse Polish Notation) support, built with JavaFX.

## Features

- **RPN Calculator**: Stack-based calculation system
- **Financial Functions**: TVM (Time Value of Money) calculations including PMT, PV, FV, RATE, NPER
- **Real Estate Analysis**: Cap Rate, NOI, Cash-on-Cash Return, DSCR, LTV, GRM, ROI, OER, EGI
- **Investment Analysis**: IRR, CAGR, Break-Even Point, Profitability Index
- **Bond Calculations**: YTM, Yield, Bond pricing
- **Options Trading**: Annualized Option Return (AOPT), Covered Call Return (CCR)
- **Scientific Functions**: Trigonometric, logarithmic, exponential functions
- **Modern UI**: Color-coded buttons for easy function identification
  - Green: TVM functions
  - Blue: Real Estate metrics
  - Gold: Bond functions
  - Purple: Investment analysis
  - Orange: Basic operators
  - Red: Stack operations
  - Gray: Scientific functions

## Building from Source

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- JavaFX 21

### Build and Run

```bash
# Compile and run
mvn clean compile javafx:run

# Run tests
mvn test

# Build JAR with dependencies
mvn clean package
```

## macOS Applications

### finCalc.app (Development - Requires Maven)

Lightweight launcher that uses Maven to run the application.

```bash
# Create the app
./create-app-launcher.sh

# Install to Applications
# Already installed at /Applications/finCalc.app
```

### richCalc.app (Standalone - No Dependencies)

Fully standalone application with bundled Java runtime and JavaFX. Can run on any Mac without Java or Maven installed.

```bash
# Build standalone app
./build-standalone-richCalc.sh
```

This creates a complete macOS app bundle at `/Applications/richCalc.app` that includes:
- Bundled Java runtime
- All JavaFX modules
- Application icon
- No external dependencies

## Project Structure

```
finCalc/
├── src/main/java/com/finCalc/
│   ├── calculator/          # Core calculator engine
│   │   ├── operations/      # Operation implementations
│   │   │   ├── basic/       # Basic arithmetic
│   │   │   ├── scientific/  # Scientific functions
│   │   │   └── financial/   # Financial calculations
│   │   ├── Stack.java       # RPN stack implementation
│   │   └── StackEvaluator.java
│   └── ui/
│       └── CalculatorApp.java  # JavaFX UI
├── src/test/java/           # Unit tests
├── pom.xml                  # Maven configuration
├── create-app-launcher.sh   # Create finCalc.app (requires Maven)
└── build-standalone-richCalc.sh  # Create richCalc.app (standalone)
```

## Usage

### RPN Calculation Example

To calculate: (10 + 20) × 3

1. Enter: `10`
2. Enter: `20`
3. Press: `+`
4. Enter: `3`
5. Press: `×`
6. Result: `90`

### Financial Calculation Example

Calculate monthly payment for a $300,000 loan at 6% annual interest for 30 years:

1. Enter: `300000` (loan amount)
2. Press: `PV` (present value)
3. Enter: `6`
4. Press: `RATE` (annual interest rate)
5. Enter: `30`
6. Press: `NPER` (number of years)
7. Press: `PMT` (calculate payment)
8. Result: Monthly payment amount

### Options Trading Example

Calculate annualized return for selling a $12.50 put for $0.26 premium with 10 days to expiration:

1. Enter: `12.50` (strike price)
2. Enter: `0.26` (premium received)
3. Enter: `10` (days to expiration)
4. Press: `AOPT`
5. Result: `0.7592` (75.92% annualized return)

Calculate covered call return: Buy stock at $50, sell $52 call for $1.50, 30 days to expiration:

1. Enter: `50` (stock cost)
2. Enter: `52` (strike price)
3. Enter: `1.50` (premium received)
4. Enter: `30` (days to expiration)
5. Press: `CCR`
6. Result: `0.8517` (85.17% annualized return if called away)

## Testing

The project includes comprehensive unit tests:

```bash
mvn test
```

Test coverage includes:
- Basic arithmetic operations
- Scientific functions
- Financial calculations (TVM, Real Estate, Bonds, Investment)
- Stack operations
- Error handling

## License

MIT License

## Author

Richard Hasting
