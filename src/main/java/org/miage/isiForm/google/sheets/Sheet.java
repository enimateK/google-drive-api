package org.miage.isiForm.google.sheets;

import org.miage.isiForm.model.mapping.ColumnMappingInfo;

import java.util.*;

public class Sheet {
    final Workbook workbook;
    public final String id;
    public final String name;
    private Map<String, ColumnMappingInfo> columns = new HashMap<>();
    private Map<String, Row> rows = new HashMap<>();

    Sheet(Workbook workbook, String id, String name, List<ColumnMappingInfo> columnInfos, String firstCell, List<List<Object>> cellss) {
        this.workbook = workbook;
        this.id   = id;
        this.name = name;
        for(ColumnMappingInfo columnInfo : columnInfos) {
            columns.put(columnInfo.getIndex(), columnInfo);
        }
        Util.RowCol rowCol = Util.getRowCol(firstCell);
        boolean first = true; // Pour éviter la ligne des labels
        for(List<Object> cells : cellss) {
            if(!first)
                rows.put(Integer.toString(rowCol.row), new Row(this, rowCol.col, rowCol.row, cells));
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
                    rows.get(Integer.toString(rowCol.row)).update(cells);
                else
                    rows.put(Integer.toString(rowCol.row), new Row(this, rowCol.col, rowCol.row, cells));
            }
            rowCol.row++;
            first = false;
        }
    }

    public void update(Map<String, Map<String, String>> rowsInfo) {
        for(Map.Entry<String, Map<String, String>> row : rowsInfo.entrySet()) {
            rows.get(row.getKey());
        }
    }

    public ColumnMappingInfo getColumn(String index) {
        return columns.getOrDefault(index, null);
    }

    public String getColumnIndex(String id) {
        for(ColumnMappingInfo column : getColumns()) {
            if(column.getId().equals(id))
                return column.getIndex();
        }
        return "";
    }

    public Collection<ColumnMappingInfo> getColumns() {
        return columns.values();
    }

    public Collection<Row> getRows() {
        return Collections.unmodifiableCollection(rows.values());
    }

    public Row getRow(String key) {
        return rows.getOrDefault(key, null);
    }

    public void save() {
        List<List<Cell>> cellss = new ArrayList<>();
        for(Row row : rows.values()) {
            cellss.add(new ArrayList<>(row.getCells()));
        }
        try {
            workbook.commit(name, cellss);
        } catch(Throwable ignored) { }
    }
}
