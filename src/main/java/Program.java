import GoogleSheet.Cell;
import GoogleSheet.GoogleSheetsFile;
import com.sun.net.httpserver.HttpServer;
import model.Column;
import model.File;
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
        URI baseUri = UriBuilder.fromUri(HOST).port(PORT).build();
        ResourceConfig config = new ResourceConfig(GoogleSheetsRESTService.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    public static void main2()  throws GeneralSecurityException, IOException {
        String fileId = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";
        String version = new GoogleSheetsFile(fileId).getMetadata().getVersion();
        File file = new XMLReader().getGoogleSheetsFile(version);
        List<List<List<Cell>>> content = new ArrayList<>();
        List<String> sheetsNames = new ArrayList<>();
        for (Sheet sheet : file.getSheets()) {
            sheetsNames.add(sheet.getName());
            List<String> indexes = new ArrayList<>();
            for (Column column : sheet.getColumns()) {
                indexes.add(column.getIndex());
            }
            List<List<Cell>> sheetContent = new GoogleSheetsFile(fileId).getCells(sheet.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
            content.add(sheetContent);
            indexes.clear();
        }
        JSONObject json = new JSONFormatter().getJson(content, sheetsNames);

        System.out.println(json);
    }
}
