package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesPairDTO;
import org.imperial.fastquantanalysis.service.IQuantAnalysisCryptoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Quant analysis crypto controller
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@RestController
@RequestMapping("/quant/analysis/crypto")
@Tag(name = "Quant Analysis Interface")
public class QuantAnalysisCryptoController {

    @Resource
    private IQuantAnalysisCryptoService quantAnalysisCryptoService;

    /**
     * Donchian channel for crypto prices
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param windowSize Window size
     * @return OK or fail message
     * @postmantest passed
     */
    @PostMapping("/donchian")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Donchian channel strategy for crypto prices")
    public ResponseEntity<?> donchian(
            @Parameter(name = "User's Polygon.io API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @Parameter(name = "Window size") @RequestParam Integer windowSize) {
        return quantAnalysisCryptoService.donchian(polygonApiKey, cryptoAggregatesDTO, windowSize);
    }

    /**
     * Pair trading for 2 cryptos
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesPairDTO DTO for carrying necessary information, but in pairs
     * @param windowSize Window size
     * @param zScoreThreshold Threshold of z-score
     * @param x Previous x days
     * @return OK or fail message
     * @postmantest passed
     */
    @PostMapping("/pair/trading")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Pair trading strategy for crypto prices")
    public CompletableFuture<ResponseEntity<?>> pairTrading(
            @Parameter(name = "User's Polygon.io API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesPairDTO cryptoAggregatesPairDTO,
            @Parameter(name = "Window size") @RequestParam Integer windowSize,
            @Parameter(name = "Threshold of z-score") @RequestParam Double zScoreThreshold,
            @Parameter(name = "Previous x days") @RequestParam Integer x) {
        return quantAnalysisCryptoService.pairTrading(polygonApiKey, cryptoAggregatesPairDTO, windowSize, zScoreThreshold, x);
    }

    /**
     * EMA with fixed percentage stop loss
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesDTO DTO for carrying necessary information
     * @param emaPeriod EMA window size
     * @param stopLossPercentage Fixed percentage of stop loss
     * @return OK or fail message
     * @postmantest untested
     */
    @PostMapping("ema/stop/loss/percentage")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "EMA with fixed percentage stop loss strategy for crypto prices")
    public ResponseEntity<?> EMAWithStopLossPercentage(
            @Parameter(name = "User's Polygon.io API key") @RequestParam("polygon_api_key") String polygonApiKey,
            @Parameter(name = "DTO for carrying necessary information") @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @Parameter(name = "Window size, as know as EMA period") @RequestParam Integer emaPeriod,
            @Parameter(name = "Fixed percentage of stop loss") @RequestParam Double stopLossPercentage) {
        return quantAnalysisCryptoService.EMAWithStopLossPercentage(polygonApiKey, cryptoAggregatesDTO, emaPeriod, stopLossPercentage);
    }


}