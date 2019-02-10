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

    public Collection<Cell> getCells() {
        return Collections.unmodifiableCollection(cells.values());
    }

    public void save() {
        List<List<Cell>> cellss = new ArrayList<>();
        cellss.add(new ArrayList<>(cells.values()));
        try {
            sheet.workbook.commit(sheet.name, cellss);
        } catch(Exception ignored) { }
    }
}
