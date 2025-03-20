package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.mapper.QuantAnalysisMapper;
import org.imperial.fastquantanalysis.service.IQuantAnalysisService;
import org.springframework.stereotype.Service;

/**
 * Quant analysis service implementation class
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
@Slf4j
@Service
public class QuantAnalysisServiceImpl extends ServiceImpl<QuantAnalysisMapper, QuantStrategy> implements IQuantAnalysisService {
}
