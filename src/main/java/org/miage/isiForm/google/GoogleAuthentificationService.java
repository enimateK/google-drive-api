package org.miage.isiForm.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.mortbay.util.IO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public abstract class GoogleAuthentificationService {

    // Adresse mail à autoriser par la sheet : processdev@process-dev-gdrive.iam.gserviceaccount.com

    private static final String APPLICATION_NAME      = "GoogleDriveApi";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final List<String> SCOPES_SHEETS_READ  = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private final List<String> SCOPES_SHEETS_WRITE = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final List<String> SCOPES_DOCS_READ    = Collections.singletonList(DocsScopes.DOCUMENTS_READONLY);
    private final List<String> SCOPES_DOCS_WRITE   = Collections.singletonList(DocsScopes.DOCUMENTS);
    private final List<String> SCOPES_DRIVE_READ   = Collections.singletonList(DriveScopes.DRIVE_READONLY);
    private final List<String> SCOPES_DRIVE_WRITE  = Collections.singletonList(DriveScopes.DRIVE);
    private final JsonFactory  JSON_FACTORY        = JacksonFactory.getDefaultInstance();

    /**
     * @return Authentification
     */
    private Credential getCredentials(final List<String> SCOPES) throws IOException {
        return GoogleCredential.fromStream(GoogleAuthentificationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);
    }

    /**
     * @return Authentification Google Sheets en lecture seule
     */
    private Credential getSheetsReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_SHEETS_READ);
    }

    /**
     * @return Authentification Google Sheets en écriture
     */
    private Credential getSheetsWriteCredentials() throws IOException {
        return getCredentials(SCOPES_SHEETS_WRITE);
    }

    /**
     * @return Service de lecture Google Sheets
     */
    protected Sheets.Spreadsheets.Values getSheetsService(boolean write) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, write ? getSheetsWriteCredentials() : getSheetsReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().spreadsheets().values();
    }

    /**
     * @return Authentification Google Docs en lecture seule
     */
    private Credential getDocsReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_DOCS_READ);
    }

    /**
     * @return Authentification Google Docs en écriture
     */
    private Credential getDocsWriteCredentials() throws IOException {
        return getCredentials(SCOPES_DOCS_WRITE);
    }

    /**
     * @return Service de lecture Google Docs
     */
    protected Docs.Documents getDocsService(boolean write) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, write ? getDocsWriteCredentials() : getDocsReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().documents();
    }

    /**
     * @return Authentification Google Drive en lecture seule
     */
    private Credential getDriveReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_DRIVE_READ);
    }

    /**
     * @return Authentification Google Drive en écriture
     */
    private Credential getDriveWriteCredentials() throws IOException {
        return getCredentials(SCOPES_DRIVE_WRITE);
    }

    /**
     * @return Service de lecture Google Drive
     */
    protected Drive.Files getDriveService(boolean write) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, write ? getDriveWriteCredentials() : getDriveReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().files();
    }
}
