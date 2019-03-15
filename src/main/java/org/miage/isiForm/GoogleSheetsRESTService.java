package org.miage.isiForm;

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
    static abstract class Workbooks {
        private static final Map<String, Workbook> workbooks = new HashMap<>();

        static void add(Workbook workbook) {
            workbooks.put(workbook.fileId, workbook);
        }

        static boolean contains(String id) {
            return workbooks.containsKey(id);
        }

        static Workbook get(String id) {
            return workbooks.getOrDefault(id, null);
        }
    }

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
            Workbook workbook;
            if(Workbooks.contains(id)) {
                workbook = Workbooks.get(id);
                // TODO: update depuis la sheet
            } else {
                workbook = new Workbook(id);
                Workbooks.add(workbook);
            }
            return new WorkbookInfo(workbook).getJson();
        } catch(Throwable ex) {
            return ErrorInfo.getJson(ex);
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
        if(!Workbooks.contains(id))
            return ErrorInfo.getJson(new Error("Vous devez d'abord obtenir les données du google sheet avant d'écrire dedans !"));
        Workbook workbook = Workbooks.get(id);
        try {
            workbook.writeInCells(sheet, cell, value);
        } catch (Throwable ex) {
            return ErrorInfo.getJson(ex);
        }
        return Message.getJson("La modification a bien été effectuée !");
    }

    @POST
    @Path("/update")
    @Consumes("application/json")
    public String updateWorkbook(WorkbookInfo workbookInfo) {
        if(!Workbooks.contains(workbookInfo.getFileId()))
            return ErrorInfo.getJson(new Error("Vous devez d'abord obtenir les données du google sheet avant d'écrire dedans !"));
        Workbook workbook = Workbooks.get(workbookInfo.getFileId());
        // TODO charger les données dans le workbook
        return Message.getJson("La modification a bien été effectuée !");
    }
}
