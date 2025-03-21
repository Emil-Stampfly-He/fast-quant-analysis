package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.mapper.QuantAnalysisCryptoMapper;
import org.imperial.fastquantanalysis.service.IQuantAnalysisCryptoService;
import org.imperial.fastquantanalysis.strategy.CryptoStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Quant analysis service implementation class
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@Service
public class QuantAnalysisCryptoServiceImpl extends ServiceImpl<QuantAnalysisCryptoMapper, QuantStrategy> implements IQuantAnalysisCryptoService {

    @Resource
    private CryptoStrategy cryptoStrategy;

    /**
     * Donchian channel for crypto prices
     * @return OK or fail message
     * @postmantest untested
     * TODO Unfinished
     */
    @Override
    public ResponseEntity<String> donchian(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO) {



        return null;
    }
}
