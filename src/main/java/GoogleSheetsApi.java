import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

class GoogleSheetsApi {

    // Adresse mail à autoriser par la sheet : processdev@process-dev-gdrive.iam.gserviceaccount.com

    private static final String APPLICATION_NAME      = "GoogleDriveApi";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final List<String> SCOPES_READ   = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private final List<String> SCOPES_WRITE  = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final JsonFactory JSON_FACTORY   = JacksonFactory.getDefaultInstance();

    private static final String META_DATA_SHEET_NAME   = "Meta-Donnees";
    private static final String VERSION_COLUMN = "A2";

    private String mFileId;

    GoogleSheetsApi(String fileId) {
        mFileId = fileId;
    }

    private Credential getCredentials(final List<String> SCOPES) throws IOException {
        return GoogleCredential.fromStream(GoogleSheetsApi.class.getResourceAsStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);
    }

    private Credential getReadOnlyCredentials() throws IOException {
        return getCredentials(SCOPES_READ);
    }

    private Credential getWriteCredentials() throws IOException {
        return getCredentials(SCOPES_WRITE);
    }

    String getVersion() {
        List<List<Object>> metaData = null;
        try {
            metaData = new GoogleSheetsApi(mFileId).getCells(META_DATA_SHEET_NAME, VERSION_COLUMN, VERSION_COLUMN);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return metaData != null ? metaData.get(0).get(0).toString() : "Error";
    }

    List<List<Object>> getCells(String sheet, String cellStart, String cellEnd) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = sheet + "!" + cellStart + ":" + cellEnd;
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getReadOnlyCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(mFileId, range)
                .execute();
        return response.getValues();
    }
}
