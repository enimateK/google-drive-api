package org.miage.isiForm;

import org.miage.isiForm.google.docs.Document;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.miage.isiForm.google.sheets.Workbook;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Program {
    private final static int    PORT = 9998;
    private final static String HOST = "http://localhost";

    public static void main(String... args) {
        System.out.println("Lancement de l'API...");
        //getPDF();
        buildAPIlocally();
        System.out.println("API lancée ! (" + HOST + ':' + PORT + ")");
    }

    private static void buildAPIlocally() {
        URI baseUri = UriBuilder.fromUri(HOST + '/').port(PORT).build();
        ResourceConfig config = new ResourceConfig(GoogleSheetsRESTService.class);
        config.register(new CORSFilter());
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    private static void getPDF() {
        try {
            Workbook workbook = new Workbook(GoogleSheetsRESTService.SHEET_EXAMPLE);
            new Document(GoogleSheetsRESTService.DOC_EXAMPLE_FORM, workbook, "formations", "code_formation", "PHP1-01");
            new Document(GoogleSheetsRESTService.DOC_EXAMPLE_LIST, workbook);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
