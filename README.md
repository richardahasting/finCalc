# finCalc

A powerful RPN (Reverse Polish Notation) financial calculator available as a web app, Electron desktop app, and native JavaFX application.

## Live Demo

**Web App**: [https://hastingtx.org/fincalc](https://hastingtx.org/fincalc)

## Features

- **RPN Calculator**: Stack-based calculation system for efficient financial analysis
- **53 Operations** across Basic, Scientific, and Financial categories
- **Financial Functions**:
  - TVM (Time Value of Money): PMT, PV, FV, RATE, NPER
  - Real Estate: Cap Rate, NOI, Cash-on-Cash Return, DSCR, LTV, GRM, ROI, OER, EGI
  - Investment Analysis: CAGR, Break-Even Point, Payback Period, Profitability Index
  - Bond Calculations: Current Yield, YTM, Bond Price
  - Loans: Remaining Balance, Total Interest, APR to APY, Debt-to-Income
  - Tax & Retirement: Effective Tax Rate, After-Tax Return, RMD
  - Options Trading: Annualized Option Return (AOPT), Covered Call Return (CCR)
- **Scientific Functions**: Trig, logarithmic, exponential, power/root functions
- **Precision Control**: Adjustable decimal precision (1-50 places)
- **Dark Theme**: Modern UI with color-coded operation buttons

## Quick Start

### Web App
Visit [https://hastingtx.org/fincalc](https://hastingtx.org/fincalc) - no installation required.

### Electron Desktop App (Linux/macOS)
```bash
# From the web-app directory
cd web-app
npm install
npm run electron
```

Or use the launcher script (if installed to ~/bin):
```bash
fincalc
```

### JavaFX Desktop App (macOS)
```bash
# Requires Java 17+ and Maven
mvn clean compile javafx:run
```

## Project Structure

```
finCalc/
├── web-app/                    # React/TypeScript web & Electron app
│   ├── src/
│   │   ├── App.tsx             # Main calculator UI
│   │   ├── App.css             # Styling
│   │   └── calculator/         # Calculator engine
│   │       ├── types.ts        # TypeScript types
│   │       ├── index.ts        # Stack evaluator
│   │       └── operations/     # Operation implementations
│   │           ├── basic/      # +, -, ×, ÷, ABS, MOD
│   │           ├── scientific/ # Trig, log, exp, power
│   │           └── financial/  # TVM, real estate, bonds, etc.
│   ├── electron/
│   │   └── main.cjs            # Electron main process
│   ├── package.json
│   └── vite.config.ts
│
├── src/main/java/              # JavaFX application
│   └── com/finCalc/
│       ├── calculator/         # Java calculator engine
│       └── ui/                 # JavaFX UI
│
├── src/test/java/              # JUnit tests (530+ tests)
└── pom.xml                     # Maven configuration
```

## Development

### Web App / Electron

```bash
cd web-app

# Install dependencies
npm install

# Development server (hot reload)
npm run dev

# Electron development mode
npm run electron:dev

# Build for web deployment
npm run build

# Build for Electron (local use)
npm run build:electron

# Run Electron production build
npm run electron
```

### JavaFX App

```bash
# Build and run
mvn clean compile javafx:run

# Run tests
mvn test

# Package as JAR
mvn clean package
```

## Usage Examples

### RPN Calculation
Calculate: (10 + 20) × 3
```
10 ENTER
20 +
3 ×
→ 90
```

### Mortgage Payment
Calculate monthly payment for $300,000 at 6% for 30 years:
```
300000 ENTER    (loan amount)
6 ENTER         (annual rate %)
30 ENTER        (years)
PMT
→ $1,798.65
```

### Options: Cash-Secured Put
Annualized return for $12.50 put, $0.26 premium, 10 days to expiration:
```
12.50 ENTER     (strike)
0.26 ENTER      (premium)
10 ENTER        (days)
AOPT
→ 75.92%
```

### Options: Covered Call
Buy at $50, sell $52 call for $1.50, 30 days to expiration:
```
50 ENTER        (cost basis)
52 ENTER        (strike)
1.50 ENTER      (premium)
30 ENTER        (days)
CCR
→ 85.17% (annualized if called)
```

## Deployment

### Web Deployment
```bash
cd web-app
npm run build
# Copy dist/* to your web server
```

### Creating ~/bin Launcher (Linux)
```bash
#!/bin/bash
cd /path/to/finCalc/web-app
npm run build:electron
NODE_ENV=production npx electron . --no-sandbox
```

## Testing

```bash
# Java tests (530+ unit tests)
mvn test

# Specific test class
mvn test -Dtest=PaymentCalculationTest
```

## Technology Stack

- **Web/Electron**: React 19, TypeScript, Vite, Electron
- **Desktop**: Java 17+, JavaFX 21
- **Precision**: decimal.js (web), BigDecimal (Java)
- **Testing**: JUnit 5

## License

MIT License

## Author

Richard Hasting
