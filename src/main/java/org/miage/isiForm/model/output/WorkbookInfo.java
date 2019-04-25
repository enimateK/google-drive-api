package org.miage.isiForm.model.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.miage.isiForm.google.sheets.Sheet;
import org.miage.isiForm.google.sheets.Workbook;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkbookInfo {
    @JsonProperty("file-id")
    private String fileId;
    @JsonProperty("version")
    private String version;
    @JsonProperty("sheets")
    private Map<String, SheetInfo> sheets = new HashMap<>();

    public WorkbookInfo(Workbook workbook) throws GeneralSecurityException, IOException {
        fileId  = workbook.fileId;
        version = workbook.getMetadata().getVersion();
        for(Sheet sheet : workbook.getSheets()) {
            sheets.put(sheet.id, new SheetInfo(sheet));
        }
    }

    @JsonIgnore
    public String getJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch(Throwable error) {
            return ErrorInfo.getJson(error);
        }
    }

    @JsonIgnore
    public String getFileId() {
        return fileId;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @JsonIgnore
    public Map<String, SheetInfo> getSheets() {
        return Collections.unmodifiableMap(sheets);
    }
}
