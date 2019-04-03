package org.miage.isiForm.google.docs;

import com.aspose.pdf.EpubLoadOptions;
import com.aspose.pdf.HtmlLoadOptions;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.docs.v1.model.*;
import com.google.api.services.drive.model.File;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.miage.isiForm.google.GoogleAuthentificationService;
import org.mortbay.util.IO;
import org.w3c.tidy.Tidy;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document extends GoogleAuthentificationService {

    public final String modelFileId;
    private final String duplicateFileId;

    private String content;

    public Document(String fileId) throws Exception {
        this.modelFileId = fileId;
        this.content     = getContent(true);

        /*com.google.api.services.docs.v1.model.Document doc = getDocsService(false).get(modelFileId).execute();
        System.out.println(doc.getBody().getContent());
        getDocsService(true).create(doc);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(doc.getBody().getContent().toString());
        Stack<JsonNode> stack = new Stack<>();
        JsonNode node = findText(stack, root);
        for(JsonNode node2 : stack) {
            System.out.println(node2.get("startIndex"));
        }
        System.out.println(stack.get(4));*/

        File file = getDriveService(true)
                .copy(modelFileId, null)
                .execute();
        duplicateFileId = file.getId();
    }

    public JsonNode findText(Stack<JsonNode> stack, JsonNode parent) {
        JsonNode value = null;
        stack.push(parent);
        for(JsonNode node : parent) {
            if(node.asText().contains("{{"))
                return node;
            value = findText(stack, node);
            if(value != null)
                break;
        }
        if(value == null)
            stack.pop();
        return value;
    }

    public String getContent(boolean html) throws Exception {
        InputStream stream = getDriveService(false)
                .export(modelFileId, html ? "text/html" : "text/plain")
                .executeMediaAsInputStream();
        return toString(stream);
    }

    public void toPdf() throws Exception {
        Tidy tidy = new Tidy();
        tidy.setShowWarnings(false);
        tidy.setXmlTags(false);
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);
        tidy.setMakeClean(true);
        org.w3c.dom.Document xmlDoc = tidy.parseDOM(getDriveService(false).export(modelFileId, "text/html").executeMediaAsInputStream(), null);
        tidy.pprint(xmlDoc, new FileOutputStream("output/test.xhtml"));
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("output/test.pdf"));
        doc.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, doc, new FileInputStream("output/test.xhtml"));
        doc.close();
    }

    public void toWord() throws Exception {
        toFile(getDriveService(false).export(modelFileId, "application/vnd.openxmlformats-officedocument.wordprocessingml.document").executeMediaAsInputStream(), "output/test.docx");
    }

    /*public void toPdfFromepub() throws Exception {
        InputStream stream = getDriveService(false).export(modelFileId, "text/html").executeMediaAsInputStream();
        toFile(stream, "output/test.html");
        HtmlLoadOptions optionsepub = new HtmlLoadOptions("testPdf");
        com.aspose.pdf.Document docepub = new com.aspose.pdf.Document("output/test.html", optionsepub);
        docepub.save("output/testhtml.pdf");
    }*/

    public Request getRequest(String varName, String replaceValue) {
        return new Request()
                .setReplaceAllText(new ReplaceAllTextRequest()
                    .setContainsText(new SubstringMatchCriteria()
                        .setText("{{" + varName + "}}")
                        .setMatchCase(true))
                    .setReplaceText(replaceValue));
    }

    public void updateDocument(List<Request> requests) throws GeneralSecurityException, IOException {
        BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest();
        getDocsService(true)
                .batchUpdate(duplicateFileId, body.setRequests(requests))
                .execute();
    }

    public String downloadToPDF() throws GeneralSecurityException, IOException {
        InputStream stream = getDriveService(false)
                .export(duplicateFileId, "application/pdf")
                .executeMediaAsInputStream();
        toFile(stream, "output/test.pdf");
        stream = getDriveService(false)
                .export(duplicateFileId, "application/pdf")
                .executeMediaAsInputStream();
        return toString(stream);
    }

    public void deleteDuplicate() throws GeneralSecurityException, IOException {
        getDriveService(true)
                .delete(duplicateFileId)
                .execute();
    }

    protected String toString(InputStream stream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }

    protected void toFile(InputStream stream, String outputFile) throws IOException {
        OutputStream output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = stream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
    }

    public List<String> getVars() throws Exception {
        List<String> vars = new ArrayList<>();
        String doc = getContent(false);
        Pattern pattern = Pattern.compile("\\{\\{.*}}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(doc);
        int start = 0;
        while(matcher.find(start)) {
            vars.add(matcher.group().replaceFirst("\\{\\{", "").replaceFirst("}}", ""));
            start = matcher.end();
        }
        return vars;
    }
}
