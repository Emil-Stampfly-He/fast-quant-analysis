package org.imperial.fastquantanalysis.strategy;

import lombok.NoArgsConstructor;
import org.imperial.fastquantanalysis.entity.QuantStrategy;

@NoArgsConstructor
public class UserStrategy {

    public QuantStrategy runStrategy(Object... args) {
        return new QuantStrategy();
    }
}
