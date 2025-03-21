package org.imperial.fastquantanalysis.constant;

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

    private String getValue() {
        return value;
    }
}
