package org.imperial.fastquantanalysis.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.service.impl.QuantAnalysisServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quant analysis controller
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@RestController
@RequestMapping("/quant/analysis")
public class QuantAnalysisController {

    @Resource
    private QuantAnalysisServiceImpl quantAnalysisService;


}