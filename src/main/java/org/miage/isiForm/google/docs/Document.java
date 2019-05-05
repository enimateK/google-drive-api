package org.miage.isiForm.google.docs;

import com.aspose.words.*;
import com.aspose.words.Table;
import javafx.util.Pair;
import org.miage.isiForm.google.GoogleAuthentificationService;
import org.miage.isiForm.google.sheets.Sheet;
import org.miage.isiForm.google.sheets.Workbook;

import javax.rmi.CORBA.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private String sheetKey;
    private String valKey;
    private String val;
    private org.miage.isiForm.google.sheets.Row referenceRow;

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
        toPDF();
    }

    public Document(String fileId, Workbook workbook, String sheetKey, String valKey, String val) throws Exception {
        this.workbook    = workbook;
        this.modelFileId = fileId;
        this.uniqueId    = getNextId();
        this.sheetKey    = sheetKey;
        this.valKey      = valKey;
        this.val         = val;
        Sheet sheet      = workbook.getSheet(sheetKey);
        if(sheet != null) {
            for(org.miage.isiForm.google.sheets.Row row : sheet.getRows()) {
                String colId = sheet.getColumnIndex(valKey);
                if(row.getCell(colId).getValue().equals(val)) {
                    referenceRow = row;
                    break;
                }
            }
        }
        UtilWord.writeToFile(getDriveService(false)
                        .export(modelFileId, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        .executeMediaAsInputStream(),
                getTempWordPath());
        this.doc = new com.aspose.words.Document(getTempWordPath());
        replaceTags();
        toPDF();
    }

    private void replaceTags() {
        replaceTableLoopTags();
        //replaceLoopTags();
        replaceVarTags();
        replaceUnusedTags();
    }

    private void replaceUnusedTags() {
        for(Object runObj : doc.getChildNodes(NodeType.RUN, true)) {
            Run run = (Run)runObj;
            for(String fullVar : UtilWord.findAll(run.getText(), UtilWord.VAR)) {
                run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, fullVar, ""));
            }
            for(String fullVar : UtilWord.findAll(run.getText(), UtilWord.TABLE_LOOP)) {
                run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, fullVar, ""));
            }
            for(String fullVar : UtilWord.findAll(run.getText(), UtilWord.TABLE_ELEM)) {
                run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, fullVar, ""));
            }
        }
    }

    private void replaceVarTags() {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        for(Object runObj : doc.getChildNodes(NodeType.RUN, true)) {
            Run run = (Run)runObj;
            run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, "date", date));
            if(referenceRow != null) {
                for(String fullVar : UtilWord.findAll(run.getText(), UtilWord.VAR)) {
                    String varSheet = UtilWord.getVarSheet(fullVar);
                    Sheet  sheet    = workbook.getSheet(varSheet);
                    String var      = UtilWord.getVar(fullVar);
                    if(sheet == null)
                        continue;
                    String colId    = sheet.getColumnIndex(var);
                    if(UtilWord.isClause(fullVar)) {
                        String varClause   = UtilWord.getVarVarClause(fullVar);
                        String valClause   = UtilWord.getValueClause(fullVar);
                        String colIdClause = sheet.getColumnIndex(varClause);
                        String refColId    = sheet.getColumnIndex(this.valKey);
                        for(org.miage.isiForm.google.sheets.Row row : sheet.getRows()) {
                            if(row.getCell(refColId).getValue().equals(this.val)) {
                                if(row.getCell(colIdClause).getValue().equalsIgnoreCase(valClause)) {
                                    run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, fullVar, row.getCell(colId).getValue()));
                                }
                            }
                        }
                    } else {
                        run.setText(UtilWord.replace(run.getText(), UtilWord.VAR, fullVar, referenceRow.getCell(colId).getValue()));
                    }
                }
            }
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
                if(row.getValue().size() > 0 || UtilWord.canFind(row.getKey().getText(), UtilWord.TABLE_LOOP))
                    entry.getKey().removeChild(row.getKey());
            }
        }
    }

    private List<Row> replaceTableLoopTags_rows(Row doc_row) {
        if(UtilWord.canFind(doc_row.getText(), UtilWord.TABLE_LOOP)) {
            String sheetVar  = UtilWord.findFirst(doc_row.getText(), UtilWord.TABLE_LOOP);
            String sheetName = UtilWord.getVar(sheetVar);
            Sheet sheet = workbook.getSheet(sheetName);
            if(sheet == null)
                return new ArrayList<>();
            List<Row> newRows  = new ArrayList<>();
            String colIdClause = UtilWord.isClause(sheetVar) ? sheet.getColumnIndex(UtilWord.getVarVarClause(sheetVar)) : null;
            String refColId    = referenceRow != null ? sheet.getColumnIndex(this.valKey) : null;
            for(org.miage.isiForm.google.sheets.Row sheet_row : sheet.getRows()) {
                org.miage.isiForm.google.sheets.Cell cellClause = null;
                if(UtilWord.isClause(sheetVar)) {
                    cellClause  = sheet_row.getCell(colIdClause);
                }
                if(!UtilWord.isClause(sheetVar) || ((referenceRow == null || sheet_row.getCell(refColId).getValue().equals(this.val)) && cellClause != null && cellClause.getValue().equalsIgnoreCase(UtilWord.getValueClause(sheetVar)))) {
                    Row newRow = (Row)doc_row.deepClone(true);
                    for(Object runObj : newRow.getChildNodes(NodeType.RUN, true)) {
                        Run run = (Run)runObj;
                        for(String var : UtilWord.findAll(run.getText(), UtilWord.TABLE_ELEM)) {
                            org.miage.isiForm.google.sheets.Cell sheet_cell = sheet_row.getCell(sheet.getColumnIndex(var));
                            String value = sheet_cell != null ? sheet_cell.getValue() : "";
                            run.setText(UtilWord.replace(run.getText(), UtilWord.TABLE_ELEM, var, value));
                            run.setText(UtilWord.replace(run.getText(), UtilWord.TABLE_LOOP, sheetVar, ""));
                        }
                    }
                    newRows.add(newRow);
                }
            }
            return newRows;
        }
        return new ArrayList<>();
    }

    private void toPDF() throws Exception {
        doc.save(getTempPDFPath());
    }

    public byte[] getPDF() throws IOException {
        return Files.readAllBytes(Paths.get(getTempPDFPath()));
    }
}
