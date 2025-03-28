package org.imperial.fastquantanalysis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

@Slf4j
@SpringBootTest
public class YahooFinanceTest {

    @Test
    void testGetASingleStockExample() {
        try {
            Calendar fromDate = Calendar.getInstance();
            Calendar toDate = Calendar.getInstance();
            fromDate.add(Calendar.DATE, -1); // 5 years
            Stock stock = YahooFinance.get("INTC", fromDate, toDate);

            BigDecimal price = stock.getQuote().getPrice();
            BigDecimal changeInAbsolute = stock.getQuote().getChange();
            BigDecimal changeInPercentage = stock.getQuote().getChangeInPercent();
            BigDecimal peg = stock.getStats().getPeg();
            BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();

            System.out.println(stock);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
