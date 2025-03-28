package org.imperial.fastquantanalysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesDTO;
import org.imperial.fastquantanalysis.dto.CryptoAggregatesPairDTO;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

/**
 * Quant analysis service Interface
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
public interface IQuantAnalysisCryptoService extends IService<QuantStrategy> {

    ResponseEntity<?> donchian(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO, Integer windowSize);

    CompletableFuture<ResponseEntity<?>> pairTrading(String polygonApiKey, CryptoAggregatesPairDTO cryptoAggregatesPairDTO, Integer windowSize, Double zScoreThreshold, Integer x);

    ResponseEntity<?> EMAWithStopLossPercentage(String polygonApiKey, CryptoAggregatesDTO cryptoAggregatesDTO, Integer emaPeriod, Double stopLossPercentage);
}
