package GoogleSheet;

import java.util.*;

public class Sheet {
    public final String name;
    private Map<String, String> columns = new HashMap<>();
    private List<Row> rows = new ArrayList<>();

    Sheet(String name) {
        this.name = name;
    }

    private List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }
}
