package org.imperial.fastquantanalysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.imperial.fastquantanalysis.constant.Sort;
import org.imperial.fastquantanalysis.constant.Timespan;

import java.time.LocalDate;

/**
 * All parameters required to get two crypto data
 *
 * @author Emil S. He
 * @since 2025-03-23
 */
@Data
public class CryptoAggregatesPairDTO {

    @Schema(description = "ticker name 1")
    @NotBlank
    private String tickerName1;

    @Schema(description = "ticker name 2")
    @NotBlank
    private String tickerName2;

    @Schema(description = "the size of the timespan multiplier")
    private Long multiplier; // default value: 1

    @Schema(description = "timespan")
    @NotBlank
    private Timespan timespan;

    @Schema(description = "the starting date of strategy")
    @NotBlank
    private LocalDate fromDate;

    @Schema(description = "the ending date of strategy")
    @NotBlank
    private LocalDate toDate;

    @Schema(description = "whether or not the results are adjusted for splits")
    private Boolean unadjusted; // default value: false

    @Schema(description = "the number of base aggregates queried to create the result")
    private Long limit; // default value: 50000

    @Schema(description = "asc will return results in ascending order (oldest at the top), " +
            "desc will return results in descending order (newest at the top)")
    @NotBlank
    private Sort sort;

}
