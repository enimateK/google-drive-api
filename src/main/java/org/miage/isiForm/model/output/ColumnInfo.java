package org.miage.isiForm.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.miage.isiForm.model.mapping.ColumnMappingInfo;

import java.util.HashMap;
import java.util.Map;

public class ColumnInfo {
    @JsonProperty("label")
    private final Map<String, String> labels;

    ColumnInfo(ColumnMappingInfo column) {
        this.labels = new HashMap<>(column.getLabels());
    }
}
