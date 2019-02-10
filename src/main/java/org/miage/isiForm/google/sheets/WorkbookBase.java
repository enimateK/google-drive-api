package org.miage.isiForm.google.sheets;

import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.miage.isiForm.google.GoogleAuthentificationService;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

abstract class WorkbookBase extends GoogleAuthentificationService {

    public final String fileId;

    protected WorkbookBase(String fileId) {
        this.fileId = fileId;
    }

    protected List<List<Object>> getCells(String sheet, String cellStart, String cellEnd) throws GeneralSecurityException, IOException {
        final String range = sheet + "!" + cellStart + ":" + cellEnd;
        ValueRange response = getSheetsService(false)
                .get(fileId, range)
                .execute();
        return response.getValues();
    }

    public void writeInCells(String sheet, String cell, String value) throws GeneralSecurityException, IOException {
        final String range = sheet + "!" + cell;
        final ValueRange valueRange = new ValueRange();
        valueRange.setValues(Collections.singletonList(Collections.singletonList(value)));
        getSheetsService(true)
                .update(fileId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();
    }

    protected void writeInCells(String sheet, Map<String, String> ranges_values) throws GeneralSecurityException, IOException {
        List<ValueRange> data = new ArrayList<>();
        for(Map.Entry<String, String> entry : ranges_values.entrySet()) {
            List<List<Object>> value = Collections.singletonList(
                    Collections.singletonList(
                            entry.getValue()
                    )
            );
            data.add(new ValueRange()
                    .setRange(entry.getKey())
                    .setValues(value));
        }

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption("RAW")
                .setData(data);
        getSheetsService(true)
                .batchUpdate(sheet, body).execute();
    }

    protected void commit(String sheet, List<List<Cell>> cellss) throws GeneralSecurityException, IOException {
        Map<String, String> ranges_cells = new HashMap<>();
        for(List<Cell> cells : cellss) {
            for(Cell cell : cells) {
                if(cell.hasChanged()) {
                    String col = cell.getCol();
                    String range = col + cell.row + ':' + col + cell.row;
                    ranges_cells.put(range, cell.getValue());
                    cell.updateLastValue();
                }
            }
        }
        writeInCells(sheet, ranges_cells);
    }
}
