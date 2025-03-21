package org.imperial.fastquantanalysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.springframework.http.ResponseEntity;

/**
 * Quant analysis service Interface
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
public interface IQuantAnalysisCryptoService extends IService<QuantStrategy> {

    ResponseEntity<?> donchian(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO, Integer windowSize);
}
