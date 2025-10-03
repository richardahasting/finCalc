package com.finCalc.ui;

import com.finCalc.calculator.*;
import com.finCalc.calculator.operations.basic.*;
import com.finCalc.calculator.operations.scientific.*;
import com.finCalc.calculator.operations.financial.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Main JavaFX application for the RPN calculator.
 *
 * <p>Features:
 * <ul>
 *   <li>Scrollable stack view showing all values</li>
 *   <li>RPN expression display</li>
 *   <li>Calculator-style button layout</li>
 *   <li>Precision control</li>
 *   <li>Keyboard support</li>
 * </ul>
 */
public class CalculatorApp extends Application {

    private final Stack<StackItem> calculatorStack = new Stack<>();
    private final StringBuilder currentEntry = new StringBuilder();
    private final StringBuilder rpnExpression = new StringBuilder();

    // Modifier key states
    private boolean shiftActive = false;
    private boolean ctrlActive = false;

    // UI Components
    private ListView<String> stackView;
    private Label rpnExpressionLabel;
    private TextField entryField;
    private Spinner<Integer> precisionSpinner;
    private Button shiftButton;
    private Button ctrlButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("finCalc - RPN Calculator");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // Menu bar at top
        root.setTop(createMenuBar());

        // Main content area
        VBox centerContent = new VBox(10);
        centerContent.setPadding(new Insets(10));
        centerContent.setStyle("-fx-background-color: #2b2b2b;");

        // Settings bar
        centerContent.getChildren().add(createSettingsBar());

        // Stack view
        centerContent.getChildren().add(createStackView());

        // RPN expression display
        centerContent.getChildren().add(createRpnExpressionView());

        // Entry field
        centerContent.getChildren().add(createEntryField());

        // Calculator buttons (simplified - just numbers and basic ops)
        centerContent.getChildren().add(createButtonPanel());

        root.setCenter(centerContent);

        Scene scene = new Scene(root, 480, 900);

        // Load CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("/calculator.css").toExternalForm());

        // Add keyboard support
        scene.setOnKeyPressed(event -> handleKeyPress(event.getText(), event.getCode().toString()));

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial update
        updateDisplay();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #1e1e1e;");

        // Basic Operations Menu
        Menu basicMenu = new Menu("Basic");
        basicMenu.setStyle("-fx-text-fill: #ffffff;");
        basicMenu.getItems().addAll(
            createMenuItem("Add (+)", Add.INSTANCE),
            createMenuItem("Subtract (−)", Subtract.INSTANCE),
            createMenuItem("Multiply (×)", Multiply.INSTANCE),
            createMenuItem("Divide (÷)", Divide.INSTANCE),
            new SeparatorMenuItem(),
            createMenuItem("Reciprocal (1/x)", Reciprocal.INSTANCE),
            createMenuItem("Absolute Value", AbsoluteValue.INSTANCE),
            createMenuItem("Modulo", Modulo.INSTANCE)
        );

        // Scientific Operations Menu
        Menu scientificMenu = new Menu("Scientific");
        scientificMenu.setStyle("-fx-text-fill: #ffffff;");
        scientificMenu.getItems().addAll(
            createMenuItem("Square Root (√)", SquareRoot.INSTANCE),
            createMenuItem("Square (x²)", Square.INSTANCE),
            createMenuItem("Power (xⁿ)", Power.INSTANCE),
            createMenuItem("Nth Root (ⁿ√x)", NthRoot.INSTANCE),
            new SeparatorMenuItem(),
            createMenuItem("Natural Log (ln)", NaturalLog.INSTANCE),
            createMenuItem("Log Base 10 (log)", Log10.INSTANCE),
            createMenuItem("e^x", Exponential.INSTANCE),
            createMenuItem("10^x", Exp10.INSTANCE),
            new SeparatorMenuItem(),
            createMenuItem("Sine", Sine.INSTANCE),
            createMenuItem("Cosine", Cosine.INSTANCE),
            createMenuItem("Tangent", Tangent.INSTANCE),
            createMenuItem("Arc Sine", ArcSine.INSTANCE),
            createMenuItem("Arc Cosine", ArcCosine.INSTANCE),
            createMenuItem("Arc Tangent", ArcTangent.INSTANCE)
        );

        // Financial Operations Menu
        Menu financialMenu = new Menu("Financial");
        financialMenu.setStyle("-fx-text-fill: #ffffff;");

        // Time Value of Money submenu
        Menu tvmMenu = new Menu("Time Value of Money");
        tvmMenu.getItems().addAll(
            createFinancialMenuItem("Payment (PMT)", Payment.INSTANCE),
            createFinancialMenuItem("Present Value (PV)", PresentValue.INSTANCE),
            createFinancialMenuItem("Future Value (FV)", FutureValue.INSTANCE),
            createFinancialMenuItem("Interest Rate (RATE)", InterestRate.INSTANCE),
            createFinancialMenuItem("Number of Periods (NPER)", NumberOfPeriods.INSTANCE)
        );

        // Investment Analysis submenu
        Menu investmentMenu = new Menu("Investment Analysis");
        investmentMenu.getItems().addAll(
            createFinancialMenuItem("Compound Annual Growth Rate (CAGR)", CompoundAnnualGrowthRate.INSTANCE),
            createFinancialMenuItem("Break-Even Point (BEP)", BreakEvenPoint.INSTANCE),
            createFinancialMenuItem("Payback Period", PaybackPeriod.INSTANCE),
            createFinancialMenuItem("Profitability Index (PI)", ProfitabilityIndex.INSTANCE)
        );

        // Real Estate Analysis submenu
        Menu realEstateMenu = new Menu("Real Estate Analysis");
        realEstateMenu.getItems().addAll(
            createFinancialMenuItem("Cap Rate (CAP)", CapRate.INSTANCE),
            createFinancialMenuItem("Net Operating Income (NOI)", NetOperatingIncome.INSTANCE),
            createFinancialMenuItem("Cash-on-Cash Return (CoC)", CashOnCash.INSTANCE),
            createFinancialMenuItem("Debt Service Coverage Ratio (DSCR)", DebtServiceCoverageRatio.INSTANCE),
            createFinancialMenuItem("Loan-to-Value (LTV)", LoanToValue.INSTANCE),
            createFinancialMenuItem("Gross Rent Multiplier (GRM)", GrossRentMultiplier.INSTANCE),
            createFinancialMenuItem("Return on Investment (ROI)", ReturnOnInvestment.INSTANCE),
            createFinancialMenuItem("Cash Flow After Taxes (CFAT)", CashFlowAfterTaxes.INSTANCE),
            createFinancialMenuItem("Operating Expense Ratio (OER)", OperatingExpenseRatio.INSTANCE),
            createFinancialMenuItem("Vacancy Loss", VacancyLoss.INSTANCE),
            createFinancialMenuItem("Effective Gross Income (EGI)", EffectiveGrossIncome.INSTANCE),
            createFinancialMenuItem("Price Per Square Foot (PPSF)", PricePerSquareFoot.INSTANCE),
            createFinancialMenuItem("Rent Per Square Foot (RPSF)", RentPerSquareFoot.INSTANCE)
        );

        // Loan & Mortgage submenu
        Menu loanMenu = new Menu("Loan & Mortgage");
        loanMenu.getItems().addAll(
            createFinancialMenuItem("Remaining Balance", RemainingBalance.INSTANCE),
            createFinancialMenuItem("Total Interest Paid", TotalInterestPaid.INSTANCE),
            createFinancialMenuItem("APR to APY Conversion", AprToApy.INSTANCE),
            createFinancialMenuItem("Debt-to-Income Ratio (DTI)", DebtToIncomeRatio.INSTANCE)
        );

        // Bond Calculations submenu
        Menu bondMenu = new Menu("Bond Calculations");
        bondMenu.getItems().addAll(
            createFinancialMenuItem("Current Yield", CurrentYield.INSTANCE),
            createFinancialMenuItem("Yield to Maturity (YTM)", YieldToMaturity.INSTANCE),
            createFinancialMenuItem("Bond Price", BondPrice.INSTANCE)
        );

        // Tax & Retirement submenu
        Menu taxRetirementMenu = new Menu("Tax & Retirement");
        taxRetirementMenu.getItems().addAll(
            createFinancialMenuItem("Effective Tax Rate", EffectiveTaxRate.INSTANCE),
            createFinancialMenuItem("After-Tax Return", AfterTaxReturn.INSTANCE),
            createFinancialMenuItem("Required Minimum Distribution (RMD)", RequiredMinimumDistribution.INSTANCE)
        );

        financialMenu.getItems().addAll(tvmMenu, investmentMenu, realEstateMenu, loanMenu, bondMenu, taxRetirementMenu);

        // Stack Operations Menu
        Menu stackMenu = new Menu("Stack");
        stackMenu.setStyle("-fx-text-fill: #ffffff;");
        stackMenu.getItems().addAll(
            createStackMenuItem("Drop", this::dropStack),
            createStackMenuItem("Swap", this::swapStack),
            createStackMenuItem("Clear All", this::clearAll)
        );

        // Help Menu
        Menu helpMenu = new Menu("Help");
        helpMenu.setStyle("-fx-text-fill: #ffffff;");
        MenuItem aboutItem = new MenuItem("About finCalc");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(basicMenu, scientificMenu, financialMenu, stackMenu, helpMenu);
        return menuBar;
    }

    private MenuItem createMenuItem(String label, Calculation operation) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(e -> performOperation(operation, operation.getSymbol()));
        return item;
    }

    private MenuItem createFinancialMenuItem(String label, Calculation operation) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(e -> showOperationWizard(operation, operation.getSymbol(), label));
        return item;
    }

    private MenuItem createStackMenuItem(String label, Runnable action) {
        MenuItem item = new MenuItem(label);
        item.setOnAction(e -> action.run());
        return item;
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About finCalc");
        alert.setHeaderText("finCalc - RPN Financial Calculator");
        alert.setContentText("""
            Version 1.0.0

            A Reverse Polish Notation (RPN) calculator designed for
            real estate and financial calculations.

            Features:
            • Basic arithmetic operations
            • Scientific functions
            • Financial calculations (TVM)
            • Arbitrary precision using BigDecimal

            Right-click financial operations for detailed help.
            """);
        alert.showAndWait();
    }

    private HBox createSettingsBar() {
        HBox settingsBar = new HBox(10);
        settingsBar.setPadding(new Insets(5));
        settingsBar.setAlignment(Pos.CENTER_LEFT);
        settingsBar.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 5;");

        Label precisionLabel = new Label("Precision:");
        precisionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        precisionSpinner = new Spinner<>(1, 50, 10);
        precisionSpinner.setEditable(true);
        precisionSpinner.setPrefWidth(70);
        precisionSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                BigNumber.setPrecision(newVal);
                updateDisplay(); // Refresh display with new precision
            }
        });

        settingsBar.getChildren().addAll(precisionLabel, precisionSpinner);
        return settingsBar;
    }

    private VBox createStackView() {
        VBox stackContainer = new VBox(5);

        Label stackLabel = new Label("Stack:");
        stackLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px; -fx-font-weight: bold;");

        stackView = new ListView<>();
        stackView.setPrefHeight(150);
        stackView.setStyle(
            "-fx-background-color: #1a1a1a; " +
            "-fx-control-inner-background: #1a1a1a; " +
            "-fx-text-fill: #00ff00; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 14px;"
        );

        stackContainer.getChildren().addAll(stackLabel, stackView);
        return stackContainer;
    }

    private VBox createRpnExpressionView() {
        VBox rpnContainer = new VBox(5);

        Label rpnLabel = new Label("RPN Expression:");
        rpnLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px; -fx-font-weight: bold;");

        rpnExpressionLabel = new Label("");
        rpnExpressionLabel.setWrapText(true);
        rpnExpressionLabel.setPrefHeight(40);
        rpnExpressionLabel.setMaxWidth(Double.MAX_VALUE);
        rpnExpressionLabel.setStyle(
            "-fx-background-color: #1a1a1a; " +
            "-fx-text-fill: #ffaa00; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 5; " +
            "-fx-background-radius: 3;"
        );

        rpnContainer.getChildren().addAll(rpnLabel, rpnExpressionLabel);
        return rpnContainer;
    }

    private HBox createEntryField() {
        HBox entryContainer = new HBox(5);
        entryContainer.setAlignment(Pos.CENTER);

        Label entryLabel = new Label("Entry:");
        entryLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        entryField = new TextField();
        entryField.setEditable(false);
        entryField.setPrefHeight(40);
        entryField.setStyle(
            "-fx-background-color: #000000; " +
            "-fx-text-fill: #00ff00; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-alignment: center-right;"
        );
        HBox.setHgrow(entryField, Priority.ALWAYS);

        Button enterButton = createCalcButton("ENTER", "#4CAF50", 80);
        enterButton.setOnAction(e -> handleEnter());

        entryContainer.getChildren().addAll(entryLabel, entryField, enterButton);
        return entryContainer;
    }

    private VBox createButtonPanel() {
        VBox buttonPanel = new VBox(6);
        buttonPanel.setPadding(new Insets(10, 0, 0, 0));

        // Modifier buttons row (only SHIFT for scientific functions)
        HBox modifierRow = createModifierButtons();
        buttonPanel.getChildren().add(modifierRow);

        // Row 1: Numbers 7-9, basic ops, TVM functions
        GridPane row1 = createButtonRow(
            new ButtonSpec("7", null, null, "#444444"),
            new ButtonSpec("8", null, null, "#444444"),
            new ButtonSpec("9", null, null, "#444444"),
            new ButtonSpec("÷", null, null, "#ff9500"),
            new ButtonSpec("PMT", null, null, "#1e7a46"),
            new ButtonSpec("DROP", null, null, "#aa3333")
        );

        // Row 2: Numbers 4-6, basic ops, TVM functions
        GridPane row2 = createButtonRow(
            new ButtonSpec("4", null, null, "#444444"),
            new ButtonSpec("5", null, null, "#444444"),
            new ButtonSpec("6", null, null, "#444444"),
            new ButtonSpec("×", null, null, "#ff9500"),
            new ButtonSpec("PV", null, null, "#1e7a46"),
            new ButtonSpec("SWAP", null, null, "#aa3333")
        );

        // Row 3: Numbers 1-3, basic ops, TVM functions
        GridPane row3 = createButtonRow(
            new ButtonSpec("1", null, null, "#444444"),
            new ButtonSpec("2", null, null, "#444444"),
            new ButtonSpec("3", null, null, "#444444"),
            new ButtonSpec("−", null, null, "#ff9500"),
            new ButtonSpec("FV", null, null, "#1e7a46"),
            new ButtonSpec("CLEAR", null, null, "#aa3333")
        );

        // Row 4: 0, decimal, +/-, TVM functions
        GridPane row4 = createButtonRow(
            new ButtonSpec("0", null, null, "#444444"),
            new ButtonSpec(".", null, null, "#444444"),
            new ButtonSpec("±", null, null, "#444444"),
            new ButtonSpec("+", null, null, "#ff9500"),
            new ButtonSpec("RATE", null, null, "#1e7a46"),
            new ButtonSpec("NPER", null, null, "#1e7a46")
        );

        // Row 5: Scientific functions with SHIFT modifier + Real Estate
        GridPane row5 = createButtonRow(
            new ButtonSpec("√", "sin", null, "#5a5a5a"),
            new ButtonSpec("x²", "cos", null, "#5a5a5a"),
            new ButtonSpec("xⁿ", "tan", null, "#5a5a5a"),
            new ButtonSpec("1/x", "|x|", null, "#5a5a5a"),
            new ButtonSpec("CAP", null, null, "#2a5f8f"),
            new ButtonSpec("NOI", null, null, "#2a5f8f")
        );

        // Row 6: More scientific + Real Estate + Advanced
        GridPane row6 = createButtonRow(
            new ButtonSpec("ln", "LOG", null, "#5a5a5a"),
            new ButtonSpec("e^x", "10^x", null, "#5a5a5a"),
            new ButtonSpec("ⁿ√x", "MOD", null, "#5a5a5a"),
            new ButtonSpec("NPV", null, null, "#6b4d8a"),
            new ButtonSpec("CoC", null, null, "#2a5f8f"),
            new ButtonSpec("LTV", null, null, "#2a5f8f")
        );

        // Row 7: Bond functions + Real Estate
        GridPane row7 = createButtonRow(
            new ButtonSpec("YTM", null, null, "#8b6914"),
            new ButtonSpec("YLD", null, null, "#8b6914"),
            new ButtonSpec("BND", null, null, "#8b6914"),
            new ButtonSpec("IRR", null, null, "#6b4d8a"),
            new ButtonSpec("DSCR", null, null, "#2a5f8f"),
            new ButtonSpec("GRM", null, null, "#2a5f8f")
        );

        // Row 8: Investment Analysis + Real Estate
        GridPane row8 = createButtonRow(
            new ButtonSpec("CAGR", null, null, "#6b4d8a"),
            new ButtonSpec("BEP", null, null, "#6b4d8a"),
            new ButtonSpec("PI", null, null, "#6b4d8a"),
            new ButtonSpec("ROI", null, null, "#2a5f8f"),
            new ButtonSpec("OER", null, null, "#2a5f8f"),
            new ButtonSpec("EGI", null, null, "#2a5f8f")
        );

        buttonPanel.getChildren().addAll(row1, row2, row3, row4, row5, row6, row7, row8);
        return buttonPanel;
    }

    private HBox createModifierButtons() {
        HBox modifierBox = new HBox(10);
        modifierBox.setAlignment(Pos.CENTER);
        modifierBox.setPadding(new Insets(5));

        // SHIFT button (for scientific functions)
        shiftButton = new Button("SHIFT");
        shiftButton.setPrefSize(100, 35);
        shiftButton.setStyle(getModifierButtonStyle("#ff9500", false));
        shiftButton.setOnAction(e -> toggleShift());

        // Status label
        Label statusLabel = new Label("Modifier:");
        statusLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        modifierBox.getChildren().addAll(statusLabel, shiftButton);
        return modifierBox;
    }

    private String getModifierButtonStyle(String color, boolean active) {
        if (active) {
            return String.format(
                "-fx-background-color: %s; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5;",
                color
            );
        } else {
            return String.format(
                "-fx-background-color: #3a3a3a; " +
                "-fx-text-fill: %s; " +
                "-fx-font-weight: normal; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: %s; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5;",
                color, color
            );
        }
    }

    private void toggleShift() {
        shiftActive = !shiftActive;
        shiftButton.setStyle(getModifierButtonStyle("#ff9500", shiftActive));
    }

    private void clearModifiers() {
        if (shiftActive) {
            shiftActive = false;
            shiftButton.setStyle(getModifierButtonStyle("#ff9500", false));
        }
    }

    private GridPane createButtonRow(ButtonSpec... buttons) {
        GridPane row = new GridPane();
        row.setHgap(5);
        row.setAlignment(Pos.CENTER);

        for (int i = 0; i < buttons.length; i++) {
            ButtonSpec buttonSpec = buttons[i];

            if (!buttonSpec.label.isEmpty()) {
                VBox buttonContainer = createMultiFunctionButton(buttonSpec);
                row.add(buttonContainer, i, 0);
            } else {
                Button btn = createCalcButton(buttonSpec.label, buttonSpec.color, 70);
                btn.setDisable(true);
                btn.setVisible(false);
                row.add(btn, i, 0);
            }
        }

        return row;
    }

    private VBox createMultiFunctionButton(ButtonSpec spec) {
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-padding: 0;");

        // Shift function label (orange, small text at top)
        Label shiftLabel = new Label(spec.shiftLabel != null ? spec.shiftLabel : "");
        shiftLabel.setStyle("-fx-text-fill: #ff9500; -fx-font-size: 8px; -fx-font-weight: bold;");
        shiftLabel.setMaxWidth(70);
        shiftLabel.setAlignment(Pos.CENTER);

        // Ctrl function label (blue, small text)
        Label ctrlLabel = new Label(spec.ctrlLabel != null ? spec.ctrlLabel : "");
        ctrlLabel.setStyle("-fx-text-fill: #4a90e2; -fx-font-size: 8px; -fx-font-weight: bold;");
        ctrlLabel.setMaxWidth(70);
        ctrlLabel.setAlignment(Pos.CENTER);

        // Main button
        Button btn = createCalcButton(spec.label, spec.color, 70);
        btn.setOnAction(e -> handleMultiFunctionButtonPress(spec));

        // Add right-click help for all functions on this button
        btn.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showMultiFunctionHelp(spec);
                event.consume();
            }
        });

        // Add tooltip if specified
        if (spec.tooltip != null) {
            Tooltip tooltip = new Tooltip(spec.tooltip);
            tooltip.setShowDelay(javafx.util.Duration.millis(300));
            Tooltip.install(btn, tooltip);
        }

        container.getChildren().addAll(shiftLabel, btn, ctrlLabel);
        return container;
    }

    private void handleMultiFunctionButtonPress(ButtonSpec spec) {
        String function;

        if (shiftActive && spec.shiftLabel != null) {
            function = spec.shiftLabel;
        } else if (ctrlActive && spec.ctrlLabel != null) {
            function = spec.ctrlLabel;
        } else {
            function = spec.label;
        }

        // Execute the function
        handleButtonPress(function);

        // Clear modifiers after use (Option 1 behavior)
        clearModifiers();
    }

    private Button createCalcButton(String text, String color, int width) {
        Button button = new Button(text);
        button.setPrefSize(width, 50);

        // Use smaller font for longer labels
        int fontSize = text.length() > 3 ? 11 : 14;
        button.setFont(Font.font("System", FontWeight.BOLD, fontSize));

        String style = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, derive(%s, -20%%)); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: derive(%s, -30%%); " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 3, 0, 0, 2);",
            color, color, color
        );

        button.setStyle(style);

        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(style + "-fx-scale-y: 0.98; -fx-scale-x: 0.98;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle(style);
        });

        // Press effect
        button.setOnMousePressed(e -> {
            String pressedStyle = String.format(
                "-fx-background-color: derive(%s, -30%%); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-border-color: derive(%s, -40%%); " +
                "-fx-border-width: 1; " +
                "-fx-scale-y: 0.95; -fx-scale-x: 0.95;",
                color, color
            );
            button.setStyle(pressedStyle);
        });

        button.setOnMouseReleased(e -> {
            button.setStyle(style);
        });

        return button;
    }

    private void handleButtonPress(String buttonText) {
        switch (buttonText) {
            // Numbers
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> appendDigit(buttonText);
            case "." -> appendDecimal();
            case "±" -> toggleSign();
            case "ENTER" -> handleEnter();

            // Stack operations
            case "DROP" -> dropStack();
            case "SWAP" -> swapStack();
            case "CLEAR" -> clearAll();

            // Basic arithmetic
            case "+" -> performOperation(Add.INSTANCE, "+");
            case "−" -> performOperation(Subtract.INSTANCE, "−");
            case "×" -> performOperation(Multiply.INSTANCE, "×");
            case "÷" -> performOperation(Divide.INSTANCE, "÷");

            // Scientific functions
            case "√" -> performOperation(SquareRoot.INSTANCE, "√");
            case "x²" -> performOperation(Square.INSTANCE, "SQ");
            case "xⁿ" -> performOperation(Power.INSTANCE, "POW");
            case "1/x" -> performOperation(Reciprocal.INSTANCE, "1/x");
            case "ⁿ√x" -> performOperation(NthRoot.INSTANCE, "NTHRT");
            case "LOG" -> performOperation(Log10.INSTANCE, "LOG");
            case "e^x" -> performOperation(Exponential.INSTANCE, "EXP");
            case "|x|" -> performOperation(AbsoluteValue.INSTANCE, "ABS");
            case "sin" -> performOperation(Sine.INSTANCE, "SIN");
            case "cos" -> performOperation(Cosine.INSTANCE, "COS");
            case "tan" -> performOperation(Tangent.INSTANCE, "TAN");
            case "MOD" -> performOperation(Modulo.INSTANCE, "MOD");
            case "ln" -> performOperation(NaturalLog.INSTANCE, "LN");
            case "10^x" -> performOperation(Exp10.INSTANCE, "10^x");

            // TVM (Time Value of Money) functions
            case "PMT" -> showOperationWizard(Payment.INSTANCE, "PMT", "Payment");
            case "PV" -> showOperationWizard(PresentValue.INSTANCE, "PV", "Present Value");
            case "FV" -> showOperationWizard(FutureValue.INSTANCE, "FV", "Future Value");
            case "RATE" -> showOperationWizard(InterestRate.INSTANCE, "RATE", "Interest Rate");
            case "NPER" -> showOperationWizard(NumberOfPeriods.INSTANCE, "NPER", "Number of Periods");

            // Real Estate financial functions
            case "CAP" -> performOperation(CapRate.INSTANCE, "CAP");
            case "NOI" -> performOperation(NetOperatingIncome.INSTANCE, "NOI");
            case "CoC" -> performOperation(CashOnCash.INSTANCE, "CoC");
            case "DSCR" -> performOperation(DebtServiceCoverageRatio.INSTANCE, "DSCR");
            case "LTV" -> performOperation(LoanToValue.INSTANCE, "LTV");
            case "GRM" -> performOperation(GrossRentMultiplier.INSTANCE, "GRM");
            case "ROI" -> performOperation(ReturnOnInvestment.INSTANCE, "ROI");
            case "OER" -> performOperation(OperatingExpenseRatio.INSTANCE, "OER");
            case "EGI" -> performOperation(EffectiveGrossIncome.INSTANCE, "EGI");

            // Bond Calculations
            case "YTM" -> showOperationWizard(YieldToMaturity.INSTANCE, "YTM", "Yield to Maturity");
            case "YLD" -> performOperation(CurrentYield.INSTANCE, "YLD");
            case "BND" -> showOperationWizard(BondPrice.INSTANCE, "BND", "Bond Price");

            // Investment Analysis
            case "CAGR" -> performOperation(CompoundAnnualGrowthRate.INSTANCE, "CAGR");
            case "BEP" -> performOperation(BreakEvenPoint.INSTANCE, "BEP");
            case "PI" -> performOperation(ProfitabilityIndex.INSTANCE, "PI");

            // Not yet implemented
            case "NPV" -> showNotImplemented("NPV - Net Present Value");
            case "IRR" -> showNotImplemented("IRR - Internal Rate of Return");
        }
    }

    private void showNotImplemented(String functionName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(functionName);
        alert.setContentText("This financial function will be implemented in the next update.");
        alert.showAndWait();
    }

    private void handleKeyPress(String text, String code) {
        // Handle numeric keys
        if (text.matches("[0-9]")) {
            appendDigit(text);
        } else {
            switch (code) {
                case "PERIOD", "DECIMAL" -> appendDecimal();
                case "ENTER" -> handleEnter();
                case "ESCAPE" -> clearAll();
                case "BACK_SPACE" -> backspace();
                case "MINUS" -> {
                    if (currentEntry.length() == 0) {
                        toggleSign();
                    }
                }
            }
        }
    }

    private void appendDigit(String digit) {
        currentEntry.append(digit);
        updateDisplay();
    }

    private void appendDecimal() {
        if (!currentEntry.toString().contains(".")) {
            if (currentEntry.length() == 0) {
                currentEntry.append("0");
            }
            currentEntry.append(".");
            updateDisplay();
        }
    }

    private void toggleSign() {
        if (currentEntry.length() > 0) {
            // Toggle sign of current entry
            if (currentEntry.charAt(0) == '-') {
                currentEntry.deleteCharAt(0);
            } else {
                currentEntry.insert(0, '-');
            }
            updateDisplay();
        } else if (!calculatorStack.isEmpty()) {
            // Toggle sign of top stack item by multiplying by -1
            calculatorStack.push(BigNumber.of("-1"));
            Multiply.INSTANCE.execute(calculatorStack);

            // Add custom symbol to RPN expression
            if (rpnExpression.length() > 0) {
                rpnExpression.append(" ");
            }
            rpnExpression.append("±");

            updateDisplay();
        }
    }

    private void backspace() {
        if (currentEntry.length() > 0) {
            currentEntry.deleteCharAt(currentEntry.length() - 1);
            updateDisplay();
        }
    }

    private void handleEnter() {
        if (currentEntry.length() > 0) {
            try {
                BigNumber number = BigNumber.of(currentEntry.toString());
                calculatorStack.push(number);

                // Add to RPN expression
                if (rpnExpression.length() > 0) {
                    rpnExpression.append(" ");
                }
                rpnExpression.append(currentEntry);

                currentEntry.setLength(0);
                updateDisplay();
            } catch (NumberFormatException e) {
                showError("Invalid number format");
            }
        }
    }

    private void performOperation(Calculation operation, String symbol) {
        // Auto-enter current value if present
        if (currentEntry.length() > 0) {
            handleEnter();
        }

        // Perform the operation directly on the calculator stack
        // The operation modifies the stack in place and returns it
        operation.execute(calculatorStack);

        // Add to RPN expression
        if (rpnExpression.length() > 0) {
            rpnExpression.append(" ");
        }
        rpnExpression.append(symbol);

        updateDisplay();
    }

    private void dropStack() {
        if (!calculatorStack.isEmpty()) {
            calculatorStack.pop();
            updateDisplay();
        }
    }

    private void swapStack() {
        if (calculatorStack.size() >= 2) {
            StackItem top = calculatorStack.pop();
            StackItem second = calculatorStack.pop();
            calculatorStack.push(top);
            calculatorStack.push(second);
            updateDisplay();
        }
    }

    private void clearAll() {
        calculatorStack.clear();
        currentEntry.setLength(0);
        rpnExpression.setLength(0);
        updateDisplay();
    }

    private void updateDisplay() {
        // Update entry field
        entryField.setText(currentEntry.toString());

        // Update stack view (bottom to top: [0] at bottom, top of stack at top of display)
        stackView.getItems().clear();
        for (int i = calculatorStack.size() - 1; i >= 0; i--) {
            StackItem item = calculatorStack.get(i);
            String display = String.format("[%d]: %s", i, formatStackItem(item));
            stackView.getItems().add(display);  // Add in order: top of stack first
        }

        // Update RPN expression
        rpnExpressionLabel.setText(rpnExpression.toString());
    }

    private String formatStackItem(StackItem item) {
        if (item instanceof BigNumber num) {
            // Format to current precision
            int precision = BigNumber.getPrecision();
            return num.value().setScale(precision, BigNumber.getRoundingMode()).toPlainString();
        } else if (item instanceof com.finCalc.calculator.Error err) {
            return "ERROR: " + err.message();
        } else {
            return item.toString();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showOperationHelp(Calculation operation, String operationName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(operationName + " Help");
        alert.setHeaderText(operationName + " - " + getOperationDescription(operationName));

        StringBuilder content = new StringBuilder();

        // Stack format
        content.append("Stack Format:\n");
        var descriptors = operation.getOperandDescriptors();
        content.append("[... ");
        for (int i = 0; i < descriptors.size(); i++) {
            content.append(descriptors.get(i).name());
            if (i < descriptors.size() - 1) content.append(" ");
        }
        content.append("] → result\n\n");

        // Operand descriptions
        content.append("Operands (bottom to top of stack):\n");
        for (var desc : descriptors) {
            content.append("  • ").append(desc.name()).append(": ").append(desc.description()).append("\n");
        }

        // Add examples based on operation
        content.append("\n").append(getOperationExample(operationName));

        alert.setContentText(content.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private String getOperationDescription(String operation) {
        return switch (operation) {
            // TVM Operations
            case "PMT" -> "Payment Calculation";
            case "PV" -> "Present Value Calculation";
            case "FV" -> "Future Value Calculation";
            case "RATE" -> "Interest Rate Calculation";
            case "NPER" -> "Number of Periods Calculation";

            // Investment Analysis Operations
            case "CAGR" -> "Compound Annual Growth Rate";
            case "BEP" -> "Break-Even Point";
            case "PAYBACK" -> "Payback Period";
            case "PI" -> "Profitability Index";

            // Real Estate Operations
            case "CAP" -> "Capitalization Rate";
            case "NOI" -> "Net Operating Income";
            case "CoC" -> "Cash-on-Cash Return";
            case "DSCR" -> "Debt Service Coverage Ratio";
            case "LTV" -> "Loan-to-Value Ratio";
            case "GRM" -> "Gross Rent Multiplier";
            case "ROI" -> "Return on Investment";

            default -> "Operation";
        };
    }

    private String getOperationExample(String operation) {
        return switch (operation) {
            // TVM Operations
            case "PMT" -> """
                Example: $200,000 loan at 6% annual (0.5% monthly) for 30 years (360 months)
                  Enter: 200000 ENTER 0.005 ENTER 360 ENTER PMT
                  Result: $1,199.10/month
                """;
            case "PV" -> """
                Example: What's the present value of $1,199.10/month for 360 months at 0.5% monthly?
                  Enter: 1199.10 ENTER 0.005 ENTER 360 ENTER PV
                  Result: $200,000
                """;
            case "FV" -> """
                Example: $10,000 invested at 8% annual for 10 years
                  Enter: 10000 ENTER 0.08 ENTER 10 ENTER FV
                  Result: $21,589.25
                """;
            case "RATE" -> """
                Example: $200,000 loan, $1,199.10 payment, 360 months
                  Enter: 200000 ENTER 1199.10 ENTER 360 ENTER RATE
                  Result: 0.005 (0.5% monthly = 6% annual)
                """;
            case "NPER" -> """
                Example: $200,000 loan, $1,199.10 payment, 0.5% monthly rate
                  Enter: 200000 ENTER 1199.10 ENTER 0.005 ENTER NPER
                  Result: 360 months (30 years)
                """;

            // Investment Analysis Operations
            case "CAGR" -> """
                Example: $10,000 investment grows to $15,000 in 5 years
                  Enter: 10000 ENTER 15000 ENTER 5 ENTER CAGR
                  Result: 0.0845 (8.45% average annual growth)
                """;
            case "BEP" -> """
                Example: $50,000 fixed costs, $100 price, $60 variable cost per unit
                  Enter: 50000 ENTER 100 ENTER 60 ENTER BEP
                  Result: 1250 (need to sell 1,250 units to break even)
                """;
            case "PAYBACK" -> """
                Example: $100,000 investment, $25,000 annual cash flow
                  Enter: 100000 ENTER 25000 ENTER PAYBACK
                  Result: 4.0 (4 years to recover investment)
                """;
            case "PI" -> """
                Example: $100,000 investment, $120,000 PV of future cash flows
                  Enter: 100000 ENTER 120000 ENTER PI
                  Result: 1.20 (PI > 1.0, accept project)
                """;

            // Real Estate Operations
            case "CAP" -> """
                Example: $200,000 property with $15,000 annual NOI
                  Enter: 200000 ENTER 15000 ENTER CAP
                  Result: 0.075 (7.5% cap rate)
                """;
            case "NOI" -> """
                Example: $30,000 gross income, $12,000 operating expenses
                  Enter: 30000 ENTER 12000 ENTER NOI
                  Result: $18,000 (Net Operating Income)
                """;
            case "CoC" -> """
                Example: $50,000 invested, $4,000 annual cash flow
                  Enter: 50000 ENTER 4000 ENTER CoC
                  Result: 0.08 (8% cash-on-cash return)
                """;
            case "DSCR" -> """
                Example: $18,000 annual debt service, $22,000 NOI
                  Enter: 18000 ENTER 22000 ENTER DSCR
                  Result: 1.222 (DSCR of 1.22)
                """;
            case "LTV" -> """
                Example: $200,000 property, $160,000 loan
                  Enter: 200000 ENTER 160000 ENTER LTV
                  Result: 0.80 (80% loan-to-value)
                """;
            case "GRM" -> """
                Example: $24,000 annual rent, $200,000 property price
                  Enter: 24000 ENTER 200000 ENTER GRM
                  Result: 8.333 (GRM of 8.33)
                """;
            case "ROI" -> """
                Example: Bought for $200,000, sold for $250,000
                  Enter: 200000 ENTER 250000 ENTER ROI
                  Result: 0.25 (25% return on investment)
                """;

            default -> "No example available.";
        };
    }

    private void showMultiFunctionHelp(ButtonSpec spec) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Multi-Function Button Help");
        alert.setHeaderText("Functions on this button:");

        StringBuilder content = new StringBuilder();

        // Normal function
        content.append("━━━ NORMAL ━━━\n");
        content.append(getFunctionHelpText(spec.label));
        content.append("\n\n");

        // SHIFT function
        if (spec.shiftLabel != null && !spec.shiftLabel.isEmpty()) {
            content.append("━━━ SHIFT (Orange) ━━━\n");
            content.append(getFunctionHelpText(spec.shiftLabel));
            content.append("\n\n");
        }

        // CTRL function
        if (spec.ctrlLabel != null && !spec.ctrlLabel.isEmpty()) {
            content.append("━━━ CTRL (Blue) ━━━\n");
            content.append(getFunctionHelpText(spec.ctrlLabel));
        }

        alert.setContentText(content.toString());
        alert.getDialogPane().setPrefWidth(600);
        alert.getDialogPane().setPrefHeight(500);
        alert.showAndWait();
    }

    private String getFunctionHelpText(String functionLabel) {
        // Map function labels to their Calculation instances and get help
        Calculation operation = getOperationInstance(functionLabel);

        if (operation != null) {
            StringBuilder help = new StringBuilder();
            String description = operation.getDescription();
            if (description != null && !description.isEmpty()) {
                help.append("Function: ").append(description).append("\n\n");
            } else {
                help.append("Function: ").append(functionLabel).append("\n\n");
            }

            // Stack format
            var descriptors = operation.getOperandDescriptors();
            help.append("Stack: [");
            for (int i = 0; i < descriptors.size(); i++) {
                help.append(descriptors.get(i).name());
                if (i < descriptors.size() - 1) help.append(" ");
            }
            help.append("] → result\n\n");

            // Operands
            help.append("Operands:\n");
            for (var desc : descriptors) {
                help.append("  • ").append(desc.name()).append(": ").append(desc.description()).append("\n");
            }

            // Example
            String example = operation.getExample();
            if (example != null && !example.isEmpty()) {
                help.append("\n").append(example);
            }

            return help.toString();
        } else {
            // For non-operation buttons (numbers, etc.)
            return functionLabel + " - " + getSimpleFunctionDescription(functionLabel);
        }
    }

    private Calculation getOperationInstance(String functionLabel) {
        return OperationRegistry.getOperation(functionLabel);
    }

    private String getSimpleFunctionDescription(String function) {
        return switch (function) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> "Digit entry";
            case "." -> "Decimal point";
            case "±" -> "Toggle sign";
            case "+" -> "Addition";
            case "−" -> "Subtraction";
            case "×" -> "Multiplication";
            case "÷" -> "Division";
            case "DROP" -> "Remove top stack item";
            case "SWAP" -> "Swap top two stack items";
            case "CLEAR" -> "Clear entire stack";
            case "NPV" -> "Net Present Value (coming soon)";
            case "IRR" -> "Internal Rate of Return (coming soon)";
            case "APR" -> "Annual Percentage Rate (coming soon)";
            default -> "Calculator function";
        };
    }

    private void showOperationWizard(Calculation operation, String symbol, String operationName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(operationName);
        dialog.setHeaderText(operation.getDescription() + "\n\n" + operation.getExample());

        // Create input fields for each operand
        var descriptors = operation.getOperandDescriptors();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField[] inputFields = new TextField[descriptors.size()];

        for (int i = 0; i < descriptors.size(); i++) {
            var desc = descriptors.get(i);
            Label label = new Label(desc.name() + ":");
            label.setTooltip(new Tooltip(desc.description()));

            TextField textField = new TextField();
            textField.setPromptText(desc.description());
            inputFields[i] = textField;

            grid.add(label, 0, i);
            grid.add(textField, 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Focus on first field
        javafx.application.Platform.runLater(() -> inputFields[0].requestFocus());

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Push all values onto the stack in order
                    for (TextField field : inputFields) {
                        String value = field.getText().trim();
                        if (value.isEmpty()) {
                            showError("All fields must be filled in");
                            return;
                        }
                        BigNumber number = BigNumber.of(value);
                        calculatorStack.push(number);
                    }

                    // Perform the operation
                    operation.execute(calculatorStack);

                    // Add to RPN expression
                    if (rpnExpression.length() > 0) {
                        rpnExpression.append(" ");
                    }
                    // Add all inputs and operation to expression
                    for (TextField field : inputFields) {
                        rpnExpression.append(field.getText().trim()).append(" ");
                    }
                    rpnExpression.append(symbol);

                    updateDisplay();
                } catch (NumberFormatException e) {
                    showError("Invalid number format: " + e.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class ButtonSpec {
        final String label;           // Primary function (normal press)
        final String shiftLabel;      // SHIFT + button
        final String ctrlLabel;       // CTRL + button
        final String color;
        final String tooltip;

        // Constructor for simple buttons (no modifiers)
        ButtonSpec(String label, String color) {
            this(label, null, null, color, null);
        }

        // Constructor with tooltip
        ButtonSpec(String label, String color, String tooltip) {
            this(label, null, null, color, tooltip);
        }

        // Full constructor with all three functions
        ButtonSpec(String label, String shiftLabel, String ctrlLabel, String color) {
            this(label, shiftLabel, ctrlLabel, color, null);
        }

        // Complete constructor
        ButtonSpec(String label, String shiftLabel, String ctrlLabel, String color, String tooltip) {
            this.label = label;
            this.shiftLabel = shiftLabel;
            this.ctrlLabel = ctrlLabel;
            this.color = color;
            this.tooltip = tooltip;
        }
    }
}
