package org.miage.isiForm.google.sheets;

import java.util.ArrayList;
import java.util.List;

class Util {
    static int convertColumn(String col) {
        col = col.toUpperCase();
        int sum = 0;
        for(int i = 0 ; i < col.length() ; i++) {
            sum *= 26;
            sum += (col.charAt(i) - 'A' + 1);
        }
        return sum;
    }

    static String convertColumn(int col) {
        StringBuilder sb = new StringBuilder();
        while (col-- > 0) {
            sb.append((char)('A' + (col % 26)));
            col /= 26;
        }
        return sb.reverse().toString();
    }

    static RowCol getRowCol(String cell) {
        List<List<Cell>> cellss = new ArrayList<>();
        StringBuilder colLetterStart = new StringBuilder();
        StringBuilder rowLetterStart = new StringBuilder();
        for(int i = 0 ; i < cell.length() ; i++) {
            char chr = cell.charAt(i);
            if(chr >= 'A' && chr <= 'Z')
                colLetterStart.append(chr);
            else if(chr >= '0' && chr <= '9')
                rowLetterStart.append(chr);
            else
                throw new Error();
        }
        RowCol rowCol = new RowCol();
        rowCol.col = Util.convertColumn(colLetterStart.toString());
        rowCol.row = rowLetterStart.toString().isEmpty() ? 1 : Integer.parseInt(rowLetterStart.toString());
        return rowCol;
    }

    static class RowCol {
        int row;
        int col;
    }
}
