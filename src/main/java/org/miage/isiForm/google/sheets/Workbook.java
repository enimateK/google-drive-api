package org.miage.isiForm.google.sheets;

import org.miage.isiForm.model.output.SheetInfo;
import org.miage.isiForm.model.output.WorkbookInfo;
import org.miage.isiForm.model.mapping.MappingInfo;
import org.miage.isiForm.model.mapping.SheetMappingInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbook extends WorkbookBase {

    private Instant lastUpdate = null;
    private Map<String, Sheet> sheets = new HashMap<>();
    private Metadata metadata;

    public Workbook(String fileId) throws Exception {
        super(fileId);
        createSheets();
        if(getMetadata() == null)
            throw new Exception("Ce n'est pas un fichier valide (pas de métadonnées)");
        if(getMappingFile() == null)
            throw new Exception("Ce n'est pas un fichier valide (version non prise en charge)");
    }

    MappingInfo getMappingFile() {
        return MappingInfo.get(getMetadata().getVersion());
    }

    public Metadata getMetadata() {
        if(metadata == null) {
            try {
                metadata = new Metadata(getCells(Metadata.getMetadatasSheet(), Metadata.getMetadatasStart(), Metadata.getMetadatasEnd()));
            } catch (GeneralSecurityException | IOException e) {
                return null;
            }
        }
        return metadata;
    }

    private void addSheet(Sheet sheet) {
        sheets.put(sheet.name, sheet);
    }

    public Sheet getSheet(String name) {
        return sheets.getOrDefault(name, null);
    }

    public boolean needsToUpdate() {
        return lastUpdate == null || lastUpdate.isBefore(Instant.now().minus(Duration.ofSeconds(5)));
    }

    public List<Sheet> getSheets() throws GeneralSecurityException, IOException {
        if(needsToUpdate())
            createSheets();
        return new ArrayList<>(sheets.values());
    }

    public void createSheets() throws GeneralSecurityException, IOException {
        lastUpdate = Instant.now();
        for(SheetMappingInfo sheetInfo : getMappingFile ().getSheets()) {
            String firstIndex = sheetInfo.getFirstIndex();
            List<List<Object>> cellss = getCells(sheetInfo.getName(), firstIndex, sheetInfo.getLastIndex());
            addSheet(new Sheet(this, sheetInfo.getName(), sheetInfo.getColumns(), firstIndex, cellss));
        }
    }

    public void updateSheets() {
        lastUpdate = Instant.now();
        // TODO
    }

    public void save() {
        for(Sheet sheet : sheets.values()) {
            sheet.save();
        }
    }

    public void update(WorkbookInfo workbook) {
        for(Map.Entry<String, SheetInfo> sheet : workbook.getSheets().entrySet()) {
            sheets.get(sheet.getKey()).update(sheet.getValue().getRows());
        }
    }
}
