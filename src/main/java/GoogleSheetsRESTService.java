import GoogleSheet.Cell;
import GoogleSheet.GoogleSheetsFile;
import com.cedarsoftware.util.io.JsonWriter;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import model.Column;
import model.File;
import model.Sheet;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces("application/json")
public class GoogleSheetsRESTService {
    @GET
    @Path("/get/example")
    public String getSheetInfo() {
        return getSheetInfo("1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8");
    }

    @GET
    @Path("/get/{id}")
    public String getSheetInfo(@PathParam("id") String id) {
        try {
            GoogleSheetsFile gFile = new GoogleSheetsFile(id);
            String version = gFile.getMetadata().getVersion();
            File file = new XMLReader().getGoogleSheetsFile(version);
            List<List<List<Cell>>> content = new ArrayList<>();
            List<String> sheetsNames = new ArrayList<>();
            for (Sheet sheet : file.getSheets()) {
                sheetsNames.add(sheet.getName());
                List<String> indexes = new ArrayList<>();
                for (Column column : sheet.getColumns()) {
                    indexes.add(column.getIndex());
                }
                List<List<Cell>> sheetContent = gFile.getCells(sheet.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
                content.add(sheetContent);
                indexes.clear();
            }
            JSONObject json = new JSONFormatter().getJson(content, sheetsNames);
            return JsonWriter.formatJson(json.toString());
        } catch (Throwable ex) {
            return "Erreur lors de la récupération des données : " + ex.toString();
        }
    }
}
