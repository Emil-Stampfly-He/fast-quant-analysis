package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesPairDTO;
import org.imperial.fastquantanalysis.service.IQuantAnalysisCryptoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Quant analysis crypto controller
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@RestController
@RequestMapping("/quant/analysis/crypto")
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
            @RequestParam("polygon_api_key") String polygonApiKey,
            @RequestBody CryptoAggregatesDTO cryptoAggregatesDTO,
            @RequestParam Integer windowSize) {
        return quantAnalysisCryptoService.donchian(polygonApiKey, cryptoAggregatesDTO, windowSize);
    }

    /**
     * Pair trading for 2 cryptos
     * @param polygonApiKey User's polygon API key
     * @param cryptoAggregatesPairDTO DTO for carrying necessary information
     * @param windowSize Window size
     * @return OK or fail message
     * @postmantest untested
     * TODO Unfinished
     */
    @PostMapping("/pair/trading")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Pair trading stategy for crypto prices")
    public ResponseEntity<?> pairTrading(
            @RequestParam("polygon_api_key") String polygonApiKey,
            @RequestBody CryptoAggregatesPairDTO cryptoAggregatesPairDTO,
            @RequestParam Integer windowSize) {
        return quantAnalysisCryptoService.pairTrading(polygonApiKey, cryptoAggregatesPairDTO, windowSize);
    }

}