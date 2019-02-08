package GoogleSheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

abstract class GoogleSheetsService {
    private static final String APPLICATION_NAME      = "GoogleDriveApi";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final List<String> SCOPES_READ   = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private final List<String> SCOPES_WRITE  = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final JsonFactory JSON_FACTORY   = JacksonFactory.getDefaultInstance();

    /**
     * @return authentification
     */
    private Credential getCredentials(final List<String> SCOPES) throws IOException {
        return GoogleCredential.fromStream(GoogleSheetsFile.class.getResourceAsStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);
    }

    /**
     * @return authentification en lecture seule
     */
    private Credential getReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_READ);
    }

    /**
     * @return authentification en lecture/écriture
     */
    private Credential getWriteCredentials() throws IOException {
        return getCredentials(SCOPES_WRITE);
    }

    /**
     * @return Service de lecture
     */
    Sheets.Spreadsheets.Values getService(boolean write) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, write ? getWriteCredentials() : getReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build().spreadsheets().values();
    }
}
