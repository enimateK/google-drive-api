package org.miage.isiForm;

import com.aspose.words.*;
import com.aspose.words.Body;
import com.aspose.words.Table;
import com.google.api.services.docs.v1.model.*;
import javafx.scene.control.Tab;
import org.miage.isiForm.google.docs.Document;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.simple.JSONObject;
import org.miage.isiForm.google.sheets.Workbook;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Program {
    private final static int    PORT = 9998;
    private final static String HOST = "http://localhost/";

    public static void main(String... args) {
        //generatePDF();
        //buildAPIlocally();
        getPDF();
    }

    private static void buildAPIlocally() {
        URI baseUri = UriBuilder.fromUri(HOST).port(PORT).build();
        ResourceConfig config = new ResourceConfig(GoogleSheetsRESTService.class);
        config.register(new CORSFilter());
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    private static void getPDF() {
        try {
            Document doc = new Document("1fvbeT5fwqiW-mznNvnPUy9sTHXUA7otTVY3GWjtlV08");
            doc.toWord();
            com.aspose.words.Document document = new com.aspose.words.Document("output/test.docx");
            getRecursiveChildNodes(document.getFirstSection(), (node) -> {
                if(node.getNodeType() == NodeType.TABLE) {
                    Table table = (Table)node;
                    Row row = getRow(table);
                    if(row != null) {
                        table.appendChild(row.deepClone(true));
                        return 1;
                    }
                }
                return 0;
            });
            /*for(Object object : document.getFirstSection().getBody().getChildNodes()) {
                Node node = (Node)object;
                if(node == null)
                    continue;
                System.out.println(node.getText());
                System.out.println(node.getNodeType());
                if(node.getNodeType() == NodeType.TABLE) {
                    Table table = (Table)node;
                    for(Object object2 : table.getChildNodes()) {
                        Node node2 = (Node)object2;
                        System.out.println(node2.getText());
                    }
                }
            }*/
            document.save("output/testDoc.pdf");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getRecursiveChildNodes(CompositeNode parentNode, Function<Node, Integer> lambda) {
        for(Node childNode = parentNode.getFirstChild() ; childNode != null ; childNode = tryGetNextSibling(childNode)) {
            System.out.println(childNode + " - " + childNode.getText());
            int insertedNodes = lambda.apply(childNode);
            if(childNode.isComposite())
                getRecursiveChildNodes((CompositeNode)childNode, lambda);
            for(int cpt = 0 ; cpt < insertedNodes ; cpt++) {
                childNode = tryGetNextSibling(childNode);
            }
        }
    }

    private static Node tryGetNextSibling(Node node) {
        try {
            return node.getNextSibling();
        } catch(Exception ignored) {
            return null;
        }
    }

    public static Row getRow(Table table) {
        for(Object object : table.getChildNodes()) {
            Node node = (Node)object;
            if(node != null && node.getNodeType() == NodeType.ROW && node.getText().contains("{{")) {
                return (Row)node;
            }
        }
        return null;
    }

    /*private static void getPDF() {
        BirtReporter.getReportAsPdf(null, "new_report_1.rptdesign");
    }*/

    /*private static void getPDF() {
        try {
            Document doc = new Document("1fvbeT5fwqiW-mznNvnPUy9sTHXUA7otTVY3GWjtlV08");
            List<Request> requests = new ArrayList<>();
            requests.add(new Request()
                    .setInsertTableRow(new InsertTableRowRequest()
                            .setTableCellLocation(new TableCellLocation()
                                    .setTableStartLocation(new Location()
                                            .setIndex(27))
                                            .setRowIndex(1)
                                            .setColumnIndex(1))
                            .setInsertBelow(true)));
            requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                .setText("{{Code de fiche de formation}}")
                .setLocation(new Location().setIndex(208))));
            doc.updateDocument(requests);
            doc.downloadToPDF();
            //doc.toPdf();
            System.out.println(doc.getContent(true));
            for(String str : doc.getVars())
                System.out.println(str);
            List<Request> requests = new ArrayList<>();
            requests.add(doc.getRequest("Code de fiche de formation", "PHP1-01"));
            requests.add(doc.getRequest("Libelle de fiche de formation", "Tour d’horizon PHP"));
            requests.add(doc.getRequest("Description des fiches de formation", "Principes de fonctionnement\n" +
                    "Le contexte Web\n" +
                    "Les variables\n" +
                    "Les types de données en un clin d’œil\n" +
                    "Les opérateurs\n" +
                    "Les instructions conditionnelles\n" +
                    "Un type particulier : les tableaux\n" +
                    "Les boucles, parcourir les tableaux\n" +
                    "Trier les tableaux par ordre alphabétique\n" +
                    "Introduction aux superglobales\n" +
                    "Récupération de données de formulaire\n" +
                    "Envoyer un e-mail\n" +
                    "Les constantes"));
            doc.updateDocument(requests);
            System.out.println(doc.downloadToPDF());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }*/

    /*public static void generatePDF() {
        try {
            String fileId = "1nv43m6oX6VWHg2iENxYq7f-IFfWg8MDCqRuuNAQK4o8";
            String version = new WorkbookBase(fileId).getMetadata().getVersion();
            GoogleSheetsFile fileInfo = new XMLMappingReader().getGoogleSheetsFile(version);
            List<List<List<Cell>>> content = new ArrayList<>();
            List<String> sheetsNames = new ArrayList<>();
            for (Sheet sheetInfo : fileInfo.getSheets()) {
                sheetsNames.add(sheetInfo.getName());
                List<String> indexes = new ArrayList<>();
                for (Column columnInfo : sheetInfo.getColumnsInfo()) {
                    indexes.add(columnInfo.getIndex());
                }
                List<List<Cell>> sheetContent = new WorkbookBase(fileId).getCells(sheetInfo.getName(), indexes.get(0), indexes.get(indexes.size() - 1));
                content.add(sheetContent);
                indexes.clear();
            }
            JSONObject json = new JSONFormatter().getJson(content, sheetsNames);
            PDFMaker.createPDFFromList(content);

            System.out.println(json);
        } catch(Throwable error) {
            error.printStackTrace();
        }
    }*/
}
