package org.miage.isiForm.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public abstract class GoogleAuthentificationService {

    // Adresse mail à autoriser par la sheet : processdev@process-dev-gdrive.iam.gserviceaccount.com

    private static final String APPLICATION_NAME      = "GoogleDriveApi";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final List<String> SCOPES_SHEETS_READ   = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private final List<String> SCOPES_SHEETS_WRITE  = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final List<String> SCOPES_DOCS_READ     = Collections.singletonList(DocsScopes.DOCUMENTS_READONLY);
    private final JsonFactory JSON_FACTORY          = JacksonFactory.getDefaultInstance();

    /**
     * @return authentification
     */
    private Credential getCredentials(final List<String> SCOPES) throws IOException {
        return GoogleCredential.fromStream(GoogleAuthentificationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);
    }

    /**
     * @return authentification google sheets en lecture seule
     */
    private Credential getSheetsReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_SHEETS_READ);
    }

    /**
     * @return authentification google sheets en lecture/écriture
     */
    private Credential getSheetsWriteCredentials() throws IOException {
        return getCredentials(SCOPES_SHEETS_WRITE);
    }

    /**
     * @return Service de lecture google sheets
     */
    protected Sheets.Spreadsheets.Values getSheetsService(boolean write) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, write ? getSheetsWriteCredentials() : getSheetsReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().spreadsheets().values();
    }

    /**
     * @return authentification google docs en lecture seule
     */
    private Credential getDocsReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_DOCS_READ);
    }

    /**
     * @return Service de lecture google docs
     */
    protected Docs.Documents getDocsService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getDocsReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().documents();
    }
}
