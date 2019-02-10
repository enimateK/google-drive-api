package org.miage.isiForm.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.miage.isiForm.google.sheets.Cell;
import org.miage.isiForm.google.sheets.Row;
import org.miage.isiForm.google.sheets.Sheet;
import org.miage.isiForm.model.mapping.ColumnMappingInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetInfo {
    @JsonProperty("columns")
    private Map<String, ColumnInfo> columns = new HashMap<>();
    @JsonProperty("rows")
    private List<Map<String, String>> rows = new ArrayList<>();

    SheetInfo(Sheet sheet) {
        for(ColumnMappingInfo column : sheet.getColumns()) {
            columns.put(column.getId(), new ColumnInfo(column));
        }
        for(Row row : sheet.getRows()) {
            Map<String, String> rowInfo = new HashMap<>();
            for(Cell cell : row.getCells()) {
                rowInfo.put(sheet.getColumn(cell.getCol()).getId(), cell.getValue());
            }
            rows.add(rowInfo);
        }
    }
}
