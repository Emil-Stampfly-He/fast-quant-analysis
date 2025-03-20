package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.service.impl.QuantAnalysisCryptoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    private QuantAnalysisCryptoServiceImpl quantAnalysisCryptoService;

    /**
     * Donchian channel for crypto prices
     * @return OK or fail message
     * @postmantest untested
     * TODO Unfinished
     */
    @GetMapping("/donchian")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Donchian channel strategy for crypto prices")
    public ResponseEntity<String> donchian() {
        return quantAnalysisCryptoService.donchian();
    }

}