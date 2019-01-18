import org.json.simple.JSONObject;

import java.util.List;

public class JSONFormatter {
    JSONObject getJson(List<List<List<Object>>> fileContent) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSheet = new JSONObject();
        JSONObject jsonLine = new JSONObject();

        //TODO: A refaire plus proprement
        int sheetNumber = 0;
        for(List<List<Object>> sheet : fileContent) {
            int i = 0;
            while (i < sheet.size()) {
                if (i != 0) {
                    int j = 0;
                    for (Object cell : sheet.get(i)) {
                        jsonLine.put(sheet.get(0).get(j), cell);
                        j++;
                    }
                }
                jsonSheet.put("line" + i, jsonLine);
                jsonLine = new JSONObject();
                i++;
            }
            jsonObject.put("sheet" + sheetNumber, jsonSheet);
            jsonSheet = new JSONObject();
            sheetNumber++;
        }

        return jsonObject;
    }
}