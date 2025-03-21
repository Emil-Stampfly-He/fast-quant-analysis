package org.imperial.fastquantanalysis.constant;

public enum Sort {

    ASC("asc"),
    DESC("desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    private String getValue() {
        return value;
    }
}
