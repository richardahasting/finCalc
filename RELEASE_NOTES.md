# finCalc v1.0.0 - RPN Financial Calculator

**Release Date:** October 3, 2025

First stable release of finCalc/richCalc - A powerful RPN (Reverse Polish Notation) financial calculator designed for real estate investors, options traders, and financial professionals.

## 🎯 Highlights

- **53 financial operations** with comprehensive test coverage (530 tests)
- **Options trading calculations** including annualized returns for cash-secured puts and covered calls
- **Standalone macOS app** (richCalc.app) with bundled Java runtime - no dependencies required
- **Complete financial toolkit** for real estate, bonds, TVM, investment analysis, and options trading
- **Arbitrary precision** using BigDecimal for accurate calculations
- **Modern dark theme UI** with color-coded operation buttons

## 📦 Downloads

### macOS (Apple Silicon - M1/M2/M3)
- **richCalc.app** - Standalone application, no Java or Maven required
  - Simply download, unzip, and drag to Applications folder
  - Includes bundled Java runtime and all dependencies
  - Native macOS integration with custom icon

### Cross-Platform
- **finCalc-1.0.0.jar** - Executable JAR (requires Java 17+)
  - Works on macOS, Windows, Linux
  - Run with: `java -jar finCalc-1.0.0.jar`

## 🚀 Key Features

### Calculator Operations

**Basic & Scientific** (21 operations)
- Basic arithmetic: +, −, ×, ÷
- Scientific: trigonometric, logarithmic, exponential, power functions
- Stack operations: drop, swap, clear

**Time Value of Money** (5 operations)
- Payment (PMT), Present Value (PV), Future Value (FV)
- Interest Rate (RATE), Number of Periods (NPER)

**Real Estate Analysis** (13 operations)
- Cap Rate, NOI, Cash-on-Cash Return, DSCR, LTV, GRM
- ROI, CFAT, OER, Vacancy Loss, EGI
- Price/Rent Per Square Foot

**Investment Analysis** (4 operations)
- CAGR, Break-Even Point, Payback Period, Profitability Index

**Bond Calculations** (3 operations)
- Current Yield, Yield to Maturity, Bond Price

**Loan & Mortgage** (4 operations)
- Remaining Balance, Total Interest Paid, APR to APY, DTI

**Tax & Retirement** (3 operations)
- Effective Tax Rate, After-Tax Return, RMD

**Options Trading** (2 operations)
- **AOPT (Annualized Option Return)** - Calculate annualized returns for cash-secured puts and premium-only strategies
  - Example: Sell $12.50 put for $0.26 premium, 10 DTE = 75.92% annualized
- **CCR (Covered Call Return)** - Calculate total return including capital appreciation
  - Example: Buy stock at $50, sell $52 call for $1.50, 30 DTE = 85.17% annualized

### User Interface

- **RPN Stack Display** - Real-time visualization of calculation stack
- **Expression View** - See your RPN expression as you build it
- **Color-Coded Buttons** - Easy identification by operation type
  - Green: TVM functions
  - Blue: Real Estate
  - Gold: Bonds
  - Purple: Investment Analysis
  - Orange: Basic operators
  - Red: Stack operations
  - Gray: Scientific functions
- **Operation Wizards** - Guided input for complex financial calculations
- **Precision Control** - Configurable decimal precision (1-50 places)
- **Keyboard Support** - Full keyboard operation for efficiency

## 📊 Examples

### RPN Calculation
Calculate: (10 + 20) × 3
```
10 ENTER 20 + 3 × = 90
```

### Options Trading - Cash Secured Put
Sell $12.50 put for $0.26 premium, 10 days to expiration:
```
12.50 ENTER 0.26 ENTER 10 ENTER AOPT
Result: 0.7592 (75.92% annualized return)
```

### Covered Call
Buy stock at $50, sell $52 call for $1.50, 30 days:
```
50 ENTER 52 ENTER 1.50 ENTER 30 ENTER CCR
Result: 0.8517 (85.17% annualized return if called away)
```

### Real Estate Analysis
Calculate Cap Rate: $120,000 NOI on $1,500,000 property:
```
120000 ENTER 1500000 ENTER CAP
Result: 0.08 (8% cap rate)
```

## 🧪 Testing & Quality

- **530 comprehensive unit tests** - 100% operation coverage
- **Edge case testing** - Validates error handling and boundary conditions
- **JUnit 5** test framework
- **All tests passing** before release

## 🔧 Technical Requirements

### macOS App (richCalc.app)
- macOS 11.0 or later
- Apple Silicon (M1/M2/M3) processor
- No additional software required

### JAR File (finCalc-1.0.0.jar)
- Java 17 or higher
- JavaFX 21 (included in JAR)
- Works on macOS, Windows, Linux

### Building from Source
- Java 17+
- Maven 3.6+
- JavaFX 21

## 📝 Installation

### macOS Standalone App
1. Download `richCalc-macos-1.0.0.zip`
2. Unzip the file
3. Drag `richCalc.app` to your Applications folder
4. Double-click to launch

### Cross-Platform JAR
1. Download `finCalc-1.0.0.jar`
2. Ensure Java 17+ is installed
3. Run: `java -jar finCalc-1.0.0.jar`

### Build from Source
```bash
git clone https://github.com/richardahasting/finCalc.git
cd finCalc
mvn clean package
java -jar target/finCalc-1.0.0.jar
```

## 🐛 Known Issues

- macOS apps are currently Apple Silicon only (Intel Mac users should use JAR)
- No native Windows/Linux apps yet (JAR works on all platforms)
- Financial calculations assume annual compounding unless otherwise specified
- Options calculations assume American-style exercise at expiration

## 🔮 Future Enhancements

See our [GitHub Issues](https://github.com/richardahasting/finCalc/issues) for planned features:
- Break-even price calculator for options (#5)
- Credit spread return calculator (#6)
- Option P&L calculator at any price (#7)
- Iron Condor return calculator (#8)
- Wheel strategy combined return (#9)

## 📄 License

MIT License - See [LICENSE](LICENSE) file for details

## 🙏 Acknowledgments

Built with:
- Java 17
- JavaFX 21
- Maven
- JUnit 5

---

**Full Changelog**: https://github.com/richardahasting/finCalc/blob/main/CHANGELOG.md

**Report Issues**: https://github.com/richardahasting/finCalc/issues

**Author**: Richard Hasting
