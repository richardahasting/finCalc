import { useState, useCallback, useEffect } from 'react';
import type { StackItem, Calculation } from './calculator';
import {
  bigNumber,
  isBigNumber,
  formatStackItem,
  getPrecision,
  setPrecision,
  getOperation,
} from './calculator';
import './App.css';

type OperationMode = 'basic' | 'scientific' | 'financial';
type ColorTheme = 'gold' | 'emerald' | 'sapphire' | 'rose' | 'mono';
type BackgroundTheme = 'dark' | 'midnight' | 'charcoal' | 'oled';

const COLOR_THEMES: Record<ColorTheme, { name: string; accent: string }> = {
  gold: { name: 'Gold', accent: '#c9a227' },
  emerald: { name: 'Emerald', accent: '#10b981' },
  sapphire: { name: 'Sapphire', accent: '#3b82f6' },
  rose: { name: 'Rose', accent: '#f43f5e' },
  mono: { name: 'Mono', accent: '#a1a1aa' },
};

const BACKGROUND_THEMES: Record<BackgroundTheme, { name: string; color: string }> = {
  dark: { name: 'Dark', color: '#0d0d10' },
  midnight: { name: 'Midnight', color: '#0a0a14' },
  charcoal: { name: 'Charcoal', color: '#1a1a1a' },
  oled: { name: 'OLED', color: '#000000' },
};

type StackColorTheme = 'green' | 'cyan' | 'yellow' | 'white' | 'blue';

const STACK_COLOR_THEMES: Record<StackColorTheme, { name: string; color: string }> = {
  green: { name: 'Green', color: '#4ade80' },
  cyan: { name: 'Cyan', color: '#22d3ee' },
  yellow: { name: 'Yellow', color: '#facc15' },
  white: { name: 'White', color: '#f5f5f5' },
  blue: { name: 'Blue', color: '#60a5fa' },
};

// Operations that should use the wizard (multi-input dialog)
const WIZARD_OPERATIONS = new Set([
  // Time Value of Money
  'PMT', 'PV', 'FV', 'RATE', 'NPER',
  // Real Estate
  'CAP', 'NOI', 'CoC', 'DSCR', 'LTV', 'GRM', 'ROI',
  // Investment Analysis
  'CAGR', 'BEP', 'PI',
]);

// User-friendly tooltips for financial operations
const OPERATION_TOOLTIPS: Record<string, string> = {
  // Time Value of Money
  'PMT': 'Payment - Calculate monthly loan payment',
  'PV': 'Present Value - What is the loan worth today?',
  'FV': 'Future Value - What will my investment be worth?',
  'RATE': 'Interest Rate - Find the rate from payment info',
  'NPER': 'Number of Periods - How long to pay off?',
  // Real Estate
  'CAP': 'Cap Rate - Property return rate (NOI ÷ Value)',
  'NOI': 'Net Operating Income - Gross income minus expenses',
  'CoC': 'Cash-on-Cash - Annual return on cash invested',
  'DSCR': 'Debt Service Coverage - Can income cover debt?',
  'LTV': 'Loan-to-Value - What % is financed?',
  'GRM': 'Gross Rent Multiplier - Price ÷ Annual Rent',
  'ROI': 'Return on Investment - Total gain percentage',
  // Investment Analysis
  'CAGR': 'Compound Annual Growth - Average yearly return',
  'BEP': 'Break-Even Point - Units needed to cover costs',
  'PI': 'Profitability Index - Is the project worth it?',
};

function App() {
  const [stack, setStack] = useState<StackItem[]>([]);
  const [currentEntry, setCurrentEntry] = useState('');
  const [rpnExpression, setRpnExpression] = useState('');
  const [precision, setDisplayPrecision] = useState(getPrecision());
  const [mode, setMode] = useState<OperationMode>('basic');
  const [shiftActive, setShiftActive] = useState(false);
  const [showHelp, setShowHelp] = useState<Calculation | null>(null);
  const [showSettings, setShowSettings] = useState(false);

  // Color theme
  const [colorTheme, setColorTheme] = useState<ColorTheme>(() => {
    const saved = localStorage.getItem('finCalc-colorTheme');
    return (saved as ColorTheme) || 'gold';
  });
  const [bgTheme, setBgTheme] = useState<BackgroundTheme>(() => {
    const saved = localStorage.getItem('finCalc-bgTheme');
    return (saved as BackgroundTheme) || 'dark';
  });
  const [stackColor, setStackColor] = useState<StackColorTheme>(() => {
    const saved = localStorage.getItem('finCalc-stackColor');
    return (saved as StackColorTheme) || 'green';
  });

  // UI Scale - separate controls for buttons and fonts
  const [buttonScale, setButtonScale] = useState(() => {
    const saved = localStorage.getItem('finCalc-buttonScale');
    return saved ? parseFloat(saved) : 0.50;
  });
  const [fontScale, setFontScale] = useState(() => {
    const saved = localStorage.getItem('finCalc-fontScale');
    return saved ? parseFloat(saved) : 0.95;
  });

  // Wizard state
  const [wizardOperation, setWizardOperation] = useState<Calculation | null>(null);
  const [wizardInputs, setWizardInputs] = useState<Record<string, string>>({});

  // Save and apply color theme
  useEffect(() => {
    localStorage.setItem('finCalc-colorTheme', colorTheme);
    document.documentElement.setAttribute('data-theme', colorTheme);
  }, [colorTheme]);

  // Save and apply background theme
  useEffect(() => {
    localStorage.setItem('finCalc-bgTheme', bgTheme);
    document.documentElement.setAttribute('data-bg', bgTheme);
  }, [bgTheme]);

  // Save and apply stack color
  useEffect(() => {
    localStorage.setItem('finCalc-stackColor', stackColor);
    document.documentElement.setAttribute('data-stack', stackColor);
  }, [stackColor]);

  // Save UI scales to localStorage
  useEffect(() => {
    localStorage.setItem('finCalc-buttonScale', buttonScale.toString());
    document.documentElement.style.setProperty('--button-scale', buttonScale.toString());
  }, [buttonScale]);

  useEffect(() => {
    localStorage.setItem('finCalc-fontScale', fontScale.toString());
    document.documentElement.style.setProperty('--font-scale', fontScale.toString());
  }, [fontScale]);

  // Handle keyboard input
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Don't capture keys when wizard is open
      if (wizardOperation) return;

      if (e.key >= '0' && e.key <= '9') {
        setCurrentEntry(prev => prev + e.key);
      } else if (e.key === '.') {
        if (!currentEntry.includes('.')) {
          setCurrentEntry(prev => prev === '' ? '0.' : prev + '.');
        }
      } else if (e.key === 'Enter') {
        e.preventDefault();
        handleEnter();
      } else if (e.key === 'Backspace') {
        setCurrentEntry(prev => prev.slice(0, -1));
      } else if (e.key === 'Escape') {
        handleClear();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [currentEntry, wizardOperation]);

  const handleEnter = useCallback(() => {
    // Push current entry (or 0 if empty) onto the stack
    const valueToEnter = currentEntry || '0';
    try {
      const num = bigNumber(valueToEnter);
      setStack(prev => [...prev, num]);
      setRpnExpression(prev => prev ? `${prev} ${valueToEnter}` : valueToEnter);
      setCurrentEntry('');
    } catch {
      // Invalid number, ignore
    }
  }, [currentEntry]);

  const handleOperation = useCallback((symbol: string) => {
    const operation = getOperation(symbol);
    if (!operation) return;

    // Check if this operation should use the wizard
    if (WIZARD_OPERATIONS.has(symbol)) {
      // Initialize wizard inputs
      const initialInputs: Record<string, string> = {};
      operation.operandDescriptors.forEach(desc => {
        initialInputs[desc.name] = '';
      });
      setWizardInputs(initialInputs);
      setWizardOperation(operation);
      return;
    }

    // Auto-enter if there's a current entry
    let newStack = [...stack];
    let newExpression = rpnExpression;

    if (currentEntry) {
      try {
        const num = bigNumber(currentEntry);
        newStack = [...newStack, num];
        newExpression = newExpression ? `${newExpression} ${currentEntry}` : currentEntry;
        setCurrentEntry('');
      } catch {
        return;
      }
    }

    operation.execute(newStack);
    setStack([...newStack]);
    setRpnExpression(newExpression ? `${newExpression} ${symbol}` : symbol);
    setShiftActive(false);
  }, [stack, currentEntry, rpnExpression]);

  const handleWizardSubmit = useCallback(() => {
    if (!wizardOperation) return;

    const newStack = [...stack];
    let newExpression = rpnExpression;

    // Push all wizard inputs onto the stack in order
    const inputValues: string[] = [];
    for (const desc of wizardOperation.operandDescriptors) {
      const value = wizardInputs[desc.name];
      if (!value || value.trim() === '') {
        alert(`Please enter a value for ${desc.name}`);
        return;
      }
      try {
        const num = bigNumber(value);
        newStack.push(num);
        inputValues.push(value);
      } catch {
        alert(`Invalid number for ${desc.name}`);
        return;
      }
    }

    // Execute the operation
    wizardOperation.execute(newStack);

    // Update expression
    const inputsStr = inputValues.join(' ');
    newExpression = newExpression
      ? `${newExpression} ${inputsStr} ${wizardOperation.symbol}`
      : `${inputsStr} ${wizardOperation.symbol}`;

    setStack([...newStack]);
    setRpnExpression(newExpression);
    setWizardOperation(null);
    setWizardInputs({});
  }, [wizardOperation, wizardInputs, stack, rpnExpression]);

  const handleDigit = (digit: string) => {
    setCurrentEntry(prev => prev + digit);
  };

  const handleDecimal = () => {
    if (!currentEntry.includes('.')) {
      setCurrentEntry(prev => prev === '' ? '0.' : prev + '.');
    }
  };

  const handleToggleSign = () => {
    if (currentEntry) {
      setCurrentEntry(prev => prev.startsWith('-') ? prev.slice(1) : '-' + prev);
    } else if (stack.length > 0) {
      const newStack = [...stack];
      const top = newStack.pop()!;
      if (isBigNumber(top)) {
        newStack.push(bigNumber(top.value.negated()));
        setStack(newStack);
      }
    }
  };

  const handleDrop = () => {
    if (stack.length > 0) {
      setStack(prev => prev.slice(0, -1));
    }
  };

  const handleSwap = () => {
    if (stack.length >= 2) {
      const newStack = [...stack];
      const a = newStack.pop()!;
      const b = newStack.pop()!;
      newStack.push(a, b);
      setStack(newStack);
    }
  };

  const handleClear = () => {
    setStack([]);
    setCurrentEntry('');
    setRpnExpression('');
  };

  const handlePrecisionChange = (newPrecision: number) => {
    setPrecision(newPrecision);
    setDisplayPrecision(newPrecision);
  };

  const handleResetSettings = () => {
    setColorTheme('gold');
    setBgTheme('dark');
    setStackColor('green');
    setButtonScale(0.50);
    setFontScale(0.95);
    handlePrecisionChange(2);
  };

  // Number pad - always visible
  const numberPad = [
    ['7', '8', '9', '÷'],
    ['4', '5', '6', '×'],
    ['1', '2', '3', '-'],
    ['0', '.', '±', '+'],
  ];

  const scientificButtons = shiftActive
    ? [
        ['asin', 'acos', 'atan', 'MOD'],
        ['ln', 'LOG', 'e^x', '10^x'],
        ['x²', '√', 'xⁿ', 'ⁿ√x'],
        ['1/x', 'ABS', '', ''],
      ]
    : [
        ['sin', 'cos', 'tan', 'MOD'],
        ['ln', 'LOG', 'e^x', '10^x'],
        ['x²', '√', 'xⁿ', 'ⁿ√x'],
        ['1/x', 'ABS', '', ''],
      ];

  const financialButtons = [
    ['PMT', 'PV', 'FV', 'RATE'],
    ['NPER', 'CAGR', 'ROI', 'PI'],
    ['CAP', 'NOI', 'CoC', 'DSCR'],
    ['LTV', 'GRM', 'BEP', ''],
  ];

  const getButtonClass = (label: string) => {
    if (['+', '-', '×', '÷', '*', '/'].includes(label)) return 'btn-operator';
    if (['ENTER', 'DROP', 'SWAP', 'CLEAR'].includes(label)) return 'btn-action';
    if (label === '.' || label === '±') return 'btn-secondary';
    if (/^[0-9]$/.test(label)) return 'btn-number';
    return 'btn-function';
  };

  return (
    <div className="app">
      {/* Atmospheric background */}
      <div className="bg-grid" />
      <div className="bg-glow" />

      {/* Header */}
      <header className="header">
        <div className="logo">
          <span className="logo-icon">◈</span>
          <span className="logo-text">finCalc</span>
        </div>
        <div className="header-controls">
          <div className="precision-control">
            <label>Dec</label>
            <div className="precision-buttons">
              <button onClick={() => handlePrecisionChange(Math.max(1, precision - 1))}>−</button>
              <span>{precision}</span>
              <button onClick={() => handlePrecisionChange(Math.min(20, precision + 1))}>+</button>
            </div>
          </div>
          <button
            className={`settings-btn ${showSettings ? 'active' : ''}`}
            onClick={() => setShowSettings(!showSettings)}
            title="Settings"
          >
            ⚙
          </button>
        </div>
        {showSettings && (
          <div className="settings-panel">
            <div className="settings-section">
              <label className="settings-label">Accent Color</label>
              <div className="theme-selector">
                {(Object.keys(COLOR_THEMES) as ColorTheme[]).map((theme) => (
                  <button
                    key={theme}
                    className={`theme-btn ${colorTheme === theme ? 'active' : ''}`}
                    onClick={() => setColorTheme(theme)}
                    title={COLOR_THEMES[theme].name}
                    style={{ '--theme-color': COLOR_THEMES[theme].accent } as React.CSSProperties}
                  >
                    <span className="theme-dot" />
                  </button>
                ))}
              </div>
            </div>
            <div className="settings-section">
              <label className="settings-label">Background</label>
              <div className="theme-selector">
                {(Object.keys(BACKGROUND_THEMES) as BackgroundTheme[]).map((theme) => (
                  <button
                    key={theme}
                    className={`theme-btn bg-btn ${bgTheme === theme ? 'active' : ''}`}
                    onClick={() => setBgTheme(theme)}
                    title={BACKGROUND_THEMES[theme].name}
                    style={{ '--theme-color': BACKGROUND_THEMES[theme].color } as React.CSSProperties}
                  >
                    <span className="theme-dot" />
                  </button>
                ))}
              </div>
            </div>
            <div className="settings-section">
              <label className="settings-label">Stack Numbers</label>
              <div className="theme-selector">
                {(Object.keys(STACK_COLOR_THEMES) as StackColorTheme[]).map((theme) => (
                  <button
                    key={theme}
                    className={`theme-btn ${stackColor === theme ? 'active' : ''}`}
                    onClick={() => setStackColor(theme)}
                    title={STACK_COLOR_THEMES[theme].name}
                    style={{ '--theme-color': STACK_COLOR_THEMES[theme].color } as React.CSSProperties}
                  >
                    <span className="theme-dot" />
                  </button>
                ))}
              </div>
            </div>
            <div className="settings-section">
              <label className="settings-label">Button Size</label>
              <div className="settings-slider">
                <input
                  type="range"
                  min="0.3"
                  max="1.2"
                  step="0.05"
                  value={buttonScale}
                  onChange={(e) => setButtonScale(parseFloat(e.target.value))}
                />
                <span className="scale-value">{buttonScale.toFixed(2)}</span>
              </div>
            </div>
            <div className="settings-section">
              <label className="settings-label">Font Size</label>
              <div className="settings-slider">
                <input
                  type="range"
                  min="0.5"
                  max="1.5"
                  step="0.05"
                  value={fontScale}
                  onChange={(e) => setFontScale(parseFloat(e.target.value))}
                />
                <span className="scale-value">{fontScale.toFixed(2)}</span>
              </div>
            </div>
            <button className="reset-btn" onClick={handleResetSettings}>
              Reset to Defaults
            </button>
          </div>
        )}
      </header>

      {/* Main content */}
      <main className="main">
        {/* Stack display */}
        <section className="stack-section">
          <div className="section-label">Stack</div>
          <div className="stack-container">
            <div className="stack-items">
              {stack.length === 0 ? (
                <div className="stack-empty">
                  <span className="stack-empty-icon">⌸</span>
                  <span>Stack empty</span>
                </div>
              ) : (
                // Display: most recent (top of stack) at visual top with index 0
                // Reverse so newest is first, index = position from top
                [...stack].reverse().map((item, index) => (
                  <div
                    key={index}
                    className={`stack-item ${index === 0 ? 'stack-item-top' : ''}`}
                    style={{
                      animationDelay: `${index * 0.05}s`,
                    }}
                  >
                    <span className="stack-index">{index}</span>
                    <span className={`stack-value ${isBigNumber(item) && item.value.isNegative() ? 'negative' : ''}`}>
                      {formatStackItem(item)}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </section>

        {/* Expression display */}
        <section className="expression-section">
          <div className="section-label">Expression</div>
          <div className="expression-display">
            {rpnExpression || '—'}
          </div>
        </section>

        {/* Entry display */}
        <section className="entry-section">
          <div className="entry-display">
            <span className="entry-value">{currentEntry || '0'}</span>
            <span className="entry-cursor" />
          </div>
          <button className="enter-btn" onClick={handleEnter}>
            ENTER
          </button>
        </section>

        {/* Mode tabs */}
        <nav className="mode-tabs">
          <button
            className={`mode-tab ${mode === 'basic' ? 'active' : ''}`}
            onClick={() => setMode('basic')}
          >
            Basic
          </button>
          <button
            className={`mode-tab ${mode === 'scientific' ? 'active' : ''}`}
            onClick={() => setMode('scientific')}
          >
            Scientific
          </button>
          <button
            className={`mode-tab ${mode === 'financial' ? 'active' : ''}`}
            onClick={() => setMode('financial')}
          >
            Financial
          </button>
        </nav>

        {/* Calculator buttons */}
        <section className="buttons-section">
          {/* Function buttons for non-basic modes */}
          {mode === 'scientific' && (
            <div className="function-panel">
              <button
                className={`shift-btn ${shiftActive ? 'active' : ''}`}
                onClick={() => setShiftActive(!shiftActive)}
              >
                SHIFT
              </button>
              <div className="button-grid function-grid">
                {scientificButtons.map((row, rowIndex) => (
                  row.map((label, colIndex) => (
                    label ? (
                      <button
                        key={`${rowIndex}-${colIndex}`}
                        className="calc-btn btn-function"
                        onClick={() => handleOperation(label)}
                        onContextMenu={(e) => {
                          e.preventDefault();
                          const op = getOperation(label);
                          if (op) setShowHelp(op);
                        }}
                      >
                        {label}
                      </button>
                    ) : (
                      <div key={`${rowIndex}-${colIndex}`} className="btn-spacer" />
                    )
                  ))
                ))}
              </div>
            </div>
          )}

          {mode === 'financial' && (
            <div className="function-panel">
              <div className="button-grid function-grid">
                {financialButtons.map((row, rowIndex) => (
                  row.map((label, colIndex) => (
                    label ? (
                      <button
                        key={`${rowIndex}-${colIndex}`}
                        className="calc-btn btn-financial"
                        title={OPERATION_TOOLTIPS[label] || label}
                        onClick={() => handleOperation(label)}
                        onContextMenu={(e) => {
                          e.preventDefault();
                          const op = getOperation(label);
                          if (op) setShowHelp(op);
                        }}
                      >
                        {label}
                      </button>
                    ) : (
                      <div key={`${rowIndex}-${colIndex}`} className="btn-spacer" />
                    )
                  ))
                ))}
              </div>
            </div>
          )}

          {/* Number pad - ALWAYS visible */}
          <div className="number-pad">
            <div className="button-grid basic-grid">
              {numberPad.map((row, rowIndex) => (
                row.map((label, colIndex) => (
                  <button
                    key={`num-${rowIndex}-${colIndex}`}
                    className={`calc-btn ${getButtonClass(label)}`}
                    onClick={() => {
                      if (/^[0-9]$/.test(label)) handleDigit(label);
                      else if (label === '.') handleDecimal();
                      else if (label === '±') handleToggleSign();
                      else handleOperation(label);
                    }}
                  >
                    {label}
                  </button>
                ))
              ))}
            </div>
          </div>

          {/* Stack operations */}
          <div className="stack-ops">
            <button className="calc-btn btn-stack" onClick={handleDrop}>DROP</button>
            <button className="calc-btn btn-stack" onClick={handleSwap}>SWAP</button>
            <button className="calc-btn btn-clear" onClick={handleClear}>CLEAR</button>
          </div>
        </section>
      </main>

      {/* Wizard Modal for Financial Operations */}
      {wizardOperation && (
        <div className="modal-overlay" onClick={() => setWizardOperation(null)}>
          <div className="modal wizard-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-symbol">{wizardOperation.symbol}</span>
              <h3>{wizardOperation.description}</h3>
              <button className="modal-close" onClick={() => setWizardOperation(null)}>×</button>
            </div>
            <div className="modal-body">
              <div className="wizard-inputs">
                {wizardOperation.operandDescriptors.map((desc, i) => (
                  <div key={i} className="wizard-field">
                    <label htmlFor={`wizard-${desc.name}`}>
                      <span className="field-name">{desc.name}</span>
                      <span className="field-desc">{desc.description}</span>
                    </label>
                    <input
                      id={`wizard-${desc.name}`}
                      type="text"
                      inputMode="decimal"
                      value={wizardInputs[desc.name] || ''}
                      onChange={(e) => setWizardInputs(prev => ({
                        ...prev,
                        [desc.name]: e.target.value
                      }))}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          // Move to next field or submit
                          const inputs = wizardOperation.operandDescriptors;
                          const currentIndex = inputs.findIndex(d => d.name === desc.name);
                          if (currentIndex < inputs.length - 1) {
                            const nextInput = document.getElementById(`wizard-${inputs[currentIndex + 1].name}`);
                            nextInput?.focus();
                          } else {
                            handleWizardSubmit();
                          }
                        }
                      }}
                      autoFocus={i === 0}
                      placeholder={`Enter ${desc.name}`}
                    />
                  </div>
                ))}
              </div>
              <div className="wizard-example">
                <h4>Example</h4>
                <pre>{wizardOperation.example}</pre>
              </div>
              <div className="wizard-actions">
                <button className="wizard-cancel" onClick={() => setWizardOperation(null)}>
                  Cancel
                </button>
                <button className="wizard-submit" onClick={handleWizardSubmit}>
                  Calculate
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Help modal */}
      {showHelp && (
        <div className="modal-overlay" onClick={() => setShowHelp(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-symbol">{showHelp.symbol}</span>
              <h3>{showHelp.description}</h3>
              <button className="modal-close" onClick={() => setShowHelp(null)}>×</button>
            </div>
            <div className="modal-body">
              <div className="modal-section">
                <h4>Operands</h4>
                <ul>
                  {showHelp.operandDescriptors.map((op, i) => (
                    <li key={i}>
                      <span className="operand-name">{op.name}</span>
                      <span className="operand-desc">{op.description}</span>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="modal-section">
                <h4>Example</h4>
                <pre>{showHelp.example}</pre>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Footer hint */}
      <footer className="footer">
        <span>Right-click functions for help</span>
        <span className="separator">•</span>
        <span>RPN Stack Calculator</span>
      </footer>
    </div>
  );
}

export default App;
