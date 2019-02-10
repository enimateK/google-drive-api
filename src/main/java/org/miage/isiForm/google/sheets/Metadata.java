package org.miage.isiForm.google.sheets;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metadata {
    private Map<String, String> metadatas = new HashMap<>();

    Metadata(List<List<Object>> cellss) {
        assert cellss.size() >= 2;
        assert cellss.get(0).size() >= cellss.get(1).size();
        for(int i = 0 ; i < cellss.get(0).size() ; i++) {
            metadatas.put(cellss.get(0).get(i).toString().toUpperCase(), cellss.get(1).get(i).toString());
        }
    }

    public Map<String, String> getMetadatas() {
        return Collections.unmodifiableMap(metadatas);
    }

    public String getMetadata(String key) {
        return metadatas.get(key.toUpperCase());
    }

    public String getVersion() {
        return getMetadata("Version Applicatif");
    }

    public static String getMetadatasSheet() {
        return "Meta-Donnees";
    }

    public static String getMetadatasStart() {
        return "A1";
    }

    public static String getMetadatasEnd() {
        return "Z2";
    }

    public static String getMetadatasKeysRow() {
        return "1";
    }

    public static String getMetadatasValuesRow() {
        return "2";
    }
}
