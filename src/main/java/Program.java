import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    public static void main(String... args) throws GeneralSecurityException, IOException {
        String fileId = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";
        String version = new GoogleSheetsApi(fileId).getVersion();
        GoogleSheetsFile googleSheetsFile = new XMLReader().getGoogleSheetsFile(version);
        List<String> indexes = new ArrayList<>();
        List<Object> content = new ArrayList<>();
        for (Sheet sheet : googleSheetsFile.getSheets()) {
            for (Column column : sheet.getColumns()) {
                indexes.add(column.getIndex());
            }
            List sheetContent = new GoogleSheetsApi(fileId).getCells(sheet.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
            content.add(sheetContent);
            indexes.clear();
        }
        System.out.println(content);
    }
}
