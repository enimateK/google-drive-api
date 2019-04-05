package org.miage.isiForm.model.mapping;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingInfo {
    private static Map<String, MappingInfo> mappingFiles = new HashMap<>();
    public static MappingInfo get(String version) {
        if(mappingFiles.containsKey(version))
            return mappingFiles.get(version);
        MappingInfo file;
        ObjectMapper mapper = new ObjectMapper();
        try {
            File path = new File("settings/version_" + version + ".json");
            if(path.exists() && !path.isDirectory())
                file = mapper.readValue(path, MappingInfo.class);
            else
                file = mapper.readValue(MappingInfo.class.getResource("/version_" + version + ".json"), MappingInfo.class);
        } catch (Exception ex) {
            return null;
        }
        mappingFiles.put(version, file);
        return file;
    }

    @JsonProperty("version")
    private String version;
    @JsonManagedReference
    @JsonProperty("sheets")
    private Map<String, SheetMappingInfo> sheets;

    public String getVersion() {
        return version;
    }

    String getSheetName(SheetMappingInfo sheet) {
        for(Map.Entry<String, SheetMappingInfo> entry : sheets.entrySet()) {
            if(entry.getValue() == sheet) {
                return entry.getKey();
            }
        }
        return "";
    }

    public List<SheetMappingInfo> getSheets() {
        return new ArrayList<>(sheets.values());
    }

    public SheetMappingInfo getSheet(String sheet) {
        return sheets.get(sheet);
    }
}
