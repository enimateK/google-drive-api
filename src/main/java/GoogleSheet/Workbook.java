package GoogleSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbook extends GoogleSheetsFile {
    private Map<String, Sheet> sheets = new HashMap<>();

    Workbook(String fileId) {
        super(fileId);
    }

    private void addSheet(Sheet sheet) {
        sheets.put(sheet.name, sheet);
    }

    public Sheet getSheet(String name) {
        if(!sheets.containsKey(name)) {
            // TODO : Query sheet
        }

        if(!sheets.containsKey(name))
            return null;
        return sheets.get(name);
    }
}
