import GoogleSheet.Cell;
import com.sun.net.httpserver.HttpServer;
import model.Column;
import model.GoogleSheetsFile;
import model.Sheet;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.simple.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    private final static int    PORT = 9998;
    private final static String HOST = "http://localhost/";

    public static void main(String... args) {
        api();
    }

    public static void api() {
        URI baseUri = UriBuilder.fromUri(HOST).port(PORT).build();
        ResourceConfig config = new ResourceConfig(GoogleSheetsRESTService.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    public static void pdf()  throws GeneralSecurityException, IOException {
        String fileId = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";
        String version = new GoogleSheet.GoogleSheetsFile(fileId).getMetadata().getVersion();
        GoogleSheetsFile fileInfo = new XMLReader().getGoogleSheetsFile(version);
        List<List<List<Cell>>> content = new ArrayList<>();
        List<String> sheetsNames = new ArrayList<>();
        for (Sheet sheetInfo : fileInfo.getSheets()) {
            sheetsNames.add(sheetInfo.getName());
            List<String> indexes = new ArrayList<>();
            for (Column columnInfo : sheetInfo.getColumnsInfo()) {
                indexes.add(columnInfo.getIndex());
            }
            List<List<Cell>> sheetContent = new GoogleSheet.GoogleSheetsFile(fileId).getCells(sheetInfo.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
            content.add(sheetContent);
            indexes.clear();
        }
        JSONObject json = new JSONFormatter().getJson(content, sheetsNames);
        PDFMaker.createPDFFromList(content);

        System.out.println(json);
    }
}
