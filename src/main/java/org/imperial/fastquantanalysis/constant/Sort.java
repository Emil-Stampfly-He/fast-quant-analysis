package org.imperial.fastquantanalysis.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 2 possible values of param sort
 *
 * @author Emil S. He
 * @since 2025-03-20
 */
public enum Sort {

    ASC("asc"),
    DESC("desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
