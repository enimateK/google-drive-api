import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

class XMLReader {
    private static final String META_DATA_SHEET_NAME   = "Meta-Donnees";
    private static final String VERSION_COLUMN = "A2";

    private String mFileId;

    XMLReader(String fileId) {
        mFileId = fileId;
    }

    String getVersion() {
        try {
            List<List<Object>> metaData = new GoogleSheetsApi(mFileId).getCells(META_DATA_SHEET_NAME, VERSION_COLUMN, VERSION_COLUMN);

            return metaData.get(0).get(0).toString();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return "Error";
    }


}
