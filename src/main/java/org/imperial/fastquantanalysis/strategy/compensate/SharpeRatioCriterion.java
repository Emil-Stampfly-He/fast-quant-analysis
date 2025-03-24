package org.imperial.fastquantanalysis.strategy.compensate;

import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

public class SharpeRatioCriterion implements AnalysisCriterion {

    private final static Double DEFAULT_RISK_FREE_RATE = 2.0;

    // Not supported
    @Override
    public Num calculate(BarSeries barSeries, Position position) {
        return DoubleNum.valueOf(0);
    }

    @Override
    public Num calculate(BarSeries barSeries, TradingRecord tradingRecord) {
        return this.calculate(barSeries, tradingRecord, DEFAULT_RISK_FREE_RATE);
    }

    public Num calculate(BarSeries barSeries, TradingRecord tradingRecord, double riskFreeRate) {
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < barSeries.getBarCount(); i++) {
            double prevClose = barSeries.getBar(i - 1).getClosePrice().doubleValue();
            double currClose = barSeries.getBar(i).getClosePrice().doubleValue();
            double dailyReturn = (currClose - prevClose) / prevClose;
            returns.add(dailyReturn);
        }

        double avgReturn = getAverage(returns);
        double variance = returns.stream()
                .reduce(0.0, (acc, r) ->
                        acc + Math.pow(r - avgReturn, 2.0) / returns.size());
        double stdDev = Math.sqrt(variance);

        if (stdDev == 0.0) {
            return DoubleNum.valueOf(0.0);
        }

        double sharpe = (avgReturn - riskFreeRate) / stdDev;
        return DoubleNum.valueOf(sharpe);
    }

    @Override
    public boolean betterThan(Num criterionValue1, Num criterionValue2) {
        return criterionValue1.isGreaterThan(criterionValue2);
    }

    private static double getAverage(List<Double> list) {
        double sum = 0.0;
        for (Double d : list) {
            sum += d;
        }
        return sum / list.size();
    }
}
