package com.finCalc.calculator;

import com.finCalc.calculator.operations.basic.*;
import com.finCalc.calculator.operations.scientific.*;
import com.finCalc.calculator.operations.financial.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all calculator operations.
 *
 * <p>Maps operation symbols (e.g., "PMT", "+", "√") to their Calculation instances.
 * This eliminates the need for switch statements throughout the codebase.
 *
 * <p>Operations are registered automatically at class initialization time.
 */
public class OperationRegistry {
    private static final Map<String, Calculation> operations = new HashMap<>();

    static {
        // Register all operations by calling their getSymbol() method
        registerOperation(Add.INSTANCE);
        registerOperation(Subtract.INSTANCE);
        registerOperation(Multiply.INSTANCE);
        registerOperation(Divide.INSTANCE);

        // Scientific operations
        registerOperation(SquareRoot.INSTANCE);
        registerOperation(Square.INSTANCE);
        registerOperation(Power.INSTANCE);
        registerOperation(Reciprocal.INSTANCE);
        registerOperation(NthRoot.INSTANCE);
        registerOperation(Log10.INSTANCE);
        registerOperation(NaturalLog.INSTANCE);
        registerOperation(Exponential.INSTANCE);
        registerOperation(Exp10.INSTANCE);
        registerOperation(AbsoluteValue.INSTANCE);
        registerOperation(Sine.INSTANCE);
        registerOperation(Cosine.INSTANCE);
        registerOperation(Tangent.INSTANCE);
        registerOperation(ArcSine.INSTANCE);
        registerOperation(ArcCosine.INSTANCE);
        registerOperation(ArcTangent.INSTANCE);
        registerOperation(Modulo.INSTANCE);

        // TVM operations
        registerOperation(Payment.INSTANCE);
        registerOperation(PresentValue.INSTANCE);
        registerOperation(FutureValue.INSTANCE);
        registerOperation(InterestRate.INSTANCE);
        registerOperation(NumberOfPeriods.INSTANCE);

        // Investment Analysis operations
        registerOperation(CompoundAnnualGrowthRate.INSTANCE);
        registerOperation(BreakEvenPoint.INSTANCE);
        registerOperation(PaybackPeriod.INSTANCE);
        registerOperation(ProfitabilityIndex.INSTANCE);

        // Real Estate operations
        registerOperation(CapRate.INSTANCE);
        registerOperation(NetOperatingIncome.INSTANCE);
        registerOperation(CashOnCash.INSTANCE);
        registerOperation(DebtServiceCoverageRatio.INSTANCE);
        registerOperation(LoanToValue.INSTANCE);
        registerOperation(GrossRentMultiplier.INSTANCE);
        registerOperation(ReturnOnInvestment.INSTANCE);
        registerOperation(CashFlowAfterTaxes.INSTANCE);
        registerOperation(OperatingExpenseRatio.INSTANCE);
        registerOperation(VacancyLoss.INSTANCE);
        registerOperation(EffectiveGrossIncome.INSTANCE);
        registerOperation(PricePerSquareFoot.INSTANCE);
        registerOperation(RentPerSquareFoot.INSTANCE);

        // Loan & Mortgage operations
        registerOperation(RemainingBalance.INSTANCE);
        registerOperation(TotalInterestPaid.INSTANCE);
        registerOperation(AprToApy.INSTANCE);
        registerOperation(DebtToIncomeRatio.INSTANCE);

        // Bond operations
        registerOperation(CurrentYield.INSTANCE);
        registerOperation(YieldToMaturity.INSTANCE);
        registerOperation(BondPrice.INSTANCE);

        // Tax & Retirement operations
        registerOperation(EffectiveTaxRate.INSTANCE);
        registerOperation(AfterTaxReturn.INSTANCE);
        registerOperation(RequiredMinimumDistribution.INSTANCE);
    }

    /**
     * Registers an operation in the registry using its symbol.
     *
     * @param operation the operation to register
     */
    private static void registerOperation(Calculation operation) {
        String symbol = operation.getSymbol();
        if (symbol != null && !symbol.isEmpty()) {
            operations.put(symbol, operation);
        }
    }

    /**
     * Retrieves an operation by its symbol.
     *
     * @param symbol the operation symbol (e.g., "PMT", "+", "√")
     * @return the Calculation instance, or null if not found
     */
    public static Calculation getOperation(String symbol) {
        return operations.get(symbol);
    }

    /**
     * Returns all registered operations.
     *
     * @return map of symbols to operations
     */
    public static Map<String, Calculation> getAllOperations() {
        return new HashMap<>(operations);
    }
}
