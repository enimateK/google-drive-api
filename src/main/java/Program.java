import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Program {
    public static void main(String... args) {
        String fileId    = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";
//        String sheet     = "Meta-Donnees";
//        String rangeFrom = "A1";
//        String rangeTo   = "B2";
//        try {
//            List<List<Object>> data = new GoogleSheetsApi(fileId).getCells(sheet, rangeFrom, rangeTo);
//            for(List<Object> objects : data) {
//                StringBuilder line = new StringBuilder();
//                for(Object object : objects) {
//                    line.append(object).append(",");
//                }
//                System.out.println(line.toString().replaceAll(",$", ""));
//            }
//            System.out.println();
//        } catch (GeneralSecurityException | IOException e) {
//            e.printStackTrace();
//        }
        String version = new XMLReader(fileId).getVersion();
        System.out.println(version);
    }
}
