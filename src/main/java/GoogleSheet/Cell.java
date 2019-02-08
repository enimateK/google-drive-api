package GoogleSheet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Cell {
    public final String sheet;
    public final int row;
    public final int col;
    private String lastValue;
    private String value;

    private Cell(@Nonnull String sheet, int row, int col, @Nonnull String value) {
        this.sheet     = sheet;
        this.row       = row;
        this.col       = col;
        this.value     = value;
        this.lastValue = value;
    }

    public String getSheet() {
        return sheet;
    }

    public String getCol() {
        return Util.convertColumn(col);
    }

    public String getCell() {
        return Util.convertColumn(col) + ':' + row;
    }

    void updateLastValue() {
        this.lastValue = value;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean hasChanged() {
        return !lastValue.equals(value);
    }

    static List<List<Cell>> get(String sheet, List<List<Object>> objectss, String cellStart) {
        List<List<Cell>> cellss = new ArrayList<>();
        StringBuilder colLetterStart = new StringBuilder();
        StringBuilder rowLetterStart = new StringBuilder();
        for(int i = 0 ; i < cellStart.length() ; i++) {
            char chr = cellStart.charAt(i);
            if(chr >= 'A' && chr <= 'Z')
                colLetterStart.append(chr);
            else if(chr >= '0' && chr <= '9')
                rowLetterStart.append(chr);
            else
                throw new Error();
        }
        int row = Util.convertColumn(colLetterStart.toString());
        int col = rowLetterStart.toString().isEmpty() ? 1 : Integer.parseInt(rowLetterStart.toString());
        int colStart = col;
        for(List<Object> objects : objectss) {
            List<Cell> cells = new ArrayList<>();
            for(Object object : objects) {
                cells.add(new Cell(sheet, row, col, object.toString()));
                col++;
            }
            cellss.add(cells);
            col = colStart;
            row++;
        }
        return cellss;
    }
}
