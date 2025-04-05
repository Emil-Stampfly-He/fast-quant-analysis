package org.imperial.fastquantanalysis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResultVO {

    private List<Double> predictions;
    private double mse;
}
