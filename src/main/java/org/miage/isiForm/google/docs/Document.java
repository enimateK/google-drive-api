package org.miage.isiForm.google.docs;

import com.aspose.words.*;
import com.aspose.words.Table;
import javafx.util.Pair;
import org.miage.isiForm.google.GoogleAuthentificationService;
import org.miage.isiForm.google.sheets.Sheet;
import org.miage.isiForm.google.sheets.Workbook;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Document extends GoogleAuthentificationService {

    private static int currentId = 0;
    private static int getNextId() {
        if(currentId == Integer.MAX_VALUE)
            currentId = 0;
        else
            currentId++;
        return currentId;
    }

    public final String modelFileId;
    private final int uniqueId;
    private final com.aspose.words.Document doc;
    private final Workbook workbook;

    private String getTempWordPath() {
        return "output/temp_" + this.uniqueId + ".docx";
    }

    private String getTempPDFPath() {
        return "output/temp_" + this.uniqueId + ".pdf";
    }

    public Document(String fileId, Workbook workbook) throws Exception {
        this.workbook    = workbook;
        this.modelFileId = fileId;
        this.uniqueId    = getNextId();
        UtilWord.writeToFile(getDriveService(false)
                .export(modelFileId, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .executeMediaAsInputStream(),
                getTempWordPath());
        this.doc = new com.aspose.words.Document(getTempWordPath());
        replaceTags();
    }

    private void replaceTags() {
        replaceTableLoopTags();
        replaceLoopTags();
        replaceVarTags();
    }

    private void replaceVarTags() {
        for(Object runObj : doc.getChildNodes(NodeType.RUN, true)) {
            Run run = (Run)runObj;
            run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, "date", new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
        }
    }

    private void replaceLoopTags() {
        /*for(Object object : doc.getChildNodes(NodeType.ANY, true)) {
            TODO : Parcourir tous les nodes pour trouver les starts et revenir jusqu'au node parent qui permet de trouver le end correspondant
        }*/
    }

    private void replaceTableLoopTags() {
        Map<Table, Map<Row, List<Row>>> tables = new HashMap<>();
        for(Object tableObj : doc.getChildNodes(NodeType.TABLE, true)) {
            Table table = (Table)tableObj;
            tables.put(table, new HashMap<>());
            if(UtilWord.canFind(table.getText(), UtilWord.TABLE_LOOP)) {
                for(Object rowObj : table.getChildNodes(NodeType.ROW, true)) {
                    Row doc_row = (Row)rowObj;
                    tables.get(table).put(doc_row, replaceTableLoopTags_rows(doc_row));
                }
            }
        }
        for(Map.Entry<Table, Map<Row, List<Row>>> entry : tables.entrySet()) {
            for(Map.Entry<Row, List<Row>> row : entry.getValue().entrySet()) {
                Collections.reverse(row.getValue());
                for(Row newRow : row.getValue()) {
                    entry.getKey().insertAfter(newRow, row.getKey());
                }
                if(row.getValue().size() > 0)
                    entry.getKey().removeChild(row.getKey());
            }
        }
    }

    private List<Row> replaceTableLoopTags_rows(Row doc_row) {
        if(UtilWord.canFind(doc_row.getText(), UtilWord.TABLE_LOOP)) {
            String sheetName = UtilWord.findFirst(doc_row.getText(), UtilWord.TABLE_LOOP);
            Sheet sheet = workbook.getSheet(sheetName);
            if(sheet == null)
                return new ArrayList<>();
            List<Row> newRows = new ArrayList<>();
            for(org.miage.isiForm.google.sheets.Row sheet_row : sheet.getRows()) {
                Row newRow = (Row)doc_row.deepClone(true);
                for(Object runObj : newRow.getChildNodes(NodeType.RUN, true)) {
                    Run run = (Run)runObj;
                    for(String var : UtilWord.findAll(run.getText(), UtilWord.VAR)) {
                        org.miage.isiForm.google.sheets.Cell sheet_cell = sheet_row.getCell(sheet.getColumnIndex(var));
                        String value = sheet_cell != null ? sheet_cell.getValue() : "\\{\\{Erreur, impossible de recuperer " + var + "}}";
                        run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, var, value));
                        run.setText(UtilWord.replace(run.getText(), UtilWord.TABLE_LOOP, sheetName, ""));
                    }
                }
                newRows.add(newRow);
            }
            return newRows;
        }
        return new ArrayList<>();
    }

    public void toPDF() throws Exception {
        doc.save(getTempPDFPath());
    }
}
