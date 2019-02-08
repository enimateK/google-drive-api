package GoogleSheet;

import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleSheetsFile extends GoogleSheetsService {

    // Adresse mail à autoriser par la sheet : processdev@process-dev-gdrive.iam.gserviceaccount.com

    private static final String META_DATA_SHEET_NAME  = "Meta-Donnees";
    private Metadata metadata;

    private String mFileId;

    public GoogleSheetsFile(String fileId) {
        mFileId = fileId;
    }

    public Metadata getMetadata() {
        if(metadata == null) {
            try {
                metadata = new Metadata(getCells(META_DATA_SHEET_NAME, "A1", "Z2"));
            } catch (GeneralSecurityException | IOException e) {
                return null;
            }
        }
        return metadata;
    }

    public List<List<Cell>> getCells(String sheet, String cellStart, String cellEnd) throws GeneralSecurityException, IOException {
        final String range = sheet + "!" + cellStart + ":" + cellEnd;
        ValueRange response = getService(false)
                .get(mFileId, range)
                .execute();
        return Cell.get(sheet, response.getValues(), cellStart);
    }

    private void writeInCell(String sheet, String cell, String value) throws GeneralSecurityException, IOException {
        final String range = sheet + "!" + cell;
        final ValueRange valueRange = new ValueRange();
        valueRange.setValues(Collections.singletonList(Collections.singletonList(value)));
        getService(true)
                .update(mFileId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();
    }

    private void writeInCell(String sheet, Map<String, String> ranges_values) throws GeneralSecurityException, IOException {
        List<ValueRange> data = new ArrayList<>();
        //for(int i = 0 ; i < ranges_values.size() ; i++) {
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
        getService(true)
                .batchUpdate(sheet, body).execute();
    }

    private void commit(String sheet, List<List<Cell>> cellss) throws GeneralSecurityException, IOException {
        Map<String, String> ranges_cells = new HashMap<>();
        for(List<Cell> cells : cellss) {
            for(Cell cell : cells) {
                if(cell.sheet.equalsIgnoreCase(sheet) && cell.hasChanged()) {
                    String col = cell.getCol();
                    String range = col + cell.row + ':' + col + cell.row;
                    ranges_cells.put(range, cell.getValue());
                    cell.updateLastValue();
                }
            }
        }
        writeInCell(sheet, ranges_cells);
    }
}
