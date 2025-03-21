package org.imperial.fastquantanalysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;

import java.time.LocalDateTime;

@Data
public class CryptoAggregatesDTO {

    @Schema(description = "ticker name")
    private String tickerName;

    @Schema(description = "timespan")
    private Timespan timespan;

    @Schema(description = "the starting date of strategy")
    private LocalDateTime fromDate;

    @Schema(description = "the ending date of strategy")
    private LocalDateTime toDate;

    @Schema(description = "asc will return results in ascending order (oldest at the top), " +
            "desc will return results in descending order (newest at the top)")
    private Sort sort;

    @Schema(description = "the number of base aggregates queried to create the result")
    private Integer limit;

}
