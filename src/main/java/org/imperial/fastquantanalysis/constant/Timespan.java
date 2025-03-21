package org.imperial.fastquantanalysis.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 8 possible values of param timespan
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
public enum Timespan {

    SECOND("second"),
    MINUTE("minute"),
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year");

    private final String value;

    Timespan(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
