package org.imperial.fastquantanalysis.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.constant.KafkaConstant;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.service.IQuantAnalysisCryptoService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = KafkaConstant.TOPIC_NAME, groupId = KafkaConstant.GROUP_ID)
public class QuantStrategyConsumer {

    @Resource
    private IQuantAnalysisCryptoService quantAnalysisCryptoService;

    @KafkaHandler
    public void handleQuantStrategy(QuantStrategy quantStrategy) {
        quantAnalysisCryptoService.save(quantStrategy);
    }
}
