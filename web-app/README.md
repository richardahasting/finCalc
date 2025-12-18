# finCalc Web App

A modern, web-based RPN (Reverse Polish Notation) financial calculator built with React, TypeScript, and Vite. Can run as a standalone web app or as a desktop application via Electron.

## Features

- **RPN Stack-Based Calculator** - Classic HP-style reverse polish notation
- **Financial Operations** - PMT, PV, FV, RATE, NPER, CAP, NOI, ROI, CAGR, and more
- **Scientific Operations** - Trigonometry, logarithms, powers, roots
- **Wizard Dialogs** - Fill-in-the-blanks interface for complex financial calculations
- **Tooltips** - Hover over financial buttons for plain-English explanations
- **Customizable Themes** - 5 accent colors, 4 background themes, 5 stack colors
- **Accessibility** - Colorblind-friendly stack color options
- **Precision Control** - Adjustable decimal precision (1-20 places)
- **Persistent Settings** - All preferences saved to localStorage

## Prerequisites

- Node.js 18+
- npm 9+

## Installation

```bash
cd web-app
npm install
```

## Running the App

### Web Browser (Development)

```bash
npm run dev
```

Opens at http://localhost:5173 (or next available port)

### Web Browser (Production Build)

```bash
npm run build
npm run preview
```

### Electron Desktop App (Development)

```bash
npm run electron:dev
```

### Electron Desktop App (Production)

```bash
npm run electron:build
```

## Deployment (Web Server)

To deploy as a web application:

```bash
npm run build
```

This creates a `dist/` folder with static files. Deploy these to any web server:

- **nginx** - Point root to `dist/`
- **Apache** - Point DocumentRoot to `dist/`
- **Node/Express** - Serve `dist/` as static files
- **Vercel/Netlify** - Connect repo and set build command to `npm run build`, output to `dist`

### Example nginx config:

```nginx
server {
    listen 80;
    server_name fincalc.example.com;
    root /path/to/finCalc/web-app/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

## Project Structure

```
web-app/
├── src/
│   ├── calculator/           # Calculation engine
│   │   ├── types.ts          # Core types (BigNumber, StackItem, etc.)
│   │   ├── index.ts          # Operation registry
│   │   └── operations/
│   │       ├── basic/        # +, -, ×, ÷, MOD, ABS
│   │       ├── scientific/   # sqrt, pow, log, trig
│   │       └── financial/    # TVM, real estate, investment
│   ├── App.tsx               # Main React component
│   ├── App.css               # Styles with CSS custom properties
│   └── main.tsx              # Entry point
├── electron/
│   └── main.cjs              # Electron main process
├── dist/                     # Production build output
└── package.json
```

## Keyboard Shortcuts

- **0-9** - Enter digits
- **.** - Decimal point
- **Enter** - Push to stack
- **Backspace** - Delete last digit
- **Escape** - Clear all

## Settings

Click the ⚙ gear icon to access:

- **Accent Color** - Gold, Emerald, Sapphire, Rose, Mono
- **Background** - Dark, Midnight, Charcoal, OLED
- **Stack Numbers** - Green, Cyan, Yellow, White, Blue (colorblind-friendly)
- **Button Size** - Adjustable slider
- **Font Size** - Adjustable slider
- **Reset to Defaults** - Restore all settings

## Technology Stack

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool with HMR
- **decimal.js** - Arbitrary precision arithmetic
- **Electron** - Desktop app wrapper (optional)

## License

MIT
