package org.imperial.fastquantanalysis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.*;
import org.ta4j.core.criteria.pnl.ReturnCriterion;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.StopLossRule;

import java.time.ZonedDateTime;

@Slf4j
@SpringBootTest
public class TA4JTest {

    @Test
    void testTA4JExample() {
        BarSeries barSeries = new BaseBarSeriesBuilder().withName("Bar Series").build();

        ZonedDateTime endTime = ZonedDateTime.now();
        barSeries.addBar(endTime, 105.42, 112.99, 104.01, 111.49, 1337);
        barSeries.addBar(endTime.plusDays(1), 111.43, 112.83, 107.77, 107.99, 1234);
        barSeries.addBar(endTime.plusDays(2), 107.90, 117.50, 107.90, 115.42, 4242);
        barSeries.addBar(endTime.plusDays(3), 120.69,159.63,103.26,110.23);

        Num firstClosePrice = barSeries.getBar(0).getClosePrice();
        System.out.println("First close price: " + firstClosePrice.doubleValue());

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        System.out.println(firstClosePrice.isEqual(closePriceIndicator.getValue(0)));

        EMAIndicator shortEMA = new EMAIndicator(closePriceIndicator, 2);
        EMAIndicator longEMA = new EMAIndicator(closePriceIndicator, 3);
        System.out.println(shortEMA.getValue(0).doubleValue());

        // Buying rules
        // - if the 2-bars EMA crosses over 4-bars EMA
        // - if the price goes below a defined price (e.g. 110.00)
        Rule buyingRule = new CrossedUpIndicatorRule(shortEMA, longEMA).or(new CrossedDownIndicatorRule(closePriceIndicator, 110.00));

        // Selling rules
        //  - if the 5-bars SMA crosses under 30-bars SMA
        //  - or if the price loses more than 3%
        //  - or if the price earns more than 2%
        Rule sellingRule = new CrossedDownIndicatorRule(shortEMA, longEMA)
                .or(new StopLossRule(closePriceIndicator, barSeries.numOf(3)))
                .or(new StopGainRule(closePriceIndicator, barSeries.numOf(2)));

        Strategy strategy = new BaseStrategy(buyingRule, sellingRule);

        // Backtest
        BarSeriesManager seriesManager = new BarSeriesManager(barSeries);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        System.out.println("Number of positions (trades) for our strategy: " +
                tradingRecord.getPositionCount());

        // Getting the winning positions ratio
        AnalysisCriterion winningPositionsRatio = new PositionsRatioCriterion(AnalysisCriterion.PositionFilter.PROFIT);
        System.out.println("Winning positions ratio: " + winningPositionsRatio.calculate(barSeries, tradingRecord));

        // Getting a risk-reward ratio
        AnalysisCriterion romad = new ReturnOverMaxDrawdownCriterion();
        System.out.println("Return over Max Drawdown: " + romad.calculate(barSeries, tradingRecord));

        // Total return of our strategy vs total return of a buy-and-hold strategy
        AnalysisCriterion vsBuyAndHold = new VersusEnterAndHoldCriterion(new ReturnCriterion());
        System.out.println("Our return vs buy-and-hold return: " + vsBuyAndHold.calculate(barSeries, tradingRecord));
    }
}
