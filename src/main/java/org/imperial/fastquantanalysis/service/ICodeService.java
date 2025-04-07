package org.imperial.fastquantanalysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.springframework.http.ResponseEntity;

/**
 * Code service interface
 *
 * @author Emil S. He
 * @since 2025-04-08
 */
public interface ICodeService extends IService<QuantStrategy> {

    ResponseEntity<?> runJavaCode(String code);

    ResponseEntity<?> runKotlinCode(String code);
}
