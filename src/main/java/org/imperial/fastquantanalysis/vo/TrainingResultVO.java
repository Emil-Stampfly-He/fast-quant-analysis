package org.imperial.fastquantanalysis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Training results
 *
 * @author Emil S. He
 * @since 2025-04-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResultVO {

    private List<Double> predictions;
    private double mse;
}
