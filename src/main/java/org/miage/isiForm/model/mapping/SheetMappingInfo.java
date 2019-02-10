package org.miage.isiForm.model.mapping;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class SheetMappingInfo {
    @JsonBackReference
    private MappingInfo file;
    @JsonManagedReference
    @JsonProperty("columns")
    private Map<String, ColumnMappingInfo> columns;

    public String getName() {
        return file.getSheetName(this);
    }

    String getColumnIndex(ColumnMappingInfo column) {
        for(Map.Entry<String, ColumnMappingInfo> entry : columns.entrySet()) {
            if(entry.getValue() == column) {
                return entry.getKey();
            }
        }
        return "";
    }

    public List<ColumnMappingInfo> getColumns() {
        return new ArrayList<>(columns.values());
    }

    public ColumnMappingInfo getColumn(String column) {
        return columns.get(column);
    }

    public String getFirstIndex() {
        List<String> cols = getOrderedCols();
        return cols.size() > 0 ? cols.get(0) : null;
    }

    public String getLastIndex() {
        List<String> cols = getOrderedCols();
        return cols.size() > 0 ? cols.get(cols.size() - 1) : null;
    }

    private List<String> getOrderedCols() {
        List<String> cols = new ArrayList<>(columns.keySet());
        cols.sort((o1, o2) -> {
            if(o1.length() != o2.length())
                return o2.length() - o1.length();
            return o1.compareToIgnoreCase(o2);
        });
        return cols;
    }
}
