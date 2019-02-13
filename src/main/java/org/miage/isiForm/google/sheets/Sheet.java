package org.miage.isiForm.google.sheets;

import org.miage.isiForm.model.mapping.ColumnMappingInfo;

import java.util.*;

public class Sheet {
    final Workbook workbook;
    public final String name;
    private Map<String, ColumnMappingInfo> columns = new HashMap<>();
    private List<Row> rows = new ArrayList<>();

    Sheet(Workbook workbook, String name, List<ColumnMappingInfo> columnInfos, String firstCell, List<List<Object>> cellss) {
        this.workbook = workbook;
        this.name = name;
        for(ColumnMappingInfo columnInfo : columnInfos) {
            columns.put(columnInfo.getIndex(), columnInfo);
        }
        Util.RowCol rowCol = Util.getRowCol(firstCell);
        boolean first = true; // Pour éviter la ligne des labels
        for(List<Object> cells : cellss) {
            if(!first)
                rows.add(new Row(this, rowCol.col, rowCol.row, cells));
            rowCol.row++;
            first = false;
        }
    }

    public void update(String firstCell, List<List<Object>> cellss) {
        boolean first = true; // Pour éviter la ligne des labels
        Util.RowCol rowCol = Util.getRowCol(firstCell);
        int rowsSize  = rows.size();
        for(List<Object> cells : cellss) {
            if(!first) {
                if(rowsSize >= rowCol.row - 1)
                    rows.get(rowCol.row).update(cells);
                else
                    rows.add(new Row(this, rowCol.col, rowCol.row, cells));
            }
            rowCol.row++;
            first = false;
        }
    }

    public ColumnMappingInfo getColumn(String index) {
        return columns.getOrDefault(index, null);
    }

    public Collection<ColumnMappingInfo> getColumns() {
        return columns.values();
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public void save() {
        List<List<Cell>> cellss = new ArrayList<>();
        for(Row row : rows) {
            cellss.add(new ArrayList<>(row.getCells()));
        }
        try {
            workbook.commit(name, cellss);
        } catch(Throwable ignored) { }
    }
}
