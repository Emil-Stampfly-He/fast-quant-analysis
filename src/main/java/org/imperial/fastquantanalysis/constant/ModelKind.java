package org.imperial.fastquantanalysis.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * All kinds of supported models
 *
 * @author Emil S. He
 * @since 2025-04-03
 */
public enum ModelKind {

    LSTM_RNN("lstm-rnn"),
    LSTM_DENSE_RNN("lstm-dense-rnn"),
    CNN_RNN_HYBRID("cnn-rnn-hybrid");

    private final String value;

    ModelKind(String value) {this.value = value;}

    @JsonValue
    public String getValue() {return value;}
}
