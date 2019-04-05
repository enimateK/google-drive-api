package org.miage.isiForm.google.sheets;

import java.util.*;

public class Row {
    Sheet sheet;
    private Map<String, Cell> cells = new HashMap<>();

    Row(Sheet sheet, int firstCol, int row, List<Object> cells) {
        this.sheet = sheet;
        int col = firstCol;
        for(Object object : cells) {
            this.cells.put(Util.convertColumn(col), new Cell(this, row, col, object.toString()));
            col++;
        }
    }

    public void update(List<Object> cells) {
        int col = 1;
        for(Cell cell : this.cells.values()) {
            cell.setValue(cells.get(1).toString());
            col++;
        }
    }

    public Collection<Cell> getCells() {
        return Collections.unmodifiableCollection(cells.values());
    }

    public Cell getCell(String key) {
        return cells.getOrDefault(key, null);
    }

    public void save() {
        List<List<Cell>> cellss = new ArrayList<>();
        cellss.add(new ArrayList<>(cells.values()));
        try {
            sheet.workbook.commit(sheet.name, cellss);
        } catch(Exception ignored) { }
    }

    public String getRowNumber() {
        return Integer.toString(cells.values().iterator().next().row);
    }
}
