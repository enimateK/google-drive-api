import GoogleSheet.Cell;
import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonWriter;
import model.Column;
import model.GoogleSheetsFile;
import model.Sheet;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces("application/json")
public class GoogleSheetsRESTService {
    private final String EXAMPLE = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";

    @GET
    @Path("/get/example")
    public String getWorkbook() {
        return getWorkbook(EXAMPLE);
    }

    @GET
    @Path("/get/{id}")
    public String getWorkbook(@PathParam("id") String id) {
        try {
            GoogleSheet.GoogleSheetsFile gFile = new GoogleSheet.GoogleSheetsFile(id);
            String version = gFile.getMetadata().getVersion();
            GoogleSheetsFile fileInfo = new XMLReader().getGoogleSheetsFile(version);
            List<List<List<Cell>>> content = new ArrayList<>();
            List<String> sheetsNames = new ArrayList<>();
            for (Sheet sheetInfo : fileInfo.getSheets()) {
                sheetsNames.add(sheetInfo.getName());
                List<String> indexes = new ArrayList<>();
                for (Column columnInfo : sheetInfo.getColumnsInfo()) {
                    indexes.add(columnInfo.getIndex());
                }
                List<List<Cell>> sheetContent = gFile.getCells(sheetInfo.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
                content.add(sheetContent);
                indexes.clear();
            }
            JSONObject json = new JSONFormatter().getJson(content, sheetsNames);
            return JsonWriter.formatJson(json.toString());
        } catch (Throwable ex) {
            JsonObject message = new JsonObject();
            message.put("erreur", "Erreur lors de la récupération des données : " + ex.toString());
            return JsonWriter.formatJson(message.toString());
        }
    }

    @GET
    @Path("/update/example/{sheet}/{cell}/{value}")
    public String updateWorkbook(@PathParam("sheet") String sheet, @PathParam("cell") String cell, @PathParam("value") String value) {
        return updateWorkbook(EXAMPLE, sheet, cell, value);
    }

    @GET
    @Path("/update/{id}/{sheet}/{cell}/{value}")
    public String updateWorkbook(@PathParam("id") String id, @PathParam("sheet") String sheet, @PathParam("cell") String cell, @PathParam("value") String value) {
        JsonObject message = new JsonObject();
        try {
            new GoogleSheet.GoogleSheetsFile(id).writeInCell(sheet, cell, value);
            message.put("message", "La modification a bien été effectuée");
        } catch (Exception ex) {
            message.put("erreur", "Erreur lors de l'écriture des données : " + ex.toString());
        }
        return JsonWriter.formatJson(message.toString());
    }
}
