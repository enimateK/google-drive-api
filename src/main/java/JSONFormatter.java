import GoogleSheet.Cell;
import org.json.simple.JSONObject;

import java.util.List;

public class JSONFormatter {
    JSONObject getJson(List<List<List<Cell>>> fileContent, List<String> sheetsName) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSheet = new JSONObject();
        JSONObject jsonLine = new JSONObject();

        //TODO: A refaire plus proprement
        int sheetNumber = 0;
        for(List<List<Cell>> cellss : fileContent) {
            int i = 0;
            while (i < cellss.size()) {
                if (i != 0) {
                    int j = 0;
                    for (Cell cell : cellss.get(i)) {
                        jsonLine.put(cellss.get(0).get(j).getValue(), cell.getValue());
                        j++;
                    }
                    jsonSheet.put("line" + i, jsonLine);
                    jsonLine = new JSONObject();
                }
                i++;
            }
            jsonObject.put(sheetsName.get(sheetNumber), jsonSheet);
            jsonSheet = new JSONObject();
            sheetNumber++;
        }

        return jsonObject;
    }
}