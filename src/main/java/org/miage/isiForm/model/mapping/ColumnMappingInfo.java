package org.miage.isiForm.model.mapping;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

public class ColumnMappingInfo {
    @JsonBackReference
    private SheetMappingInfo sheet;
    @JsonProperty("id")
    private String id;
    @JsonProperty("label")
    private Map<String, String> labels;
    @JsonProperty("read-only")
    private boolean readOnly;

    public String getIndex() {
        return sheet.getColumnIndex(this);
    }

    public String getId() {
        return id;
    }

    public String getLabel(Language language) {
        if(labels.containsKey(language.value))
            return labels.get(language.value);
        if(labels.size() > 0)
            return labels.values().iterator().next();
        return null;
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }
}
