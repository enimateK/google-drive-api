package org.miage.isiForm;

import org.miage.isiForm.google.docs.Document;
import org.miage.isiForm.google.sheets.Sheet;
import org.miage.isiForm.google.sheets.Workbook;
import org.miage.isiForm.model.output.ErrorInfo;
import org.miage.isiForm.model.output.Message;
import org.miage.isiForm.model.output.WorkbookInfo;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces("application/json")
public class GoogleSheetsRESTService {
    /**
     * Gère l'instanciation des Workbooks
     */
    static abstract class Workbooks {
        private static final Map<String, Workbook> workbooks = new HashMap<>();

        private static Workbook getNew(String id) throws Exception {
            Workbook workbook = new Workbook(id);
            add(workbook);
            return workbook;
        }

        private static void add(Workbook workbook) {
            workbooks.put(workbook.fileId, workbook);
        }

        private static boolean exists(String id) {
            return workbooks.containsKey(id);
        }

        public static Workbook get(String id, boolean readOnly) throws Exception {
            if(!exists(id)) {
                if(!readOnly)
                    throw new Exception("Vous devez d'abord obtenir les données du google sheet avant d'écrire dedans !");
                return getNew(id);
            }
            return workbooks.get(id);
        }
    }

    /**
     * Gère l'instanciation des Documents
     */
    static abstract class Documents {
        private static Document getNew(String id, Workbook workbook) throws Exception {
            return new Document(id, workbook);
        }

        public static Document get(String id, Workbook workbook) throws Exception {
            return getNew(id, workbook);
        }
    }

    /**
     * Identifiant de la Sheet exemple
     */
    public static final String SHEET_EXAMPLE = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";

    /**
     * Identifiant du modèle de PDF exemple 'Fiche de formation'
     */
    public static final String DOC_EXAMPLE_FORM = "1fvbeT5fwqiW-mznNvnPUy9sTHXUA7otTVY3GWjtlV08";

    /**
     * Identifiant du modèle de PDF exemple 'Programme de formation global'
     */
    public static final String DOC_EXAMPLE_LIST = "";

    /**
     * Retourne le contenu de la Sheet passé en paramètre en JSON
     */
    @GET
    @Path("/get/{id}")
    public String getWorkbook(@PathParam("id") String id) {
        try {
            return new WorkbookInfo(Workbooks.get(id, true)).getJson();
        } catch(Throwable ex) {
            return ErrorInfo.getJson(ex);
        }
    }

    @GET
    @Path("/update/{id}/{sheet}/{row}/{var}/{value}")
    public String updateWorkbook(@PathParam("id") String id, @PathParam("sheet") String sheetName, @PathParam("row") String row, @PathParam("var") String var, @PathParam("value") String value) {
        try {
            Workbook workbook = Workbooks.get(id, false);
            Sheet sheet = workbook.getSheet(sheetName);
            sheet.getRow(row).getCell(sheet.getColumnIndex(var)).setValue(value);
            workbook.save();
            return Message.getJson("La modification a bien été effectuée !");
        } catch (Throwable ex) {
            return ErrorInfo.getJson(ex);
        }
    }

    /**
     * Met à jour la Sheet passée en paramètre
     */
    @POST
    @Path("/update")
    @Consumes("application/json")
    public String updateWorkbook(WorkbookInfo workbookInfo) {
        try {
            Workbooks.get(workbookInfo.getFileId(), false).update(workbookInfo);
            return Message.getJson("La modification a bien été effectuée !");
        } catch (Throwable ex) {
            return ErrorInfo.getJson(ex);
        }
    }

    /**
     * Retourne un PDF basé sur le modèle passé en paramètre rempli avec les données de la sheet passée en paramètre
     */
    @POST
    @Path("/pdf/{sheetid}/{modelid}")
    public String getPDF(@PathParam("sheetid") String sheetId, @PathParam("modelid") String docId) {
        try {

            return "";
        } catch (Throwable ex) {
            return ErrorInfo.getJson(ex);
        }
    }
}
