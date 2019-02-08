package GoogleSheet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metadata {
    private Map<String, String> metadatas = new HashMap<>();

    Metadata(List<List<Cell>> cellss) {
        assert cellss.size() >= 2;
        assert cellss.get(0).size() >= cellss.get(1).size();
        for(int i = 0 ; i < cellss.get(0).size() ; i++) {
            metadatas.put(cellss.get(0).get(i).getValue().toUpperCase(), cellss.get(1).get(i).getValue());
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
}
